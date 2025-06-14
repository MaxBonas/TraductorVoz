package traductor;

import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.translation.*;

public class SpeechTranslatorService {
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
            SpeechTranslationConfig config = SpeechConfigProvider.getConfig();
            recognizer = new TranslationRecognizer(config);

            recognizer.recognized.addEventListener((s, e) -> {
                if (e.getResult().getReason() == ResultReason.TranslatedSpeech && listener != null) {
             
                    String original = e.getResult().getText();
                    String translated = e.getResult().getTranslations().get(targetLanguage);
                   	System.out.println(translated);
                   	System.out.println();
                    if (translated != null && !translated.trim().isEmpty()) {
                        listener.onFinalResult(original, translated);
                    }
                }
            });

            recognizer.canceled.addEventListener((s, e) -> {
                String details = e.getErrorDetails();
                System.out.println("Error: " + details);
                if (listener != null) listener.onError(details);

             
            });

            recognizer.sessionStopped.addEventListener((s, e) -> {
            	System.out.println("Sesion terminada");
                if (listener != null) listener.onSessionStopped();
            });

            recognizer.startContinuousRecognitionAsync().get();

        } catch (Exception e) {
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
            if (listener != null) listener.onError(e.getMessage());
        }
    }

 
}
