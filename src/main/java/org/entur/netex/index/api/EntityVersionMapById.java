package org.entur.netex.index.api;

import java.util.Collection;
import java.util.Map;

public interface EntityVersionMapById<V> extends PutAllCollection<V> {
    /**
     * Alias for getLatestVersion
     */
    V get(String id);

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
     * Add all versioned entities to collection
     * @param entities
     */
    @Override
    void putAll(Collection<V> entities);
}
