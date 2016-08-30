package io.github.import1024.youkong.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by caofeng on 16-8-20.
 */
public class StringUtil {
    public static String inStreamToString(InputStream inputStream) {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        try {
            while ( (line = reader.readLine()) != null)
                builder.append(line);
            reader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return builder.toString();
    }

    public static String stringToFile(String string, String path, String name)  {
        File file = new File(path,name);
        if (!name.equals("latest") && file.exists()){
            file.getPath();
        }
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(string);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getPath();
    }


}
