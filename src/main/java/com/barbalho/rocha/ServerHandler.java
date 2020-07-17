package com.barbalho.rocha;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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

	public byte [] getDateTimeByFuse(String fuse){

		byte [] bytes = new byte[6];

		Date date = new Date();
		LocalDateTime localDate = date.toInstant().atZone(ZoneId.of(fuse)).toLocalDateTime();
		
		bytes[0]  = (byte) localDate.getYear();
		bytes[1]  = (byte) localDate.getMonthValue();
		bytes[2]  = (byte) localDate.getDayOfMonth();
		bytes[3]  = (byte) localDate.getHour();
		bytes[4]  = (byte) localDate.getMinute();
		bytes[5]  = (byte) localDate.getSecond();

		// int hour   = localDate.get

		// Calendar currentDate = Calendar.getInstance(TimeZone.getTimeZone(fuse));
		// int year = currentDate.get(Calendar.YEAR);
		// int month = currentDate.get(Calendar.MONTH);
		// int day = currentDate.get(Calendar.DAY_OF_MONTH);

		// int month = currentDate.get(Calendar.MONTH);
		// int day = currentDate.get(Calendar.DAY_OF_MONTH);

		return bytes;
	}

	public static byte[] createMessage(byte[] textMessage, byte frame) {

		byte[] byteMessage = new byte[textMessage.length + 5];

		byteMessage[Protocol.INIT] = Protocol.INIT_VALUE;
		byteMessage[Protocol.BYTES] = (byte) byteMessage.length;
		byteMessage[Protocol.FRAME] = frame;

		int index = Protocol.START_DATA;

		for (int i = 0; i < textMessage.length; i++) {
			byteMessage[index++] = (byte) textMessage[i];
		}

		byte[] subMessage = Arrays.copyOfRange(byteMessage, 3, index);

		byteMessage[index++] = CRC8.calc(subMessage, subMessage.length);
		byteMessage[index++] = Protocol.END_VALUE;

		return byteMessage;
	}

	public byte [] getDateTimeFrame(String fuse){
		byte [] dateTime = getDateTimeByFuse(fuse);
		byte [] bytes = createMessage(dateTime, Protocol.TIME_FRAME);
		return bytes;
	}

	public byte [] saveData(byte frame, byte[] data){

		switch(frame){
			case Protocol.TEXT_FRAME:
				System.out.println("DATA: " + new String(data, StandardCharsets.ISO_8859_1));
				return Protocol.ACK;
			case Protocol.USER_FRAME:
				User user = new User(data);
				System.out.println("DATA: " + user.toString());
				return Protocol.ACK;
			case Protocol.TIME_FRAME:
				String fuse = new String(data, StandardCharsets.ISO_8859_1);
				System.out.println("DATA: " + fuse);
				return getDateTimeFrame(fuse);
			default:
				System.err.println("FRAME INVÁLIDO");
				return null;
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
				byte [] response = saveData(frame, messageBytes);
				
				System.out.println("CRC: " + String.format("0x%02X", crc));
				System.out.println("END: " + String.format("0x%02X", end));

				if(response != null){
					ByteBuffer encode = ByteBuffer.wrap(response);
					session.write(encode);
				}
				

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
