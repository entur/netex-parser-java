package org.entur.netex.index.api;

import java.util.Map;

public interface EntityMapById<V> extends Map<String, V>, PutAllCollection<V> {}
