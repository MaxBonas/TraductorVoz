# TraductorVoz

TraductorVoz is a small Swing application that uses the Microsoft Speech SDK to translate spoken Spanish into your chosen target language. The translation appears in a translucent window that stays on top of the screen and can optionally be saved to a text file.

## Prerequisites

- **Java 11** or later and Maven 3 installed.
- An **Azure Speech resource** with a subscription key and region.
- A microphone configured on your system.

Before running, set the following environment variables with your Azure Speech
resource credentials:

```bash
export AZURE_SPEECH_KEY=<your-key>
export AZURE_SPEECH_REGION=<your-region>
```

If these variables are not defined when the application starts, it will prompt
for the key and region and store them in a local `.traductor.env` file for
future runs. Delete or edit this file if your credentials change.

`SpeechConfigProvider` reads these variables when creating the SDK configuration.

## Building and Running

Compile the project using Maven:

```bash
mvn package
```

Run the application using Maven:

```bash
mvn exec:java -Dexec.mainClass=traductor.Main -Dexec.jvmArgs="--enable-native-access=ALL-UNNAMED"
```

The first time you run it, you will be asked for your Azure Speech credentials
if the environment variables are not already set.

## JDK 22+ Native Access

From JDK 22 onwards the Microsoft Speech SDK requires explicit native access
permission. When launching on JDK 22 or later, add
`--enable-native-access=ALL-UNNAMED` to the JVM arguments or enable the
equivalent option in your IDE.

Example:

```bash
mvn exec:java -Dexec.mainClass=traductor.Main \
  -Dexec.jvmArgs="--enable-native-access=ALL-UNNAMED"
```


## Running Tests

Execute unit tests using Maven:

```bash
mvn test
```

Launch the UI using Maven:

```bash
mvn exec:java -Dexec.mainClass=traductor.Main
```

Use the following properties to control subtitle file output:

- `subtitle.file` – path to the text file (default: `subtitles.txt`)
- `subtitle.append` – set to `true` to append, or `false` to overwrite (default)

If the specified path cannot be written, the application logs a warning and
disables subtitle file output instead of failing.

Example writing subtitles to `/tmp/subs.txt`:

```bash
mvn exec:java -Dexec.mainClass=traductor.Main -Dsubtitle.file=/tmp/subs.txt
```

To append to the same file instead of overwriting it:

```bash
mvn exec:java -Dexec.mainClass=traductor.Main -Dsubtitle.file=/tmp/subs.txt -Dsubtitle.append=true
```

You can change the translation target language by passing it as the first
command‑line argument or via the `target.language` system property. The default
target language is `en` (English). For example, to translate into French:

```bash
mvn exec:java -Dexec.mainClass=traductor.Main -Dexec.args="fr"
```

## Launching the UI

When the application starts, translation begins asynchronously so that the window appears immediately without blocking the Swing event thread. The `Main` class spawns a `SwingWorker` that calls `startTranslation()` in the background.

## Using OBS Studio

You can display the subtitles in OBS in two ways:

1. **Window capture** – add a *Window Capture* source and select the "Subtítulo en Vivo" window. Resize and crop as needed.
2. **Text from file** – add a *Text* source with *Read from file* enabled and point it to the subtitle file defined by `subtitle.file`.

Either approach lets you overlay the live translation in your stream.

## License

This project is licensed under the [MIT License](LICENSE).
