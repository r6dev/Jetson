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
                Font jetBrainsMonoFont = Font.createFont(Font.TRUETYPE_FONT, new File(System.getProperty("user.dir") + "\\JetBrainsMono-Regular.ttf")).deriveFont(12f);
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
        ImageIcon appIcon = new ImageIcon("logo.png");
        ImageIcon minimizeIcon = new ImageIcon("subtract_24px.png");
        ImageIcon playIcon = new ImageIcon("play_24px.png");
        ImageIcon folderIcon = new ImageIcon("folder_24px.png");
        ImageIcon sBGenIcon = new ImageIcon("sbgen.png");

        if (iconName.isEmpty()) {
            return new ImageIcon();
        }

        String iconNameLowercase = iconName.toLowerCase();

        switch (iconNameLowercase) {
            case "app" -> {
                return new ImageIcon(appIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_DEFAULT));
            }
            case "minimize" -> {
                return new ImageIcon(minimizeIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH));
            }
            case "folder" -> {
                return new ImageIcon(folderIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH));
            }
            case "play" -> {
                return new ImageIcon(playIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH));
            }
            case "sb" -> {
                return new ImageIcon(sBGenIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH));
            }
            default -> System.out.println("iconName value did not match any icon name");
        }
        return new ImageIcon();
    }
}