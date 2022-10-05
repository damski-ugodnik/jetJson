package server.service.impl;

import server.dao.ITextDao;
import server.service.IDictionaryService;

public class DictionaryService implements IDictionaryService {
    private final ITextDao textDao;

    public DictionaryService(ITextDao textDao) {
        this.textDao = textDao;
    }

    @Override
    public String getText(String key) {
        return textDao.getText(key);
    }

    @Override
    public void setText(String key, String text) {
        textDao.setText(key, text);
    }

    @Override
    public void deleteText(String key) {
        textDao.deleteText(key);
    }
}
