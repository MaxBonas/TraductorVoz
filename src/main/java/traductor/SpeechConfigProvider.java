package traductor;

import com.microsoft.cognitiveservices.speech.translation.SpeechTranslationConfig;

public class SpeechConfigProvider {
    private static final String KEY_ENV_VAR = "AZURE_SPEECH_KEY";
    private static final String REGION_ENV_VAR = "AZURE_SPEECH_REGION";

    public static SpeechTranslationConfig getConfig() {
        String key = System.getenv(KEY_ENV_VAR);
        if (key == null || key.isEmpty()) {
            throw new IllegalStateException("Environment variable " + KEY_ENV_VAR + " is not set");
        }

        String region = System.getenv(REGION_ENV_VAR);
        if (region == null || region.isEmpty()) {
            throw new IllegalStateException("Environment variable " + REGION_ENV_VAR + " is not set");
        }

        SpeechTranslationConfig config = SpeechTranslationConfig.fromSubscription(key, region);
        config.setSpeechRecognitionLanguage("es-ES");
        config.addTargetLanguage("en");
        return config;
    }
}
