package org.entur.netex.loader.parser;

import org.entur.netex.index.api.NetexEntitiesIndex;
import org.rutebanken.netex.model.Authority;
import org.rutebanken.netex.model.Branding;
import org.rutebanken.netex.model.Operator;
import org.rutebanken.netex.model.Organisation_VersionStructure;
import org.rutebanken.netex.model.OrganisationsInFrame_RelStructure;
import org.rutebanken.netex.model.ResourceFrame_VersionFrameStructure;
import org.rutebanken.netex.model.TypesOfValueInFrame_RelStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.Collection;

class ResourceFrameParser extends NetexParser<ResourceFrame_VersionFrameStructure> {
    private static final Logger LOG = LoggerFactory.getLogger(ResourceFrameParser.class);

    private final Collection<Authority> authorities = new ArrayList<>();
    private final Collection<Operator> operators = new ArrayList<>();
    private Collection<Branding> brandings = new ArrayList<>();

    @Override
    void parse(ResourceFrame_VersionFrameStructure frame) {
        parseOrganization(frame.getOrganisations());
        parseTypeOfValues(frame.getTypesOfValue());

        // Keep list sorted alphabetically
        informOnElementIntentionallySkipped(LOG, frame.getBlacklists());
        informOnElementIntentionallySkipped(LOG, frame.getControlCentres());
        informOnElementIntentionallySkipped(LOG, frame.getDataSources());
        informOnElementIntentionallySkipped(LOG, frame.getEquipments());
        informOnElementIntentionallySkipped(LOG, frame.getGroupsOfEntities());
        informOnElementIntentionallySkipped(LOG, frame.getGroupsOfOperators());
        informOnElementIntentionallySkipped(LOG, frame.getOperationalContexts());
        informOnElementIntentionallySkipped(LOG, frame.getResponsibilitySets());
        informOnElementIntentionallySkipped(LOG, frame.getSchematicMaps());
        informOnElementIntentionallySkipped(LOG, frame.getVehicles());
        informOnElementIntentionallySkipped(LOG, frame.getVehicleEquipmentProfiles());
        informOnElementIntentionallySkipped(LOG, frame.getVehicleModels());
        informOnElementIntentionallySkipped(LOG, frame.getVehicleTypes());
        informOnElementIntentionallySkipped(LOG, frame.getWhitelists());
        informOnElementIntentionallySkipped(LOG, frame.getZones());

        verifyCommonUnusedPropertiesIsNotSet(LOG, frame);
    }

    @Override
    void setResultOnIndex(NetexEntitiesIndex netexIndex) {
        netexIndex.getAuthorityIndex().putAll(authorities);
        netexIndex.getOperatorIndex().putAll(operators);
        netexIndex.getBrandingIndex().putAll(brandings);
    }


    /* private methods */

    private void parseOrganization(OrganisationsInFrame_RelStructure elements) {
        for (JAXBElement<?> e : elements.getOrganisation_()) {
            parseOrganization((Organisation_VersionStructure) e.getValue());
        }
    }

    private void parseOrganization(Organisation_VersionStructure element) {
        if (element instanceof Authority) {
            authorities.add((Authority) element);
        } else if (element instanceof Operator) {
            operators.add((Operator) element);
        } else {
            informOnElementIntentionallySkipped(LOG, element);
        }
    }

    private void parseTypeOfValues(TypesOfValueInFrame_RelStructure typesOfValue) {
        if (typesOfValue != null) {
            for (JAXBElement<?> e : typesOfValue.getValueSetOrTypeOfValue()) {
                if (e.getValue() instanceof Branding) {
                    brandings.add((Branding) e.getValue());
                } else {
                    informOnElementIntentionallySkipped(LOG, e);
                }
            }
        }
    }
}
