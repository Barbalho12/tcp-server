package com.barbalho.rocha;

import java.util.Arrays;

import com.barbalho.rocha.exceptions.ProtocolException;
import com.barbalho.rocha.utils.CRC8;
import com.barbalho.rocha.utils.DateUtils;

/**
 * Business rules of protocol definition
 * 
 * @author Felipe Barbalho
 *
 */
public class Protocol {

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

	public static final byte[] ACK = { INIT_VALUE, 0x05, ACK_FRAME, 0x28, END_VALUE };

	/**
	 * Create text frame
	 * 
	 * @param textMessage Frame content
	 * @param frame       frame type
	 * @return Full frame to be sent
	 */
	public static byte[] createMessage(final byte[] textMessage, final byte frame) {
		final byte[] byteMessage = new byte[textMessage.length + 5];
		byteMessage[Protocol.INIT] = Protocol.INIT_VALUE;
		byteMessage[Protocol.BYTES] = (byte) byteMessage.length;
		byteMessage[Protocol.FRAME] = frame;

		int index = Protocol.START_DATA;
		for (int i = 0; i < textMessage.length; i++) {
			byteMessage[index++] = (byte) textMessage[i];
		}

		final byte[] subMessage = Arrays.copyOfRange(byteMessage, 1, index);

		byteMessage[index++] = CRC8.calc(subMessage, subMessage.length);

		byteMessage[index++] = Protocol.END_VALUE;
		return byteMessage;
	}

	/**
	 * Creates content of the complete current time response frame from the past
	 * time zone
	 * 
	 * @param fuse standard time zone
	 * @return Full frame with datetime
	 */
	public static byte[] getDateTimeFrame(final String fuse) {
		final byte[] dateTime = DateUtils.getDateTimeByFuse(fuse);
		return createMessage(dateTime, Protocol.TIME_FRAME);
	}

	public static void validateCRC(byte bytes, byte frame, byte[] data, byte crc) throws ProtocolException {
		byte[] array = new byte[data.length + 2];
		array[0] = bytes;
		array[1] = frame;
		int index = 2;
		for (int i = 0; i < data.length; i++) {
			array[index++] = data[i];
		}
		if (CRC8.calc(array, array.length) != crc) {
			throw new ProtocolException("CRC inválido");
		}
	}

	public static void validateINIT(byte init) throws ProtocolException {
		if (init != Protocol.INIT_VALUE) {
			throw new ProtocolException("INIT inválido");
		}
	}

	public static void validateEND(byte end) throws ProtocolException {
		if (end != Protocol.END_VALUE) {
			throw new ProtocolException("END inválido");
		}
	}

}