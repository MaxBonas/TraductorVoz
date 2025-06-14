package traductor;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class Main {
    public static void main(String[] args) {
        String targetLanguage = "en";

        if (args.length > 0 && args[0] != null && !args[0].trim().isEmpty()) {
            targetLanguage = args[0].trim();
        } else {
            String prop = System.getProperty("target.language");
            if (prop != null && !prop.trim().isEmpty()) {
                targetLanguage = prop.trim();
            }
        }

        final String lang = targetLanguage;

        SwingUtilities.invokeLater(() -> {
            SpeechTranslatorService translatorService = new SpeechTranslatorService(lang);
            TranslatorAppView view = new TranslatorAppView(translatorService);
            view.setVisible(true);

            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    translatorService.startTranslation();
                    return null;
                }
            };
            worker.execute();
        });
    }
}
