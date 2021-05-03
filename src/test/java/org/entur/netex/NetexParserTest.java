package org.entur.netex;

import org.entur.netex.index.api.NetexEntityIndexReadOnlyView;
import org.junit.Assert;
import org.junit.Test;

public class NetexParserTest {

    @Test
    public void testStopPlaces() {
        var parser = new NetexParser();
        NetexEntityIndexReadOnlyView index = null;

        try {
            index = parser.parseFromZip("src/test/resources/Current_latest.zip");
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        var stopPlace = index.getStopPlaceById().lookupLastVersionById("NSR:StopPlace:59872");
        Assert.assertEquals("Oslo S", stopPlace.getName().getValue());

        var groupOfStopPlaces = index.getGroupOfStopPlacesById().lookup("NSR:GroupOfStopPlaces:1");
        Assert.assertEquals("Oslo", groupOfStopPlaces.getName().getValue());

        var tariffZone = index.getTariffZonesById().lookup("MOR:TariffZone:108");
        Assert.assertEquals("Standal", tariffZone.getName().getValue());

        var topographicPlace = index.getTopographicPlaceById().lookup("KVE:TopographicPlace:50");
        Assert.assertEquals("Trøndelag", topographicPlace.getDescriptor().getName().getValue());
        Assert.assertEquals("no", topographicPlace.getCountryRef().getRef().value());

        var parking = index.getParkingById().lookup("NSR:Parking:1");
        Assert.assertEquals("Drammen", parking.getName().getValue());
    }
}
