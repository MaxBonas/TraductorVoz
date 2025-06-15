package traductor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility for writing subtitle text to a file so it can be
 * used by external programs like OBS. The file location and
 * whether to append or overwrite can be configured using the
 * system properties:
 * <ul>
 *   <li>subtitle.file - path to the text file</li>
 *   <li>subtitle.append - set to true to append, false to overwrite</li>
 * </ul>
 */
public class SubtitleFileWriter {
    private static final Logger LOGGER = Logger.getLogger(SubtitleFileWriter.class.getName());
    private final Path filePath;
    private final boolean append;
    private boolean enabled = true;

    /**
     * Creates the writer using system properties or defaults.
     */
    public SubtitleFileWriter() {
        this(System.getProperty("subtitle.file", "subtitles.txt"),
             Boolean.parseBoolean(System.getProperty("subtitle.append", "false")));
    }

    /**
     * Creates the writer with the provided configuration.
     *
     * @param filePath path to the file
     * @param append   if true new text will be appended, otherwise the file is overwritten
     */
    public SubtitleFileWriter(String filePath, boolean append) {
        this.filePath = Paths.get(filePath);
        this.append = append;

        Path parent = this.filePath.toAbsolutePath().getParent();
        try {
            // 1) Aseguramos que exista la carpeta padre
            if (parent != null) {
                Files.createDirectories(parent);
            }

            // 2) Si el directorio padre NO es escribible, desactivamos la salida
            if (parent != null && !Files.isWritable(parent)) {
                LOGGER.log(Level.WARNING, "Parent directory not writable: {0}. Disabling file output.", parent);
                this.enabled = false;
                return;
            }

            // 3) Si el fichero YA existe y NO es escribible, desactivamos la salida
            if (Files.exists(this.filePath) && !Files.isWritable(this.filePath)) {
                LOGGER.log(Level.WARNING, "Subtitle file exists but is not writable: {0}. Disabling file output.", filePath);
                this.enabled = false;
                return;
            }

            // 4) Intentamos crear el fichero (CREATE + APPEND) para asegurarnos de que funciona
            Files.newOutputStream(this.filePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND).close();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING,
                "Unable to write to subtitle file {0}: {1}. Disabling file output.",
                new Object[]{this.filePath, e.getMessage()});
            this.enabled = false;
        }
    }

    /**
     * Writes the given text to the subtitle file.
     */
    public void write(String text) {
        if (!enabled) {
            return;
        }
        try {
            if (append) {
                Files.writeString(filePath, text + System.lineSeparator(),
                                  StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } else {
                Files.writeString(filePath, text,
                                  StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to write subtitle file", e);
        }
    }
}
