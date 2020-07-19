package com.barbalho.rocha;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import org.apache.mina.transport.nio.NioTcpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main Class for reading arguments of the execution and initialization of the
 * TCP server
 * 
 * @author Felipe Barbalho
 *
 */
public class Server {

	private static final Logger LOG = LoggerFactory.getLogger(Server.class);

	public static int PORT_SERVER = 9999;

	public static void run() {
		LOG.info("start server...");
		final NioTcpServer acceptor = new NioTcpServer();
		acceptor.setIoHandler(new ServerHandler());
		try {
			final SocketAddress address = new InetSocketAddress(PORT_SERVER);
			acceptor.bind(address);
			new BufferedReader(new InputStreamReader(System.in)).readLine();
			acceptor.unbind();
		} catch (final IOException e) {
			LOG.error("Interrupted exception", e);
		}
	}

	public static void main(String[] args) {
		if (args.length > 0) {
			PORT_SERVER = Integer.valueOf(args[0]);
		}
		run();
	}
}
