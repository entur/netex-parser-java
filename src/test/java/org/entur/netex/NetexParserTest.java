package org.entur.netex;

import org.entur.netex.index.api.NetexEntityIndexReadOnlyView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NetexParserTest {

    @Test
    public void testStopPlaces() {
        var parser = new NetexParser();
        NetexEntityIndexReadOnlyView index = null;

        try {
            index = parser.parseFromZip("src/test/resources/CurrentwithServiceFrame_latest.zip");
        } catch (Exception e) {
            Assertions.fail(e.getMessage(), e);
        }

        var stopPlace = index.getStopPlaceById().lookupLastVersionById("NSR:StopPlace:337");
        Assertions.assertEquals("Oslo S", stopPlace.getName().getValue());
        Assertions.assertEquals("NSR:StopPlace:59872", stopPlace.getParentSiteRef().getRef());

        var groupOfStopPlaces = index.getGroupOfStopPlacesById().lookup("NSR:GroupOfStopPlaces:1");
        Assertions.assertEquals("Oslo", groupOfStopPlaces.getName().getValue());

        var tariffZone = index.getTariffZonesById().lookup("MOR:TariffZone:108");
        Assertions.assertEquals("Standal", tariffZone.getName().getValue());

        var topographicPlace = index.getTopographicPlaceById().lookup("KVE:TopographicPlace:50");
        Assertions.assertEquals("Trøndelag", topographicPlace.getDescriptor().getName().getValue());
        Assertions.assertEquals("no", topographicPlace.getCountryRef().getRef().value());

        var parking = index.getParkingById().lookup("NSR:Parking:1");
        Assertions.assertEquals("Drammen", parking.getName().getValue());

        var scheduledStopPoint = index.getScheduledStopPointById().lookup("NSR:ScheduledStopPoint:S5");
        Assertions.assertEquals("Gudå", scheduledStopPoint.getName().getValue());

    }
}
