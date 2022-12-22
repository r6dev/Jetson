package cf.rsix.Resources;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ResourceLoader {
    public static final Color TITLE_BAR_COLOR = new Color(60,62,64);

    public static final Color PRIMARY_BORDER_COLOR = new Color(85,85,85);
    public static final Color SECONDARY_BORDER_COLOR = new Color(50,50,50);

    //public static final Color PRIMARY_BACKGROUND_COLOR = new Color(51,53,55);
    public static final Color SECONDARY_BACKGROUND_COLOR = new Color(33,33,33);

    public static final Color PRIMARY_TEXT_COLOR = Color.WHITE;
    public static final Color SECONDARY_TEXT_COLOR = new Color(0xafb1b3);
    public static final Color EDITOR_TEXT_COLOR = new Color(0xafb1b3);

    public static Font getFont(String fontName) {
        String toLowerCaseInput = fontName.toLowerCase();
        if (toLowerCaseInput.equals("jetbrains mono") || toLowerCaseInput.equals("jetbrains")) {
            try {
                String resourceLink = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "rsc").exists() ? System.getProperty("file.separator") + "rsc" : System.getProperty("file.separator") + "resources";

                Font jetBrainsMonoFont = Font.createFont(Font.TRUETYPE_FONT, new File(System.getProperty("user.dir") + resourceLink + "\\jetbrains.ttf")).deriveFont(12f);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(jetBrainsMonoFont);
                return jetBrainsMonoFont;
            } catch (IOException | FontFormatException e) {
                throw new RuntimeException(e);
            }
        }
        return new Font("Segoe UI", Font.PLAIN, 12);
    }

    public static ImageIcon getIcon(String iconName, short iconSize, int imageScaling) {
        if (iconName.isEmpty()) {
            return new ImageIcon();
        }

        String resourceLink = new File(System.getProperty("user.dir") + "\\" + "rsc").exists() ? "\\rsc" : "\\resources";

        String iconNameLowercase = iconName.toLowerCase();
        ImageIcon returnImg = new ImageIcon(System.getProperty("user.dir") + resourceLink + "\\" + iconNameLowercase + ".png");

        return new ImageIcon(returnImg.getImage().getScaledInstance(iconSize, iconSize, imageScaling));
    }
}