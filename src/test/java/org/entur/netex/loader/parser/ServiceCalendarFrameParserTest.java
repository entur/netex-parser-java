package org.entur.netex.loader.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import org.entur.netex.index.impl.NetexEntitiesIndexImpl;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.*;

class ServiceCalendarFrameParserTest {

  @Test
  void testParseOperatingDaysInServiceCalendarFrame() {
    NetexEntitiesIndexImpl customindex = new NetexEntitiesIndexImpl();
    ServiceCalendarFrameParser parser = new ServiceCalendarFrameParser();

    parser.parse(
      new ServiceCalendarFrame_VersionFrameStructure()
        .withId("ServiceCalendarFrameId1")
        .withServiceCalendar(
          new ServiceCalendar()
            .withId("ServiceCalendarId1")
            .withOperatingDays(
              mockOperatingDaysRelStructure(
                "1",
                LocalDateTime.of(2025, 1, 1, 0, 0)
              )
            )
        )
        .withOperatingDays(
          mockOperatingDaysInFrameRelStructure(
            "2",
            LocalDateTime.of(2025, 1, 2, 0, 0)
          )
        )
    );
    parser.setResultOnIndex(customindex);
    assertEquals(2, customindex.operatingDayById.getAll().size());
  }

  private OperatingDays_RelStructure mockOperatingDaysRelStructure(
    String id,
    LocalDateTime date
  ) {
    return new OperatingDays_RelStructure()
      .withId(id)
      .withOperatingDayRefOrOperatingDay(
        new OperatingDay().withId(id).withCalendarDate(date)
      );
  }

  private OperatingDaysInFrame_RelStructure mockOperatingDaysInFrameRelStructure(
    String id,
    LocalDateTime date
  ) {
    return new OperatingDaysInFrame_RelStructure()
      .withId(id)
      .withOperatingDay(new OperatingDay().withId(id).withCalendarDate(date));
  }
}
