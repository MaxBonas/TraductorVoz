package traductor;

import com.microsoft.cognitiveservices.speech.translation.SpeechTranslationConfig;

public class SpeechConfigProvider {
    private static final String DEFAULT_REGION = "westeurope";

    public static SpeechTranslationConfig getConfig() {
        String key = System.getenv("AZURE_SPEECH_KEY");
        String region = System.getenv().getOrDefault("AZURE_SPEECH_REGION", DEFAULT_REGION);

        if (key == null || key.trim().isEmpty()) {
            throw new IllegalStateException("AZURE_SPEECH_KEY environment variable is not set");
        }

        SpeechTranslationConfig config = SpeechTranslationConfig.fromSubscription(key, region);
        config.setSpeechRecognitionLanguage("es-ES");
        config.addTargetLanguage("en");
        return config;
    }
}
