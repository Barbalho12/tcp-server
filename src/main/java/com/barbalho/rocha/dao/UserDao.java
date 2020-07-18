package com.barbalho.rocha.dao;

import java.util.List;

import com.barbalho.rocha.exceptions.DaoException;
import com.barbalho.rocha.models.User;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class UserDao {

    public static void save(final User object) throws DaoException {
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
    
    public static List<User> findAll() throws DaoException{
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			List<User> users = session.createQuery("from User", User.class).list();
            return users;
		} catch (Exception exception) {
			throw new DaoException("Erro ao colsultar base de dado", exception);
		}
    }
    
}