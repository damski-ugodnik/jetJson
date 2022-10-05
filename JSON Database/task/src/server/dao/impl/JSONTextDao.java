package server.dao.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import server.dao.ITextDao;
import server.utils.Constants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class JSONTextDao implements ITextDao {
    private Map<String, String> dataStorage;
    private Map<String, JsonElement> objDataStorage;
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public JSONTextDao() {
        init();
    }

    @Override
    public String getText(String key) {
        Gson gson = new Gson();
        try {
            JsonElement jsonObject = gson.fromJson(key, JsonElement.class);
            JsonArray jsonArray = jsonObject.getAsJsonArray();
            String pkey = jsonArray.get(0).getAsString();
            var value = getValue(pkey);
            JsonElement element = gson.fromJson(value, JsonElement.class);
            String fieldName;
            for (int i = 1; i < jsonArray.size(); i++) {
                fieldName = jsonArray.get(i).getAsString();
                element = element.getAsJsonObject().get(fieldName);
                if (element == null) {
                    throw new IllegalArgumentException(String.format("No such field: %s in object by key: %s", fieldName, pkey));
                }
            }
            return element.toString();
        } catch (Exception e) {
            return gson.toJson(getValue(key));
        }
    }

    @Override
    public void setText(String key, String text) {
        Gson gson = new Gson();
        JsonElement jsonKey = gson.fromJson(key, JsonElement.class);
        JsonElement jsonValue = gson.fromJson(text, JsonElement.class);

        if (jsonKey.isJsonPrimitive()) {
            objDataStorage.put(jsonKey.getAsString(), jsonValue);
            save();
            return;
        }
        JsonArray keys = jsonKey.getAsJsonArray();
        if (keys.size() == 1) {
            objDataStorage.put(jsonKey.getAsString(), jsonValue);
        }
        JsonElement startKey = keys.remove(0);
        String keyStr = startKey.getAsJsonPrimitive().getAsString();

        JsonElement elementInStorage = gson.toJsonTree(objDataStorage.get(keyStr));
        JsonObject jsonObject;
        if (elementInStorage != null && elementInStorage.isJsonObject()) {
            jsonObject = elementInStorage.getAsJsonObject();
        } else {
            jsonObject = new JsonObject();
            objDataStorage.put(startKey.getAsString(), jsonObject);
        }
        initObj(jsonObject, keys, jsonValue);
        save();
        /*Gson gson = new Gson();
        JsonElement keyJson = gson.fromJson(key, JsonElement.class);
        if (keyJson.isJsonArray()) {
            JsonArray keys = keyJson.getAsJsonArray();
            JsonObject jsonObject = new JsonObject();
            for (int i = 0; i < keys.size() - 1; i++) {
                jsonObject.addProperty(keys.get(i).getAsString(), "");
                jsonObject = jsonObject.get(keys.get(i).getAsString()).getAsJsonObject();
            }
            jsonObject.addProperty(keys.get(keys.size() - 1).getAsString(), text);
            if (!dataStorage.containsKey(keys.get(0).getAsString())) {
                throw new IllegalArgumentException("No such key");
            }
            dataStorage.put(keys.get(0).getAsString(), jsonObject.getAsString());
            save();
            return;
        }
        dataStorage.put(key, text);
        save();*/
    }

    private JsonElement initObj(JsonObject object, JsonArray keys, JsonElement value) {
        JsonElement currentKey = keys.remove(0);
        JsonElement internalProperty = object.get(currentKey.getAsString());

        object.add(currentKey.getAsString(), initObj(internalProperty.getAsJsonObject(),
                keys.deepCopy(), value));
        if (keys.size() == 0) {
            return value;
        }
        return object;
    }

    @Override
    public void deleteText(String key) {
        if (!dataStorage.containsKey(key)) {
            throw new IllegalArgumentException("No such key");
        }
        dataStorage.remove(key);
        save();
    }

    @Override
    public void save() {
        Lock writeLock = lock.writeLock();
        try {
            writeLock.lock();
            Gson gson = new Gson();
            Files.writeString(Paths.get(Constants.DB_FILENAME), gson.toJson(objDataStorage), StandardOpenOption.WRITE);
            writeLock.unlock();
        } catch (IOException e) {
            throw new RuntimeException("Could not write data to file");
        }
    }

    private void init() {
        Lock readLock = lock.readLock();
        try {
            readLock.lock();
            Gson gson = new Gson();
            Path path = Paths.get(Constants.DB_FILENAME);
            String content = Files.readString(path);
            objDataStorage = gson.fromJson(content, new TypeToken<>() {
            }.getType());
            if (objDataStorage == null) {
                objDataStorage = new HashMap<>();
            }
            readLock.unlock();
        } catch (IOException e) {
            readLock.unlock();
            throw new RuntimeException("Could not read data from file");
        }
    }

    private String getValue(String key) {
        String value = dataStorage.get(key);
        if (value == null) {
            throw new IllegalArgumentException("No such key");
        }
        return value;
    }
}
