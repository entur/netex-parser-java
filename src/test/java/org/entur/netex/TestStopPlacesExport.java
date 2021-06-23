package org.entur.netex;

import org.entur.netex.index.api.NetexEntitiesIndex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TestStopPlacesExport {
    private static NetexEntitiesIndex index;

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
        var stopPlace = index.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:337");
        Assertions.assertEquals("Oslo S", stopPlace.getName().getValue());
        Assertions.assertEquals("NSR:StopPlace:59872", stopPlace.getParentSiteRef().getRef());
    }

    @Test
    public void testQuay() {
        var quay = index.getQuayIndex().getLatestVersion("NSR:Quay:3691");
        Assertions.assertEquals("01", quay.getPrivateCode().getValue());
    }

    @Test
    public void getStopPlaceIdByQuayId() {
        var stopPlaceId = index.getStopPlaceIdByQuayIdIndex().get("NSR:Quay:3691");
        Assertions.assertEquals("NSR:StopPlace:2133", stopPlaceId);
    }

    @Test
    public void testGetGroupOfStopPlaces() {
        var groupOfStopPlaces = index.getGroupOfStopPlacesIndex().get("NSR:GroupOfStopPlaces:1");
        Assertions.assertEquals("Oslo", groupOfStopPlaces.getName().getValue());
    }

    @Test
    public void testGetTariffZone() {
        var tariffZone = index.getTariffZoneIndex().get("MOR:TariffZone:108");
        Assertions.assertEquals("Standal", tariffZone.getName().getValue());
    }

    @Test
    public void testGetTopographicPlace() {
        var topographicPlace = index.getTopographicPlaceIndex().get("KVE:TopographicPlace:50");
        Assertions.assertEquals("Trøndelag", topographicPlace.getDescriptor().getName().getValue());
        Assertions.assertEquals("no", topographicPlace.getCountryRef().getRef().value());
    }

    @Test
    public void testGetParking() {
        var parking = index.getParkingIndex().get("NSR:Parking:1");
        Assertions.assertEquals("Drammen", parking.getName().getValue());
    }

    @Test
    public void testGetScheduledStopPoint() {
        var scheduledStopPoint = index.getScheduledStopPointIndex().get("NSR:ScheduledStopPoint:S5");
        Assertions.assertEquals("Gudå", scheduledStopPoint.getName().getValue());
    }

    @Test
    public void testGetPassengerStopAssignment() {
        var scheduledStopPoint = index.getScheduledStopPointIndex().get("NSR:ScheduledStopPoint:S5");
        var passengerStopAssignments = index.getPassengerStopAssignmentsByStopPointRefIndex().get(scheduledStopPoint.getId());

        Assertions.assertEquals(1, passengerStopAssignments.size());
    }

    @Test
    public void testGetFareZone() {
        var fareZone = index.getFareZoneIndex().get("AKT:FareZone:27");
        Assertions.assertEquals("Kviteseid", fareZone.getName().getValue());
    }

    @Test
    public void testGetParkingsByParentSiteRef() {
        var parkings = index.getParkingsByParentSiteRefIndex().get("NSR:StopPlace:337");
        Assertions.assertFalse(parkings.isEmpty());
    }
}
