# TraductorVoz

TraductorVoz is a small Swing application that uses the Microsoft Speech SDK to translate spoken Spanish into your chosen target language. The translation appears in a translucent window that stays on top of the screen and can optionally be saved to a text file.

## Prerequisites

- **Java 11** or later and Maven 3 installed.
- An **Azure Speech resource** with a subscription key and region.
- A microphone configured on your system.

Update `SpeechConfigProvider` with your subscription information before running.

## Building and Running

Compile the project using Maven:

```bash
mvn package
```

Launch the UI from the generated JAR:

```bash
java -jar target/TraductorVoz-0.0.1-SNAPSHOT.jar
```

Use the following properties to control subtitle file output:

- `subtitle.file` – path to the text file (default: `subtitles.txt`)
- `subtitle.append` – set to `true` to append, or `false` to overwrite (default)

Example writing subtitles to `/tmp/subs.txt`:

```bash
java -Dsubtitle.file=/tmp/subs.txt -jar target/TraductorVoz-0.0.1-SNAPSHOT.jar
```

## Launching the UI

When the application starts, translation begins asynchronously so that the window appears immediately without blocking the Swing event thread. The `Main` class spawns a `SwingWorker` that calls `startTranslation()` in the background.

## Using OBS Studio

You can display the subtitles in OBS in two ways:

1. **Window capture** – add a *Window Capture* source and select the "Subtítulo en Vivo" window. Resize and crop as needed.
2. **Text from file** – add a *Text* source with *Read from file* enabled and point it to the subtitle file defined by `subtitle.file`.

Either approach lets you overlay the live translation in your stream.
