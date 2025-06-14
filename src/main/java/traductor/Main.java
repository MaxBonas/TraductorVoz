package traductor;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        String targetLanguage = "en";

        SwingUtilities.invokeLater(() -> {
            SpeechTranslatorService translatorService = new SpeechTranslatorService(targetLanguage);
            TranslatorAppView view = new TranslatorAppView(translatorService);
            view.setVisible(true);
        });
    }
}
