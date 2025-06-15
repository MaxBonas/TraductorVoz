package traductor;

import com.microsoft.cognitiveservices.speech.translation.SpeechTranslationConfig;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Scanner;

public class SpeechConfigProvider {
    private static final String KEY_ENV_VAR = "AZURE_SPEECH_KEY";
    private static final String REGION_ENV_VAR = "AZURE_SPEECH_REGION";
    private static final Path ENV_FILE = Paths.get(".traductor.env");

    public static SpeechTranslationConfig getConfig(String targetLanguage) {
        String key = getVariable(KEY_ENV_VAR, "Azure Speech Key");
        if (key == null || key.isEmpty()) {
            throw new IllegalStateException("Azure Speech Key not provided");
        }

        String region = getVariable(REGION_ENV_VAR, "Azure Speech Region");
        if (region == null || region.isEmpty()) {
            throw new IllegalStateException("Azure Speech Region not provided");
        }

        SpeechTranslationConfig config = SpeechTranslationConfig.fromSubscription(key, region);
        config.setSpeechRecognitionLanguage("es-ES");
        config.addTargetLanguage(targetLanguage);
        return config;
    }

    private static String getVariable(String name, String prompt) {
        String value = System.getenv(name);
        if (value != null && !value.isEmpty()) {
            return value;
        }

        Properties props = new Properties();
        if (Files.exists(ENV_FILE)) {
            try (InputStream in = Files.newInputStream(ENV_FILE)) {
                props.load(in);
                value = props.getProperty(name);
            } catch (IOException ignored) {}
        }

        if (value == null || value.isEmpty()) {
            Console console = System.console();
            if (console != null) {
                value = console.readLine(prompt + ": ");
            } else {
                System.out.print(prompt + ": ");
                Scanner sc = new Scanner(System.in);
                value = sc.nextLine();
            }
            if (value != null) value = value.trim();
            if (value != null && !value.isEmpty()) {
                props.setProperty(name, value);
                try (OutputStream out = Files.newOutputStream(ENV_FILE)) {
                    props.store(out, null);
                } catch (IOException ignored) {}
            }
        }
        return value;
    }
}
