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
     * Put all entities into the collection
     * @param entities
     */
    void putAll(Collection<V> entities);
}
