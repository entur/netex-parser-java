package org.entur.netex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.entur.netex.index.api.NetexEntitiesIndex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.Block;
import org.rutebanken.netex.model.DayTypeRefStructure;
import org.rutebanken.netex.model.JourneyRefs_RelStructure;
import org.rutebanken.netex.model.VehicleScheduleFrame;

public class VehicleScheduleFrameTest {
    private static NetexEntitiesIndex index;

    @BeforeAll
    static void init() {
        try {
            NetexParser parser = new NetexParser();
            index = parser.parse("src/test/resources/chouette_netex-with-blocks_rb_atb-aggregated-netex.zip");
        } catch (Exception e) {
            Assertions.fail(e.getMessage(), e);
        }
    }

    @Test
    void testAllGetBlock() {
        Collection<Block> blocks = index.getBlockIndex().getAll();

        List<Block> mappedBlocks = new ArrayList<>();
        for (Block block : blocks) {
            List<String> dayTypes = new ArrayList<>();
            if (block.getDayTypes() != null && block.getDayTypes().getDayTypeRef() != null) {
                for (JAXBElement<? extends DayTypeRefStructure> dayTypeRef : block.getDayTypes().getDayTypeRef()) {
                    dayTypes.add(dayTypeRef.getValue().getValue());
                }
            }

            List<String> journeys = new ArrayList<>();
            if (block.getJourneys() != null && block.getJourneys().getJourneyRefOrJourneyDesignatorOrServiceDesignator() != null) {
                for (JAXBElement<?> journeyElement : block.getJourneys().getJourneyRefOrJourneyDesignatorOrServiceDesignator()) {
                    journeys.add(journeyElement.getValue().toString());
                }
            }
            String privateCode = block.getPrivateCode() != null ? block.getPrivateCode().getValue() : "";
        }
        Assertions.assertEquals(70, blocks.size());
    }

    @Test
    void testAllJourneys() {
        Collection<Block> blocks = index.getBlockIndex().getAll();
        int journeysSize = 0;

        for (Block block : blocks) {
            List<String> journeys = new ArrayList<>();
            if (block.getJourneys() != null && block.getJourneys().getJourneyRefOrJourneyDesignatorOrServiceDesignator() != null) {
                for (JAXBElement<?> journeyElement : block.getJourneys().getJourneyRefOrJourneyDesignatorOrServiceDesignator()) {
                    journeys.add(journeyElement.getValue().toString());
                }
            }
            journeysSize = journeysSize + journeys.size();
        }
        Assertions.assertEquals(497, journeysSize);
    }

    @Test
    void testAllDayTypes() {
        Collection<Block> blocks = index.getBlockIndex().getAll();
        int dayTypesSize = 0;

        for (Block block : blocks) {
            List<String> dayTypes = new ArrayList<>();
            if (block.getDayTypes() != null && block.getDayTypes().getDayTypeRef() != null) {
                for (JAXBElement<? extends DayTypeRefStructure> dayTypeRef : block.getDayTypes().getDayTypeRef()) {
                    dayTypes.add(dayTypeRef.getValue().getRef());
                }
                dayTypesSize = dayTypesSize + dayTypes.size();
            }
        }
        Assertions.assertEquals(70, dayTypesSize);
    }

    @Test
    void testGetBlock() {
        Block block = index.getBlockIndex().get("ATB:Block:1615_230227084816203");
        Assertions.assertNotNull(block);
    }

    @Test
    void testGetJourneys() {
        Block block = index.getBlockIndex().get("ATB:Block:1615_230227084816203");
        JourneyRefs_RelStructure result = block.getJourneys();
        List<JAXBElement<?>> journeys = result.getJourneyRefOrJourneyDesignatorOrServiceDesignator();
        Assertions.assertEquals(6, journeys.size());
    }

    @Test
    void testVehicleScheduleFrames() {
        Collection<VehicleScheduleFrame> vehicleScheduleFrame = index.getVehicleScheduleFrames();
        Assertions.assertEquals(1, vehicleScheduleFrame.size());
    }
}
