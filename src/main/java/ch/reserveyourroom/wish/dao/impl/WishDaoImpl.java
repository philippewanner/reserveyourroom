package ch.reserveyourroom.wish.dao.impl;

import ch.reserveyourroom.common.dao.impl.GenericDaoImpl;
import ch.reserveyourroom.wish.dao.WishDao;
import ch.reserveyourroom.wish.model.Wish;

import javax.ejb.Stateless;

@Stateless
public class WishDaoImpl extends GenericDaoImpl<Wish> implements WishDao {
/*
    public List<Wish> findByEnd(Date end) {
        throw new NotImplementedException(); //@TODO: to implement
    }

    public List<Wish> findByStart(Date start) {
        throw new NotImplementedException(); //@TODO: to implement
    }

    public List<Wish> findByRoomId(Long roomId) {
        throw new NotImplementedException(); //@TODO: to implement
    }
    */
}
