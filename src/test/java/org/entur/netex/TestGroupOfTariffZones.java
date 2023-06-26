package org.entur.netex;

import org.entur.netex.index.api.NetexEntitiesIndex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.GroupOfTariffZones;

import java.io.File;
import java.nio.file.Files;
import java.util.Collection;

class TestGroupOfTariffZones {
    private static NetexEntitiesIndex index;

    @BeforeAll
    static void init() {
        try {
            NetexParser parser = new NetexParser();
            File file = new File("src/test/resources/FareZones_NOR_TzGroupTest_2.xml");
            index = parser.parse(Files.newInputStream(file.toPath()));
        } catch (Exception e) {
            Assertions.fail(e.getMessage(), e);
        }
    }

    @Test
    void testGetAllGroupsOfTariffZones() {
        Collection<GroupOfTariffZones> groups = index.getGroupOfTariffZonesIndex().getLatestVersions();
        Assertions.assertFalse(groups.isEmpty());
    }
}
