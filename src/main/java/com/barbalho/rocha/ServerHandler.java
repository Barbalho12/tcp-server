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
import com.barbalho.rocha.models.FrameMessage;
import com.barbalho.rocha.utils.LogFile;

import org.apache.mina.api.IdleStatus;
import org.apache.mina.api.IoHandler;
import org.apache.mina.api.IoService;
import org.apache.mina.api.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Agent responsible for answering messages received by the TCP server
 * 
 * @author Felipe Barbalho
 *
 */
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

	public byte[] processDataFrame(FrameMessage frameMessage) throws ProtocolException, DaoException {
		switch (frameMessage.frame) {

		case Protocol.TEXT_FRAME:
			final String text = new String(frameMessage.data);
			final TextMessage textMessage = new TextMessage(text);
			LogFile.log("server receive TEXT FRAME: [ " + frameMessage.toString() + " ] = " + text.toString());
			System.out.println("DATA: " + textMessage.toString());
			TextMessageDao.save(textMessage);
			return Protocol.ACK;

		case Protocol.USER_FRAME:
			final User user = new User(frameMessage.data);
			LogFile.log("server receive USER FRAME: [ " + frameMessage.toString() + " ] = " + user.toString());
			System.out.println("DATA: " + user.toString());
			UserDao.save(user);
			return Protocol.ACK;

		case Protocol.TIME_FRAME:
			final String fuse = new String(frameMessage.data);
			LogFile.log("server receive TIME FRAME: [ " + frameMessage.toString() + " ] = " + fuse.toString());
			System.out.println("DATA: " + fuse);
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
				final ByteBuffer byteBuffer = (ByteBuffer) message;
				FrameMessage frameMessage = new FrameMessage(byteBuffer);

				frameMessage.validate();

				final byte[] response = processDataFrame(frameMessage);
				final ByteBuffer encode = ByteBuffer.wrap(response);
				session.write(encode);

				FrameMessage frameMessageResponse = new FrameMessage(response);
				LogFile.log("server send: [ " + frameMessageResponse.toString() + " ]");

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
