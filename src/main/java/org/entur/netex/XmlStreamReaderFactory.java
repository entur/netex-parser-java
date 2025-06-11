package org.entur.netex;

import java.io.InputStream;
import javax.xml.stream.XMLStreamReader;

/**
 * Factory for XMLStreamReader. Concrete implementations can be used to ignore elements within the XML document
 */
public interface XmlStreamReaderFactory {
  XMLStreamReader createXmlStreamReader(InputStream inputStream);
}
