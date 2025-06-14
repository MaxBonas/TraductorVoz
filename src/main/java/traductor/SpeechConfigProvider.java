package traductor;

import com.microsoft.cognitiveservices.speech.translation.SpeechTranslationConfig;

public class SpeechConfigProvider {
    private static final String SPEECH_KEY = "ClTwLQZs1pBc2bIKgeZLrWaLoUYgERDfgYkSFUd4jRRnlg1wADXUJQQJ99BFAC5RqLJXJ3w3AAAYACOGxHNI"; 
    private static final String REGION = "westeurope";

    public static SpeechTranslationConfig getConfig() {
        SpeechTranslationConfig config = SpeechTranslationConfig.fromSubscription(SPEECH_KEY, REGION);
        config.setSpeechRecognitionLanguage("es-ES");
        config.addTargetLanguage("en");
        return config;
    }
}

// Hacer Readme
//Poner comentarios