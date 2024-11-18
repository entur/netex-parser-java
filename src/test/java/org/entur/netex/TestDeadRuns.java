package org.entur.netex;

import org.entur.netex.index.api.NetexEntitiesIndex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.DeadRun;

import java.util.Collection;

class TestDeadRuns {
    private static NetexEntitiesIndex index;

    @BeforeAll
    static void init() {
        try {
            NetexParser parser = new NetexParser();
            index = parser.parse("src/test/resources/deadRuns.zip");
        } catch (Exception e) {
            Assertions.fail(e.getMessage(), e);
        }
    }

    @Test
    void testDeadRuns() {
        Collection<DeadRun> deadRuns = index.getDeadRunIndex().getAll();
        Assertions.assertFalse(deadRuns.isEmpty());
    }

}
