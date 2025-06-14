package traductor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

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
    private final Path filePath;
    private final boolean append;

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
    }

    /**
     * Writes the given text to the subtitle file.
     */
    public void write(String text) {
        try {
            if (append) {
                Files.writeString(filePath, text + System.lineSeparator(),
                                   StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } else {
                Files.writeString(filePath, text,
                                   StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            System.err.println("Failed to write subtitle file: " + e.getMessage());
        }
    }
}
