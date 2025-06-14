package traductor;

public interface TranslationListener {
    void onPartialResult(String original, String translated);
    void onFinalResult(String original, String translated);
    void onError(String message);
    void onSessionStopped();
}
