package networklib.synchronic.timer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import networklib.NetworkConstants;
import networklib.client.task.ITaskScheduler;
import networklib.client.task.packets.SyncTasksPacket;
import networklib.client.task.packets.TaskPacket;

/**
 * This is a basic game timer. All synchronous actions must be based on this clock. The {@link NetworkTimer} also triggers the execution of
 * synchronous tasks in the network game.
 * 
 * @author Andreas Eberle
 * 
 */
public final class NetworkTimer extends TimerTask implements ITaskScheduler {
	public static final short TIME_SLICE = 50;
	private static NetworkTimer instance;

	private final Timer timer;

	private final List<ScheduledTimerable> timerables = new ArrayList<ScheduledTimerable>();
	private final List<ScheduledTimerable> newTimerables = new LinkedList<ScheduledTimerable>();
	private final List<INetworkTimerable> timerablesToBeRemoved = new LinkedList<INetworkTimerable>();

	private final LinkedList<SyncTasksPacket> tasks = new LinkedList<SyncTasksPacket>();

	private final Object lockstepLock = new Object();

	private int gameTime = 0;
	private int maxAllowedLockstep = 0;

	private boolean isPausing;
	private int pauseTime;
	private float speedFactor = 1.0f;
	private float progress = 0.0f;

	private boolean scheduled = false;

	private ITaskExecutor taskExecutor;

	private NetworkTimer() {
		super();
		this.timer = new Timer("NetworkTimer");
	}

	public synchronized static void destroyNetworkTimer() {
		if (instance != null) {
			instance.cancel();
			instance.timer.cancel();
			instance = null;
		}
	}

	public synchronized static NetworkTimer get() {
		if (instance == null) {
			instance = new NetworkTimer();
		}

		return instance;
	}

	public synchronized void schedule() {
		if (!scheduled) {
			scheduled = true;
			timer.schedule(this, 0, TIME_SLICE);
		}
	}

	@Override
	public void run() {
		if (!isPausing) {
			if (pauseTime <= 0) { // this is used for synchronizing the network clients
				progress += speedFactor;

				while (progress >= 1) {
					executeRun();
					progress--;
				}
			} else {
				pauseTime -= TIME_SLICE;
			}
		}
	}

	private synchronized void executeRun() {
		try {
			gameTime += TIME_SLICE;
			final int lockstep = gameTime / NetworkConstants.Client.LOCKSTEP_PERIOD;

			// check if the lockstep is allowed
			synchronized (lockstepLock) {
				while (lockstep > maxAllowedLockstep) {
					lockstepLock.wait();
				}
			}

			SyncTasksPacket tasksPacket;
			synchronized (tasks) {
				tasksPacket = tasks.peekFirst();
			}

			if (tasksPacket.getLockstepNumber() <= lockstep) {
				System.out.println("Executing task in lockstep: " + lockstep + " at game time: " + gameTime);
				try {
					executeTasksPacket(tasksPacket);
				} catch (Throwable t) {
					System.err.println("Error during execution of scheduled task:");
					t.printStackTrace();
				}
				synchronized (tasks) {
					tasks.pollFirst();
				}
			}

			addNewTimerables();
			handleRemovedTimerables();

			for (ScheduledTimerable curr : timerables) {
				curr.checkExecution(TIME_SLICE);
			}
		} catch (Throwable t) {
			System.err.println("WARNING: Networking Timer catched Throwable!!!");
			t.printStackTrace();
		}
	}

	private void executeTasksPacket(SyncTasksPacket tasksPacket) {
		if (taskExecutor != null) {
			for (TaskPacket currTask : tasksPacket.getTasks()) {
				taskExecutor.executeTask(currTask);
			}
		} else {
			System.err.println("couldn't exeucte task, due to missing taskExecutor!");
		}
	}

	private final void addNewTimerables() {
		synchronized (newTimerables) {
			timerables.addAll(newTimerables);
			newTimerables.clear();
		}
	}

	private final void handleRemovedTimerables() {
		synchronized (timerablesToBeRemoved) {
			for (INetworkTimerable currToBeRemoved : timerablesToBeRemoved) {
				for (Iterator<ScheduledTimerable> iter = timerables.iterator(); iter.hasNext();) {
					if (iter.next().getTimerable() == currToBeRemoved) {
						iter.remove();
						break;
					}
				}
				System.err.println("tried to remove a object from timer that's not registered!");
			}
			timerablesToBeRemoved.clear();
		}
	}

	/**
	 * Schedules the given {@link INetworkTimerable} with given delay. The internal delay of NetworkTimer is {@value #TIME_SLICE}, but you may choose
	 * smaller delays for the {@link INetworkTimerable}. The NetworkTimer will then call the {@link INetworkTimerable} multiple times on each internal
	 * tick in the exact rate to ensure the given delay in the long run.
	 * 
	 * @param timerable
	 *            {@link INetworkTimerable} to be scheduled.
	 * @param delay
	 *            delay of the given {@link INetworkTimerable}.
	 */
	public static void schedule(INetworkTimerable timerable, short delay) {
		if (instance != null) {
			synchronized (instance.newTimerables) {
				instance.newTimerables.add(new ScheduledTimerable(timerable, delay));
			}
		}
	}

	/**
	 * removes an INetworkTimerable from the list of scheduled tasks.
	 * 
	 * @param timerable
	 */
	public static void remove(INetworkTimerable timerable) {
		if (instance != null) {
			synchronized (instance.timerablesToBeRemoved) {
				instance.timerablesToBeRemoved.add(timerable);
			}
		}
	}

	public int getGameTime() {
		return gameTime;
	}

	public void setGameTime(int newGameTime) {
		gameTime = newGameTime;
	}

	/**
	 * Goes 60 * 1000 milliseconds forward as fast as possible
	 */
	public synchronized void fastForward() {
		this.setPausing(true);

		final int runs = 60 * 1000 / TIME_SLICE;
		for (int i = 0; i < runs; i++) {
			executeRun();
		}

		this.setPausing(false);
	}

	// methods for pausing

	public void setPausing(boolean b) {
		this.isPausing = b;
	}

	public void invertPausing() {
		this.isPausing = !this.isPausing;
	}

	public static boolean isPausing() {
		return get().isPausing;
	}

	/**
	 * pauses this game for at least the given period of milliseconds
	 * 
	 * @param pauseTime
	 *            milliseconds to pause the game
	 */
	public void pauseAtLeastFor(int pauseTime) {
		this.pauseTime = pauseTime;
		System.err.println("pausing for " + this.pauseTime + " ms");
	}

	public static void setGameSpeed(float speedFactor) {
		instance.speedFactor = speedFactor;
	}

	public static void multiplyGameSpeed(float factor) {
		instance.speedFactor *= factor;
	}

	public void setTaskExecutor(ITaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	@Override
	public void scheduleTasksAndUnlockStep(SyncTasksPacket tasksPacket) {
		assert maxAllowedLockstep + 1 == tasksPacket.getLockstepNumber() : "received unlock for wrong step! current max allowed: "
				+ maxAllowedLockstep + " new: " + tasksPacket.getLockstepNumber();

		if (!tasksPacket.getTasks().isEmpty()) {
			synchronized (tasks) {
				tasks.addLast(tasksPacket);
			}
		}
		maxAllowedLockstep = tasksPacket.getLockstepNumber();

		synchronized (lockstepLock) {
			lockstepLock.notifyAll();
		}
	}
}
