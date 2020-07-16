package com.barbalho.rocha;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class Server {

	public static void main(String[] args) {
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
	}
}
