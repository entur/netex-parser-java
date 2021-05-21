package org.entur.netex.index.api;

import com.google.common.collect.Multimap;
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

/**
 * Entrypoint to the NeTEx entities index
 */
public interface NetexEntitiesIndex {

    /**
     * Get an entity index of GroupOfLines
     * @return
     */
    NetexEntityIndex<GroupOfLines> getGroupOfLinesById();

    /**
     * Get an entity index of Network
     * @return
     */
    NetexEntityIndex<Network> getNetworkById();

    /**
     * Get a map of Network id by GroupOfLine id
     * @return
     */
    Map<String, String> getNetworkIdByGroupOfLineId();

    /**
     * Get an enitity index of Authority
     * @return
     */
    NetexEntityIndex<Authority> getAuthorityById();

    /**
     * Get an entity index of DayType
     * @return
     */
    NetexEntityIndex<DayType> getDayTypeById();

    /**
     * Get a Multimap of DayTypeAssignment by DayType id
     * @return
     */
    Multimap<String, DayTypeAssignment> getDayTypeAssignmentByDayTypeId();

    /**
     * Get an enitity index of DatedServiceJourney
     * @return
     */
    NetexEntityIndex<DatedServiceJourney> getDatedServiceJourneyById();

    /**
     * Get an enitity index of DestinationDisplay
     * @return
     */
    NetexEntityIndex<DestinationDisplay> getDestinationDisplayById();

    /**
     * Get an enitity index of FlexibleStopPlace
     * @return
     */
    NetexEntityIndex<FlexibleStopPlace> getFlexibleStopPlaceById();

    /**
     * Get an enitity index of GroupOfStopPlaces
     * @return
     */
    NetexEntityIndex<GroupOfStopPlaces> getGroupOfStopPlacesById();

    /**
     * Get an enitity index of JourneyPattern
     * @return
     */
    NetexEntityIndex<JourneyPattern> getJourneyPatternById();

    /**
     * Get an enitity index of FlexibleLine
     * @return
     */
    NetexEntityIndex<FlexibleLine> getFlexibleLineById();

    /**
     * Get an enitity index of Line
     * @return
     */
    NetexEntityIndex<Line> getLineById();

    /**
     * Get an enitity index of Notice
     * @return
     */
    NetexEntityIndex<Notice> getNoticeById();

    /**
     * Get an enitity index of NoticeAssignment
     * @return
     */
    NetexEntityIndex<NoticeAssignment> getNoticeAssignmentById();

    /**
     * Get an enitity index of OperatingDay
     * @return
     */
    NetexEntityIndex<OperatingDay> getOperatingDayById();

    /**
     * Get an enitity index of OperatingPeriod
     * @return
     */
    NetexEntityIndex<OperatingPeriod> getOperatingPeriodById();

    /**
     * Get an enitity index of Operator
     * @return
     */
    NetexEntityIndex<Operator> getOperatorById();

    /**
     * Get a versioned entity index of Quay
     * @return
     */
    VersionedNetexEntityIndex<Quay> getQuayById();

    /**
     * Get a map of Quay id by StopPoint ref
     * @return
     */
    Map<String, String> getQuayIdByStopPointRef();

    /**
     * Get a map of StopPlace id by StopPoint ref
     * @return
     */
    Map<String, String> getStopPlaceIdByStopPointRef();

    /**
     * Get a map of FlexibleStopPlace id by StopPoint ref
     * @return
     */
    Map<String, String> getFlexibleStopPlaceIdByStopPointRef();

    /**
     * Get an enitity index of Route
     * @return
     */
    NetexEntityIndex<Route> getRouteById();

    /**
     * Get an enitity index of ServiceJourney
     * @return
     */
    NetexEntityIndex<ServiceJourney> getServiceJourneyById();

    /**
     * Get an enitity index of ServiceLink
     * @return
     */
    NetexEntityIndex<ServiceLink> getServiceLinkById();

    /**
     * Get a versioned entity index of StopPlace
     * @return
     */
    VersionedNetexEntityIndex<StopPlace> getStopPlaceById();

    /**
     * Get an enitity index of TariffZone
     * @return
     */
    NetexEntityIndex<TariffZone> getTariffZoneById();

    /**
     * Get an enitity index of TopographicPlace
     * @return
     */
    NetexEntityIndex<TopographicPlace> getTopographicPlaceById();

    /**
     * Get an enitity index of Parking
     * @return
     */
    NetexEntityIndex<Parking> getParkingById();

    /**
     * Get an enitity index of ScheduledStopPoint
     * @return
     */
    NetexEntityIndex<ScheduledStopPoint> getScheduledStopPointById();


    /**
     * Get an enitity index of FareZone
     * @return
     */
    NetexEntityIndex<FareZone> getFareZoneById();

    /**
     * Set timezone of publication delivery
     * @param timezone
     */
    void setTimeZone(String timezone);

    /**
     * Get timezone of publication delivery
     * @return
     */
    String getTimeZone();
}
