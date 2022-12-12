package com.sBGen;

import com.resources.ResourceLoader;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class sBOperator {
    public static void main(String[] args) {
        // Config
        ResourceLoader resourceLoader = new ResourceLoader();

        Color titleBarColor = resourceLoader.TITLE_BAR_COLOR;

        Color primaryBorderColor = resourceLoader.PRIMARY_BORDER_COLOR;
        //Color secondaryBorderColor = resourceLoader.SECONDARY_BORDER_COLOR;

        Color primaryBackgroundColor = resourceLoader.PRIMARY_BACKGROUND_COLOR;
        //Color secondaryBackgroundColor = resourceLoader.SECONDARY_BACKGROUND_COLOR;

        Color primaryTextColor = resourceLoader.PRIMARY_TEXT_COLOR;
        Color secondaryTextColor = resourceLoader.SECONDARY_TEXT_COLOR;
        //Color editorTextColor = resourceLoader.EDITOR_TEXT_COLOR;

        // Title bar
        JPanel titleBar = new JPanel();
        titleBar.setBackground(titleBarColor);
        titleBar.setPreferredSize(new Dimension(140, 25));
        titleBar.setLayout(new BorderLayout());
        titleBar.setBorder(BorderFactory.createMatteBorder(0,0,1,0, primaryBorderColor));

        JLabel title = new JLabel("rJ Toolbox");
        titleBar.add(title, BorderLayout.CENTER);
        title.setFont(resourceLoader.getFont("jetbrains"));
        title.setForeground(secondaryTextColor);
        title.setHorizontalAlignment(JLabel.CENTER);

        // Content
        JPanel content = new JPanel();
        content.setPreferredSize(new Dimension(140, 25));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(3,0,3,0));
        content.setBackground(primaryBackgroundColor);

        // sBGen
        JTextField sBGen = new JTextField("sBGen");
        content.add(sBGen);
        sBGen.setFont(resourceLoader.getFont("jetbrains"));
        sBGen.setBorder(new EmptyBorder(0,15,0,0));
        sBGen.setForeground(primaryTextColor);
        sBGen.setBackground(primaryBackgroundColor);

        sBGen.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                sBGen.setBackground(new Color(0x458ea8));
            }

            @Override
            public void focusLost(FocusEvent e) {
                sBGen.setBackground(primaryBackgroundColor);
            }
        });

        // JFrame setup
        JFrame frame = new JFrame();
        frame.setTitle("rJ Toolbox");
        frame.setIconImage(resourceLoader.getIcon("app", (short) 24).getImage());
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(primaryBackgroundColor);
        frame.getRootPane().setForeground(primaryTextColor);
        frame.getRootPane().setBorder(BorderFactory.createLineBorder(primaryBorderColor));
        frame.setLayout(new BorderLayout());
        frame.add(titleBar, BorderLayout.NORTH);
        frame.add(content, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
