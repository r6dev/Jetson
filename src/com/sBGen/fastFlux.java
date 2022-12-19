package com.sBGen;

import com.resources.ResourceLoader;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;

public class fastFlux {
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

        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setTitle("Fast Flux");
        frame.setIconImage(resourceLoader.getIcon("rj-logo", (short) 24).getImage());
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(primaryBackgroundColor);
        frame.getRootPane().setForeground(primaryTextColor);
        frame.getRootPane().setBorder(BorderFactory.createLineBorder(primaryBorderColor));

        // Title bar
        JPanel titleBar = new JPanel();
        frame.add(titleBar, BorderLayout.NORTH);
        titleBar.setBackground(titleBarColor);
        titleBar.setPreferredSize(new Dimension(140, 25));
        titleBar.setLayout(new BorderLayout());
        titleBar.setBorder(BorderFactory.createMatteBorder(0,0,1,0, primaryBorderColor));

        JLabel title = new JLabel();
        titleBar.add(title, BorderLayout.CENTER);
        title.setText(frame.getTitle());
        title.setFont(resourceLoader.getFont("jetbrains"));
        title.setForeground(secondaryTextColor);
        title.setHorizontalAlignment(JLabel.CENTER);

        // Content
        JPanel content = new JPanel();
        frame.add(content, BorderLayout.CENTER);
        content.setPreferredSize(new Dimension(140, 25));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(3,0,3,0));
        content.setBackground(primaryBackgroundColor);

        // inputOne
        JTextField inputOne = new JTextField();
        content.add(inputOne);
        String inputOnePlaceholderString = "Directory or file";
        inputOne.setFont(new Font("Consolas", Font.PLAIN, 12));
        inputOne.setBorder(new EmptyBorder(0,10,0,0));
        inputOne.setForeground(primaryTextColor);
        inputOne.setBackground(primaryBackgroundColor);
        inputOne.setToolTipText("Directory to overflow");

        inputOne.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                inputOne.setBackground(new Color(0x458ea8));
            }

            @Override
            public void focusLost(FocusEvent e) {
                inputOne.setBackground(primaryBackgroundColor);
            }
        });

        // JFrame setup
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        //Logic
        inputOne.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    System.out.println("");
                }
            }
        });

        inputOne.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (inputOne.getText().equals(inputOnePlaceholderString)) {
                    inputOne.setText("");
                    inputOne.setForeground(primaryTextColor);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (inputOne.getText().isBlank()) {
                    inputOne.setText(inputOnePlaceholderString);
                    inputOne.setForeground(secondaryTextColor);
                }
            }
        });
    }
}
