package jsettlers.ai.highlevel;

import jsettlers.logic.map.grid.MainGrid;
import jsettlers.network.client.interfaces.ITaskScheduler;

import java.util.ArrayList;
import java.util.List;


public class AiThread extends Thread {

	private final List<IWhatToDoAi> whatToDoAis;
	private boolean shutdownRequested;
	
	public AiThread(List<Byte> aiPlayers, MainGrid mainGrid, ITaskScheduler taskScheduler) {
		shutdownRequested = false;
		this.whatToDoAis = new ArrayList<IWhatToDoAi>();
		for (byte playerId : aiPlayers) {
			whatToDoAis.add(new WhatToDoAi(playerId, mainGrid, taskScheduler));
		}
	}
	
	@Override
	public void run() {
		System.out.println("AI Thread started");
		while (!shutdownRequested) {
			try {
				for (IWhatToDoAi whatToDoAi: whatToDoAis) {
					whatToDoAi.applyRules();
				}
				AiThread.sleep(3000l);
			} catch (InterruptedException e) {
				//continiue and then return run
			}
		}
		System.out.println("AI Thread finished");
	}
	
	public void shutdown() {
		shutdownRequested = true;
	}

}
