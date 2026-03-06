package org.entur.netex.support;

import java.io.Serializable;
import java.util.List;
import org.rutebanken.netex.model.MultilingualString;

/**
 * Helper for extracting string values from {@link MultilingualString}.
 * In netex-java-model 3.x, MultilingualString uses a mixed content model
 * where the text is stored in {@code getContent()} as a list of serializable objects.
 */
public final class MultilingualStringHelper {

  private MultilingualStringHelper() {}

  public static String getStringValue(MultilingualString multilingualString) {
    if (multilingualString == null) {
      return null;
    }
    List<Serializable> content = multilingualString.getContent();
    if (content == null || content.isEmpty()) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    for (Serializable item : content) {
      if (item instanceof String s) {
        sb.append(s);
      }
    }
    return sb.isEmpty() ? null : sb.toString();
  }
}
