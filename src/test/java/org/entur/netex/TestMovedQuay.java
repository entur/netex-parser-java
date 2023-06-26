package org.entur.netex;

import org.entur.netex.index.api.NetexEntitiesIndex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.StopPlace;

import java.io.File;
import java.nio.file.Files;
import java.util.Collection;

class TestMovedQuay {
    private static NetexEntitiesIndex index;

    @BeforeAll
    static void init() {
        try {
            NetexParser parser = new NetexParser();
            File file = new File("src/test/resources/MovedQuay.xml");
            index = parser.parse(Files.newInputStream(file.toPath()));
        } catch (Exception e) {
            Assertions.fail(e.getMessage(), e);
        }
    }

    @Test
    void testGetAllVersionsOfStopPlace() {
        Collection<StopPlace> stopPlaces = index.getStopPlaceIndex().getAllVersions("NSR:StopPlace:5543");
        Assertions.assertEquals(2, stopPlaces.size());
    }

    @Test
    void testMovedQuayLinkedToNewStopPlace() {
        Assertions.assertEquals("NSR:StopPlace:9999", index.getStopPlaceIdByQuayIdIndex().get("NSR:Quay:10149"));
    }


}
