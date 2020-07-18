package com.barbalho.rocha.dao;

import java.util.List;

import com.barbalho.rocha.exceptions.DaoException;
import com.barbalho.rocha.models.TextMessage;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class TextMessageDao {

    public static void save(final TextMessage object) throws DaoException {
		Transaction transaction = null;
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			transaction = session.beginTransaction();
			session.save(object);
			transaction.commit();
		} catch (final Exception exception) {
			if (transaction != null) {
				transaction.rollback();
			}
			throw new DaoException("Erro ao tentar salvar na base de dado", exception);
		}
    }
    
    public static List<TextMessage> findAll() throws DaoException{
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			List<TextMessage> textMessages = session.createQuery("from TextMessage", TextMessage.class).list();
            return textMessages;
		} catch (Exception exception) {
			throw new DaoException("Erro ao colsultar base de dado", exception);
		}
    }
    
}