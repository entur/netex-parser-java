package org.entur.netex;

import org.entur.netex.index.api.NetexEntitiesIndex;
import org.entur.netex.index.impl.NetexEntitiesIndexImpl;
import org.entur.netex.loader.NetexXmlParser;
import org.entur.netex.loader.parser.NetexDocumentParser;
import org.rutebanken.netex.model.PublicationDeliveryStructure;

import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Main entry point to the library. Used to parse a NeTEx publication
 * delivery into a queryable index.
 */
public class NetexParser {
    private final NetexXmlParser xmlParser = new NetexXmlParser();

    /**
     * Parse a NeTEx publication delivery from one or more files in
     * a zip archive
     *
     * @param pathToZip Path to zip file
     * @return A queryable index of NeTEx entities
     */
    public NetexEntitiesIndex parse(String pathToZip) throws IOException {
        NetexEntitiesIndex index = new NetexEntitiesIndexImpl();
        return parse(pathToZip, index);
    }

    /**
     * Parse a NeTEx publication delivery from one or more files in
     * a zip archive into an existing index.
     *
     * Existing entities with same id will be overwritten.
     *
     * @param pathToZip Path to zip file
     * @param index An instance of NetexEntitiesIndex
     * @return The mutated index
     */
    public NetexEntitiesIndex parse(String pathToZip, NetexEntitiesIndex index) throws IOException {
        try(ZipFile zipFile = new ZipFile(pathToZip)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while(entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                InputStream stream = zipFile.getInputStream(entry);
                load(index, stream);
            }
            return index;
        }
    }

    /**
     * Parse an input stream of a single NeTEx public delivery
     *
     * @param inputStream
     * @return A queryable index of NeTEx entities
     */
    public NetexEntitiesIndex parse(InputStream inputStream) {
        NetexEntitiesIndex index = new NetexEntitiesIndexImpl();
        load(index, inputStream);
        return index;
    }

    /**
     * Parse an input stream of a single NeTEx publication delivery
     * into an existing index
     *
     * Existing entities with same id will be overwritten.
     *
     * @param inputStream An InputStream
     * @param index An instance of NetexEntitiesIndex
     * @return The mutated index
     */
    public NetexEntitiesIndex parse(InputStream inputStream, NetexEntitiesIndex index) {
        load(index, inputStream);
        return index;
    }

    private void load(NetexEntitiesIndex index, InputStream inputStream) {
        try {
            PublicationDeliveryStructure doc = xmlParser.parseXmlDoc(inputStream);
            NetexDocumentParser.parseAndPopulateIndex(index, doc);
        } catch (JAXBException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
