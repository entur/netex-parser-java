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
     * {@code null} if not element is found.
     */
    V getLatestVersion(String id);

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
     * Put all entities into the collection
     * @param entities
     */
    void putAll(Collection<V> entities);
}
