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

    public Font registerFont(File fontToRegister, float size) throws FontFormatException, IOException {
        Font registeredFont = Font.createFont(Font.TRUETYPE_FONT, fontToRegister).deriveFont(size);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        for (Font font : ge.getAllFonts()) {
            if (font.getFontName().equals(registeredFont.getFontName())) {
                System.out.println(font.getFontName());
                return font;
            }
        }

        ge.registerFont(registeredFont);
        return registeredFont;
    }

    public @NotNull Font getTerminalFont(int size) {
        if (Jetson.IS_WINDOWS) {
            return new Font("Consolas", Font.PLAIN, size);
        } else if (Jetson.IS_LINUX) {
            try {
                return registerFont(new File(RESOURCE_FOLDER + System.getProperty("file.separator") + "dejavu-mono"), size);
            } catch (IOException | FontFormatException e) {
                throw new RuntimeException(e);
            }
        } else if (Jetson.IS_MAC) {
            try {
                return registerFont(new File(RESOURCE_FOLDER + System.getProperty("file.separator") + "unifont.ttf"), size);
            } catch (IOException | FontFormatException e) {
                throw new RuntimeException(e);
            }
        }
        return new Font("Consolas", Font.PLAIN, 12);
    }

    public @NotNull Font getTerminalFont() {
        return getTerminalFont(12);
    }

    public @NotNull Font getMono(float size) {
        if (Jetson.IS_WINDOWS) {
            try {
                return registerFont(new File(RESOURCE_FOLDER + System.getProperty("file.separator") + "jetbrains-mono.ttf"), size);
            } catch (IOException | FontFormatException e) {
                throw new RuntimeException(e);
            }
        }
        return getTerminalFont();
    }

    public @NotNull Font getMono() {
        return getMono(12);
    }

    public ImageIcon getIcon(@NotNull String iconName, short iconSize, int imageScaling) {
        if (iconName.isEmpty()) {
            return new ImageIcon();
        }

        String iconNameLowercase = iconName.toLowerCase();
        ImageIcon returnImg = new ImageIcon(RESOURCE_FOLDER + System.getProperty("file.separator") + iconNameLowercase + ".png");

        return new ImageIcon(returnImg.getImage().getScaledInstance(iconSize, iconSize, imageScaling));
    }
}
