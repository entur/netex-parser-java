package org.entur.netex.loader.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.xml.bind.JAXBElement;
import java.time.LocalDateTime;
import java.util.List;
import javax.annotation.Nonnull;
import javax.xml.namespace.QName;
import org.entur.netex.index.impl.NetexEntitiesIndexImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.*;

class ServiceCalendarFrameParserTest {

  private NetexEntitiesIndexImpl index;
  private ServiceCalendarFrameParser parser;

  private LocalDateTime january1st = LocalDateTime.of(2025, 1, 1, 0, 0);
  private LocalDateTime january2nd = LocalDateTime.of(2025, 1, 2, 0, 0);
  private LocalDateTime january3rd = LocalDateTime.of(2025, 1, 3, 0, 0);
  private LocalDateTime january4th = LocalDateTime.of(2025, 1, 4, 0, 0);

  @BeforeEach
  void setUp() {
    this.index = new NetexEntitiesIndexImpl();
    this.parser = new ServiceCalendarFrameParser();
  }

  @Test
  void testParseOperatingPeriodsInServiceCalendarFrame() {
    parser.parse(
      new ServiceCalendarFrame_VersionFrameStructure()
        .withId("ServiceCalendarFrame2")
        .withOperatingPeriods(
          new OperatingPeriodsInFrame_RelStructure()
            .withId("OperatingPeriodsInFrame2")
            .withOperatingPeriodOrUicOperatingPeriod(
              List.of(
                mockOperatingPeriodWithDates(
                  "OperatingPeriod1",
                  january1st,
                  january2nd
                )
              )
            )
        )
        .withServiceCalendar(
          new ServiceCalendar()
            .withOperatingPeriods(
              new OperatingPeriods_RelStructure()
                .withId("OperatingPeriodsRelStructure")
                .withOperatingPeriodRefOrOperatingPeriodOrUicOperatingPeriod(
                  createJaxbElement(
                    mockOperatingPeriodWithDates(
                      "OperatingPeriod2",
                      january3rd,
                      january4th
                    )
                  ),
                  createJaxbElement(
                    mockOperatingPeriodWithRef(
                      "OperatingPeriod3",
                      "OperatingPeriodRef"
                    )
                  )
                )
            )
        )
    );
    parser.setResultOnIndex(index);
    assertEquals(3, index.operatingPeriodById.getAll().size());
  }

  private OperatingPeriod mockOperatingPeriodWithDates(
    String id,
    LocalDateTime fromDate,
    LocalDateTime toDate
  ) {
    return new OperatingPeriod()
      .withId(id)
      .withFromDate(fromDate)
      .withToDate(toDate);
  }

  private OperatingPeriod mockOperatingPeriodWithRef(String id, String ref) {
    return new OperatingPeriod()
      .withId(id)
      .withFromOperatingDayRef(
        new OperatingDayRefStructure().withValue("From" + ref)
      )
      .withToOperatingDayRef(
        new OperatingDayRefStructure().withValue("To" + ref)
      );
  }

  public static <T> JAXBElement<T> createJaxbElement(@Nonnull T value) {
    return new JAXBElement(new QName("x"), value.getClass(), value);
  }

  @Test
  void testParseOperatingDaysInServiceCalendarFrame() {
    ServiceCalendarFrameParser parser = new ServiceCalendarFrameParser();

    parser.parse(
      new ServiceCalendarFrame_VersionFrameStructure()
        .withId("ServiceCalendarFrameId1")
        .withServiceCalendar(
          new ServiceCalendar()
            .withId("ServiceCalendarId1")
            .withOperatingDays(mockOperatingDaysRelStructure("1", january1st))
        )
        .withOperatingDays(
          mockOperatingDaysInFrameRelStructure("2", january2nd)
        )
    );
    parser.setResultOnIndex(index);
    assertEquals(2, index.operatingDayById.getAll().size());
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
