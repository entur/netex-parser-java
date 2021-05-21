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
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.Route;
import org.rutebanken.netex.model.ScheduledStopPoint;
import org.rutebanken.netex.model.ServiceJourney;
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
    public final NetexEntityIndex<Route> routeById;
    public final NetexEntityIndex<ServiceJourney> serviceJourneyById;
    public final NetexEntityIndex<ServiceLink> serviceLinkById;
    public final VersionedNetexEntityIndex<StopPlace> stopPlaceById;
    public final NetexEntityIndex<TariffZone> tariffZonesById;
    public final NetexEntityIndex<TopographicPlace> topographicPlaceById;
    public final NetexEntityIndex<Parking> parkingById;
    public final NetexEntityIndex<ScheduledStopPoint> scheduledStopPointById;
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
        this.routeById = new NetexEntityMapByIdImpl<>();
        this.serviceJourneyById = new NetexEntityMapByIdImpl<>();
        this.serviceLinkById = new NetexEntityMapByIdImpl<>();
        this.stopPlaceById = new VersionedNetexEntityIndexImpl<>();
        this.tariffZonesById = new NetexEntityMapByIdImpl<>();
        this.topographicPlaceById = new NetexEntityMapByIdImpl<>();
        this.parkingById = new NetexEntityMapByIdImpl<>();
        this.scheduledStopPointById = new NetexEntityMapByIdImpl<>();
        this.fareZoneById = new NetexEntityMapByIdImpl<>();
    }

    @Override
    public NetexEntityIndex<GroupOfLines> getGroupOfLinesById() {
        return groupOfLinesById;
    }

    @Override
    public NetexEntityIndex<Network> getNetworkById() {
        return networkById;
    }

    @Override
    public Map<String, String> getNetworkIdByGroupOfLineId() {
        return networkIdByGroupOfLineId;
    }

    @Override
    public NetexEntityIndex<Authority> getAuthorityById() {
        return authoritiesById;
    }

    @Override
    public NetexEntityIndex<DayType> getDayTypeById() {
        return dayTypeById;
    }

    /**
     * @deprecated This should be replaced with a collection of DayTypeAssignment. The
     *             mapper is responsible for indexing its data, except for entities by id.
     * @return
     */
    @Deprecated
    public Multimap<String, DayTypeAssignment> getDayTypeAssignmentByDayTypeId() {
        return dayTypeAssignmentByDayTypeId;
    }

    @Override
    public NetexEntityIndex<DatedServiceJourney> getDatedServiceJourneyById() {
        return datedServiceJourneys;
    }

    @Override
    public NetexEntityIndex<DestinationDisplay> getDestinationDisplayById() {
        return destinationDisplayById;
    }

    @Override
    public NetexEntityIndex<FlexibleStopPlace> getFlexibleStopPlaceById() {
        return flexibleStopPlaceById;
    }

    @Override
    public NetexEntityIndex<GroupOfStopPlaces> getGroupOfStopPlacesById() {
        return groupOfStopPlacesById;
    }

    @Override
    public NetexEntityIndex<JourneyPattern> getJourneyPatternById() {
        return journeyPatternsById;
    }

    @Override
    public NetexEntityIndex<FlexibleLine> getFlexibleLineById() {
        return flexibleLineByid;
    }

    @Override
    public NetexEntityIndex<Line> getLineById() {
        return lineById;
    }

    @Override
    public NetexEntityIndex<Notice> getNoticeById() {
        return noticeById;
    }

    @Override
    public NetexEntityIndex<NoticeAssignment> getNoticeAssignmentById() {
        return noticeAssignmentById;
    }

    @Override
    public NetexEntityIndex<OperatingDay> getOperatingDayById() {
        return operatingDayById;
    }

    @Override
    public NetexEntityIndex<OperatingPeriod> getOperatingPeriodById() {
        return operatingPeriodById;
    }

    @Override
    public NetexEntityIndex<Operator> getOperatorById() {
        return operatorsById;
    }

    @Override
    public VersionedNetexEntityIndex<Quay> getQuayById() {
        return quayById;
    }

    @Override
    public Map<String, String> getFlexibleStopPlaceIdByStopPointRef() {
        return flexibleStopPlaceByStopPointRef;
    }

    @Override
    public Map<String, String> getQuayIdByStopPointRef() {
        return quayIdByStopPointRef;
    }

    @Override
    public Map<String, String> getStopPlaceIdByStopPointRef() {
        return stopPlaceIdByStopPointRef;
    }

    @Override
    public NetexEntityIndex<Route> getRouteById() {
        return routeById;
    }

    @Override
    public NetexEntityIndex<ServiceJourney> getServiceJourneyById() {
        return serviceJourneyById;
    }

    @Override
    public NetexEntityIndex<ServiceLink> getServiceLinkById() {
        return serviceLinkById;
    }

    @Override
    public VersionedNetexEntityIndex<StopPlace> getStopPlaceById() {
        return stopPlaceById;
    }

    @Override
    public NetexEntityIndex<TariffZone> getTariffZoneById() {
        return tariffZonesById;
    }

    @Override
    public NetexEntityIndex<TopographicPlace> getTopographicPlaceById() {
        return topographicPlaceById;
    }

    @Override
    public NetexEntityIndex<Parking> getParkingById() {
        return parkingById;
    }

    @Override
    public NetexEntityIndex<ScheduledStopPoint> getScheduledStopPointById() {
        return scheduledStopPointById;
    }

    @Override
    public NetexEntityIndex<FareZone> getFareZoneById() {
        return fareZoneById;
    }

    @Override
    public String getTimeZone() {
        return timeZone;
    }
}
