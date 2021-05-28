package org.entur.netex;

import org.entur.netex.index.api.NetexEntitiesIndex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;

public class TestMultipleVersionsStopPlaces {
    private static NetexEntitiesIndex index;

    @BeforeAll
    static void init() {
        try {
            var parser = new NetexParser();
            var file = new File("src/test/resources/MultipleVersionsStopPlaces.xml");
            index = parser.parse(new FileInputStream(file));
        } catch (Exception e) {
            Assertions.fail(e.getMessage(), e);
        }
    }

    @Test
    void testGetAllVersionsOfStopPlace() {
        var stopPlaces = index.getStopPlaceIndex().getAllVersions("NSR:StopPlace:5543");
        Assertions.assertEquals(8, stopPlaces.size());
    }

    @Test
    void testGetAllVersionsOfAllStopPlaces() {
        var stopPlaces = index.getStopPlaceIndex().getAllVersions();
        Assertions.assertEquals(8, stopPlaces.get("NSR:StopPlace:5543").size());
    }
}
