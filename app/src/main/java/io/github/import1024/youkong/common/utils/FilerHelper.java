package io.github.import1024.youkong.common.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by redback on 8/22/16.
 */
public class FilerHelper {
    public static boolean isMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }


    public static void writeByPath(String fileName, String content) {
        if (isMounted()) {
            File file = new File(fileName);
            FileWriter fileWriter = null;
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                fileWriter = new FileWriter(file);
                fileWriter.write(content);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileWriter != null) {
                    try {
                        fileWriter.flush();
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
