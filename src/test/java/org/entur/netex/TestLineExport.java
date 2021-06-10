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
        var routePoint = index.getServiceJourneyInterchangeIndex().get("ATB:ServiceJourneyInterchange:548");
        Assertions.assertNotNull(routePoint);
    }

}
