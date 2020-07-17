package com.barbalho.rocha;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.mina.api.IdleStatus;
import org.apache.mina.api.IoHandler;
import org.apache.mina.api.IoService;
import org.apache.mina.api.IoSession;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerHandler implements IoHandler {

	private static final Logger LOG = LoggerFactory.getLogger(ServerHandler.class);

	@Override
	public void sessionOpened(final IoSession session) {
		LOG.info("server session opened {" + session + "}");
	}

	@Override
	public void sessionClosed(final IoSession session) {
		LOG.info("IP:" + session.getRemoteAddress().toString() + " close");
	}

	@Override
	public void sessionIdle(final IoSession session, final IdleStatus status) {

	}

	public byte [] getDateTimeByFuse(final String fuse){
		final byte [] bytes = new byte[6];
		final Date date = new Date();
		final LocalDateTime localDate = date.toInstant().atZone(ZoneId.of(fuse)).toLocalDateTime();
		bytes[0]  = (byte) localDate.getYear();
		bytes[1]  = (byte) localDate.getMonthValue();
		bytes[2]  = (byte) localDate.getDayOfMonth();
		bytes[3]  = (byte) localDate.getHour();
		bytes[4]  = (byte) localDate.getMinute();
		bytes[5]  = (byte) localDate.getSecond();
		return bytes;
	}

	public static byte[] createMessage(final byte[] textMessage, final byte frame) {
		final byte[] byteMessage = new byte[textMessage.length + 5];
		byteMessage[Protocol.INIT] = Protocol.INIT_VALUE;
		byteMessage[Protocol.BYTES] = (byte) byteMessage.length;
		byteMessage[Protocol.FRAME] = frame;

		int index = Protocol.START_DATA;
		for (int i = 0; i < textMessage.length; i++) {
			byteMessage[index++] = (byte) textMessage[i];
		}

		final byte[] subMessage = Arrays.copyOfRange(byteMessage, 3, index);

		byteMessage[index++] = CRC8.calc(subMessage, subMessage.length);
		byteMessage[index++] = Protocol.END_VALUE;
		return byteMessage;
	}

	public byte [] getDateTimeFrame(final String fuse){
		final byte [] dateTime = getDateTimeByFuse(fuse);
		final byte [] bytes = createMessage(dateTime, Protocol.TIME_FRAME);
		return bytes;
	}

	public void saveUser(final User object){
		Transaction transaction = null;
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			transaction = session.beginTransaction();
			session.save(object);
			transaction.commit();
		} catch (final Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		}
	}

	public void saveTextMessage(final TextMessage object){
		Transaction transaction = null;
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			transaction = session.beginTransaction();
			session.save(object);
			transaction.commit();
		} catch (final Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		}
	}

	public byte [] saveData(final byte frame, final byte[] data){
		switch(frame){
			case Protocol.TEXT_FRAME:
				String text = new String(data);
				final TextMessage textMessage = new TextMessage(text);
				System.out.println("DATA: " + textMessage.toString());
				saveTextMessage(textMessage);
				return Protocol.ACK;
			case Protocol.USER_FRAME:
				User user = new User(data);
				System.out.println("DATA: " + user.toString());
				saveUser(user);
				return Protocol.ACK;
			case Protocol.TIME_FRAME:
				final String fuse = new String(data);
				System.out.println("DATA: " + fuse);
				return getDateTimeFrame(fuse);
			default:
				System.err.println("FRAME INVÁLIDO");
				return null;
		}	
	}

	public void showdatabase(){
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			List<User> users = session.createQuery("from User", User.class).list();
			users.forEach(s -> System.out.println(s.toString()));

			List<TextMessage> textMessages = session.createQuery("from TextMessage", TextMessage.class).list();
			textMessages.forEach(s -> System.out.println(s));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void messageReceived(final IoSession session, final Object message) {
		if (message instanceof ByteBuffer) {
			try {
				final ByteBuffer b = (ByteBuffer) message;

				final byte init = b.get();
				final int bytes = b.get();
				final byte frame = b.get();

				final byte[] messageBytes = new byte[bytes - 5];
				b.get(messageBytes);

				final byte crc = b.get();
				final byte end = b.get();

				System.out.println("INIT: " + String.format("0x%02X", init));
				System.out.println("BYTES: " + String.format("0x%02X", bytes) + " = " + ((int) bytes));
				System.out.println("FRAME: " + String.format("0x%02X", frame));
				
				final byte [] response = saveData(frame, messageBytes);
				
				System.out.println("CRC: " + String.format("0x%02X", crc));
				System.out.println("END: " + String.format("0x%02X", end));

				if(response != null){
					final ByteBuffer encode = ByteBuffer.wrap(response);
					session.write(encode);
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		System.out.println("-----DB-----");
		showdatabase();
		System.out.println("------------");
	}

	@Override
	public void messageSent(final IoSession session, final Object message) {
		LOG.info("send message:" + message.toString());
	}

	@Override
	public void serviceActivated(final IoService service) {

	}

	@Override
	public void serviceInactivated(final IoService service) {

	}

	@Override
	public void exceptionCaught(final IoSession session, final Exception cause) {

	}

}
