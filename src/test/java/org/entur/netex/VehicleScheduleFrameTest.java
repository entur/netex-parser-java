package org.entur.netex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import jakarta.xml.bind.JAXBElement;

import org.entur.netex.index.api.NetexEntitiesIndex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.Block;
import org.rutebanken.netex.model.DayTypeRefStructure;
import org.rutebanken.netex.model.JourneyRefs_RelStructure;
import org.rutebanken.netex.model.VehicleScheduleFrame;

class VehicleScheduleFrameTest {
    private static NetexEntitiesIndex index;

    @BeforeAll
    static void init() {
        try {
            NetexParser parser = new NetexParser();
            index = parser.parse("src/test/resources/data.zip");
        } catch (Exception e) {
            Assertions.fail(e.getMessage(), e);
        }
    }

    @Test
    void testBlocks() {
        Collection<Block> blocks = index.getBlockIndex().getAll();
        Assertions.assertEquals(2, blocks.size());
    }

    @Test
    void testJourneys() {
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
        Assertions.assertEquals(5, journeysSize);
    }

    @Test
    void testDayTypes() {
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
        Assertions.assertEquals(2, dayTypesSize);
    }

    @Test
    void testGetBlock() {
        Block block = index.getBlockIndex().get("AAA:BBB:666");
        Assertions.assertNotNull(block);
    }

    @Test
    void testGetJourneys() {
        Block block = index.getBlockIndex().get("AAA:BBB:666");
        JourneyRefs_RelStructure result = block.getJourneys();
        List<JAXBElement<?>> journeys = result.getJourneyRefOrJourneyDesignatorOrServiceDesignator();
        Assertions.assertEquals(2, journeys.size());
    }

    @Test
    void testVehicleScheduleFrames() {
        Collection<VehicleScheduleFrame> vehicleScheduleFrame = index.getVehicleScheduleFrames();
        Assertions.assertEquals(1, vehicleScheduleFrame.size());
    }
}
