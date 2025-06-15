package traductor;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;
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

    public SubtitleFileWriter() {
        this(System.getProperty("subtitle.file", "subtitles.txt"),
             Boolean.parseBoolean(System.getProperty("subtitle.append", "false")));
    }

    public SubtitleFileWriter(String filePath, boolean append) {
        this.filePath = Paths.get(filePath);
        this.append = append;

        Path parent = this.filePath.toAbsolutePath().getParent();
        try {
            // 1) Asegura directorio
            if (parent != null) {
                Files.createDirectories(parent);
            }

            // 2) Comprueba bits POSIX: owner-write en el directorio
            if (parent != null) {
                Set<PosixFilePermission> parentPerms = Files.getPosixFilePermissions(parent);
                if (!parentPerms.contains(PosixFilePermission.OWNER_WRITE)) {
                    LOGGER.log(Level.WARNING,
                        "Parent directory not writable by owner (no OWNER_WRITE bit): {0}. Disabling file output.",
                        parent);
                    this.enabled = false;
                    return;
                }
            }

            // 3) Si el fichero ya existía, comprueba bits POSIX owner-write
            if (Files.exists(this.filePath)) {
                Set<PosixFilePermission> filePerms = Files.getPosixFilePermissions(this.filePath);
                if (!filePerms.contains(PosixFilePermission.OWNER_WRITE)) {
                    LOGGER.log(Level.WARNING,
                        "Subtitle file exists but is not owner-writable: {0}. Disabling file output.",
                        this.filePath);
                    this.enabled = false;
                    return;
                }
            }

            // 4) Prueba a crear / abrir el fichero
            Files.newOutputStream(this.filePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND).close();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING,
                "Unable to write to subtitle file {0}: {1}. Disabling file output.",
                new Object[]{this.filePath, e.getMessage()});
            this.enabled = false;
        } catch (UnsupportedOperationException e) {
            // En sistemas no-POSIX fallback a isWritable
            if (parent != null && !Files.isWritable(parent)) {
                this.enabled = false;
                return;
            }
            if (Files.exists(this.filePath) && !Files.isWritable(this.filePath)) {
                this.enabled = false;
                return;
            }
        }
    }

    public void write(String text) {
        if (!enabled) return;
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
