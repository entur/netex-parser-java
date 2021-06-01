package org.entur.netex.loader.parser;

import org.entur.netex.index.api.NetexEntitiesIndex;
import org.rutebanken.netex.model.VersionFrame_VersionStructure;
import org.slf4j.Logger;

import java.util.Collection;

/**
 * An abstract parser of given type T. Enforce two steps parsing:
 * <ol>
 *     <li>parse(...)</li>
 *     <li>setResultOnIndex(...)</li>
 * </ol>
 */
@SuppressWarnings("SameParameterValue")
abstract class NetexParser<T> {

    /** Perform parsing and keep the parsed objects internally. */
    abstract void parse(T node);

    /** Add the result - the parsed objects - to the index. */
    abstract void setResultOnIndex(NetexEntitiesIndex netexIndex);


    /* static methods for logging unhandled elements - this ensure consistent logging. */

    static void verifyCommonUnusedPropertiesIsNotSet(Logger log, VersionFrame_VersionStructure rel) {
        informOnElementIntentionallySkipped(log, rel.getTypeOfFrameRef());
        informOnElementIntentionallySkipped(log, rel.getBaselineVersionFrameRef());
        informOnElementIntentionallySkipped(log, rel.getCodespaces());
        informOnElementIntentionallySkipped(log, rel.getFrameDefaults());
        informOnElementIntentionallySkipped(log, rel.getVersions());
        informOnElementIntentionallySkipped(log, rel.getTraces());
        informOnElementIntentionallySkipped(log, rel.getContentValidityConditions());
        informOnElementIntentionallySkipped(log, rel.getKeyList());
        informOnElementIntentionallySkipped(log, rel.getExtensions());
        informOnElementIntentionallySkipped(log, rel.getBrandingRef());
    }

    static void informOnElementIntentionallySkipped(Logger log, Object rel) {
        if(rel == null) return;
        if(rel instanceof Collection) throw new IllegalArgumentException("Do not pass in collections to this method.");
        log.info("Netex import - Element skipped: {}", rel.getClass().getName());
    }

}
