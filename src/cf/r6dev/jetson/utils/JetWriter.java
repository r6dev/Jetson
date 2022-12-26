package cf.r6dev.jetson.utils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public final class JetWriter {

    public static boolean write(@NotNull File file, String data, boolean takeNewLineSyntax) throws IOException {
        if (file.isFile() && file.canWrite()) {
            FileWriter writer = new FileWriter(file);
            if (takeNewLineSyntax) {
                writer.write(data.replace("\\n", "\n"));
            } else {
                writer.write(data);
            }
            writer.flush();
            writer.close();
            return true;
        }
        return false;
    }

    public static boolean write(@NotNull File file, String data) throws IOException {
        return write(file, data, false);
    }

    public static void main(String[] args) throws IOException {
        write(new File("D:\\Sandbox\\testthingyding.txt"), "TESTING 123");
    }
}
