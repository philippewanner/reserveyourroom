package ch.reserveyourroom.common.service;

import ch.reserveyourroom.common.model.AbstractEntity;
import ch.reserveyourroom.common.exception.business.BusinessUnprocessableOperationException;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface that defines the CRUD operations that can perform each service.
 * @param <T> The given entity to perform the operations on.
 */

public interface GenericService<T extends AbstractEntity> {

    /**
     * Find a <T> with the given id.
     *
     * @param key Id of the <T> to find
     * @return return the found T, otherwise throws an EntityNotFoundException.
     */
    T find(@NotNull UUID key) throws BusinessUnprocessableOperationException;

    /**
     * Search a <T> corresponding to the given id.
     *
     * @param key Id of the <T> to search
     * @return return the found T, or an empty Optional if not found.
     */
    Optional<T> search(@NotNull UUID key);

    /**
     * Load all entity <T>
     *
     * @return a list of <T>
     */
    List<T> loadAll();

    /**
     * Save a new <T>.
     *
     * @param t the <T> to save.
     * @return the persisted <T>.
     */
    UUID save(@NotNull @Valid T t);

    /**
     * Update a <T>.
     *
     * @param t the <T> to update in the database.
     * @return updated <T>.
     */
    T update(@NotNull @Valid T t)
            throws BusinessUnprocessableOperationException;

    /**
     * Deletes a <T>
     */
    void delete(@NotNull UUID id) throws BusinessUnprocessableOperationException;
}
