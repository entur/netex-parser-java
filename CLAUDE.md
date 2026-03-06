# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Java library for parsing NeTEx (Network Timetable Exchange) XML files into a queryable in-memory index. Designed for the Nordic NeTEx Profile. Published to Maven Central as `org.entur:netex-parser-java`.

## Build Commands

- **Build**: `mvn compile`
- **Test**: `mvn test`
- **Skip prettier during development**: `mvn test -Dprettier.skip=true`
- **Run a single test**: `mvn test -Dprettier.skip=true -pl . -Dtest=TestLineExport`
- **Format code (prettier)**: `mvn prettier:write`
- **Full build with formatting check (CI)**: `mvn verify -P prettierCheck`

Requires Java 17+. Uses JUnit 5 and AssertJ for tests.

## Architecture

### Parsing Pipeline

1. **`NetexParser`** (entry point, `org.entur.netex`) — accepts a ZIP path or `InputStream`, uses JAXB to unmarshal NeTEx XML into `PublicationDeliveryStructure` objects from the `netex-java-model` dependency.
2. **`NetexDocumentParser`** — routes each NeTEx frame type to its specialized parser. Handles recursive `CompositeFrame` nesting.
3. **Frame parsers** (`org.entur.netex.loader.parser`) — one per frame type (`ServiceFrameParser`, `TimeTableFrameParser`, `SiteFrameParser`, `ServiceCalendarFrameParser`, `ResourceFrameParser`, `FareFrameParser`, `VehicleScheduleFrameParser`). Each extends the abstract `NetexParser<T>` which enforces a two-step pattern: `parse(T node)` then `setResultOnIndex(NetexEntitiesIndex)`.

### Index Layer

- **`NetexEntitiesIndex`** (interface, `org.entur.netex.index.api`) — the query API. Provides typed indexes for all NeTEx entity types (Lines, StopPlaces, ServiceJourneys, DayTypes, etc.) plus cross-reference maps (e.g., QuayId-by-StopPointRef, StopPlaceId-by-QuayId).
- **`NetexEntityIndex<T>`** / **`VersionedNetexEntityIndex<T>`** — generic per-entity indexes. The versioned variant handles entities with multiple versions (StopPlace, Quay, TariffZone, etc.).
- **`NetexEntitiesIndexImpl`** — mutable implementation populated by the parsers.

### Key Dependency

`org.entur:netex-java-model` provides all JAXB-generated NeTEx model classes (`org.rutebanken.netex.model.*`).

## Adding Support for New NeTEx Entities

1. Add a getter to `NetexEntitiesIndex` interface
2. Add the backing collection and getter to `NetexEntitiesIndexImpl`
3. Parse the entity in the appropriate frame parser's `parse()` method
4. Populate the index in the frame parser's `setResultOnIndex()` method
