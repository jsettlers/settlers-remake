package jsettlers.ai.highlevel;

import jsettlers.logic.map.grid.MainGrid;
import jsettlers.network.client.interfaces.ITaskScheduler;

import java.util.ArrayList;
import java.util.List;


public class AiThread implements Runnable {

	private final List<IWhatToDoAi> whatToDoAis;
	AiStatistics aiStatistics;
	private boolean shutdownRequested;
	
	public AiThread(List<Byte> aiPlayers, MainGrid mainGrid, ITaskScheduler taskScheduler) {
		shutdownRequested = false;
		aiStatistics = new AiStatistics();
		this.whatToDoAis = new ArrayList<IWhatToDoAi>();
		for (byte playerId : aiPlayers) {
			whatToDoAis.add(new WhatToDoAi(playerId, aiStatistics, mainGrid, taskScheduler));
		}
	}
	
	@Override
	public void run() {
		System.out.println("AI Thread started");
		while (!shutdownRequested) {
			try {
				aiStatistics.updateStatistics();
				for (IWhatToDoAi whatToDoAi: whatToDoAis) {
					whatToDoAi.applyRules();
				}
				Thread.sleep(3000l);
			} catch (InterruptedException e) {
				//continiue and then return run
			}
		}
		System.out.println("AI Thread finished");
	}
	
	public void start() {
		(new Thread(this)).start();
	}
	
	public void shutdown() {
		shutdownRequested = true;
	}

}
