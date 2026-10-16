package org.entur.netex.loader.parser;

import java.util.ArrayList;
import java.util.Collection;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.entur.netex2.model.FareFrame_VersionFrameStructure;
import org.entur.netex2.model.FareZone;

public class FareFrameParser
  extends NetexParser<FareFrame_VersionFrameStructure> {

  private final Collection<FareZone> fareZones = new ArrayList<>();

  @Override
  void parse(FareFrame_VersionFrameStructure node) {
    if (node.getFareZones() != null) {
      parseFareZones(node.getFareZones().getFareZone());
    }
  }

  @Override
  void setResultOnIndex(NetexEntitiesIndex netexIndex) {
    netexIndex.getFareZoneIndex().putAll(fareZones);
  }

  private void parseFareZones(Collection<FareZone> fareZoneList) {
    fareZones.addAll(fareZoneList);
  }
}
