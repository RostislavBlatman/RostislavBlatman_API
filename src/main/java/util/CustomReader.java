package util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import entity.TestingValues;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

public class CustomReader {
    private static Map<String, TestingValues> dataMap;

    public static Map<String, TestingValues> readJson(String s) {
        try (JsonReader jsonReader = new JsonReader(new FileReader(new File(s)))) {
            Type token = new TypeToken<Map<String, TestingValues>>() {
            }.getType();
            return dataMap = new Gson().fromJson(jsonReader, token);
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new NullPointerException();
    }
}
