package server.service;

public interface IDictionaryService {
    String getText(String key);
    void setText(String key, String text);
    void deleteText(String key);
}
