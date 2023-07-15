package cf.r6dev.jetson.utils;

import cf.r6dev.jetson.Jetson;
import cf.r6dev.resources.ResourceLoader;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class JetRL extends ResourceLoader {
    @SuppressWarnings("unused") public static Color TITLE_BAR_COLOR = new Color(60, 62, 64);
    @SuppressWarnings("unused") public static Color PRIMARY_BORDER_COLOR = new Color(85, 85, 85);
    @SuppressWarnings("unused") public static Color SECONDARY_BORDER_COLOR = new Color(50, 50, 50);
    @SuppressWarnings("unused") public static Color PRIMARY_BACKGROUND_COLOR = new Color(51,53,55);
    @SuppressWarnings("unused") public static Color SECONDARY_BACKGROUND_COLOR = new Color(33, 33, 33);
    @SuppressWarnings("unused") public static Color PRIMARY_TEXT_COLOR  = Color.WHITE;
    @SuppressWarnings("unused") public static Color SECONDARY_TEXT_COLOR = new Color(0xafb1b3);
    @SuppressWarnings("unused") public static Color EDITOR_TEXT_COLOR = new Color(0xafb1b3);
    @SuppressWarnings("unused") public static Color SCROLL_BAR_HOVER_COLOR = new Color(0x9E9E9E);

    public JetRL(File resourceFolder) {
        // Set up all variables
        super(resourceFolder);
    }

    public static @NotNull Font registerFont(File fontToRegister, float size) throws FontFormatException, IOException {
        Font registeredFont = Font.createFont(Font.TRUETYPE_FONT, fontToRegister).deriveFont(size);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        ge.registerFont(registeredFont);
        return registeredFont;
    }

    public @NotNull Font createTerminalFont(int size) {
        if (Jetson.IS_WINDOWS) {
            return new Font("Consolas", Font.PLAIN, size);
        } else if (Jetson.IS_LINUX || Jetson.IS_MAC) {
            try {
                return registerFont(new File(RESOURCE_FOLDER + System.getProperty("file.separator") + "dejavu-mono.ttf"), size);
            } catch (IOException | FontFormatException e) {
                throw new RuntimeException(e);
            }
        }
        return new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    }

    public @NotNull Font createTerminalFont() {
        return createTerminalFont(12);
    }

    public @NotNull Font createMonoFont(float size) {
        if (Jetson.IS_WINDOWS) {
            try {
                return registerFont(new File(RESOURCE_FOLDER + System.getProperty("file.separator") + "jetbrains-mono.ttf"), size);
            } catch (IOException | FontFormatException e) {
                throw new RuntimeException(e);
            }
        }
        return createTerminalFont();
    }

    public @NotNull Font createMonoFont() {
        return createMonoFont(12);
    }

    public ImageIcon getIcon(@NotNull String iconName, Dimension size, int imageScaling) {
        ImageIcon returnImg;
        returnImg = new ImageIcon(RESOURCE_FOLDER + System.getProperty("file.separator") + iconName);
        return new ImageIcon(returnImg.getImage().getScaledInstance((int) size.getWidth(), (int) size.getHeight(), imageScaling));
    }
}
