package server.dao;

public interface ITextDao {
    String getText(String key);
    void setText(String key, String text);
    void deleteText(String key);
    void save();
}
