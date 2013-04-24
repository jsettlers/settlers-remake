package networklib;

import java.net.ServerSocket;

import networklib.channel.Channel;

public class TestUtils {

	private Channel c1;
	private Channel c2;

	public void setUpLoopbackChannels() throws InterruptedException {
		Thread t1 = new Thread() {
			@Override
			public void run() {
				try {
					ServerSocket serverSocket = new ServerSocket(10000);
					c1 = new Channel(serverSocket.accept());
					serverSocket.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		t1.start();

		Thread.sleep(100);

		Thread t2 = new Thread() {
			@Override
			public void run() {
				try {
					c2 = new Channel("localhost", 10000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		t2.start();

		t1.join();
		t2.join();

		c1.start();
		c2.start();
		c1.initPinging();
	}

	public Channel getChannel1() {
		return c1;
	}

	public Channel getChannel2() {
		return c2;
	}
}
