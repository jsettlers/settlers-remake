/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.network.synchronic.timer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jsettlers.network.NetworkConstants;
import jsettlers.network.client.INetworkClientClock;
import jsettlers.network.client.task.packets.SyncTasksPacket;
import jsettlers.network.client.task.packets.TaskPacket;

/**
 * This is a basic game timer. All synchronous actions must be based on this clock. The {@link NetworkTimer} also triggers the execution of
 * synchronous tasks in the network game.
 *
 * @author Andreas Eberle
 *
 */
public final class NetworkTimer extends TimerTask implements INetworkClientClock {
	public static final short TIME_SLICE = 50;
	private Comparator<SyncTasksPacket> tasksByTimeComperator = new Comparator<SyncTasksPacket>() {
		@Override
		public int compare(SyncTasksPacket o1, SyncTasksPacket o2) {
			return o1.getLockstepNumber() - o2.getLockstepNumber();
		}
	};

	private final Timer timer;
	private final Object lockstepLock = new Object();

	private final List<ScheduledTimerable> timerables = new ArrayList<ScheduledTimerable>();
	private final List<ScheduledTimerable> newTimerables = new LinkedList<ScheduledTimerable>();
	private final List<INetworkTimerable> timerablesToBeRemoved = new LinkedList<INetworkTimerable>();

	private final LinkedList<SyncTasksPacket> tasks = new LinkedList<SyncTasksPacket>();

	private int time = 0;
	private int maxAllowedLockstep = -1;

	private boolean isPausing;
	private int pauseTime;
	private float speedFactor = 1.0f;
	private float progress = 0.0f;

	private boolean scheduled = false;

	private ITaskExecutor taskExecutor;
	private DataOutputStream replayLogStream;

	public NetworkTimer() {
		super();
		this.timer = new Timer("NetworkTimer");
	}

	public NetworkTimer(boolean disableLockstepWaiting) {
		this();

		if (disableLockstepWaiting) {
			maxAllowedLockstep = Integer.MAX_VALUE;
		}
	}

	@Override
	public synchronized void startExecution() {
		if (!scheduled) {
			scheduled = true;
			timer.schedule(this, 0, TIME_SLICE);
		}
	}

	@Override
	public void stopExecution() {
		setPausing(true);
		timer.cancel();

		closeReplayLogStreamIfNeeded();
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
			time += TIME_SLICE;
			final int lockstep = time / NetworkConstants.Client.LOCKSTEP_PERIOD;

			// check if the lockstep is allowed
			synchronized (lockstepLock) {
				while (lockstep > maxAllowedLockstep) {
					System.out.println("WAITING for lockstep!");
					lockstepLock.wait();
				}
			}

			SyncTasksPacket tasksPacket;
			synchronized (tasks) {
				tasksPacket = tasks.peekFirst();
			}

			while (tasksPacket != null && tasksPacket.getLockstepNumber() <= lockstep) {
				assert tasksPacket.getLockstepNumber() == lockstep : "FOUND TasksPacket FOR older lockstep!";

				System.out.println("Executing SyncTaskPacket(" + tasksPacket + ") in " + getLockstepText(lockstep));

				try {
					executeTasksPacket(tasksPacket);
				} catch (Throwable t) {
					System.err.println("Error during execution of scheduled task:");
					t.printStackTrace();
				}

				synchronized (tasks) {// remove the executed tasksPacket and retrieve the next one to check it.
					tasks.pollFirst();
					tasksPacket = tasks.peekFirst();
				}
			}

			addNewTimerables();
			handleRemovedTimerables();

			for (ScheduledTimerable curr : timerables) {
				curr.checkExecution(TIME_SLICE);
			}
		} catch (InterruptedException e) {

		} catch (Throwable t) {
			System.err.println("WARNING: Networking Timer catched Throwable!!!");
			t.printStackTrace();
			throw t;
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

	private void addNewTimerables() {
		synchronized (newTimerables) {
			timerables.addAll(newTimerables);
			newTimerables.clear();
		}
	}

	private void handleRemovedTimerables() {
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
	@Override
	public void schedule(INetworkTimerable timerable, short delay) {
		synchronized (newTimerables) {
			newTimerables.add(new ScheduledTimerable(timerable, delay));
		}
	}

	/**
	 * removes an INetworkTimerable from the list of scheduled tasks.
	 *
	 * @param timerable
	 */
	@Override
	public void remove(INetworkTimerable timerable) {
		synchronized (timerablesToBeRemoved) {
			timerablesToBeRemoved.add(timerable);
		}
	}

	/**
	 * Goes 60 * 1000 milliseconds forward as fast as possible
	 */
	@Override
	public synchronized void fastForward() {
		this.setPausing(true);

		final int runs = 60 * 1000 / TIME_SLICE;
		for (int i = 0; i < runs; i++) {
			executeRun();
		}

		this.setPausing(false);
	}

	@Override
	public synchronized void fastForwardTo(int targetGameTime) {
		this.setPausing(true);

		System.out.println("Playing game forward to game time: " + targetGameTime);

		while (time < targetGameTime) {
			executeRun();
		}
	}

	// methods for pausing

	@Override
	public void setPausing(boolean b) {
		this.isPausing = b;
	}

	@Override
	public void invertPausing() {
		this.isPausing = !this.isPausing;
	}

	@Override
	public boolean isPausing() {
		return isPausing;
	}

	@Override
	public void pauseClockFor(int timeDelta) {
		this.pauseTime = timeDelta;
		System.err.println("pausing for " + timeDelta + " ms");
	}

	@Override
	public void setGameSpeed(float speedFactor) {
		this.speedFactor = speedFactor;
	}

	@Override
	public void multiplyGameSpeed(float factor) {
		this.speedFactor *= factor;
	}

	@Override
	public void setTaskExecutor(ITaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	@Override
	public void scheduleSyncTasksPacket(SyncTasksPacket tasksPacket) {
		assert maxAllowedLockstep == Integer.MAX_VALUE || maxAllowedLockstep + 1 == tasksPacket.getLockstepNumber() : "received unlock for wrong step! current max allowed: "
				+ maxAllowedLockstep + " new: " + tasksPacket.getLockstepNumber();

		if (!tasksPacket.getTasks().isEmpty()) {
			synchronized (tasks) {
				System.out.println("Scheduled SyncTasksPacket(" + tasksPacket + " for " + getLockstepText(tasksPacket.getLockstepNumber()));
				tasks.addLast(tasksPacket);
				Collections.sort(tasks, tasksByTimeComperator);
				saveReplayIfNeeded(tasksPacket);
			}
		}
		maxAllowedLockstep = Math.max(maxAllowedLockstep, tasksPacket.getLockstepNumber());

		synchronized (lockstepLock) {
			lockstepLock.notifyAll();
		}
	}

	private void saveReplayIfNeeded(SyncTasksPacket tasksPacket) {
		if (replayLogStream != null) {
			try {
				tasksPacket.serialize(replayLogStream);
				replayLogStream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setTime(int newTime) {
		this.time = newTime;
	}

	@Override
	public int getTime() {
		return time;
	}

	@Override
	public void setReplayLogStream(DataOutputStream replayFileStream) {
		if (this.replayLogStream != null) {
			throw new IllegalStateException("Replay log stream cannot be set twice!");
		}

		if (replayFileStream != null) {
			replayLogStream = replayFileStream;
		} else {
			closeReplayLogStreamIfNeeded();
		}
	}

	@Override
	public synchronized void saveRemainingTasks(DataOutputStream dos) throws IOException {
		for (SyncTasksPacket task : tasks) {
			task.serialize(dos);
		}
		dos.flush();
	}

	private void closeReplayLogStreamIfNeeded() {
		if (replayLogStream != null) {
			try {
				replayLogStream.flush();
				replayLogStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				replayLogStream = null;
			}
		}
	}

	@Override
	public void loadReplayLogFromStream(DataInputStream dataInputStream) {
		try {
			while (true) {
				SyncTasksPacket currPacket = new SyncTasksPacket();
				currPacket.deserialize(dataInputStream);
				scheduleSyncTasksPacket(currPacket);
			}
		} catch (IOException e1) { // something went wrong, or the stream was empty
			try {
				if (dataInputStream.read() == -1) {
					System.out.println("Successfully loaded replay file.");
				} else {
					System.out.println("Error loading replay file.");
					e1.printStackTrace();
				}
			} catch (IOException e2) {
				System.out.println("Error loading replay file.");
				e1.printStackTrace();
				e2.printStackTrace();
			}
		}
	}

	private String getLockstepText(int lockstep) {
		int time = lockstep * NetworkConstants.Client.LOCKSTEP_PERIOD;
		int hours = time / (1000 * 60 * 60);
		int minutes = (time / (1000 * 60)) % 60;
		int seconds = (time / 1000) % 60;
		int millis = time % 1000;
		return String.format("lockstep: %d (game time: %dms / %02d:%02d:%02d:%03d)", lockstep, time, hours, minutes, seconds, millis);
	}

}
