package cf.r6dev.jetson.utils;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class JetReader {
    public static @NotNull String readFile(@NotNull File file) throws IOException {
        if (file.isFile() && file.canRead()) {
            BufferedReader fileReader = new BufferedReader(new FileReader(file));
            String data;
            String returnString = "";
            while ((data = fileReader.readLine()) != null) {
                returnString = data;
            }
            if (!returnString.isEmpty()) {
                return returnString;
            }
        }
        return "";
    }
}
