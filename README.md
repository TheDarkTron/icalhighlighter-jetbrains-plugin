# iCal Highlighter Plugin for PhpStorm

A PhpStorm plugin for iCalendar and vCard syntax highlighting and templates.

## Features

- Separate file types for iCalendar (`.ics`, `.ical`, `.ifb`) and vCard (`.vcf`, `.vcard`)
- Separate colors for property names and values, parameter names and values, BEGIN/END keywords, and component names
- Separately colored semicolons in structured property values (such as `N`, `ADR`, and `RRULE`); escaped `\;` stays part of the text
- Quoted parameter values, comma-separated parameters, and folded content lines
- Configurable colors under **Settings → Editor → Color Scheme → iCal / vCard**
- Two file templates and ten live templates, restricted to the corresponding file type or HTTP request content type

## Building

Requires JDK 25. The plugin targets PhpStorm 2026.3 (IntelliJ Platform build 263).

1. Clone this repository
2. Open the project in IntelliJ IDEA
3. Run `./gradlew buildPlugin`

The plugin will be built in `build/distributions/`.

## Installation

1. In PhpStorm, go to Settings/Preferences → Plugins
2. Click the gear icon and select "Install Plugin from Disk..."
3. Navigate to the built plugin zip file and select it
4. Restart PhpStorm

## Usage

Highlighting activates automatically for the supported file extensions. For example:

```ical
BEGIN:VEVENT
DTSTART;TZID=Europe/Berlin:20261112T100000
ATTENDEE;CN="Alex Example";ROLE=REQ-PARTICIPANT:mailto:alex@example.com
SUMMARY:Planning meeting
END:VEVENT
```

Here `DTSTART` and `ATTENDEE` are property names; `TZID`, `CN`, and `ROLE` are parameter names. Parameter values use a separate color from the property values after the colon. `BEGIN` and `END` are keywords, and `VEVENT` is a component name.

### File templates

Use **File → New** (or the project directory's **New** menu) and select a template:

- **iCal Calendar** creates an `.ics` calendar with a demo event.
- **vCard Contact** creates a `.vcf` contact.

The calendar file template produces a complete file with a `VCALENDAR` wrapper. Add more components using the live templates below. Templates use example dates and contact information: edit these before use. File-template UIDs incorporate the filename and creation timestamp.

Customize these under **Settings → Editor → File and Code Templates**.

### Component snippets

Calendar snippets are available in iCalendar files; the `vcard` snippet is available in vCard files. They also work in matching HTTP Client request bodies as described below. Type an abbreviation and press **Tab** (or your configured live-template expansion key):

| Abbreviation | Content | Insert inside |
| --- | --- | --- |
| `icalcalendar` | Calendar with an event | Empty file |
| `icalevent` | Event | VCALENDAR |
| `icaltodo` | Todo | VCALENDAR |
| `icaljournal` | Journal | VCALENDAR |
| `icaltimezone` | Timezone with standard/daylight rules | VCALENDAR |
| `icalfreebusy` | Free/busy | VCALENDAR |
| `icalalarm` | Display alarm | VEVENT or VTODO |
| `icalstandard` | Standard time observance | VTIMEZONE |
| `icaldaylight` | Daylight time observance | VTIMEZONE |
| `vcard` | Contact | Empty file or after another vCard |

Snippets with UIDs stop at an editable UID field; replace the sample with a unique identifier. Edit snippets under **Settings → Editor → Live Templates → iCalendar** or **vCard**.

### HTTP Client request bodies

With the JetBrains HTTP Client plugin enabled, the same live templates work in `.http` and `.rest` request bodies. The request's `Content-Type` determines the available templates:

- `text/calendar`: `icalcalendar`, `icalevent`, `icaltodo`, and the other calendar component snippets.
- `text/vcard`: `vcard`.

For example, type `icalcalendar` at the indicated position and press **Tab**:

```http
PUT http://localhost:8080/calendars/demo.ics
Content-Type: text/calendar; charset=UTF-8

icalcalendar
```

For a contact, use `Content-Type: text/vcard` and type `vcard` in the body. Content types are case-insensitive and can include parameters such as `charset=UTF-8`. Each request in a file uses its own content type. Templates are restricted to inline request bodies; they are unavailable in request headers, response scripts, or requests without a matching content type.

The HTTP Client integration is optional; standalone calendar and vCard support also works without that plugin.

## Development

To run the plugin in a development instance:

```bash
./gradlew runIde
```

To build against an installed PhpStorm 2026.3 instead of downloading it:

```bash
./gradlew buildPlugin -PlocalPlatformPath="/path/to/PhpStorm.app/Contents"
```

Run the lexer, template-context, and real-editor live-template expansion regression tests with `./gradlew test`. To test against an installed IntelliJ IDEA, pass `-PlocalPlatformPath="/path/to/IntelliJ IDEA.app/Contents"`.

To check binary compatibility with PhpStorm 2026.3:

```bash
./gradlew verifyPlugin
```

## License

This project is licensed under the MIT License.
