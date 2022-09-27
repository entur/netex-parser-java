package org.entur.netex;

import org.entur.netex.index.api.NetexEntitiesIndex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.StopPlace;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;

class TestUpdateStopPlaces {
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
    void testStopPlacesCanBeUpdatedAndRemoved() throws IOException {
        Assertions.assertEquals(1, index.getStopPlaceIndex().getAllVersions("NSR:StopPlace:9999").size());
        NetexParser parser = new NetexParser();
        File file = new File("src/test/resources/StopPlaceUpdated.xml");
        NetexEntitiesIndex newIndex = parser.parse(Files.newInputStream(file.toPath()));
        Collection<StopPlace> updatedStopPlace = newIndex.getStopPlaceIndex().getAllVersions("NSR:StopPlace:9999");
        index.getStopPlaceIndex().put("NSR:StopPlace:9999", updatedStopPlace);
        Assertions.assertEquals(2, index.getStopPlaceIndex().getAllVersions("NSR:StopPlace:9999").size());
        index.getStopPlaceIndex().remove("NSR:StopPlace:9999");
        Assertions.assertEquals(0, index.getStopPlaceIndex().getAllVersions("NSR:StopPlace:9999").size());
    }
}
