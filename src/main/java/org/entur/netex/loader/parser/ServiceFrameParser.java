package org.entur.netex.loader.parser;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.entur.netex.index.api.NetexEntityIndex;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.rutebanken.netex.model.DestinationDisplay;
import org.rutebanken.netex.model.DestinationDisplaysInFrame_RelStructure;
import org.rutebanken.netex.model.FlexibleLine;
import org.rutebanken.netex.model.FlexibleStopAssignment;
import org.rutebanken.netex.model.FlexibleStopPlace;
import org.rutebanken.netex.model.GroupOfLines;
import org.rutebanken.netex.model.GroupsOfLinesInFrame_RelStructure;
import org.rutebanken.netex.model.JourneyPattern;
import org.rutebanken.netex.model.ServiceJourneyPattern;
import org.rutebanken.netex.model.JourneyPatternsInFrame_RelStructure;
import org.rutebanken.netex.model.Line;
import org.rutebanken.netex.model.LinesInFrame_RelStructure;
import org.rutebanken.netex.model.Network;
import org.rutebanken.netex.model.NetworksInFrame_RelStructure;
import org.rutebanken.netex.model.PassengerStopAssignment;
import org.rutebanken.netex.model.Route;
import org.rutebanken.netex.model.RoutePoint;
import org.rutebanken.netex.model.RoutePointsInFrame_RelStructure;
import org.rutebanken.netex.model.RoutesInFrame_RelStructure;
import org.rutebanken.netex.model.ScheduledStopPoint;
import org.rutebanken.netex.model.ScheduledStopPointsInFrame_RelStructure;
import org.rutebanken.netex.model.ServiceLink;
import org.rutebanken.netex.model.ServiceLinksInFrame_RelStructure;
import org.rutebanken.netex.model.Service_VersionFrameStructure;
import org.rutebanken.netex.model.StopAssignmentsInFrame_RelStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class ServiceFrameParser extends NetexParser<Service_VersionFrameStructure> {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceFrameParser.class);

    private final NetexEntityIndex<FlexibleStopPlace> flexibleStopPlaceById;

    private final Collection<Network> networks = new ArrayList<>();

    private final Collection<GroupOfLines> groupOfLines = new ArrayList<>();

    private final Collection<Route> routes = new ArrayList<>();

    private final Collection<FlexibleLine> flexibleLines = new ArrayList<>();

    private final Collection<Line> lines = new ArrayList<>();

    private final Map<String, String> networkIdByGroupOfLineId = new HashMap<>();

    private final Collection<JourneyPattern> journeyPatterns = new ArrayList<>();

    private final Collection<ServiceJourneyPattern> serviceJourneyPatterns = new ArrayList<>();

    private final Collection<DestinationDisplay> destinationDisplays = new ArrayList<>();

    private final Map<String, String> quayIdByStopPointRef = new HashMap<>();

    private final Map<String, String> stopPlaceIdByStopPointRef = new HashMap<>();

    private final Map<String, String> flexibleStopPlaceByStopPointRef = new HashMap<>();

    private final Collection<ServiceLink> serviceLinks = new ArrayList<>();

    private final Collection<ScheduledStopPoint> scheduledStopPoints = new ArrayList<>();

    private final Collection<RoutePoint> routePoints = new ArrayList<>();

    private final Multimap<String, PassengerStopAssignment> passengerStopAssignmentByStopPointRef = ArrayListMultimap.create();

    private final NoticeParser noticeParser = new NoticeParser();

    ServiceFrameParser(NetexEntityIndex<FlexibleStopPlace> flexibleStopPlaceById) {
        this.flexibleStopPlaceById = flexibleStopPlaceById;
    }

    @Override
    void parse(Service_VersionFrameStructure frame) {
        parseStopAssignments(frame.getStopAssignments());
        parseRoutes(frame.getRoutes());
        parseNetwork(frame.getNetwork());
        parseAdditionalNetworks(frame.getAdditionalNetworks());
        noticeParser.parseNotices(frame.getNotices());
        noticeParser.parseNoticeAssignments(frame.getNoticeAssignments());
        parseLines(frame.getLines());
        parseJourneyPatterns(frame.getJourneyPatterns());
        parseDestinationDisplays(frame.getDestinationDisplays());
        parseServiceLinks(frame.getServiceLinks());
        parseScheduledStopPoints(frame.getScheduledStopPoints());
        parseRoutePoints(frame.getRoutePoints());

        // Keep list sorted alphabetically
        informOnElementIntentionallySkipped(LOG, frame.getCommonSections());
        informOnElementIntentionallySkipped(LOG, frame.getConnections());
        informOnElementIntentionallySkipped(LOG, frame.getDirections());
        informOnElementIntentionallySkipped(LOG, frame.getDisplayAssignments());
        informOnElementIntentionallySkipped(LOG, frame.getFlexibleLinkProperties());
        informOnElementIntentionallySkipped(LOG, frame.getFlexiblePointProperties());
        informOnElementIntentionallySkipped(LOG, frame.getGeneralSections());
        informOnElementIntentionallySkipped(LOG, frame.getGroupsOfLines());
        informOnElementIntentionallySkipped(LOG, frame.getGroupsOfLinks());
        informOnElementIntentionallySkipped(LOG, frame.getGroupsOfPoints());
        informOnElementIntentionallySkipped(LOG, frame.getLineNetworks());
        informOnElementIntentionallySkipped(LOG, frame.getLogicalDisplays());
        informOnElementIntentionallySkipped(LOG, frame.getPassengerInformationEquipments());
        informOnElementIntentionallySkipped(LOG, frame.getRouteLinks());
        informOnElementIntentionallySkipped(LOG, frame.getRoutingConstraintZones());
        informOnElementIntentionallySkipped(LOG, frame.getServiceExclusions());
        informOnElementIntentionallySkipped(LOG, frame.getServicePatterns());
        informOnElementIntentionallySkipped(LOG, frame.getStopAreas());
        informOnElementIntentionallySkipped(LOG, frame.getTariffZones());
        informOnElementIntentionallySkipped(LOG, frame.getTimeDemandTypes());
        informOnElementIntentionallySkipped(LOG, frame.getTimeDemandTypeAssignments());
        informOnElementIntentionallySkipped(LOG, frame.getTimingPoints());
        informOnElementIntentionallySkipped(LOG, frame.getTimingLinks());
        informOnElementIntentionallySkipped(LOG, frame.getTimingLinkGroups());
        informOnElementIntentionallySkipped(LOG, frame.getTimingPatterns());
        informOnElementIntentionallySkipped(LOG, frame.getTransferRestrictions());

        verifyCommonUnusedPropertiesIsNotSet(LOG, frame);
    }

    @Override
    void setResultOnIndex(NetexEntitiesIndex index) {
        // update entities
        index.getDestinationDisplayIndex().putAll(destinationDisplays);
        index.getGroupOfLinesIndex().putAll(groupOfLines);
        index.getJourneyPatternIndex().putAll(journeyPatterns);
        index.getServiceJourneyPatternIndex().putAll(serviceJourneyPatterns);
        index.getFlexibleLineIndex().putAll(flexibleLines);
        index.getLineIndex().putAll(lines);
        index.getNetworkIndex().putAll(networks);
        noticeParser.setResultOnIndex(index);
        index.getQuayIdByStopPointRefIndex().putAll(quayIdByStopPointRef);
        index.getStopPlaceIdByStopPointRefIndex().putAll(stopPlaceIdByStopPointRef);
        index.getFlexibleStopPlaceIdByStopPointRefIndex().putAll(flexibleStopPlaceByStopPointRef);
        index.getRouteIndex().putAll(routes);
        index.getServiceLinkIndex().putAll(serviceLinks);
        index.getScheduledStopPointIndex().putAll(scheduledStopPoints);
        index.getRoutePointIndex().putAll(routePoints);
        index.getPassengerStopAssignmentsByStopPointRefIndex().putAll(passengerStopAssignmentByStopPointRef);

        // update references
        index.getNetworkIdByGroupOfLineIdIndex().putAll(networkIdByGroupOfLineId);
    }

    private void parseStopAssignments(StopAssignmentsInFrame_RelStructure stopAssignments) {
        if (stopAssignments == null) return;

        for (JAXBElement<?> stopAssignment : stopAssignments.getStopAssignment()) {
            if (stopAssignment.getValue() instanceof PassengerStopAssignment) {
                PassengerStopAssignment assignment = (PassengerStopAssignment) stopAssignment.getValue();

                String stopPointRef = assignment.getScheduledStopPointRef().getValue().getRef();

                passengerStopAssignmentByStopPointRef.put(stopPointRef, assignment);

                if (assignment.getQuayRef() != null) {
                    String quayRef = assignment.getQuayRef().getRef();
                    quayIdByStopPointRef.put(stopPointRef, quayRef);
                }

                if (assignment.getStopPlaceRef() != null) {
                    String stopPlaceRef = assignment.getStopPlaceRef().getRef();
                    stopPlaceIdByStopPointRef.put(stopPointRef, stopPlaceRef);
                }
            }
            else if (stopAssignment.getValue() instanceof FlexibleStopAssignment) {
                FlexibleStopAssignment assignment = (FlexibleStopAssignment) stopAssignment.getValue();
                String flexibleStopPlaceRef = assignment.getFlexibleStopPlaceRef().getRef();

                // TODO OTP2 - This check belongs to the mapping or as a separate validation
                //           - step. The problem is that we do not want to relay on the
                //           - the order in witch elements are loaded.
                FlexibleStopPlace flexibleStopPlace = flexibleStopPlaceById.get(
                    flexibleStopPlaceRef);

                if (flexibleStopPlace != null) {
                    String stopPointRef = assignment.getScheduledStopPointRef().getValue().getRef();
                    flexibleStopPlaceByStopPointRef.put(stopPointRef, flexibleStopPlace.getId());
                }
                else {
                    LOG.warn(
                        "FlexibleStopPlace {} not found in stop place file.",
                        flexibleStopPlaceRef
                    );
                }
            }
        }
    }

    private void parseRoutes(RoutesInFrame_RelStructure routes) {
        if (routes == null) return;

        for (JAXBElement<?> element : routes.getRoute_()) {
            if (element.getValue() instanceof Route) {
                Route route = (Route) element.getValue();
                this.routes.add(route);
            }
        }
    }

    private void parseNetwork(Network network) {
        if (network == null) return;

        networks.add(network);

        GroupsOfLinesInFrame_RelStructure groupsOfLines = network.getGroupsOfLines();

        if (groupsOfLines != null) {
            parseGroupOfLines(groupsOfLines.getGroupOfLines(), network);
        }
    }

    private void parseAdditionalNetworks(NetworksInFrame_RelStructure additionalNetworks) {
        if (additionalNetworks == null) { return; }

        for (Network additionalNetwork : additionalNetworks.getNetwork()) {
            parseNetwork(additionalNetwork);
        }
    }

    private void parseGroupOfLines(Collection<GroupOfLines> groupOfLines, Network network) {
        for (GroupOfLines group : groupOfLines) {
            networkIdByGroupOfLineId.put(group.getId(), network.getId());
            this.groupOfLines.add(group);
        }
    }

    private void parseLines(LinesInFrame_RelStructure lines) {
        if (lines == null) return;

        for (JAXBElement<?> element : lines.getLine_()) {
            if (element.getValue() instanceof Line) {
                this.lines.add((Line) element.getValue());
            } else if (element.getValue() instanceof FlexibleLine) {
                this.flexibleLines.add((FlexibleLine) element.getValue());
            }
            else {
                informOnElementIntentionallySkipped(LOG, element.getValue());
            }
        }
    }

    private void parseJourneyPatterns(JourneyPatternsInFrame_RelStructure journeyPatterns) {
        if (journeyPatterns == null) return;

        for (JAXBElement<?> pattern : journeyPatterns.getJourneyPattern_OrJourneyPatternView()) {
            if (pattern.getValue() instanceof JourneyPattern) {
                this.journeyPatterns.add((JourneyPattern) pattern.getValue());
            } else if (pattern.getValue() instanceof ServiceJourneyPattern){
                this.serviceJourneyPatterns.add((ServiceJourneyPattern)pattern.getValue());
            } else {
                informOnElementIntentionallySkipped(LOG, pattern.getValue());
            }
        }
    }

    private void parseDestinationDisplays(DestinationDisplaysInFrame_RelStructure destDisplays) {
        if (destDisplays == null) return;

        this.destinationDisplays.addAll(destDisplays.getDestinationDisplay());
    }

    private void parseServiceLinks(ServiceLinksInFrame_RelStructure serviceLinks) {
        if (serviceLinks == null) return;

        this.serviceLinks.addAll(serviceLinks.getServiceLink());
    }

    private void parseScheduledStopPoints(ScheduledStopPointsInFrame_RelStructure scheduledStopPoints) {
        if (scheduledStopPoints == null) return;

        this.scheduledStopPoints.addAll(scheduledStopPoints.getScheduledStopPoint());
    }

    private void parseRoutePoints(RoutePointsInFrame_RelStructure routePoints) {
        if (routePoints == null) return;

        this.routePoints.addAll(routePoints.getRoutePoint());
    }
}
