package cf.r6dev.jetson.utils;

import cf.r6dev.jetson.Jetson;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public final class JetCorrupter {
    public static boolean corrupt(@NotNull File file) throws IOException {
        if (file.isDirectory()) {
            File[] listOfFiles = file.listFiles();

            if (listOfFiles != null) {
                for (File selectedFile : listOfFiles) {
                    if (selectedFile.isFile()) {
                        if (selectedFile.canWrite()) {
                            Jetson.verifyJetsonDirectory();
                            return JetWriter.write(selectedFile, JetReader.readFile(selectedFile) + JetReader.readFile(Jetson.getJetsonTempTxt()));
                        }
                    } else {
                        corrupt(selectedFile);
                    }
                }
            }
        } else if (file.isFile() && file.canWrite()) {
            return JetWriter.write(file, JetReader.readFile(file) + JetReader.readFile(Jetson.getJetsonTempTxt()));
        }

        return false;
    }
}
