package org.entur.netex.index.api;

import java.util.Collection;

public interface PutAllCollection<V> {
    void putAll(Collection<V> entities);
}
