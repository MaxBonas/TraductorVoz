package traductor;

import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.translation.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SpeechTranslatorService {
    private static final Logger LOGGER = Logger.getLogger(SpeechTranslatorService.class.getName());
    private TranslationRecognizer recognizer;
    private TranslationListener listener;
    private final String targetLanguage;

    public SpeechTranslatorService(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    public void setTranslationListener(TranslationListener listener) {
        this.listener = listener;
    }

    public void startTranslation() {
     

        try {
            SpeechTranslationConfig config = SpeechConfigProvider.getConfig(targetLanguage);
            recognizer = new TranslationRecognizer(config);

            recognizer.recognized.addEventListener((s, e) -> {
                if (e.getResult().getReason() == ResultReason.TranslatedSpeech && listener != null) {
             
                    String original = e.getResult().getText();
                    String translated = e.getResult().getTranslations().get(targetLanguage);
                    if (translated != null) {
                        LOGGER.info(translated);
                    }
                    if (translated != null && !translated.trim().isEmpty()) {
                        listener.onFinalResult(original, translated);
                    }
                }
            });

            recognizer.canceled.addEventListener((s, e) -> {
                String details = e.getErrorDetails();
                LOGGER.log(Level.SEVERE, "Error: {0}", details);
                if (listener != null) listener.onError(details);

             
            });

            recognizer.sessionStopped.addEventListener((s, e) -> {
                LOGGER.info("Session ended");
                if (listener != null) listener.onSessionStopped();
            });

            recognizer.startContinuousRecognitionAsync().get();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to start translation", e);
            if (listener != null) listener.onError(e.getMessage());

        }
    }

    public void stopTranslation() {
        try {
            if (recognizer != null) {
                recognizer.stopContinuousRecognitionAsync().get();
                recognizer.close();
                recognizer = null;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to stop translation", e);
            if (listener != null) listener.onError(e.getMessage());
        }
    }

 
}
