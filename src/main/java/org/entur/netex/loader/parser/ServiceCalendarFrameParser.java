package org.entur.netex.loader.parser;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.rutebanken.netex.model.DayType;
import org.rutebanken.netex.model.DayTypeAssignment;
import org.rutebanken.netex.model.DayTypeAssignmentsInFrame_RelStructure;
import org.rutebanken.netex.model.DayTypeAssignments_RelStructure;
import org.rutebanken.netex.model.DayTypesInFrame_RelStructure;
import org.rutebanken.netex.model.DayTypes_RelStructure;
import org.rutebanken.netex.model.OperatingDay;
import org.rutebanken.netex.model.OperatingDaysInFrame_RelStructure;
import org.rutebanken.netex.model.OperatingPeriod;
import org.rutebanken.netex.model.OperatingPeriod_VersionStructure;
import org.rutebanken.netex.model.OperatingPeriodsInFrame_RelStructure;
import org.rutebanken.netex.model.ServiceCalendar;
import org.rutebanken.netex.model.ServiceCalendarFrame_VersionFrameStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class ServiceCalendarFrameParser extends NetexParser<ServiceCalendarFrame_VersionFrameStructure> {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceCalendarFrameParser.class);

    private final Collection<DayType> dayTypes = new ArrayList<>();
    private final Collection<OperatingPeriod> operatingPeriods = new ArrayList<>();
    private final Collection<OperatingDay> operatingDays = new ArrayList<>();
    private final Multimap<String, DayTypeAssignment> dayTypeAssignmentByDayTypeId = ArrayListMultimap.create();


    @Override
    void parse(ServiceCalendarFrame_VersionFrameStructure frame) {
        parseServiceCalendar(frame.getServiceCalendar());
        parseDayTypes(frame.getDayTypes());
        parseOperatingPeriods(frame.getOperatingPeriods());
        parseOperatingDays(frame.getOperatingDays());
        parseDayTypeAssignments(frame.getDayTypeAssignments());

        // Keep list sorted alphabetically

        informOnElementIntentionallySkipped(LOG, frame.getTimebands());
        informOnElementIntentionallySkipped(LOG, frame.getGroupOfTimebands());

        verifyCommonUnusedPropertiesIsNotSet(LOG, frame);
    }

    @Override
    void setResultOnIndex(NetexEntitiesIndex netexIndex) {
        netexIndex.getDayTypeIndex().putAll(dayTypes);
        netexIndex.getOperatingPeriodIndex().putAll(operatingPeriods);
        netexIndex.getOperatingDayIndex().putAll(operatingDays);
        netexIndex.getDayTypeAssignmentsByDayTypeIdIndex().putAll(dayTypeAssignmentByDayTypeId);
    }

    private void parseServiceCalendar(ServiceCalendar serviceCalendar) {
        if(serviceCalendar == null) return;

        parseDayTypes(serviceCalendar.getDayTypes());
        // TODO OTP2 - What about OperatingPeriods here?
        parseDayTypeAssignments(serviceCalendar.getDayTypeAssignments());
    }

    //List<JAXBElement<? extends DataManagedObjectStructure>>
    private void parseDayTypes(DayTypesInFrame_RelStructure element) {
        if(element == null) return;
        for (JAXBElement<?> dt : element.getDayType_()) {
            parseDayType(dt);
        }
    }

    private void parseDayTypes(DayTypes_RelStructure dayTypes) {
        for (JAXBElement<?> dt : dayTypes.getDayTypeRefOrDayType_()) {
            parseDayType(dt);
        }
    }

    private void parseDayType(JAXBElement<?> dt) {
        if (dt.getValue() instanceof DayType) {
            dayTypes.add((DayType) dt.getValue());
        }
    }

    private void parseOperatingPeriods(OperatingPeriodsInFrame_RelStructure element) {
        if(element == null) { return; }

        for (OperatingPeriod_VersionStructure p : element.getOperatingPeriodOrUicOperatingPeriod()) {
            operatingPeriods.add((OperatingPeriod) p);
        }
    }

    private void parseOperatingDays(OperatingDaysInFrame_RelStructure element) {
        if(element == null) { return; }
        operatingDays.addAll(element.getOperatingDay());
    }

    private void parseDayTypeAssignments(DayTypeAssignments_RelStructure element) {
        if(element == null) { return; }
        parseDayTypeAssignments(element.getDayTypeAssignment());
    }

    private void parseDayTypeAssignments(DayTypeAssignmentsInFrame_RelStructure element) {
        if(element == null) { return; }
        parseDayTypeAssignments(element.getDayTypeAssignment());
    }

    private void parseDayTypeAssignments(List<DayTypeAssignment> elements) {
        for (DayTypeAssignment it : elements) {
            String ref = it.getDayTypeRef().getValue().getRef();
            dayTypeAssignmentByDayTypeId.put(ref, it);
        }
    }
}
