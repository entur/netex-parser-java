package org.entur.netex.loader.parser;

import org.entur.netex.index.api.NetexEntitiesIndex;
import org.rutebanken.netex.model.Common_VersionFrameStructure;
import org.rutebanken.netex.model.CompositeFrame;
import org.rutebanken.netex.model.FareFrame;
import org.rutebanken.netex.model.GeneralFrame;
import org.rutebanken.netex.model.InfrastructureFrame;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.ResourceFrame;
import org.rutebanken.netex.model.ServiceCalendarFrame;
import org.rutebanken.netex.model.ServiceFrame;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.TimetableFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBElement;
import java.util.Collection;
import java.util.List;

/**
 * This is the root parser for a Netex XML Document. The parser ONLY read the document and
 * populate the index with entities. The parser is only responsible for populating the
 * index, not for validating the document, nor linking of entities or mapping the OTP
 * internal data structures.
 */
public class NetexDocumentParser {
    private static final Logger LOG = LoggerFactory.getLogger(NetexDocumentParser.class);

    private final NetexEntitiesIndex netexIndex;

    private NetexDocumentParser(NetexEntitiesIndex netexIndex) {
        this.netexIndex = netexIndex;
    }

    /**
     * This static method create a new parser and parse the document. The result is added
     * to given index for further processing.
     */
    public static void parseAndPopulateIndex(NetexEntitiesIndex index, PublicationDeliveryStructure doc) {
        new NetexDocumentParser(index).parse(doc);
    }

    /** Top level parse method - parses the document. */
    private void parse(PublicationDeliveryStructure doc) {
        parseFrameList(doc.getDataObjects().getCompositeFrameOrCommonFrame());
    }

    private void parseFrameList(List<JAXBElement<? extends Common_VersionFrameStructure>> frames) {
        for (JAXBElement<? extends Common_VersionFrameStructure> frame : frames) {
            parseCommonFrame(frame.getValue());
        }
    }

    private void parseCommonFrame(Common_VersionFrameStructure value) {
        if(value instanceof ResourceFrame) {
            netexIndex.getResourceFrames().add((ResourceFrame) value);
            parse((ResourceFrame) value, new ResourceFrameParser());
        } else if(value instanceof ServiceCalendarFrame) {
            netexIndex.getServiceCalendarFrames().add((ServiceCalendarFrame) value);
            parse((ServiceCalendarFrame) value, new ServiceCalendarFrameParser());
        } else if(value instanceof TimetableFrame) {
            netexIndex.getTimetableFrames().add((TimetableFrame) value);
            parse((TimetableFrame) value, new TimeTableFrameParser());
        } else if(value instanceof ServiceFrame) {
            netexIndex.getServiceFrames().add((ServiceFrame) value);
            parse((ServiceFrame) value, new ServiceFrameParser(
                netexIndex.getFlexibleStopPlaceIndex()
            ));
        }  else if (value instanceof SiteFrame) {
            netexIndex.getSiteFrames().add((SiteFrame) value);
            parse((SiteFrame) value, new SiteFrameParser());
        } else if (value instanceof FareFrame) {
            parse((FareFrame) value, new FareFrameParser());
        } else if (value instanceof CompositeFrame) {
            netexIndex.getCompositeFrames().add((CompositeFrame) value);
            // We recursively parse composite frames and content until there
            // is no more nested frames - this is accepting documents witch
            // are not withing the specification, but we leave this for the
            // document schema validation - not a OTP responsibility
            parseCompositeFrame((CompositeFrame) value);
        } else if (
                value instanceof GeneralFrame ||
                value instanceof InfrastructureFrame
        ) {
            NetexParser.informOnElementIntentionallySkipped(LOG, value);
        } else {
            NetexParser.informOnElementIntentionallySkipped(LOG, value);
        }
    }

    private void parseCompositeFrame(CompositeFrame frame) {
        // Declare some ugly types to prevent obstructing the reading later...
        Collection<JAXBElement<? extends Common_VersionFrameStructure>> frames;

        frames = frame.getFrames().getCommonFrame();

        for (JAXBElement<? extends Common_VersionFrameStructure> it : frames) {
            parseCommonFrame(it.getValue());
        }
    }



    private <T> void parse(T node, NetexParser<T> parser) {
        parser.parse(node);
        parser.setResultOnIndex(netexIndex);
    }
}
