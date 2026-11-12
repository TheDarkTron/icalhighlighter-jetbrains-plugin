# iCal Highlighter Plugin for PhpStorm

A PhpStorm plugin that provides syntax highlighting for iCal (.ics) files.

## Features

- Syntax highlighting for iCal files
- Support for iCal properties and components
- Color configuration for different iCal elements

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

The plugin will automatically activate for any files with the `.ics` extension. The following elements will be highlighted:

- Components (BEGIN:VCALENDAR, END:VCALENDAR, etc.)
- Property keys (DTSTART, SUMMARY, etc.)
- Property values
- Separators (:)

## Development

To run the plugin in a development instance:

```bash
./gradlew runIde
```

To build against an installed PhpStorm 2026.3 instead of downloading it:

```bash
./gradlew buildPlugin -PlocalPlatformPath="/path/to/PhpStorm.app/Contents"
```

To check binary compatibility with PhpStorm 2026.3:

```bash
./gradlew verifyPlugin
```

## License

This project is licensed under the MIT License. 
