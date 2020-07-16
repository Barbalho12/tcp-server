package com.barbalho.rocha;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.nio.NioTcpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
	
	private static final Logger LOG = LoggerFactory.getLogger(Server.class);

	public static void run() {
		LOG.info("start server...");
		final NioTcpServer acceptor = new NioTcpServer();
		acceptor.setFilters(new LoggingFilter("LoggingFilter1"));
		acceptor.setIoHandler(new ServerHandler());

		try {

			final SocketAddress address = new InetSocketAddress(9999);
			acceptor.bind(address);
			new BufferedReader(new InputStreamReader(System.in)).readLine();
			acceptor.unbind();
		} catch (final IOException e) {
			LOG.error("Interrupted exception", e);
		}
	}

	public static void main(String[] args) {
		run();
		/*
		User user1 = new User(25, 61, 174, 6, "Felipe");
		User user2 = new User(23, 80, 178, 4, "Luis");
		Transaction transaction = null;
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			transaction = session.beginTransaction();
			session.save(user1);
			session.save(user2);
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		}
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			List<User> users = session.createQuery("from User", User.class).list();
			users.forEach(s -> System.out.println(s.getName()));
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		}
		*/
	}
}
