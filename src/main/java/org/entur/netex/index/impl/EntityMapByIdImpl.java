package org.entur.netex.index.impl;

import org.entur.netex.index.api.EntityMapById;
import org.rutebanken.netex.model.EntityStructure;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class EntityMapByIdImpl<V extends EntityStructure> extends ConcurrentHashMap<String, V> implements EntityMapById<V> {
    @Override
    public void putAll(Collection<V> entities) {
        entities.forEach(this::add);
    }

    public void add(V entity) {
        super.put(entity.getId(), entity);
    }
}
