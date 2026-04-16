package umleditor.domain;

public interface DocumentObserver {
    void onDocumentChanged(DocumentEvent event);
}

