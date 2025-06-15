package traductor;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;

import org.junit.jupiter.api.Test;

public class SubtitleFileWriterTest {

    @Test
    public void writesAndOverwritesFile() throws Exception {
        Path temp = Files.createTempFile("sub", ".txt");
        SubtitleFileWriter writer = new SubtitleFileWriter(temp.toString(), false);
        writer.write("hola");
        assertEquals("hola", Files.readString(temp));
        writer.write("adios");
        assertEquals("adios", Files.readString(temp));
    }

    @Test
    public void appendsLinesWhenEnabled() throws Exception {
        Path temp = Files.createTempFile("sub", ".txt");
        SubtitleFileWriter writer = new SubtitleFileWriter(temp.toString(), true);
        writer.write("uno");
        writer.write("dos");
        assertEquals("uno" + System.lineSeparator() + "dos" + System.lineSeparator(), Files.readString(temp));
    }

    @Test
    public void disablesWritingWhenPathNotWritable() throws Exception {
        Path dir = Files.createTempDirectory("subdir");
        Files.setPosixFilePermissions(dir, PosixFilePermissions.fromString("---------"));
        Path file = dir.resolve("out.txt");

        SubtitleFileWriter writer = new SubtitleFileWriter(file.toString(), false);
        writer.write("text");

        assertFalse(Files.exists(file));
    }
}
