package org.entur.netex.index.api;

import org.rutebanken.netex.model.EntityStructure;

import java.util.Collection;

/**
 * A simple index of NeTEx entities of a specific type
 * @param <V>
 */
public interface NetexEntityIndex<V extends EntityStructure> {
    /**
     * Get an entity by its id
     *
     * @param id
     * @return The entity
     */
    V get(String id);

    /**
     * Get all entities in the index
     *
     * @return A collection of the entity type
     */
    Collection<V> getAll();

    /**
     * Put an entity into the collection
     * If the entity already exists in the index, all versions
     * will be replaced.
     * @param entity
     */
    void put(String id, V entity);

    /**
     * Put all entities into the collection
     * If an entity already exists in the index, all versions
     * will be replaced.
     * @param entities
     */
    void putAll(Collection<V> entities);

    /**
     * Remove an entity from the index given its id
     * @param id
     */
    void remove(String id);
}
