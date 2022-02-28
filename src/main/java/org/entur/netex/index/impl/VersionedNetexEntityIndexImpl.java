package org.entur.netex.index.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.entur.netex.index.api.VersionedNetexEntityIndex;
import org.rutebanken.netex.model.EntityInVersionStructure;
import org.rutebanken.netex.model.EntityStructure;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static org.entur.netex.support.NetexVersionHelper.latestVersionedElementIn;
import static org.entur.netex.support.NetexVersionHelper.versionOfElementIn;

public class VersionedNetexEntityIndexImpl<V extends EntityInVersionStructure> implements VersionedNetexEntityIndex<V> {
    private final Multimap<String,V> map  = Multimaps.synchronizedListMultimap(ArrayListMultimap.create());

    private Map<String,V> latestMap;

    @Override
    public V getLatestVersion(String id) {
        if (latestMap == null) {
            populateLatestMap();
        }
        return latestMap.get(id);
    }

    @Override
    public V getVersion(String id, String version) {
        return versionOfElementIn(map.get(id), version);
    }

    @Override
    public Collection<V> getLatestVersions() {
        if (latestMap == null) {
            populateLatestMap();
        }
        return latestMap.values();
    }

    @Override
    public Collection<V> getAllVersions(String id) {
        return map.get(id);
    }

    @Override
    public Map<String, Collection<V>> getAllVersions() {
        synchronized (map) {
            return map.asMap();
        }
    }

    @Override
    public void putAll(Collection<V> entities) {
        entities.forEach(this::put);
    }

    private void put(V v) {
        map.put(v.getId(), v);
    }

    private void populateLatestMap() {
        latestMap = map.keySet().stream()
                .map(id -> latestVersionedElementIn(map.get(id)))
                .collect(Collectors.toMap(EntityStructure::getId, e -> e));
    }
}
