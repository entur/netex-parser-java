package org.entur.netex.loader.parser;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.JAXBElement;

import org.entur.netex.index.api.NetexEntitiesIndex;
import org.rutebanken.netex.model.Block;
import org.rutebanken.netex.model.Block_VersionStructure;
import org.rutebanken.netex.model.BlocksInFrame_RelStructure;
import org.rutebanken.netex.model.DataManagedObjectStructure;
import org.rutebanken.netex.model.TypesOfValueInFrame_RelStructure;
import org.rutebanken.netex.model.VehicleSchedule_VersionFrameStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class VehicleScheduleFrameParser extends NetexParser<VehicleSchedule_VersionFrameStructure> {
    private static final Logger LOG = LoggerFactory.getLogger(VehicleScheduleFrameParser.class);

    private final Collection<Block> blocks = new ArrayList<>();

    @Override
    void parse(VehicleSchedule_VersionFrameStructure frame) {
        parseBlocks(frame.getBlocks());
    }

    private void parseBlocks(BlocksInFrame_RelStructure elements) {
        for (DataManagedObjectStructure e : elements.getBlockOrCompoundBlockOrTrainBlock()) {
            parseBlock((Block_VersionStructure) e);
        }
    }

    @Override
    void setResultOnIndex(NetexEntitiesIndex netexIndex) {
        netexIndex.getBlockIndex().putAll(blocks);
    }


    /* private methods */

    private void parseBlock(Block_VersionStructure element) {
        if (element instanceof Block) {
            blocks.add((Block) element);
        } else {
            informOnElementIntentionallySkipped(LOG, element);
        }
    }

    private void parseTypeOfValues(TypesOfValueInFrame_RelStructure typesOfValue) {
        if (typesOfValue != null) {
            for (JAXBElement<?> e : typesOfValue.getValueSetOrTypeOfValue()) {
                if (e.getValue() instanceof Block) {
                    blocks.add((Block) e.getValue());
                } else {
                    informOnElementIntentionallySkipped(LOG, e);
                }
            }
        }
    }
}

