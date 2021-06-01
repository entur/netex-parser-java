package org.entur.netex.loader.parser;

import org.entur.netex.index.api.NetexEntitiesIndex;
import org.rutebanken.netex.model.FlexibleStopPlace;
import org.rutebanken.netex.model.GroupOfStopPlaces;
import org.rutebanken.netex.model.Parking;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.Quays_RelStructure;
import org.rutebanken.netex.model.Site_VersionFrameStructure;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.TariffZone;
import org.rutebanken.netex.model.TopographicPlace;
import org.rutebanken.netex.model.Zone_VersionStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class SiteFrameParser extends NetexParser<Site_VersionFrameStructure> {
    private static final Logger LOG = LoggerFactory.getLogger(NetexParser.class);

    private final Collection<FlexibleStopPlace> flexibleStopPlaces = new ArrayList<>();

    private final Collection<GroupOfStopPlaces> groupsOfStopPlaces = new ArrayList<>();

    private final Collection<StopPlace> stopPlaces = new ArrayList<>();

    private final Collection<TariffZone> tariffZones = new ArrayList<>();

    private final Collection<TopographicPlace> topographicPlaces = new ArrayList<>();

    private final Collection<Parking> parkings = new ArrayList<>();

    private final Collection<Quay> quays = new ArrayList<>();

    @Override
    public void parse(Site_VersionFrameStructure frame) {
        if(frame.getStopPlaces() != null) {
            parseStopPlaces(frame.getStopPlaces().getStopPlace());
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

        if  (frame.getTopographicPlaces() != null) {
            parseTopographicPlaces(frame.getTopographicPlaces().getTopographicPlace());
        }

        if (frame.getParkings() != null) {
            parseParkings(frame.getParkings().getParking());
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
        netexIndex.getQuayIndex().putAll(quays);
    }

    private void parseFlexibleStopPlaces(Collection<FlexibleStopPlace> flexibleStopPlacesList ) {
        flexibleStopPlaces.addAll(flexibleStopPlacesList);
    }

    private void parseGroupsOfStopPlaces(Collection<GroupOfStopPlaces> groupsOfStopPlacesList ) {
        groupsOfStopPlaces.addAll(groupsOfStopPlacesList);
    }

    private void parseStopPlaces(Collection<StopPlace> stopPlaceList) {
        for (StopPlace stopPlace : stopPlaceList) {
                stopPlaces.add(stopPlace);
                if (!isMultiModalStopPlace(stopPlace)) {
                    parseQuays(stopPlace.getQuays());
                }
        }
    }

    private void parseTariffZones(List<JAXBElement<? extends Zone_VersionStructure>> tariffZoneList) {
        for (JAXBElement<? extends Zone_VersionStructure> tariffZone : tariffZoneList) {
            if(tariffZone.getValue() instanceof TariffZone) {
                tariffZones.add((TariffZone) tariffZone.getValue());
            }
        }
    }

    private void parseTopographicPlaces(Collection<TopographicPlace> topographicPlaceList) {
        topographicPlaces.addAll(topographicPlaceList);
    }

    private void parseParkings(Collection<Parking> parkingList) {
        parkings.addAll(parkingList);
    }

    private void parseQuays(Quays_RelStructure quayRefOrQuay) {
        if(quayRefOrQuay == null) return;

        for (Object quayObject : quayRefOrQuay.getQuayRefOrQuay()) {
            if (quayObject instanceof Quay) {
                quays.add((Quay) quayObject);
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
}
