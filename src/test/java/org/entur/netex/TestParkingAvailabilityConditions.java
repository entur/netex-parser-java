package org.entur.netex;

import java.io.File;
import java.nio.file.Files;
import java.util.Collection;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.AvailabilityCondition;

class TestParkingAvailabilityConditions {

  private static NetexEntitiesIndex index;

  @BeforeAll
  static void init() {
    try {
      NetexParser parser = new NetexParser();
      File file = new File(
        "src/test/resources/ParkingWithAvailabilityConditions.xml"
      );
      index = parser.parse(Files.newInputStream(file.toPath()));
    } catch (Exception e) {
      Assertions.fail(e.getMessage(), e);
    }
  }

  @Test
  void parkingWithAvailabilityConditionsIsIndexedByParkingId() {
    Collection<AvailabilityCondition> conditions = index
      .getAvailabilityConditionsByParkingIdIndex()
      .get("FSR:Parking:1");
    Assertions.assertEquals(2, conditions.size());
  }

  @Test
  void availabilityConditionOpenDayHasCorrectFields() {
    AvailabilityCondition openCondition = index
      .getAvailabilityConditionsByParkingIdIndex()
      .get("FSR:Parking:1")
      .stream()
      .filter(ac -> "FSR:AvailabilityCondition:1".equals(ac.getId()))
      .findFirst()
      .orElseThrow();

    Assertions.assertTrue(openCondition.isIsAvailable());
    Assertions.assertNotNull(openCondition.getDayTypes());
    Assertions.assertFalse(
      openCondition.getDayTypes().getDayTypeRefOrDayType_().isEmpty()
    );
  }

  @Test
  void availabilityConditionClosedDayHasIsAvailableFalse() {
    AvailabilityCondition closedCondition = index
      .getAvailabilityConditionsByParkingIdIndex()
      .get("FSR:Parking:1")
      .stream()
      .filter(ac -> "FSR:AvailabilityCondition:2".equals(ac.getId()))
      .findFirst()
      .orElseThrow();

    Assertions.assertFalse(closedCondition.isIsAvailable());
  }

  @Test
  void parkingWithoutAvailabilityConditionsReturnsEmptyCollection() {
    Collection<AvailabilityCondition> conditions = index
      .getAvailabilityConditionsByParkingIdIndex()
      .get("FSR:Parking:2");
    Assertions.assertTrue(conditions.isEmpty());
  }
}
