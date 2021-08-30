package org.entur.netex;

import org.entur.netex.index.api.NetexEntitiesIndex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.RoutePoint;

class TestReferenceDataExport {
    private static NetexEntitiesIndex index;

    @BeforeAll
    static void init() {
        try {
            NetexParser parser = new NetexParser();
            index = parser.parse("src/test/resources/common_file.zip");
        } catch (Exception e) {
            Assertions.fail(e.getMessage(), e);
        }
    }

    @Test
    void testGetRoutePoint() {
        RoutePoint routePoint = index.getRoutePointIndex().get("AVI:RoutePoint:76586");
        Assertions.assertNotNull(routePoint);
    }

}
