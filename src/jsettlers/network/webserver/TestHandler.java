package jsettlers.network.webserver;


public final class TestHandler implements IHttpGetHandler {

	public void handleGetRequest(HttpResponse response, String[] parameters) {
		response.setResponseText("Hello world by handler!");
	}

	public String getRessourceName() {
		return "/test/";
	}

}
