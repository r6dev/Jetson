package cf.r6dev.jetson.utils;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public final class JetReader {
    public static @NotNull String readFile(@NotNull File file) throws IOException {
        if (file.isFile() && file.canRead()) {
            BufferedReader fileReader = new BufferedReader(new FileReader(file));
            String data;
            StringBuilder returnString = new StringBuilder();
            while ((data = fileReader.readLine()) != null) {
                returnString.append(data);
            }

            fileReader.close();

            if (!returnString.isEmpty()) {
                return returnString.toString();
            }
        }
        return "";
    }
}
