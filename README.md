# TraductorVoz

This project is a small Swing application that uses the Microsoft Speech SDK to translate spoken Spanish into another language in real time. The translated text appears in a translucent window that can stay on top of the screen.

## Subtitle File Output

The application can also write the latest translated text to a file so it can be used by streaming software such as OBS. The location of this file and whether new translations overwrite or append to it can be controlled with system properties:

- `subtitle.file` – path to the text file (default: `subtitles.txt`)
- `subtitle.append` – set to `true` to append to the file, or `false` to overwrite (default)

Example of running the program and writing subtitles to `/tmp/subs.txt`:

```bash
java -Dsubtitle.file=/tmp/subs.txt -jar target/TraductorVoz-0.0.1-SNAPSHOT.jar
```

## Using the Subtitle File in OBS

1. In OBS, add a new **Text (GDI+)** or **Text (FreeType 2)** source.
2. Enable the **Read from file** option.
3. Browse and select the subtitle file specified by `subtitle.file`.
4. Adjust font, size and positioning as desired.
5. When the application runs, OBS will update the text source automatically with the latest translation.

This allows the translated captions to appear directly in your stream.
