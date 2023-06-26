package org.entur.netex;

import org.entur.netex.index.api.NetexEntitiesIndex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.StopPlace;

import java.io.File;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;

class TestMultipleVersionsStopPlaces {
    private static NetexEntitiesIndex index;

    @BeforeAll
    static void init() {
        try {
            NetexParser parser = new NetexParser();
            File file = new File("src/test/resources/MultipleVersionsStopPlaces.xml");
            index = parser.parse(Files.newInputStream(file.toPath()));
        } catch (Exception e) {
            Assertions.fail(e.getMessage(), e);
        }
    }

    @Test
    void testGetAllVersionsOfStopPlace() {
        Collection<StopPlace> stopPlaces = index.getStopPlaceIndex().getAllVersions("NSR:StopPlace:5543");
        Assertions.assertEquals(8, stopPlaces.size());
    }

    @Test
    void testGetAllVersionsOfAllStopPlaces() {
        Map<String, Collection<StopPlace>> stopPlaces = index.getStopPlaceIndex().getAllVersions();
        Assertions.assertEquals(8, stopPlaces.get("NSR:StopPlace:5543").size());
    }

    @Test
    void testGetLatestVersionOfAllStopPlaces() {
        Collection<StopPlace> stopPlaces = index.getStopPlaceIndex().getLatestVersions();
        Assertions.assertEquals(2, stopPlaces.size());
    }

    @Test
    void testGetStopPlaceWithVersion() {
        StopPlace stopPlace = index.getStopPlaceIndex().getVersion("NSR:StopPlace:5543", "1");
        Assertions.assertEquals(1, Integer.parseInt(stopPlace.getVersion()));
    }

    @Test
    void testGetLatestVersionOfVersionedStopPlace() {
        StopPlace stopPlace = index.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:5543");
        Assertions.assertEquals(8, Integer.parseInt(stopPlace.getVersion()));
    }
}
