package org.entur.netex;

import org.entur.netex.index.NetexEntityIndex;
import org.entur.netex.index.api.NetexEntityIndexReadOnlyView;
import org.entur.netex.loader.NetexXmlParser;
import org.entur.netex.loader.parser.NetexDocumentParser;
import org.rutebanken.netex.model.PublicationDeliveryStructure;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class NetexParser {
    private final NetexXmlParser xmlParser = new NetexXmlParser();

    public NetexEntityIndexReadOnlyView parseFromZip(String pathToZip) throws IOException {
        NetexEntityIndex index = new NetexEntityIndex();
        ZipFile zipFile = new ZipFile(pathToZip);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while(entries.hasMoreElements()){
            ZipEntry entry = entries.nextElement();
            InputStream stream = zipFile.getInputStream(entry);
            load(index, stream);
        }
        return index.readOnlyView();
    }

    private void load(NetexEntityIndex index, InputStream inputStream) {
        try {
            PublicationDeliveryStructure doc = xmlParser.parseXmlDoc(inputStream);
            NetexDocumentParser.parseAndPopulateIndex(index, doc);
        } catch (JAXBException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
