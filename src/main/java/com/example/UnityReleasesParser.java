package com.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

public class UnityReleasesParser {

    private static final String FILE_PATH = "src/data/versions.txt"; // Update this path

    public static void main(String[] args) {
        try {
            // Read the file
            String jsonContent = readFile(FILE_PATH);
            // Parse JSON and find versions
            findVersions(jsonContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String readFile(String filePath) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                contentBuilder.append(currentLine);
            }
        }
        return contentBuilder.toString();
    }

    public static void writeVersionsToFile(List<String> versions) {
        String filePath = "src/data/res.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (int i = 0; i < versions.size(); i++) {
                writer.write(versions.get(i));
                // Append a comma after each version except the last one
                if (i < versions.size() - 1) {
                    writer.write(",");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void findVersions(String response) {
        JSONObject jsonResponse = new JSONObject(response);
        JSONObject data = jsonResponse.getJSONObject("data");
        JSONObject getUnityReleases = data.getJSONObject("getUnityReleases");
        JSONArray edges = getUnityReleases.getJSONArray("edges");

        List<String> versions = new ArrayList<>();

        Pattern pattern = Pattern.compile("^(2021\\.3|2022\\.2|2022\\.3|2023\\.1|2023\\.2|6000\\.|2022\\.1\\.(19|[2-9][0-9])).*f1$");
        for (int i = 0; i < edges.length(); i++) {
            JSONObject node = edges.getJSONObject(i).getJSONObject("node");
            String version = node.getString("version");
            Matcher matcher = pattern.matcher(version);
            if (matcher.matches()) {
                versions.add(version);
                System.out.println(version);
            }
        }

        System.out.println("Versions found: " + versions.size());
        writeVersionsToFile(versions);
    }
}