package ch.reserveyourroom.room.dao.impl;

import ch.reserveyourroom.address.dao.AddressDao;
import ch.reserveyourroom.address.dao.impl.AddressDaoImpl;
import ch.reserveyourroom.address.model.Address;
import ch.reserveyourroom.building.dao.BuildingDao;
import ch.reserveyourroom.building.dao.impl.BuildingDaoImpl;
import ch.reserveyourroom.building.model.Building;
import ch.reserveyourroom.common.exception.persistence.EntityOptimisticLockException;
import ch.reserveyourroom.room.dao.RoomDao;
import ch.reserveyourroom.room.model.Room;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/*
 * Unit testing the room dao implementation.
 */
@RunWith(MockitoJUnitRunner.class)
public class RoomDaoImplTest {

    private RoomDao roomDao = new RoomDaoImpl();
    private AddressDao addressDao = new AddressDaoImpl();
    private BuildingDao buildingDao = new BuildingDaoImpl();

    private UUID addressId;
    private UUID buildingId;

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

        roomDao.setEntityManager(em);
        addressDao.setEntityManager(em);
        buildingDao.setEntityManager(em);

        em.getTransaction().begin();

        //Create an address
        Address a = new Address();
        a.setCity("Sion");
        a.setCountry("Schweiz");
        a.setHousenumber("5");
        a.setZipcode("1950");
        a.setStreet("myStreet");
        UUID addressId = addressDao.create(a);

        // Create a building
        Building b = new Building();
        b.setAddressId(addressId);
        b.setName("buildingName");
        buildingId = buildingDao.create(b);
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
    public void should_countAllObjects() {
        // Given
        int nbObjectToCreate = 2;
        for (int i = 0; i < nbObjectToCreate; i++) {
            createSampleRoomInDb(i);
        }

        // When
        final long count = roomDao.countAll();

        // Then
        assertTrue("The number of objects has to be two", nbObjectToCreate == count);
    }

    @Test
    public void should_createANewObjectInTheDb() {

        // Given

        // When
        final UUID objectId = createSampleRoomInDb(1);

        // Then
        Room objectRead = roomDao.read(objectId).get();
        assertTrue("The Id of the object can not be read", objectId.compareTo(objectRead.getUuid()) == 0);
    }

    @Test
    public void should_loadAllObjectsFromDb() {

        // Given
        int nbObjectToCreate = 5;
        for (int i = 0; i < nbObjectToCreate; i++) {
            this.createSampleRoomInDb(i);
        }

        // When
        final List<Room> objectsFound = roomDao.loadAll();

        // Then
        assertTrue("The objects size created has to match the objects found", nbObjectToCreate == objectsFound.size());
    }

    @Test
    public void should_deleteObjectFromDb() {

        // Given
        UUID pk = this.createSampleRoomInDb(1);
        Optional<Room> objectFound = roomDao.read(pk);

        // When
        roomDao.delete(objectFound.get().getUuid());

        // Then
        assertFalse("The object has to be deleted from the DB", roomDao.read(pk).isPresent());
    }

    @Test
    public void should_updateObjectFromDb() {

        // Given
        UUID pk = this.createSampleRoomInDb(1);
        Optional<Room> objectFound = roomDao.read(pk);
        String newRoomName = "newRoomName";

        // When
        try {
            objectFound.get().setName(newRoomName);
            roomDao.update(objectFound.get());
        } catch (EntityOptimisticLockException e) {
            e.printStackTrace();
        }

        // Then
        assertTrue("The update can not occur", roomDao.read(pk).get().getName().compareTo(newRoomName) == 0);
    }

    @Test
    public void should_readObjectFromDb() {

        // Given
        UUID pk = this.createSampleRoomInDb(1);

        // When
        Optional<Room> objectFound = roomDao.read(pk);

        // Then
        assertTrue("The system cannot read the object from DB", objectFound.isPresent());
        assertFalse("The system cannot read the object from DB", objectFound.get().getName().isEmpty());
    }

    private UUID createSampleRoomInDb(int number) {

        Room r = new Room();
        r.setFloor(2);
        r.setName("roomName"+number);
        r.setSeatnumber(198);
        r.setSize((float) (34));
        r.setBuildingId(buildingId);
        return roomDao.create(r);
    }
}
