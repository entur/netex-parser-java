package org.entur.netex.support;

import java.util.ArrayList;
import java.util.Collection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.EntityInVersionStructure;

class NetexVersionHelperTest {

  @Test
  void testVersionOf() {
    EntityInVersionStructure entityInVersionStructure1 =
      new EntityInVersionStructure();
    entityInVersionStructure1.setVersion("20250101000000");
    EntityInVersionStructure entityInVersionStructure2 =
      new EntityInVersionStructure();
    entityInVersionStructure2.setVersion("20250101000001");

    Collection<EntityInVersionStructure> entitiesInVersionStructure =
      new ArrayList<>();
    entitiesInVersionStructure.add(entityInVersionStructure1);
    entitiesInVersionStructure.add(entityInVersionStructure2);

    Assertions.assertEquals(
      entityInVersionStructure2,
      NetexVersionHelper.latestVersionedElementIn(entitiesInVersionStructure)
    );
  }
}
