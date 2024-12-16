package com.carvaluation;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CarFileUtils {
    private static final Pattern REG_PATTERN = Pattern.compile("[A-Z]{2}\\d{2}\\s?[A-Z]{3}|[A-Z]{2}\\d{2}\\s?[A-Z]{2}[A-Z0-9]");

    public static List<String> extractRegistrationsFromFile(String filename) {
        List<String> registrations = new ArrayList<>();
        try (InputStream is = CarFileUtils.class.getClassLoader().getResourceAsStream(filename);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = REG_PATTERN.matcher(line);
                while (matcher.find()) {
                    registrations.add(matcher.group().replaceAll("\\s", ""));
                }
            }
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("Error reading input file: " + filename + " - " + e.getMessage());
        }
        return registrations;
    }

    public static Map<String, CarDetails> readExpectedOutput(String filename) {
        Map<String, CarDetails> expectedDetails = new HashMap<>();
        try (InputStream is = CarFileUtils.class.getClassLoader().getResourceAsStream(filename);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            
            // Skip header
            reader.readLine();
            
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 4);
                if (parts.length == 4) {
                    CarDetails details = new CarDetails(
                        parts[0].trim(),
                        parts[1].trim(),
                        parts[2].trim(),
                        parts[3].trim()
                    );
                    expectedDetails.put(parts[0].trim(), details);
                }
            }
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("Error reading output file: " + filename + " - " + e.getMessage());
        }
        return expectedDetails;
    }
}