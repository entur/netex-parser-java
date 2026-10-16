package org.entur.netex.loader.parser;

import java.util.ArrayList;
import java.util.Collection;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.entur.netex2.model.Block;
import org.entur.netex2.model.Block_VersionStructure;
import org.entur.netex2.model.BlocksInFrame_RelStructure;
import org.entur.netex2.model.DataManagedObjectStructure;
import org.entur.netex2.model.VehicleSchedule_VersionFrameStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class VehicleScheduleFrameParser
  extends NetexParser<VehicleSchedule_VersionFrameStructure> {

  private static final Logger LOG = LoggerFactory.getLogger(
    VehicleScheduleFrameParser.class
  );

  private final Collection<Block> blocks = new ArrayList<>();

  @Override
  void parse(VehicleSchedule_VersionFrameStructure frame) {
    parseBlocks(frame.getBlocks());
  }

  @Override
  void setResultOnIndex(NetexEntitiesIndex netexIndex) {
    netexIndex.getBlockIndex().putAll(blocks);
  }

  private void parseBlock(Block_VersionStructure element) {
    if (element instanceof Block block) {
      blocks.add(block);
    } else {
      informOnElementIntentionallySkipped(LOG, element);
    }
  }

  private void parseBlocks(BlocksInFrame_RelStructure elements) {
    for (DataManagedObjectStructure e : elements.getBlockOrCompoundBlockOrTrainBlock()) {
      parseBlock((Block_VersionStructure) e);
    }
  }
}
