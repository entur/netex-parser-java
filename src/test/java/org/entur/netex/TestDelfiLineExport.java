package org.entur.netex;

import java.util.Collection;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.DatedServiceJourney;
import org.rutebanken.netex.model.ServiceJourney;
import org.rutebanken.netex.model.ServiceJourneyInterchange;
import org.rutebanken.netex.model.ServiceJourneyPattern;

class TestDelfiLineExport {

  private static NetexEntitiesIndex index;

  @BeforeAll
  static void init() {
    try {
      NetexParser parser = new NetexParser();
      index = parser.parse("src/test/resources/delfi_line_file.zip");
    } catch (Exception e) {
      Assertions.fail(e.getMessage(), e);
    }
  }

  @Test
  void testGetDatedServiceJourneysByServiceJourneyRef() {
    ServiceJourneyPattern serviceJourneyPattern = index
      .getServiceJourneyPatternIndex()
      .get("DE::ServiceJourneyPattern:267263690_0::");
    Assertions.assertNotNull(serviceJourneyPattern);
  }
}
