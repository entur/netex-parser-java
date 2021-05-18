package org.entur.netex;

import org.entur.netex.index.api.NetexEntityIndex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TestStopPlacesExport {
    private static NetexEntityIndex index;

    @BeforeAll
    static void init() {
        try {
            var parser = new NetexParser();
            index = parser.parse("src/test/resources/CurrentwithServiceFrame_latest.zip");
        } catch (Exception e) {
            Assertions.fail(e.getMessage(), e);
        }
    }

    @Test
    public void testGetStopPlace() {
        var stopPlace = index.getStopPlaceById().getLatestVersion("NSR:StopPlace:337");
        Assertions.assertEquals("Oslo S", stopPlace.getName().getValue());
        Assertions.assertEquals("NSR:StopPlace:59872", stopPlace.getParentSiteRef().getRef());
    }

    @Test
    public void testQuay() {
        var quay = index.getQuayById().getLatestVersion("NSR:Quay:3691");
        Assertions.assertEquals("01", quay.getPrivateCode().getValue());
    }

    @Test
    public void testGetGroupOfStopPlaces() {
        var groupOfStopPlaces = index.getGroupOfStopPlacesById().get("NSR:GroupOfStopPlaces:1");
        Assertions.assertEquals("Oslo", groupOfStopPlaces.getName().getValue());
    }

    @Test
    public void testGetTariffZone() {
        var tariffZone = index.getTariffZonesById().get("MOR:TariffZone:108");
        Assertions.assertEquals("Standal", tariffZone.getName().getValue());
    }

    @Test
    public void testGetTopographicPlace() {
        var topographicPlace = index.getTopographicPlaceById().get("KVE:TopographicPlace:50");
        Assertions.assertEquals("Trøndelag", topographicPlace.getDescriptor().getName().getValue());
        Assertions.assertEquals("no", topographicPlace.getCountryRef().getRef().value());
    }

    @Test
    public void testGetParking() {
        var parking = index.getParkingById().get("NSR:Parking:1");
        Assertions.assertEquals("Drammen", parking.getName().getValue());
    }

    @Test
    public void testGetScheduledStopPoint() {
        var scheduledStopPoint = index.getScheduledStopPointById().get("NSR:ScheduledStopPoint:S5");
        Assertions.assertEquals("Gudå", scheduledStopPoint.getName().getValue());
    }

    @Test
    public void testGetFareZone() {
        var fareZone = index.getFareZoneById().get("AKT:FareZone:27");
        Assertions.assertEquals("Kviteseid", fareZone.getName().getValue());
    }
}
