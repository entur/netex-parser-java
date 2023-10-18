package org.entur.netex.loader.parser;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.entur.netex.support.NetexVersionHelper;
import org.rutebanken.netex.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SiteFrameParser extends NetexParser<Site_VersionFrameStructure> {
    private static final Logger LOG = LoggerFactory.getLogger(NetexParser.class);

    private final Collection<FlexibleStopPlace> flexibleStopPlaces = new ArrayList<>();

    private final Collection<GroupOfStopPlaces> groupsOfStopPlaces = new ArrayList<>();

    private final Collection<StopPlace> stopPlaces = new ArrayList<>();

    private final Collection<TariffZone> tariffZones = new ArrayList<>();

    private final Collection<GroupOfTariffZones> groupsOfTariffZones = new ArrayList<>();

    private final Collection<TopographicPlace> topographicPlaces = new ArrayList<>();

    private final Collection<Parking> parkings = new ArrayList<>();

    private final Multimap<String, Quay> quays = ArrayListMultimap.create();

    private final Map<String, String> stopPlaceIdByQuayId = new HashMap<>();

    private final Multimap<String, Parking> parkingsByStopPlaceId = ArrayListMultimap.create();

    @Override
    public void parse(Site_VersionFrameStructure frame) {
        if (frame.getStopPlaces() != null) {
            parseStopPlaces(frame.getStopPlaces().getStopPlace_());
        }

        if (frame.getGroupsOfStopPlaces() != null) {
            parseGroupsOfStopPlaces(frame.getGroupsOfStopPlaces().getGroupOfStopPlaces());
        }

        if (frame.getFlexibleStopPlaces() != null) {
            parseFlexibleStopPlaces(frame.getFlexibleStopPlaces().getFlexibleStopPlace());
        }

        if (frame.getTariffZones() != null) {
            parseTariffZones(frame.getTariffZones().getTariffZone());
        }

        if (frame.getTopographicPlaces() != null) {
            parseTopographicPlaces(frame.getTopographicPlaces().getTopographicPlace());
        }

        if (frame.getParkings() != null) {
            parseParkings(frame.getParkings().getParking());
        }

        if (frame.getGroupsOfTariffZones() != null) {
            parseGroupsOfTariffZones(frame.getGroupsOfTariffZones().getGroupOfTariffZones());
        }

        // Keep list sorted alphabetically
        informOnElementIntentionallySkipped(LOG, frame.getAccesses());
        informOnElementIntentionallySkipped(LOG, frame.getAddresses());
        informOnElementIntentionallySkipped(LOG, frame.getCountries());
        informOnElementIntentionallySkipped(LOG, frame.getCheckConstraints());
        informOnElementIntentionallySkipped(LOG, frame.getCheckConstraintDelays());
        informOnElementIntentionallySkipped(LOG, frame.getCheckConstraintThroughputs());
        informOnElementIntentionallySkipped(LOG, frame.getNavigationPaths());
        informOnElementIntentionallySkipped(LOG, frame.getPathJunctions());
        informOnElementIntentionallySkipped(LOG, frame.getPathLinks());
        informOnElementIntentionallySkipped(LOG, frame.getPointsOfInterest());
        informOnElementIntentionallySkipped(LOG, frame.getPointOfInterestClassifications());
        informOnElementIntentionallySkipped(LOG, frame.getPointOfInterestClassificationHierarchies());
        informOnElementIntentionallySkipped(LOG, frame.getSiteFacilitySets());

        verifyCommonUnusedPropertiesIsNotSet(LOG, frame);
    }

    @Override
    void setResultOnIndex(NetexEntitiesIndex netexIndex) {
        netexIndex.getFlexibleStopPlaceIndex().putAll(flexibleStopPlaces);
        netexIndex.getGroupOfStopPlacesIndex().putAll(groupsOfStopPlaces);
        netexIndex.getStopPlaceIndex().putAll(stopPlaces);
        netexIndex.getTariffZoneIndex().putAll(tariffZones);
        netexIndex.getTopographicPlaceIndex().putAll(topographicPlaces);
        netexIndex.getParkingIndex().putAll(parkings);
        netexIndex.getQuayIndex().putAll(quays.values());
        netexIndex.getStopPlaceIdByQuayIdIndex().putAll(stopPlaceIdByQuayId);
        netexIndex.getParkingsByParentSiteRefIndex().putAll(parkingsByStopPlaceId);
        netexIndex.getGroupOfTariffZonesIndex().putAll(groupsOfTariffZones);
    }

    private void parseFlexibleStopPlaces(Collection<FlexibleStopPlace> flexibleStopPlacesList) {
        flexibleStopPlaces.addAll(flexibleStopPlacesList);
    }

    private void parseGroupsOfStopPlaces(Collection<GroupOfStopPlaces> groupsOfStopPlacesList) {
        groupsOfStopPlaces.addAll(groupsOfStopPlacesList);
    }

    private void parseStopPlaces(List<JAXBElement<? extends Site_VersionStructure>> stopPlaceList) {
        for (JAXBElement<? extends Site_VersionStructure> jaxbStopPlace : stopPlaceList) {
            StopPlace stopPlace = (StopPlace) jaxbStopPlace.getValue();
            stopPlaces.add(stopPlace);
            if (!isMultiModalStopPlace(stopPlace)) {
                parseQuays(stopPlace.getQuays(), stopPlace.getId());
            }
        }
    }

    private void parseTariffZones(List<JAXBElement<? extends Zone_VersionStructure>> tariffZoneList) {
        for (JAXBElement<? extends Zone_VersionStructure> tariffZone : tariffZoneList) {
            if (tariffZone.getValue() instanceof TariffZone) {
                tariffZones.add((TariffZone) tariffZone.getValue());
            }
        }
    }

    private void parseTopographicPlaces(Collection<TopographicPlace> topographicPlaceList) {
        topographicPlaces.addAll(topographicPlaceList);
    }

    private void parseParkings(Collection<Parking> parkingList) {
        for (Parking parking : parkingList) {
            parkings.add(parking);
            parkingsByStopPlaceId.put(parking.getParentSiteRef().getRef(), parking);
        }
    }


    /**
     * Parse Quays and update the Map (quay id --> stop place id).
     * Special case: when a Quay is moved from one StopPlace to another, Quay versions are referenced under different StopPlaces.
     * In this case, this is the latest version of the Quay across all StopPlaces that is indexed in the map (quay id --> stop place id).
     *
     * @param quayRefOrQuay
     * @param stopPlaceId
     */
    private void parseQuays(Quays_RelStructure quayRefOrQuay, String stopPlaceId) {
        if (quayRefOrQuay == null) return;

        for (JAXBElement<?> jaxbQuay : quayRefOrQuay.getQuayRefOrQuay()) {
            if (jaxbQuay.getValue() instanceof Quay quay) {
                String quayId = quay.getId();
                quays.put(quayId, quay);
                if (!stopPlaceIdByQuayId.containsKey(quayId)) {
                    stopPlaceIdByQuayId.put(quayId, stopPlaceId);
                } else if (!stopPlaceIdByQuayId.get(quayId).equals(stopPlaceId)) {
                    // the Quay has been moved to another StopPlace. The latest version of the Quay is used for updating the Map (quay id --> stop place id)
                    Quay latestVersion = NetexVersionHelper.latestVersionedElementIn(quays.get(quayId));
                    if (quay.equals(latestVersion)) {
                        stopPlaceIdByQuayId.put(quayId, stopPlaceId);
                    }
                }
            }
        }
    }

    private boolean isMultiModalStopPlace(StopPlace stopPlace) {
        return stopPlace.getKeyList() != null
                && stopPlace.getKeyList().getKeyValue().stream().anyMatch(
                keyValueStructure ->
                        keyValueStructure.getKey().equals("IS_PARENT_STOP_PLACE")
                                && keyValueStructure.getValue().equals("true"));
    }

    private void parseGroupsOfTariffZones(List<GroupOfTariffZones> groupOfTariffZones) {
        groupsOfTariffZones.addAll(groupOfTariffZones);
    }
}
