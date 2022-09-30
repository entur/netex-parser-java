package org.entur.netex.index.impl;

import org.entur.netex.index.api.NetexEntityIndex;
import org.rutebanken.netex.model.EntityStructure;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NetexEntityMapByIdImpl<V extends EntityStructure> implements NetexEntityIndex<V> {
    private final Map<String, V> map = new ConcurrentHashMap<>();

    @Override
    public V get(String id) {
        return map.get(id);
    }

    @Override
    public Collection<V> getAll() {
        return map.values();
    }

    @Override
    public void put(String id, V entity) {
        map.put(id, entity);
    }

    @Override
    public void putAll(Collection<V> entities) {
        entities.forEach(this::add);
    }

    @Override
    public void remove(String id) {
        map.remove(id);
    }

    public void add(V entity) {
        map.put(entity.getId(), entity);
    }
}
