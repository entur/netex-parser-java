package org.entur.netex.index.api;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import org.rutebanken.netex.model.Authority;
import org.rutebanken.netex.model.Block;
import org.rutebanken.netex.model.Branding;
import org.rutebanken.netex.model.CompositeFrame;
import org.rutebanken.netex.model.DatedServiceJourney;
import org.rutebanken.netex.model.DayType;
import org.rutebanken.netex.model.DayTypeAssignment;
import org.rutebanken.netex.model.DeadRun;
import org.rutebanken.netex.model.DestinationDisplay;
import org.rutebanken.netex.model.FareZone;
import org.rutebanken.netex.model.FlexibleLine;
import org.rutebanken.netex.model.FlexibleStopPlace;
import org.rutebanken.netex.model.GroupOfLines;
import org.rutebanken.netex.model.GroupOfStopPlaces;
import org.rutebanken.netex.model.GroupOfTariffZones;
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
import org.rutebanken.netex.model.ResourceFrame;
import org.rutebanken.netex.model.Route;
import org.rutebanken.netex.model.RoutePoint;
import org.rutebanken.netex.model.ScheduledStopPoint;
import org.rutebanken.netex.model.ServiceCalendarFrame;
import org.rutebanken.netex.model.ServiceFrame;
import org.rutebanken.netex.model.ServiceJourney;
import org.rutebanken.netex.model.ServiceJourneyInterchange;
import org.rutebanken.netex.model.ServiceLink;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.TariffZone;
import org.rutebanken.netex.model.TimetableFrame;
import org.rutebanken.netex.model.TopographicPlace;
import org.rutebanken.netex.model.VehicleScheduleFrame;

import com.google.common.collect.Multimap;

/**
 * Entrypoint to the NeTEx entities index
 */
public interface NetexEntitiesIndex {

    /**
     * Get an entity index of GroupOfLines
     * @return
     */
    NetexEntityIndex<GroupOfLines> getGroupOfLinesIndex();

    /**
     * Get an entity index of Network
     * @return
     */
    NetexEntityIndex<Network> getNetworkIndex();

    /**
     * Get a map of Network id by GroupOfLine id
     * @return
     */
    Map<String, String> getNetworkIdByGroupOfLineIdIndex();

    /**
     * Get an entity index of Authority
     * @return
     */
    NetexEntityIndex<Authority> getAuthorityIndex();

    /**
     * Get an entity index of DayType
     * @return
     */
    NetexEntityIndex<DayType> getDayTypeIndex();

    /**
     * Get a Multimap of DayTypeAssignment by DayType id
     * @return
     */
    Multimap<String, DayTypeAssignment> getDayTypeAssignmentsByDayTypeIdIndex();


    /**
     * Get a Multimap of PassengerStopAssignment by StopPoint ref
     * * @return
     */
    Multimap<String, PassengerStopAssignment> getPassengerStopAssignmentsByStopPointRefIndex();

    /**
     * Get an entity index of DatedServiceJourney
     * @return
     */
    NetexEntityIndex<DatedServiceJourney> getDatedServiceJourneyIndex();

    /**
     * Get an entity index of DeadRun
     * @return
     */
    NetexEntityIndex<DeadRun> getDeadRunIndex();


    /**
     * Get a Multimap of DatedServiceJourney by ServiceJourney id
     * @return
     */
    Multimap<String, DatedServiceJourney> getDatedServiceJourneyByServiceJourneyRefIndex();


    /**
     * Get an entity index of DestinationDisplay
     * @return
     */
    NetexEntityIndex<DestinationDisplay> getDestinationDisplayIndex();

    /**
     * Get an entity index of FlexibleStopPlace
     * @return
     */
    NetexEntityIndex<FlexibleStopPlace> getFlexibleStopPlaceIndex();

    /**
     * Get an entity index of GroupOfStopPlaces
     * @return
     */
    NetexEntityIndex<GroupOfStopPlaces> getGroupOfStopPlacesIndex();

    /**
     * Get an entity index of JourneyPattern
     * @return
     */
    NetexEntityIndex<JourneyPattern> getJourneyPatternIndex();

    /**
     * Get an entity index of FlexibleLine
     * @return
     */
    NetexEntityIndex<FlexibleLine> getFlexibleLineIndex();

    /**
     * Get an entity index of Line
     * @return
     */
    NetexEntityIndex<Line> getLineIndex();

    /**
     * Get an entity index of Notice
     * @return
     */
    NetexEntityIndex<Notice> getNoticeIndex();

    /**
     * Get an entity index of NoticeAssignment
     * @return
     */
    NetexEntityIndex<NoticeAssignment> getNoticeAssignmentIndex();

    /**
     * Get an entity index of OperatingDay
     * @return
     */
    NetexEntityIndex<OperatingDay> getOperatingDayIndex();

    /**
     * Get an entity index of OperatingPeriod
     * @return
     */
    NetexEntityIndex<OperatingPeriod> getOperatingPeriodIndex();

    /**
     * Get an entity index of Operator
     * @return
     */
    NetexEntityIndex<Operator> getOperatorIndex();

    /**
     * Get an entity index of Branding
     * @return
     */
    NetexEntityIndex<Branding> getBrandingIndex();

    /**
     * Get a versioned entity index of Quay
     * @return
     */
    VersionedNetexEntityIndex<Quay> getQuayIndex();

    /**
     * Get a map of Quay id by StopPoint ref
     * @return
     */
    Map<String, String> getQuayIdByStopPointRefIndex();

    /**
     * Get a map of StopPlace id by StopPoint ref
     * @return
     */
    Map<String, String> getStopPlaceIdByStopPointRefIndex();

    /**
     * Get a map of StopPlace id by Quay id
     * @return
     */
    Map<String, String> getStopPlaceIdByQuayIdIndex();

    /**
     * Get a map of FlexibleStopPlace id by StopPoint ref
     * @return
     */
    Map<String, String> getFlexibleStopPlaceIdByStopPointRefIndex();

    /**
     * Get an entity index of Route
     * @return
     */
    NetexEntityIndex<Route> getRouteIndex();

    /**
     * Get an entity index of ServiceJourney
     * @return
     */
    NetexEntityIndex<ServiceJourney> getServiceJourneyIndex();

    /**
     * Get an entity index of ServiceJourneyInterchange
     * @return
     */
    NetexEntityIndex<ServiceJourneyInterchange> getServiceJourneyInterchangeIndex();

    /**
     * Get a map of ServiceJourneyInterchange by feeder or consumer ServiceJourney
     * @return
     */
    Multimap<String, ServiceJourneyInterchange> getServiceJourneyInterchangeByServiceJourneyRefIndex();

    /**
     * Get an entity index of ServiceLink
     * @return
     */
    NetexEntityIndex<ServiceLink> getServiceLinkIndex();

    /**
     * Get a versioned entity index of StopPlace
     * @return
     */
    VersionedNetexEntityIndex<StopPlace> getStopPlaceIndex();

    /**
     * Get an entity index of TariffZone
     * @return
     */
    VersionedNetexEntityIndex<TariffZone> getTariffZoneIndex();

    /**
     * Get an entity index of TopographicPlace
     * @return
     */
    VersionedNetexEntityIndex<TopographicPlace> getTopographicPlaceIndex();

    /**
     * Get an entity index of Parking
     * @return
     */
    VersionedNetexEntityIndex<Parking> getParkingIndex();

    /**
     * Get an entity index of ScheduledStopPoint
     * @return
     */
    VersionedNetexEntityIndex<ScheduledStopPoint> getScheduledStopPointIndex();

    /**
     * Get an entity index of RoutePoint
     * @return
     */
    NetexEntityIndex<RoutePoint> getRoutePointIndex();

    /**
     * Get an entity index of Block
     * @return
     */
    NetexEntityIndex<Block> getBlockIndex();

    /**
     * Get a Multimap of parkings by ParentSite ref
     * @return
     */
    Multimap<String, Parking> getParkingsByParentSiteRefIndex();

    /**
     * Get an entity index of FareZone
     * @return
     */
    VersionedNetexEntityIndex<FareZone> getFareZoneIndex();

    VersionedNetexEntityIndex<GroupOfTariffZones> getGroupOfTariffZonesIndex();

    Collection<CompositeFrame> getCompositeFrames();
    Collection<ResourceFrame> getResourceFrames();
    Collection<SiteFrame> getSiteFrames();
    Collection<ServiceFrame> getServiceFrames();
    Collection<ServiceCalendarFrame> getServiceCalendarFrames();
    Collection<VehicleScheduleFrame> getVehicleScheduleFrames();
    Collection<TimetableFrame> getTimetableFrames();

    LocalDateTime getPublicationTimestamp();
    void setPublicationTimestamp(LocalDateTime publicationTimestamp);
}
