package com.resources;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ResourceLoader {
    public Color TITLE_BAR_COLOR = new Color(60,62,64);

    public Color PRIMARY_BORDER_COLOR = new Color(85,85,85);
    //public Color SECONDARY_BORDER_COLOR = new Color(50,50,50);

    public Color PRIMARY_BACKGROUND_COLOR = new Color(51,53,55);
    //public Color SECONDARY_BACKGROUND_COLOR = new Color(33,33,33);

    public Color PRIMARY_TEXT_COLOR = Color.WHITE;
    public Color SECONDARY_TEXT_COLOR = new Color(0xafb1b3);
    //public Color EDITOR_TEXT_COLOR = new Color(0xafb1b3);

    public Font getFont(String fontName) {
        String toLowerCaseInput = fontName.toLowerCase();
        if (toLowerCaseInput.equals("jetbrains mono") || toLowerCaseInput.equals("jetbrains")) {
            try {
                Boolean rscExists = new File(System.getProperty("user.dir") + "rsc").exists();
                String resourceLink = rscExists ? "rsc" : "resources";

                Font jetBrainsMonoFont = Font.createFont(Font.TRUETYPE_FONT, new File(System.getProperty("user.dir") + "\\" + resourceLink + "\\JetBrainsMono-Regular.ttf")).deriveFont(12f);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(jetBrainsMonoFont);
                return jetBrainsMonoFont;
            } catch (IOException | FontFormatException e) {
                throw new RuntimeException(e);
            }
        }
        return new Font("Segoe UI", Font.PLAIN, 12);
    }

    public ImageIcon getIcon(String iconName, short iconSize) {
        if (iconName.isEmpty()) {
            return new ImageIcon();
        }

        Boolean rscExists = new File(System.getProperty("user.dir") + "rsc").exists();
        String resourceLink = rscExists ? "rsc" : "resources";

        String iconNameLowercase = iconName.toLowerCase();
        ImageIcon returnImg = new ImageIcon("\\" + resourceLink + "\\" + iconNameLowercase + ".png");

        return new ImageIcon(returnImg.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH));
    }
}