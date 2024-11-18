package org.entur.netex.loader.parser;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.rutebanken.netex.model.DatedServiceJourney;
import org.rutebanken.netex.model.DeadRun;
import org.rutebanken.netex.model.Interchange_VersionStructure;
import org.rutebanken.netex.model.JourneyInterchangesInFrame_RelStructure;
import org.rutebanken.netex.model.Journey_VersionStructure;
import org.rutebanken.netex.model.JourneysInFrame_RelStructure;
import org.rutebanken.netex.model.ServiceJourney;
import org.rutebanken.netex.model.ServiceJourneyInterchange;
import org.rutebanken.netex.model.ServiceJourneyRefStructure;
import org.rutebanken.netex.model.Timetable_VersionFrameStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

class TimeTableFrameParser extends NetexParser<Timetable_VersionFrameStructure> {

    private static final Logger LOG = LoggerFactory.getLogger(TimeTableFrameParser.class);

    private final List<ServiceJourney> serviceJourneys = new ArrayList<>();

    private final List<DatedServiceJourney> datedServiceJourneys = new ArrayList<>();
    private final Multimap<String, DatedServiceJourney> datedServiceJourneyByServiceJourneyId = ArrayListMultimap.create();

    private final List<DeadRun> deadRuns = new ArrayList<>();

    private final List<ServiceJourneyInterchange> serviceJourneyInterchanges = new ArrayList<>();
    private final Multimap<String, ServiceJourneyInterchange> serviceJourneyInterchangesByServiceJourneyId = ArrayListMultimap.create();

    private final NoticeParser noticeParser = new NoticeParser();


    @Override
    void parse(Timetable_VersionFrameStructure frame) {
        parseJourneys(frame.getVehicleJourneys());
        parseInterchanges(frame.getJourneyInterchanges());

        noticeParser.parseNotices(frame.getNotices());
        noticeParser.parseNoticeAssignments(frame.getNoticeAssignments());

        informOnElementIntentionallySkipped(LOG, frame.getNetworkView());
        informOnElementIntentionallySkipped(LOG, frame.getLineView());
        informOnElementIntentionallySkipped(LOG, frame.getOperatorView());
        informOnElementIntentionallySkipped(LOG, frame.getAccessibilityAssessment());

        // Keep list sorted alphabetically
        informOnElementIntentionallySkipped(LOG, frame.getBookingTimes());
        informOnElementIntentionallySkipped(LOG, frame.getCoupledJourneys());
        informOnElementIntentionallySkipped(LOG, frame.getDefaultInterchanges());
        informOnElementIntentionallySkipped(LOG, frame.getFlexibleServiceProperties());
        informOnElementIntentionallySkipped(LOG, frame.getFrequencyGroups());
        informOnElementIntentionallySkipped(LOG, frame.getGroupsOfServices());
        informOnElementIntentionallySkipped(LOG, frame.getInterchangeRules());
        informOnElementIntentionallySkipped(LOG, frame.getJourneyAccountingRef());
        informOnElementIntentionallySkipped(LOG, frame.getJourneyAccountings());
        informOnElementIntentionallySkipped(LOG, frame.getJourneyMeetings());
        informOnElementIntentionallySkipped(LOG, frame.getJourneyPartCouples());
        informOnElementIntentionallySkipped(LOG, frame.getServiceCalendarFrameRef());
        informOnElementIntentionallySkipped(LOG, frame.getServiceFacilitySets());
        informOnElementIntentionallySkipped(LOG, frame.getTimeDemandTypes());
        informOnElementIntentionallySkipped(LOG, frame.getTimeDemandTypeAssignments());
        informOnElementIntentionallySkipped(LOG, frame.getTimingLinkGroups());
        informOnElementIntentionallySkipped(LOG, frame.getTrainNumbers());
        informOnElementIntentionallySkipped(LOG, frame.getTypesOfService());
        informOnElementIntentionallySkipped(LOG, frame.getVehicleTypes());

        verifyCommonUnusedPropertiesIsNotSet(LOG, frame);
    }

    @Override
    void setResultOnIndex(NetexEntitiesIndex netexIndex) {
        netexIndex.getServiceJourneyIndex().putAll(serviceJourneys);

        netexIndex.getDatedServiceJourneyIndex().putAll(datedServiceJourneys);
        netexIndex.getDatedServiceJourneyByServiceJourneyRefIndex().putAll(datedServiceJourneyByServiceJourneyId);

        netexIndex.getDeadRunIndex().putAll(deadRuns);

        netexIndex.getServiceJourneyInterchangeIndex().putAll(serviceJourneyInterchanges);
        netexIndex.getServiceJourneyInterchangeByServiceJourneyRefIndex().putAll(serviceJourneyInterchangesByServiceJourneyId);

        noticeParser.setResultOnIndex(netexIndex);
    }

    private void parseJourneys(JourneysInFrame_RelStructure element) {
        for (Journey_VersionStructure it : element.getVehicleJourneyOrDatedVehicleJourneyOrNormalDatedVehicleJourney()) {
            if (it instanceof ServiceJourney serviceJourney) {
                serviceJourneys.add(serviceJourney);
            } else if (it instanceof DatedServiceJourney datedServiceJourney) {
                datedServiceJourneys.add(datedServiceJourney);
                datedServiceJourney.getJourneyRef()
                        .stream()
                        .filter(journeyRef -> journeyRef.getValue() instanceof ServiceJourneyRefStructure)
                        .map(journeyRef -> journeyRef.getValue().getRef())
                        .forEach(serviceJourneyId -> datedServiceJourneyByServiceJourneyId.put(serviceJourneyId, datedServiceJourney));
            } else if (it instanceof DeadRun deadRun) {
                deadRuns.add(deadRun);
            } else {
                informOnElementIntentionallySkipped(LOG, it);
            }
        }
    }

    private void parseInterchanges(JourneyInterchangesInFrame_RelStructure journeyInterchangesElement) {
        if (journeyInterchangesElement == null) {
            return;
        }
        for (Interchange_VersionStructure it : journeyInterchangesElement.getServiceJourneyPatternInterchangeOrServiceJourneyInterchange()) {
            if (it instanceof ServiceJourneyInterchange serviceJourneyInterchange) {
                serviceJourneyInterchanges.add(serviceJourneyInterchange);

                String fromRef = serviceJourneyInterchange.getFromJourneyRef().getRef();
                serviceJourneyInterchangesByServiceJourneyId.put(fromRef, serviceJourneyInterchange);
                String toRef = serviceJourneyInterchange.getToJourneyRef().getRef();
                serviceJourneyInterchangesByServiceJourneyId.put(toRef, serviceJourneyInterchange);

            } else {
                informOnElementIntentionallySkipped(LOG, it);
            }
        }
    }
}
