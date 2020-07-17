package com.barbalho.rocha;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.annotation.XmlElement.DEFAULT;

import org.apache.mina.api.IdleStatus;
import org.apache.mina.api.IoHandler;
import org.apache.mina.api.IoService;
import org.apache.mina.api.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	public void saveData(byte frame, byte[] data){

		switch(frame){
			case Protocol.TEXT_FRAME:
				System.out.println("DATA: " + new String(data, StandardCharsets.ISO_8859_1));
				break;
			case Protocol.USER_FRAME:
				User user = new User(data);
				System.out.println("DATA: " + user.toString());
				break;
			case Protocol.TIME_FRAME:
				System.out.println("DATA: TIME");
				break;
			default:
				System.err.println("FRAME INVÁLIDO");
		}	

	}

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
				
				
				// System.out.println("DATA: " + new String(messageBytes, StandardCharsets.ISO_8859_1));
				saveData(frame, messageBytes);
				
				System.out.println("CRC: " + String.format("0x%02X", crc));
				System.out.println("END: " + String.format("0x%02X", end));

				ByteBuffer encode = ByteBuffer.wrap(Protocol.ACK);

				session.write(encode);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void messageSent(IoSession session, Object message) {
		LOG.info("send message:" + message.toString());
		// System.out.println("server send message:" + message.toString());
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
