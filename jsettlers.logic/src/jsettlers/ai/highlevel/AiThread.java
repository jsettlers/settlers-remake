package jsettlers.ai.highlevel;

import jsettlers.logic.map.grid.MainGrid;
import java.util.ArrayList;
import java.util.List;


public class AiThread extends Thread {

	private List<IWhatToDoAi> whatToDoAis;
	private boolean shutdownRequested;
	
	public AiThread(List<Byte> aiPlayers, MainGrid mainGrid) {
		shutdownRequested = false;
		this.whatToDoAis = new ArrayList<IWhatToDoAi>();
		for (byte playerId : aiPlayers) {
			whatToDoAis.add(new WhatToDoAi(playerId, mainGrid));
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
				AiThread.sleep(1000l);
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
