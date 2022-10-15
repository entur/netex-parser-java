package org.entur.netex.index.api;

import org.rutebanken.netex.model.EntityStructure;

import java.util.Collection;
import java.util.Map;

/**
 * An index of versioned NeTEx entities
 * @param <V>
 */
public interface VersionedNetexEntityIndex<V extends EntityStructure> {

    /**
     * Return the element with the latest version with the given {@code id}. Returns
     * {@code null} if no element is found.
     */
    V getLatestVersion(String id);

    /**
     * Return the element with the given {@code id} and {@code version}. Returns
     * {@code null} if no element is found
     * @param id
     * @param version
     * @return
     */
    V getVersion(String id, String version);


    /**
     * Return the latest version of all entities
     * @return
     */
    Collection<V> getLatestVersions();

    /**
     * Lookup all versions of element with the given {@code id}.
     *
     * @return an empty collection if no elements are found.
     */
    Collection<V> getAllVersions(String id);

    /**
     * Get all versions of all entities
     * @return
     */
    Map<String, Collection<V>> getAllVersions();

    /**
     * Put all versions of an entity into the index.
     * If the entity already exists in the index, all versions
     * will be replaced.
     *
     * @param id
     * @param entities
     */
    void put(String id, Collection<V> entities);

    /**
     * Put all entities into the collection
     * If an entity already exists in the index, all versions
     * will be replaced.
     * @param entities
     */
    void putAll(Collection<V> entities);

    /**
     * Remove all versions of an entity from the index given its id
     */
    void remove(String id);
}
