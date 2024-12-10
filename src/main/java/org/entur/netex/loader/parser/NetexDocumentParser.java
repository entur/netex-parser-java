package org.entur.netex.loader.parser;

import jakarta.xml.bind.JAXBElement;
import java.util.Collection;
import java.util.List;
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
import org.rutebanken.netex.model.VehicleScheduleFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the root parser for a Netex XML Document. The parser ONLY read the document and
 * populate the index with entities. The parser is only responsible for populating the
 * index, not for validating the document, nor linking of entities.
 */
public class NetexDocumentParser {

  private static final Logger LOG = LoggerFactory.getLogger(
    NetexDocumentParser.class
  );

  private final NetexEntitiesIndex netexIndex;

  private NetexDocumentParser(NetexEntitiesIndex netexIndex) {
    this.netexIndex = netexIndex;
  }

  /**
   * This static method create a new parser and parse the document. The result is added
   * to given index for further processing.
   */
  public static void parseAndPopulateIndex(
    NetexEntitiesIndex index,
    PublicationDeliveryStructure doc
  ) {
    new NetexDocumentParser(index).parse(doc);
  }

  /** Top level parse method - parses the document. */
  private void parse(PublicationDeliveryStructure doc) {
    netexIndex.setPublicationTimestamp(doc.getPublicationTimestamp());
    parseFrameList(doc.getDataObjects().getCompositeFrameOrCommonFrame());
  }

  private void parseFrameList(
    List<JAXBElement<? extends Common_VersionFrameStructure>> frames
  ) {
    for (JAXBElement<? extends Common_VersionFrameStructure> frame : frames) {
      parseCommonFrame(frame.getValue());
    }
  }

  private void parseCommonFrame(Common_VersionFrameStructure value) {
    if (value instanceof ResourceFrame resourceFrame) {
      netexIndex.getResourceFrames().add(resourceFrame);
      parse((ResourceFrame) value, new ResourceFrameParser());
    } else if (value instanceof ServiceCalendarFrame serviceCalendarFrame) {
      netexIndex.getServiceCalendarFrames().add(serviceCalendarFrame);
      parse((ServiceCalendarFrame) value, new ServiceCalendarFrameParser());
    } else if (value instanceof VehicleScheduleFrame vehicleScheduleFrame) {
      netexIndex.getVehicleScheduleFrames().add(vehicleScheduleFrame);
      parse((VehicleScheduleFrame) value, new VehicleScheduleFrameParser());
    } else if (value instanceof TimetableFrame timetableFrame) {
      netexIndex.getTimetableFrames().add(timetableFrame);
      parse((TimetableFrame) value, new TimeTableFrameParser());
    } else if (value instanceof ServiceFrame serviceFrame) {
      netexIndex.getServiceFrames().add(serviceFrame);
      parse(
        (ServiceFrame) value,
        new ServiceFrameParser(netexIndex.getFlexibleStopPlaceIndex())
      );
    } else if (value instanceof SiteFrame siteFrame) {
      netexIndex.getSiteFrames().add(siteFrame);
      parse((SiteFrame) value, new SiteFrameParser());
    } else if (value instanceof FareFrame fareFrame) {
      parse(fareFrame, new FareFrameParser());
    } else if (value instanceof CompositeFrame compositeFrame) {
      netexIndex.getCompositeFrames().add(compositeFrame);
      // We recursively parse composite frames and content until there
      // is no more nested frames - this is accepting documents witch
      // are not withing the specification, but we leave this for the
      // document schema validation
      parseCompositeFrame(compositeFrame);
    } else if (
      value instanceof GeneralFrame || value instanceof InfrastructureFrame
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
