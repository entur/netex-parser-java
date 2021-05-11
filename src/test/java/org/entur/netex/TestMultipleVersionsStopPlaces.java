package org.entur.netex;

import org.entur.netex.index.api.NetexEntityIndexReadOnlyView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;

public class TestMultipleVersionsStopPlaces {
    private static NetexEntityIndexReadOnlyView index;

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
        var stopPlaces = index.getStopPlaceById().lookupAllVersionsById("NSR:StopPlace:5543");
        Assertions.assertEquals(8, stopPlaces.size());
    }
}
