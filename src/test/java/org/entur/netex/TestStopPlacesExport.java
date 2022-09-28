package org.entur.netex;

import org.entur.netex.index.api.NetexEntitiesIndex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.FareZone;
import org.rutebanken.netex.model.GroupOfStopPlaces;
import org.rutebanken.netex.model.Parking;
import org.rutebanken.netex.model.PassengerStopAssignment;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.ScheduledStopPoint;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.TariffZone;
import org.rutebanken.netex.model.TopographicPlace;

import java.time.LocalDateTime;
import java.util.Collection;

class TestStopPlacesExport {
    private static NetexEntitiesIndex index;

    @BeforeAll
    static void init() {
        try {
            NetexParser parser = new NetexParser();
            index = parser.parse("src/test/resources/CurrentwithServiceFrame_latest.zip");
        } catch (Exception e) {
            Assertions.fail(e.getMessage(), e);
        }
    }

    @Test
    void testPublicationTimestamp() {
        Assertions.assertEquals(LocalDateTime.parse("2021-05-04T03:24:52.46"), index.getPublicationTimestamp());
    }

    @Test
    void testGetStopPlace() {
        StopPlace stopPlace = index.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:337");
        Assertions.assertEquals("Oslo S", stopPlace.getName().getValue());
        Assertions.assertEquals("NSR:StopPlace:59872", stopPlace.getParentSiteRef().getRef());
    }

    @Test
    void testQuay() {
        Quay quay = index.getQuayIndex().getLatestVersion("NSR:Quay:3691");
        Assertions.assertEquals("01", quay.getPrivateCode().getValue());
    }

    @Test
    void getStopPlaceIdByQuayId() {
        String stopPlaceId = index.getStopPlaceIdByQuayIdIndex().get("NSR:Quay:3691");
        Assertions.assertEquals("NSR:StopPlace:2133", stopPlaceId);
    }

    @Test
    void testGetGroupOfStopPlaces() {
        GroupOfStopPlaces groupOfStopPlaces = index.getGroupOfStopPlacesIndex().get("NSR:GroupOfStopPlaces:1");
        Assertions.assertEquals("Oslo", groupOfStopPlaces.getName().getValue());
    }

    @Test
    void testGetTariffZone() {
        TariffZone tariffZone = index.getTariffZoneIndex().getLatestVersion("MOR:TariffZone:108");
        Assertions.assertEquals("Standal", tariffZone.getName().getValue());
    }

    @Test
    void testGetTopographicPlace() {
        TopographicPlace topographicPlace = index.getTopographicPlaceIndex().getLatestVersion("KVE:TopographicPlace:50");
        Assertions.assertEquals("Trøndelag", topographicPlace.getDescriptor().getName().getValue());
        Assertions.assertEquals("no", topographicPlace.getCountryRef().getRef().value());
    }

    @Test
    void testGetParking() {
        Parking parking = index.getParkingIndex().getLatestVersion("NSR:Parking:1");
        Assertions.assertEquals("Drammen", parking.getName().getValue());
    }

    @Test
    void testGetScheduledStopPoint() {
        ScheduledStopPoint scheduledStopPoint = index.getScheduledStopPointIndex().getLatestVersion("NSR:ScheduledStopPoint:S5");
        Assertions.assertEquals("Gudå", scheduledStopPoint.getName().getValue());
    }

    @Test
    void testGetPassengerStopAssignment() {
        ScheduledStopPoint scheduledStopPoint = index.getScheduledStopPointIndex().getLatestVersion("NSR:ScheduledStopPoint:S5");
        Collection<PassengerStopAssignment> passengerStopAssignments = index.getPassengerStopAssignmentsByStopPointRefIndex().get(scheduledStopPoint.getId());

        Assertions.assertEquals(1, passengerStopAssignments.size());
    }

    @Test
    void testGetFareZone() {
        FareZone fareZone = index.getFareZoneIndex().getLatestVersion("AKT:FareZone:27");
        Assertions.assertEquals("Kviteseid", fareZone.getName().getValue());
    }

    @Test
    void testGetParkingsByParentSiteRef() {
        Collection<Parking> parkings = index.getParkingsByParentSiteRefIndex().get("NSR:StopPlace:337");
        Assertions.assertFalse(parkings.isEmpty());
    }
}
