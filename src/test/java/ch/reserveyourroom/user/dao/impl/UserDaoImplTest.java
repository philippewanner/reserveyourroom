package ch.reserveyourroom.user.dao.impl;

import ch.reserveyourroom.common.exception.persistence.EntityOptimisticLockException;
import ch.reserveyourroom.reservation.dao.ReservationDao;
import ch.reserveyourroom.reservation.dao.impl.ReservationDaoImpl;
import ch.reserveyourroom.user.dao.UserDao;
import ch.reserveyourroom.user.model.User;
import ch.reserveyourroom.wish.dao.WishDao;
import ch.reserveyourroom.wish.dao.impl.WishDaoImpl;
import ch.reserveyourroom.wish.model.Wish;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.Date;
import java.time.Instant;
import java.util.*;
import java.util.function.ObjDoubleConsumer;

import static org.junit.Assert.assertTrue;

/**
 * Unit testing the user dao implementation.
 */

@RunWith(MockitoJUnitRunner.class)
public class UserDaoImplTest {

    private UserDao userDao = new UserDaoImpl();

    private WishDao wishDao = new WishDaoImpl();

    protected static EntityManagerFactory emf;
    protected EntityManager em;

    @BeforeClass
    public static void createEntityManagerFactory() {
        emf = Persistence.createEntityManagerFactory("myPersistenceUnitTest");
    }

    @AfterClass
    public static void closeEntityManagerFactory() {
        if (emf != null) {
            emf.close();
        }
    }

    @Before
    public void beginTransaction() {

        em = emf.createEntityManager();

        userDao.setEntityManager(em);

        wishDao.setEntityManager(em);

        em.getTransaction().begin();
    }

    @After
    public void rollbackTransaction() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }

        if (em.isOpen()) {
            em.close();
        }
    }

    @Test
    public void should_countAllUsers() {
        // Given
        User u1 = new User();
        u1.setEmail("u1@mail.com");
        u1.setFirstname("u");
        u1.setLastname("1");
        userDao.create(u1);

        User u2 = new User();
        u2.setEmail("u2@mail.com");
        u2.setFirstname("u");
        u2.setLastname("2");
        userDao.create(u2);

        // When
        final long count = userDao.countAll();

        // Then
        assertTrue("The number of users has to be two", 2 == count);
    }

    @Test
    public void should_createANewUserInTheDb() {

        // Given
        User u1 = new User();
        u1.setEmail("u1@mail.com");
        u1.setFirstname("u");
        u1.setLastname("1");

        // When
        final UUID userId = userDao.create(u1);

        // Then
        User userRead = userDao.read(userId).get();
        assertTrue("The Id of the user can not be read", userId.compareTo(userRead.getUuid()) == 0);
    }

    @Test
    public void should_loadAllUsersFromDb() {

        // Given
        int nbUserToCreate = 5;
        for(int i=0; i < nbUserToCreate; i++) {
            this.createSampleUserInDb();
        }

        // When
        final List<User> users = userDao.loadAll();

        // Then
        assertTrue("The users size has to match the users found", nbUserToCreate == users.size());
    }

    @Test
    public void should_deleteUserFromDb() {

        // Given
        UUID pk = this.createSampleUserInDb();
        Optional<User> userFound = userDao.read(pk);

        // When
        userDao.delete(userFound.get().getUuid());

        // Then
        assertTrue("The user has to be deleted from the DB", !userDao.read(pk).isPresent());
    }

    @Test
    public void should_updateUserFromDb() {

        // Given
        UUID pk = this.createSampleUserInDb();
        Optional<User> userFound = userDao.read(pk);
        String newEmail = "test";

        // When
        try {
            userFound.get().setEmail(newEmail);
            userDao.update(userFound.get());
        } catch (EntityOptimisticLockException e) {
            e.printStackTrace();
        }

        // Then
        assertTrue("The update can not occur", userDao.read(pk).get().getEmail().compareTo(newEmail) == 0);
    }

    @Test
    public void should_readUserFromDb() {

        // Given
        UUID pk = this.createSampleUserInDb();

        // When
        Optional<User> userFound = userDao.read(pk);

        // Then
        assertTrue("The system cannot read the user from DB", userFound.isPresent());
        assertTrue(userFound.get().getFirstname().compareTo("first") == 0);
        assertTrue(userFound.get().getLastname().compareTo("last") == 0);
    }

    private UUID createSampleUserInDb(){

        User u = new User();
        u.setEmail("first.last@mail.com");
        u.setFirstname("first");
        u.setLastname("last");
        UUID userId = userDao.create(u);

        Wish wish = new Wish();
        wish.setStart(Date.from(Instant.now()));
        wish.setEnd(Date.from(Instant.now()));
        wish.setUserId(userId);
        wishDao.create(wish);

        return userId;
    }
}