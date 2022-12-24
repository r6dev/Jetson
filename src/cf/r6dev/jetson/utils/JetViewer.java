package cf.r6dev.jetson.utils;

import cf.r6dev.jetson.Jetson;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public final class JetViewer {
    public static boolean view(@NotNull File file) throws IOException {
        if (Jetson.IS_WINDOWS && Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(file);
            return true;
        } else if (Jetson.IS_LINUX || Jetson.IS_MAC) {
            Runtime.getRuntime().exec(new String[]{"/usr/bin/open", file.getAbsolutePath()});
            return true;
        } else {
            // Unknown OS?
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
                return true;
            }
        }
        return false;
    }
}
