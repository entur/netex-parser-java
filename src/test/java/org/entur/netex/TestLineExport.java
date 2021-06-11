package org.entur.netex;

import org.entur.netex.index.api.NetexEntitiesIndex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TestLineExport {
    private static NetexEntitiesIndex index;

    @BeforeAll
    static void init() {
        try {
            var parser = new NetexParser();
            index = parser.parse("src/test/resources/line_file.zip");
        } catch (Exception e) {
            Assertions.fail(e.getMessage(), e);
        }
    }

    @Test
    void testGetServiceJourneyInterchange() {
        var serviceJourneyInterchange = index.getServiceJourneyInterchangeIndex().get("GOA:ServiceJourneyInterchange:6");
        Assertions.assertNotNull(serviceJourneyInterchange);
    }

    @Test
    void testGetDatedServiceJourneysByServiceJourneyRef() {
        var datedServiceJourneys = index.getDatedServiceJourneyByServiceJourneyRefIndex().get("GOA:ServiceJourney:B701-B5_200");
        Assertions.assertNotNull(datedServiceJourneys);
        Assertions.assertFalse(datedServiceJourneys.isEmpty());
    }

}
