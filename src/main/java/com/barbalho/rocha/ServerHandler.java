package com.barbalho.rocha;

import static com.barbalho.rocha.Protocol.getDateTimeFrame;

import java.nio.ByteBuffer;
import java.util.List;

import com.barbalho.rocha.dao.TextMessageDao;
import com.barbalho.rocha.dao.UserDao;
import com.barbalho.rocha.exceptions.DaoException;
import com.barbalho.rocha.exceptions.ProtocolException;
import com.barbalho.rocha.models.TextMessage;
import com.barbalho.rocha.models.User;

import org.apache.mina.api.IdleStatus;
import org.apache.mina.api.IoHandler;
import org.apache.mina.api.IoService;
import org.apache.mina.api.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerHandler implements IoHandler {

	private static final Logger LOG = LoggerFactory.getLogger(ServerHandler.class);

	@Override
	public void sessionOpened(final IoSession session) {
	}

	@Override
	public void sessionClosed(final IoSession session) {
	}

	@Override
	public void sessionIdle(final IoSession session, final IdleStatus status) {

	}

	public byte[] processDataFrame(final byte frame, final byte[] data) throws ProtocolException, DaoException {
		switch (frame) {

			case Protocol.TEXT_FRAME:
				final String text = new String(data);
				final TextMessage textMessage = new TextMessage(text);
				LOG.info("DATA: " + textMessage.toString());
				TextMessageDao.save(textMessage);
				return Protocol.ACK;

			case Protocol.USER_FRAME:
				final User user = new User(data);
				LOG.info("DATA: " + user.toString());
				UserDao.save(user);
				return Protocol.ACK;

			case Protocol.TIME_FRAME:
				final String fuse = new String(data);
				LOG.info("DATA: " + fuse);
				return getDateTimeFrame(fuse);

			default:
				LOG.warn("FRAME INVÁLIDO");
				throw new ProtocolException("Frame inválido");
		}
	}

	public void showdatabase() {
		try {
			final List<User> users = UserDao.findAll();
			users.forEach(s -> System.out.println(s.toString()));

			final List<TextMessage> textMessages = TextMessageDao.findAll();
			textMessages.forEach(s -> System.out.println(s));

		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	

	@Override
	public void messageReceived(final IoSession session, final Object message) {
		if (message instanceof ByteBuffer) {
			try {
				final ByteBuffer b = (ByteBuffer) message;

				Protocol.validateINIT(b.get());

				final byte bytes = b.get();
				final byte frame = b.get();

				final byte[] messageBytes = new byte[((int) bytes) - 5];
				b.get(messageBytes);

				byte crc = b.get();

				Protocol.validateCRC(bytes, frame, messageBytes, crc);
				Protocol.validateEND(b.get());

				final byte [] response = processDataFrame(frame, messageBytes);
				
				final ByteBuffer encode = ByteBuffer.wrap(response);

				session.write(encode);
				
			} catch (final Exception exception) {
				LOG.error("Erro ao tentar processar mensagem do cliente", exception);
			}
		}
		// showdatabase();
	}

	@Override
	public void messageSent(final IoSession session, final Object message) {
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
