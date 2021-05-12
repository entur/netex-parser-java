package org.entur.netex.index;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.entur.netex.index.api.EntityMapById;
import org.entur.netex.index.api.EntityVersionMapById;
import org.entur.netex.index.impl.EntityVersionMapByIdImpl;
import org.entur.netex.index.impl.EntityMapByIdImpl;
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
public class NetexEntityIndex {

    // Indexes to entities
    public final EntityMapById<Authority> authoritiesById;
    public final EntityMapById<DatedServiceJourney> datedServiceJourneys;
    public final EntityMapById<DayType> dayTypeById;
    public final Multimap<String, DayTypeAssignment> dayTypeAssignmentByDayTypeId;
    public final EntityMapById<DestinationDisplay> destinationDisplayById;
    public final EntityMapById<FlexibleStopPlace> flexibleStopPlaceById;
    public final EntityMapById<GroupOfLines> groupOfLinesById;
    public final EntityMapById<GroupOfStopPlaces> groupOfStopPlacesById;
    public final EntityMapById<JourneyPattern> journeyPatternsById;
    public final EntityMapById<FlexibleLine> flexibleLineByid;
    public final EntityMapById<Line> lineById;
    public final EntityMapById<Network> networkById;
    public final EntityMapById<Notice> noticeById;
    public final EntityMapById<NoticeAssignment> noticeAssignmentById;
    public final EntityMapById<OperatingDay> operatingDayById;
    public final EntityMapById<OperatingPeriod> operatingPeriodById;
    public final EntityMapById<Operator> operatorsById;
    public final EntityVersionMapById<Quay> quayById;
    public final Map<String, String> flexibleStopPlaceByStopPointRef;
    public final Map<String, String> quayIdByStopPointRef;
    public final Map<String, String> stopPlaceIdByStopPointRef;
    public final EntityMapById<Route> routeById;
    public final EntityMapById<ServiceJourney> serviceJourneyById;
    public final EntityMapById<ServiceLink> serviceLinkById;
    public final EntityVersionMapById<StopPlace> stopPlaceById;
    public final EntityMapById<TariffZone> tariffZonesById;
    public final EntityMapById<TopographicPlace> topographicPlaceById;
    public final EntityMapById<Parking> parkingById;
    public final EntityMapById<ScheduledStopPoint> scheduledStopPointById;
    public final EntityMapById<FareZone> fareZoneById;


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
    public NetexEntityIndex() {
        this.authoritiesById = new EntityMapByIdImpl<>();
        this.dayTypeById = new EntityMapByIdImpl<>();
        this.dayTypeAssignmentByDayTypeId = Multimaps.synchronizedListMultimap(ArrayListMultimap.create());
        this.datedServiceJourneys = new EntityMapByIdImpl<>();
        this.destinationDisplayById = new EntityMapByIdImpl<>();
        this.flexibleStopPlaceById = new EntityMapByIdImpl<>();
        this.groupOfLinesById = new EntityMapByIdImpl<>();
        this.groupOfStopPlacesById = new EntityMapByIdImpl<>();
        this.journeyPatternsById = new EntityMapByIdImpl<>();
        this.flexibleLineByid = new EntityMapByIdImpl<>();
        this.lineById = new EntityMapByIdImpl<>();
        this.networkById = new EntityMapByIdImpl<>();
        this.networkIdByGroupOfLineId = new ConcurrentHashMap<>();
        this.noticeById = new EntityMapByIdImpl<>();
        this.noticeAssignmentById = new EntityMapByIdImpl<>();
        this.operatingDayById = new EntityMapByIdImpl<>();
        this.operatingPeriodById = new EntityMapByIdImpl<>();
        this.operatorsById = new EntityMapByIdImpl<>();
        this.quayById = new EntityVersionMapByIdImpl<>();
        this.flexibleStopPlaceByStopPointRef = new ConcurrentHashMap<>();
        this.quayIdByStopPointRef = new ConcurrentHashMap<>();
        this.stopPlaceIdByStopPointRef = new ConcurrentHashMap<>();
        this.routeById = new EntityMapByIdImpl<>();
        this.serviceJourneyById = new EntityMapByIdImpl<>();
        this.serviceLinkById = new EntityMapByIdImpl<>();
        this.stopPlaceById = new EntityVersionMapByIdImpl<>();
        this.tariffZonesById = new EntityMapByIdImpl<>();
        this.topographicPlaceById = new EntityMapByIdImpl<>();
        this.parkingById = new EntityMapByIdImpl<>();
        this.scheduledStopPointById = new EntityMapByIdImpl<>();
        this.fareZoneById = new EntityMapByIdImpl<>();
    }

    public org.entur.netex.index.api.NetexEntityIndex api() {
        return new org.entur.netex.index.api.NetexEntityIndex() {

            /**
             * Lookup a Network given a GroupOfLine id or an Network id. If the given
             * {@code groupOfLineOrNetworkId} is a GroupOfLine ID, we lookup the GroupOfLine, and then
             * lookup its Network. If the given {@code groupOfLineOrNetworkId} is a Network ID then we
             * can lookup the Network directly.
             * <p/>
             * If no Network is found {@code null} is returned.
             */
            @Override
            public Network lookupNetworkForLine(String groupOfLineOrNetworkId) {
                var groupOfLines = groupOfLinesById.get(groupOfLineOrNetworkId);

                String networkId = groupOfLines == null
                        ? groupOfLineOrNetworkId
                        : networkIdByGroupOfLineId.get(groupOfLines.getId());

                return networkById.get(networkId);
            }

            @Override
            public EntityMapById<Authority> getAuthoritiesById() {
                return authoritiesById;
            }

            @Override
            public EntityMapById<DayType> getDayTypeById() {
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
            public EntityMapById<DatedServiceJourney> getDatedServiceJourneys() {
                return datedServiceJourneys;
            }

            @Override
            public EntityMapById<DestinationDisplay> getDestinationDisplayById() {
                return destinationDisplayById;
            }

            @Override
            public EntityMapById<FlexibleStopPlace> getFlexibleStopPlacesById() {
                return flexibleStopPlaceById;
            }

            @Override
            public EntityMapById<GroupOfStopPlaces> getGroupOfStopPlacesById() {
                return groupOfStopPlacesById;
            }

            @Override
            public EntityMapById<JourneyPattern> getJourneyPatternsById() {
                return journeyPatternsById;
            }

            @Override
            public EntityMapById<FlexibleLine> getFlexibleLineById() {
                return flexibleLineByid;
            }

            @Override
            public EntityMapById<Line> getLineById() {
                return lineById;
            }

            @Override
            public EntityMapById<Notice> getNoticeById() {
                return noticeById;
            }

            @Override
            public EntityMapById<NoticeAssignment> getNoticeAssignmentById() {
                return noticeAssignmentById;
            }
            @Override
            public EntityMapById<OperatingDay> getOperatingDayById() {
                return operatingDayById;
            }

            @Override
            public EntityMapById<OperatingPeriod> getOperatingPeriodById() {
                return operatingPeriodById;
            }

            @Override
            public EntityMapById<Operator> getOperatorsById() {
                return operatorsById;
            }

            @Override
            public EntityVersionMapById<Quay> getQuayById() {
                return quayById;
            }

            @Override
            public Map<String, String> getFlexibleStopPlaceByStopPointRef() {
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
            public EntityMapById<Route> getRouteById() {
                return routeById;
            }

            @Override
            public EntityMapById<ServiceJourney> getServiceJourneyById() {
                return serviceJourneyById;
            }

            @Override
            public EntityMapById<ServiceLink> getServiceLinkById() {
                return serviceLinkById;
            }

            @Override
            public EntityVersionMapById<StopPlace> getStopPlaceById() {
                return stopPlaceById;
            }

            @Override
            public EntityMapById<TariffZone> getTariffZonesById() {
                return tariffZonesById;
            }

            @Override
            public EntityMapById<TopographicPlace> getTopographicPlaceById() {
                return topographicPlaceById;
            }

            @Override
            public EntityMapById<Parking> getParkingById() {
                return parkingById;
            }

            @Override
            public EntityMapById<ScheduledStopPoint> getScheduledStopPointById() {
                return scheduledStopPointById;
            }

            @Override
            public EntityMapById<FareZone> getFareZoneById() {
                return fareZoneById;
            }

            @Override
            public String getTimeZone() {
                return timeZone;
            }
        };
    }
}
