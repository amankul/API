package com.org.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class FileUtils {


    public static String getJsonTextFromFile(String filePath) throws IOException {

        String jsonText = null;
        try (BufferedReader br = new BufferedReader(new FileReader(new File(filePath)))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            jsonText = sb.toString();
        }
        return jsonText;
    }


}
