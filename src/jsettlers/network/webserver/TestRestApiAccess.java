package jsettlers.network.webserver;

import jsettlers.network.client.rest.IRestRequester;
import jsettlers.network.client.rest.ServerRestApiInterface;
import jsettlers.network.server.restapi.MatchesInfoList;

public class TestRestApiAccess {
	public static void main(String args[]) throws InterruptedException {
		ServerRestApiInterface.request("localhost", new IRestRequester<MatchesInfoList>() {
			@Override
			public void receive(MatchesInfoList object) {
				System.out.println("received object:\n" + object);
			}
		}, "/matches/");

		Thread.sleep(5000);
	}
}
