package org.entur.netex;

import org.entur.netex.index.api.NetexEntitiesIndex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.DatedServiceJourney;
import org.rutebanken.netex.model.ServiceJourneyInterchange;

import java.util.Collection;

class TestLineExport {
    private static NetexEntitiesIndex index;

    @BeforeAll
    static void init() {
        try {
            NetexParser parser = new NetexParser();
            index = parser.parse("src/test/resources/line_file.zip");
        } catch (Exception e) {
            Assertions.fail(e.getMessage(), e);
        }
    }

    @Test
    void testGetServiceJourneyInterchange() {
        ServiceJourneyInterchange serviceJourneyInterchange = index.getServiceJourneyInterchangeIndex().get("GOA:ServiceJourneyInterchange:6");
        Assertions.assertNotNull(serviceJourneyInterchange);
    }

    @Test
    void testGetDatedServiceJourneysByServiceJourneyRef() {
        Collection<DatedServiceJourney> datedServiceJourneys = index.getDatedServiceJourneyByServiceJourneyRefIndex().get("GOA:ServiceJourney:B701-B5_200");
        Assertions.assertNotNull(datedServiceJourneys);
        Assertions.assertFalse(datedServiceJourneys.isEmpty());
    }

}
