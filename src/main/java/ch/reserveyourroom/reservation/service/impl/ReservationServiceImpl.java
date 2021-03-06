package ch.reserveyourroom.reservation.service.impl;

import ch.reserveyourroom.common.exception.business.BusinessOperation;
import ch.reserveyourroom.common.exception.business.BusinessUnprocessableOperationException;
import ch.reserveyourroom.common.exception.persistence.EntityNotFoundException;
import ch.reserveyourroom.common.exception.persistence.EntityOptimisticLockException;
import ch.reserveyourroom.common.exception.persistence.PersistenceException;
import ch.reserveyourroom.common.exception.persistence.EntityUnprocessableException;
import ch.reserveyourroom.reservation.dao.ReservationDao;
import ch.reserveyourroom.reservation.model.Reservation;
import ch.reserveyourroom.reservation.service.ReservationService;
import ch.reserveyourroom.reservation.dao.ReservationDao;
import ch.reserveyourroom.reservation.model.Reservation;
import ch.reserveyourroom.reservation.service.ReservationService;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Stateless
public class ReservationServiceImpl implements ReservationService {


    private Logger LOGGER = LoggerFactory.getLogger(ReservationServiceImpl.class);

    @Inject
    private ReservationDao reservationDao;

    public Reservation find(@NotNull UUID key) throws BusinessUnprocessableOperationException {

        Optional<Reservation> entity = this.reservationDao.read(key);
        if (entity.isPresent()) {
            return entity.get();
        } else {
            throw new BusinessUnprocessableOperationException(BusinessOperation.FIND);
        }
    }

    public Optional<Reservation> search(@NotNull UUID key) {

        return this.reservationDao.read(key);
    }

    public List<Reservation> loadAll() {

        return this.reservationDao.loadAll();
    }

    public UUID save(@NotNull @Valid Reservation reservation) {

        return this.reservationDao.create(reservation);
    }

    public Reservation update(@NotNull @Valid Reservation reservation) throws BusinessUnprocessableOperationException {

        try {
            return this.reservationDao.update(reservation);
        } catch (EntityOptimisticLockException e) {
            throw new BusinessUnprocessableOperationException(BusinessOperation.UPDATE, e);
        }

    }

    public void delete(@NotNull UUID id) throws BusinessUnprocessableOperationException {

        List<Reservation> reservations = this.reservationDao.loadAll();
        Optional<Reservation> t = null;
        for(Reservation r : reservations){
            if(r.getUuid().equals(id)){
                t = Optional.ofNullable(r);
            }
        }
        //Optional<Reservation> t = this.reservationDao.read(id);
        if(t.isPresent()){
            this.reservationDao.delete(t.get().getUuid());
        } else {
            throw new BusinessUnprocessableOperationException(BusinessOperation.DELETE);
        }
    }


}

