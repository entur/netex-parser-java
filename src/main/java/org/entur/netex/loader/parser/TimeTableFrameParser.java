package org.entur.netex.loader.parser;

import org.entur.netex.index.api.NetexEntitiesIndex;
import org.rutebanken.netex.model.DatedServiceJourney;
import org.rutebanken.netex.model.Journey_VersionStructure;
import org.rutebanken.netex.model.JourneysInFrame_RelStructure;
import org.rutebanken.netex.model.ServiceJourney;
import org.rutebanken.netex.model.Timetable_VersionFrameStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

class TimeTableFrameParser extends NetexParser<Timetable_VersionFrameStructure> {

    private static final Logger LOG = LoggerFactory.getLogger(TimeTableFrameParser.class);

    private final List<ServiceJourney> serviceJourneys = new ArrayList<>();
    private final List<DatedServiceJourney> datedServiceJourneys = new ArrayList<>();

    private final NoticeParser noticeParser = new NoticeParser();


    @Override
    void parse(Timetable_VersionFrameStructure frame) {
        parseJourneys(frame.getVehicleJourneys());

        noticeParser.parseNotices(frame.getNotices());
        noticeParser.parseNoticeAssignments(frame.getNoticeAssignments());

        informOnElementIntentionallySkipped(LOG, frame.getNetworkView());
        informOnElementIntentionallySkipped(LOG, frame.getLineView());
        informOnElementIntentionallySkipped(LOG, frame.getOperatorView());
        informOnElementIntentionallySkipped(LOG, frame.getAccessibilityAssessment());

        // Keep list sorted alphabetically
        informOnElementIntentionallySkipped(LOG, frame.getBookingTimes());
        informOnElementIntentionallySkipped(LOG, frame.getVehicleTypeRef());
        informOnElementIntentionallySkipped(LOG, frame.getCoupledJourneys());
        informOnElementIntentionallySkipped(LOG, frame.getDefaultInterchanges());
        informOnElementIntentionallySkipped(LOG, frame.getFlexibleServiceProperties());
        informOnElementIntentionallySkipped(LOG, frame.getFrequencyGroups());
        informOnElementIntentionallySkipped(LOG, frame.getGroupsOfServices());
        informOnElementIntentionallySkipped(LOG, frame.getInterchangeRules());
        informOnElementIntentionallySkipped(LOG, frame.getJourneyAccountingRef());
        informOnElementIntentionallySkipped(LOG, frame.getJourneyAccountings());
        informOnElementIntentionallySkipped(LOG, frame.getJourneyInterchanges());
        informOnElementIntentionallySkipped(LOG, frame.getJourneyMeetings());
        informOnElementIntentionallySkipped(LOG, frame.getJourneyPartCouples());
        informOnElementIntentionallySkipped(LOG, frame.getNotices());
        informOnElementIntentionallySkipped(LOG, frame.getNoticeAssignments());
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
        noticeParser.setResultOnIndex(netexIndex);
    }

    private void parseJourneys(JourneysInFrame_RelStructure element) {
        for (Journey_VersionStructure it : element.getVehicleJourneyOrDatedVehicleJourneyOrNormalDatedVehicleJourney()) {
            if (it instanceof ServiceJourney) {
                serviceJourneys.add((ServiceJourney)it);
            }
            else if(it instanceof DatedServiceJourney) {
                datedServiceJourneys.add((DatedServiceJourney) it);
            }
            else {
                informOnElementIntentionallySkipped(LOG, it);
            }
        }
    }
}
