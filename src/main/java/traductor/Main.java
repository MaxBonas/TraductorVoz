package traductor;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class Main {
    public static void main(String[] args) {
        String targetLanguage = "en";

        SwingUtilities.invokeLater(() -> {
            SpeechTranslatorService translatorService = new SpeechTranslatorService(targetLanguage);
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
