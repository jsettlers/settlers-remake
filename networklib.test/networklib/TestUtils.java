package networklib;

import java.net.ServerSocket;

import networklib.channel.AsyncChannel;
import networklib.channel.Channel;

public class TestUtils {

	public Channel[] setUpLoopbackChannels() throws InterruptedException {
		final Channel[] channels = new Channel[2];

		Thread t1 = new Thread() {
			@Override
			public void run() {
				try {
					ServerSocket serverSocket = new ServerSocket(10000);
					channels[0] = new Channel(serverSocket.accept());
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
					channels[1] = new Channel("localhost", 10000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		t2.start();

		t1.join();
		t2.join();

		channels[0].start();
		channels[1].start();
		channels[0].initPinging();

		return channels;
	}

	public AsyncChannel[] setUpAsyncLoopbackChannels() throws InterruptedException {
		final AsyncChannel[] channels = new AsyncChannel[2];

		Thread t1 = new Thread() {
			@Override
			public void run() {
				try {
					ServerSocket serverSocket = new ServerSocket(10000);
					channels[0] = new AsyncChannel(serverSocket.accept());
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
					channels[1] = new AsyncChannel("localhost", 10000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		t2.start();

		t1.join();
		t2.join();

		channels[0].start();
		channels[1].start();
		channels[0].initPinging();

		return channels;
	}

}
