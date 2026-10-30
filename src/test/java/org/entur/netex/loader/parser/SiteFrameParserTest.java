package org.entur.netex.loader.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.entur.netex.index.impl.NetexEntitiesIndexImpl;
import org.entur.netex2.model.*;
import org.junit.jupiter.api.Test;

class SiteFrameParserTest {

  private static final ObjectFactory FACTORY = new ObjectFactory();

  @Test
  void parkingWithoutParentSiteRefIsIndexed() {
    Parking withoutParent = new Parking()
      .withId("NSR:Parking:1")
      .withVersion("1");
    Parking withParent = new Parking()
      .withId("NSR:Parking:2")
      .withVersion("1")
      .withParentSiteRef(new SiteRefStructure().withRef("NSR:StopPlace:1"));

    SiteFrameParser parser = new SiteFrameParser();
    parser.parse(
      new SiteFrame()
        .withId("NSR:SiteFrame:1")
        .withParkings(
          new ParkingsInFrame_RelStructure()
            .withParking(
              FACTORY.createParking(withoutParent),
              FACTORY.createParking(withParent)
            )
        )
    );
    NetexEntitiesIndexImpl index = new NetexEntitiesIndexImpl();
    parser.setResultOnIndex(index);

    assertNotNull(index.getParkingIndex().getLatestVersion("NSR:Parking:1"));
    assertNotNull(index.getParkingIndex().getLatestVersion("NSR:Parking:2"));
    assertEquals(1, index.getParkingsByParentSiteRefIndex().size());
    assertEquals(
      withParent,
      index
        .getParkingsByParentSiteRefIndex()
        .get("NSR:StopPlace:1")
        .iterator()
        .next()
    );
  }
}
