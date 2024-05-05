package org.foxesworld.engine.utils;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class InputReader {

    public static String inputReader(String path) {
        Gson gson = new Gson();
        StringBuilder jsonStringBuilder = new StringBuilder();
        String line;
        try {
        InputStreamReader reader = new InputStreamReader(InputReader.class.getClassLoader().getResourceAsStream(path), StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(reader);

        while ((line = bufferedReader.readLine()) != null) {
            jsonStringBuilder.append(line);
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonStringBuilder.toString();
    }
}
