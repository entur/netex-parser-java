package org.entur.netex.index.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.entur.netex.index.api.NetexEntityIndex;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.entur.netex.index.api.VersionedNetexEntityIndex;
import org.rutebanken.netex.model.Authority;
import org.rutebanken.netex.model.DatedServiceJourney;
import org.rutebanken.netex.model.DayType;
import org.rutebanken.netex.model.DayTypeAssignment;
import org.rutebanken.netex.model.DestinationDisplay;
import org.rutebanken.netex.model.FareZone;
import org.rutebanken.netex.model.FlexibleLine;
import org.rutebanken.netex.model.FlexibleStopPlace;
import org.rutebanken.netex.model.GroupOfLines;
import org.rutebanken.netex.model.GroupOfStopPlaces;
import org.rutebanken.netex.model.JourneyPattern;
import org.rutebanken.netex.model.Line;
import org.rutebanken.netex.model.Network;
import org.rutebanken.netex.model.Notice;
import org.rutebanken.netex.model.NoticeAssignment;
import org.rutebanken.netex.model.OperatingDay;
import org.rutebanken.netex.model.OperatingPeriod;
import org.rutebanken.netex.model.Operator;
import org.rutebanken.netex.model.Parking;
import org.rutebanken.netex.model.PassengerStopAssignment;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.Route;
import org.rutebanken.netex.model.RoutePoint;
import org.rutebanken.netex.model.ScheduledStopPoint;
import org.rutebanken.netex.model.ServiceJourney;
import org.rutebanken.netex.model.ServiceJourneyInterchange;
import org.rutebanken.netex.model.ServiceLink;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.TariffZone;
import org.rutebanken.netex.model.TopographicPlace;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class NetexEntitiesIndexImpl implements NetexEntitiesIndex {

    // Indexes to entities
    public final NetexEntityIndex<Authority> authoritiesById;
    public final NetexEntityIndex<DatedServiceJourney> datedServiceJourneys;
    public final NetexEntityIndex<DayType> dayTypeById;
    public final Multimap<String, DayTypeAssignment> dayTypeAssignmentByDayTypeId;
    public final Multimap<String, PassengerStopAssignment> passengerStopAssignmentByStopPointRef;
    public final NetexEntityIndex<DestinationDisplay> destinationDisplayById;
    public final NetexEntityIndex<FlexibleStopPlace> flexibleStopPlaceById;
    public final NetexEntityIndex<GroupOfLines> groupOfLinesById;
    public final NetexEntityIndex<GroupOfStopPlaces> groupOfStopPlacesById;
    public final NetexEntityIndex<JourneyPattern> journeyPatternsById;
    public final NetexEntityIndex<FlexibleLine> flexibleLineByid;
    public final NetexEntityIndex<Line> lineById;
    public final NetexEntityIndex<Network> networkById;
    public final NetexEntityIndex<Notice> noticeById;
    public final NetexEntityIndex<NoticeAssignment> noticeAssignmentById;
    public final NetexEntityIndex<OperatingDay> operatingDayById;
    public final NetexEntityIndex<OperatingPeriod> operatingPeriodById;
    public final NetexEntityIndex<Operator> operatorsById;
    public final VersionedNetexEntityIndex<Quay> quayById;
    public final Map<String, String> flexibleStopPlaceByStopPointRef;
    public final Map<String, String> quayIdByStopPointRef;
    public final Map<String, String> stopPlaceIdByStopPointRef;
    public final Map<String, String> stopPlaceIdByQuayId;
    public final NetexEntityIndex<Route> routeById;
    public final NetexEntityIndex<ServiceJourney> serviceJourneyById;
    public final NetexEntityIndex<ServiceJourneyInterchange> serviceJourneyInterchangeById;
    public final Multimap<String, ServiceJourneyInterchange> serviceJourneyInterchangeByServiceJourneyRef;
    public final NetexEntityIndex<ServiceLink> serviceLinkById;
    public final VersionedNetexEntityIndex<StopPlace> stopPlaceById;
    public final NetexEntityIndex<TariffZone> tariffZonesById;
    public final NetexEntityIndex<TopographicPlace> topographicPlaceById;
    public final NetexEntityIndex<Parking> parkingById;
    public final NetexEntityIndex<ScheduledStopPoint> scheduledStopPointById;
    public final NetexEntityIndex<RoutePoint> routePointById;
    public final NetexEntityIndex<FareZone> fareZoneById;


    // Relations between entities - The Netex XML sometimes rely on the the
    // nested structure of the XML document, rater than explicit references.
    // Since we throw away the document we need to keep track of these.

    public final Map<String, String> networkIdByGroupOfLineId;

    private String timeZone;

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    /**
     * Create a root node.
     */
    public NetexEntitiesIndexImpl() {
        this.authoritiesById = new NetexEntityMapByIdImpl<>();
        this.dayTypeById = new NetexEntityMapByIdImpl<>();
        this.dayTypeAssignmentByDayTypeId = Multimaps.synchronizedListMultimap(ArrayListMultimap.create());
        this.passengerStopAssignmentByStopPointRef = Multimaps.synchronizedListMultimap(ArrayListMultimap.create());
        this.datedServiceJourneys = new NetexEntityMapByIdImpl<>();
        this.destinationDisplayById = new NetexEntityMapByIdImpl<>();
        this.flexibleStopPlaceById = new NetexEntityMapByIdImpl<>();
        this.groupOfLinesById = new NetexEntityMapByIdImpl<>();
        this.groupOfStopPlacesById = new NetexEntityMapByIdImpl<>();
        this.journeyPatternsById = new NetexEntityMapByIdImpl<>();
        this.flexibleLineByid = new NetexEntityMapByIdImpl<>();
        this.lineById = new NetexEntityMapByIdImpl<>();
        this.networkById = new NetexEntityMapByIdImpl<>();
        this.networkIdByGroupOfLineId = new ConcurrentHashMap<>();
        this.noticeById = new NetexEntityMapByIdImpl<>();
        this.noticeAssignmentById = new NetexEntityMapByIdImpl<>();
        this.operatingDayById = new NetexEntityMapByIdImpl<>();
        this.operatingPeriodById = new NetexEntityMapByIdImpl<>();
        this.operatorsById = new NetexEntityMapByIdImpl<>();
        this.quayById = new VersionedNetexEntityIndexImpl<>();
        this.flexibleStopPlaceByStopPointRef = new ConcurrentHashMap<>();
        this.quayIdByStopPointRef = new ConcurrentHashMap<>();
        this.stopPlaceIdByStopPointRef = new ConcurrentHashMap<>();
        this.stopPlaceIdByQuayId = new ConcurrentHashMap<>();
        this.routeById = new NetexEntityMapByIdImpl<>();
        this.serviceJourneyById = new NetexEntityMapByIdImpl<>();
        this.serviceJourneyInterchangeById= new NetexEntityMapByIdImpl<>();
        this.serviceJourneyInterchangeByServiceJourneyRef = Multimaps.synchronizedListMultimap(ArrayListMultimap.create());
        this.serviceLinkById = new NetexEntityMapByIdImpl<>();
        this.stopPlaceById = new VersionedNetexEntityIndexImpl<>();
        this.tariffZonesById = new NetexEntityMapByIdImpl<>();
        this.topographicPlaceById = new NetexEntityMapByIdImpl<>();
        this.parkingById = new NetexEntityMapByIdImpl<>();
        this.scheduledStopPointById = new NetexEntityMapByIdImpl<>();
        this.routePointById = new NetexEntityMapByIdImpl<>();
        this.fareZoneById = new NetexEntityMapByIdImpl<>();
    }

    @Override
    public NetexEntityIndex<GroupOfLines> getGroupOfLinesIndex() {
        return groupOfLinesById;
    }

    @Override
    public NetexEntityIndex<Network> getNetworkIndex() {
        return networkById;
    }

    @Override
    public Map<String, String> getNetworkIdByGroupOfLineIdIndex() {
        return networkIdByGroupOfLineId;
    }

    @Override
    public NetexEntityIndex<Authority> getAuthorityIndex() {
        return authoritiesById;
    }

    @Override
    public NetexEntityIndex<DayType> getDayTypeIndex() {
        return dayTypeById;
    }

    @Override
    public Multimap<String, DayTypeAssignment> getDayTypeAssignmentsByDayTypeIdIndex() {
        return dayTypeAssignmentByDayTypeId;
    }

    @Override
    public Multimap<String, PassengerStopAssignment> getPassengerStopAssignmentsByStopPointRefIndex() {
        return passengerStopAssignmentByStopPointRef;
    }

    @Override
    public NetexEntityIndex<DatedServiceJourney> getDatedServiceJourneyIndex() {
        return datedServiceJourneys;
    }

    @Override
    public NetexEntityIndex<DestinationDisplay> getDestinationDisplayIndex() {
        return destinationDisplayById;
    }

    @Override
    public NetexEntityIndex<FlexibleStopPlace> getFlexibleStopPlaceIndex() {
        return flexibleStopPlaceById;
    }

    @Override
    public NetexEntityIndex<GroupOfStopPlaces> getGroupOfStopPlacesIndex() {
        return groupOfStopPlacesById;
    }

    @Override
    public NetexEntityIndex<JourneyPattern> getJourneyPatternIndex() {
        return journeyPatternsById;
    }

    @Override
    public NetexEntityIndex<FlexibleLine> getFlexibleLineIndex() {
        return flexibleLineByid;
    }

    @Override
    public NetexEntityIndex<Line> getLineIndex() {
        return lineById;
    }

    @Override
    public NetexEntityIndex<Notice> getNoticeIndex() {
        return noticeById;
    }

    @Override
    public NetexEntityIndex<NoticeAssignment> getNoticeAssignmentIndex() {
        return noticeAssignmentById;
    }

    @Override
    public NetexEntityIndex<OperatingDay> getOperatingDayIndex() {
        return operatingDayById;
    }

    @Override
    public NetexEntityIndex<OperatingPeriod> getOperatingPeriodIndex() {
        return operatingPeriodById;
    }

    @Override
    public NetexEntityIndex<Operator> getOperatorIndex() {
        return operatorsById;
    }

    @Override
    public VersionedNetexEntityIndex<Quay> getQuayIndex() {
        return quayById;
    }

    @Override
    public Map<String, String> getFlexibleStopPlaceIdByStopPointRefIndex() {
        return flexibleStopPlaceByStopPointRef;
    }

    @Override
    public Map<String, String> getQuayIdByStopPointRefIndex() {
        return quayIdByStopPointRef;
    }

    @Override
    public Map<String, String> getStopPlaceIdByStopPointRefIndex() {
        return stopPlaceIdByStopPointRef;
    }

    @Override
    public Map<String, String> getStopPlaceIdByQuayIdIndex() {
        return stopPlaceIdByQuayId;
    }

    @Override
    public NetexEntityIndex<Route> getRouteIndex() {
        return routeById;
    }

    @Override
    public NetexEntityIndex<ServiceJourney> getServiceJourneyIndex() {
        return serviceJourneyById;
    }

    @Override
    public NetexEntityIndex<ServiceJourneyInterchange> getServiceJourneyInterchangeIndex() {
        return serviceJourneyInterchangeById;
    }

    @Override
    public Multimap<String, ServiceJourneyInterchange> getServiceJourneyInterchangeByServiceJourneyRefIndex() {
        return serviceJourneyInterchangeByServiceJourneyRef;
    }

    @Override
    public NetexEntityIndex<ServiceLink> getServiceLinkIndex() {
        return serviceLinkById;
    }

    @Override
    public VersionedNetexEntityIndex<StopPlace> getStopPlaceIndex() {
        return stopPlaceById;
    }

    @Override
    public NetexEntityIndex<TariffZone> getTariffZoneIndex() {
        return tariffZonesById;
    }

    @Override
    public NetexEntityIndex<TopographicPlace> getTopographicPlaceIndex() {
        return topographicPlaceById;
    }

    @Override
    public NetexEntityIndex<Parking> getParkingIndex() {
        return parkingById;
    }

    @Override
    public NetexEntityIndex<ScheduledStopPoint> getScheduledStopPointIndex() {
        return scheduledStopPointById;
    }

    @Override
    public NetexEntityIndex<RoutePoint> getRoutePointIndex() {
        return routePointById;
    }

    @Override
    public NetexEntityIndex<FareZone> getFareZoneIndex() {
        return fareZoneById;
    }

    @Override
    public String getTimeZone() {
        return timeZone;
    }
}
