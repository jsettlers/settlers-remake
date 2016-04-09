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
package jsettlers.network.infrastructure.channel.socket.delayed;

import java.io.IOException;
import java.net.Socket;

import jsettlers.network.infrastructure.channel.socket.ISocket;
import jsettlers.network.infrastructure.channel.socket.ISocketFactory;
import jsettlers.network.infrastructure.channel.socket.SocketConnectException;

/**
 * Factory class to create {@link DelayedSocket}s.
 * 
 * @author Andreas Eberle
 * 
 */
public class DelayedSocketFactory implements ISocketFactory {

	@Override
	public ISocket generateSocket(String host, int port) throws SocketConnectException {
		try {
			return new DelayedSocket(new Socket(host, port));
		} catch (IOException e) {
			e.printStackTrace();
			throw new SocketConnectException("Error during socket connection");
		}
	}

	@Override
	public ISocket generateSocket(Socket socket) throws SocketConnectException {
		try {
			return new DelayedSocket(socket);
		} catch (IOException e) {
			e.printStackTrace();
			throw new SocketConnectException("Error during socket connection");
		}
	}

}
