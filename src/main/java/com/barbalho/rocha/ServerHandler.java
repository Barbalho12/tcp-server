package com.barbalho.rocha;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.apache.mina.api.IdleStatus;
import org.apache.mina.api.IoHandler;
import org.apache.mina.api.IoService;
import org.apache.mina.api.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.barbalho.rocha.TCPClient.Message;

public class ServerHandler implements IoHandler {

	private static final Logger LOG = LoggerFactory.getLogger(ServerHandler.class);

	@Override
	public void sessionOpened(IoSession session) {
		LOG.info("server session opened {" + session + "}");
	}

	@Override
	public void sessionClosed(IoSession session) {
		LOG.info("IP:" + session.getRemoteAddress().toString() + " close");
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) {

	}

	public static final int INIT = 0;
	public static final int BYTES = 1;
	public static final int FRAME = 2;
	public static final int START_DATA = 3;

	public static final byte INIT_VALUE = 0x0A;
	public static final byte END_VALUE = 0x0D;

	public static final byte TEXT_FRAME = (byte) 0xA1;
	public static final byte USER_FRAME = (byte) 0xA2;
	public static final byte TIME_FRAME = (byte) 0xA3;
	public static final byte ACK_FRAME = (byte) 0xA0;

	@Override
	public void messageReceived(IoSession session, Object message) {
		if (message instanceof ByteBuffer) {
			try {

				ByteBuffer b = (ByteBuffer) message;

				byte init = b.get();
				int bytes = b.get();
				byte frame = b.get();

				byte[] messageBytes = new byte[bytes - 5];
				b.get(messageBytes);

				byte crc = b.get();
				byte end = b.get();

				System.out.println("INIT: " + String.format("0x%02X", init));
				System.out.println("BYTES: " + String.format("0x%02X", bytes) + " = " + ((int) bytes));
				System.out.println("FRAME: " + String.format("0x%02X", frame));
				System.out.println("DATA: " + new String(messageBytes, StandardCharsets.UTF_8));
				System.out.println("CRC: " + String.format("0x%02X", crc));
				System.out.println("END: " + String.format("0x%02X", end));

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void messageSent(IoSession session, Object message) {
		LOG.info("send message:" + message.toString());
		System.out.println("server send message:" + message.toString());
	}

	@Override
	public void serviceActivated(IoService service) {

	}

	@Override
	public void serviceInactivated(IoService service) {

	}

	@Override
	public void exceptionCaught(IoSession session, Exception cause) {

	}

}
