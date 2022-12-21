package com.rsix.JetSon;

import com.rsix.Resources.ResourceLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class jetSon extends JFrame {
    public static JTextField inputField = new JTextField();
    public static JLabel userNameLabel = new JLabel(System.getProperty("user.name"));
    public static JPanel infoPanel = new JPanel();
    public static File inputFileSelected;
    public static File previousDir;

    public static void JCMSearch(File dir) {
        try {
            File[] listOfFiles = dir.listFiles();

            if (listOfFiles != null) {
                for (File selectedFile : listOfFiles) {
                    if (selectedFile.isFile()) {
                        FileWriter corrupter = new FileWriter(selectedFile);
                        if (selectedFile.canWrite()) {
                            corrupter.write("JCM Operation: " + Math.random());
                            corrupter.close();
                        }
                    } else {
                        JCMSearch(selectedFile);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void JCMCorrupt(File file) throws IOException {
        FileWriter corrupter = new FileWriter(file);
        corrupter.write("JCM Operation: " + Math.floor(Math.random() / 100));
        corrupter.close();
    }

    public static void deselectAllItems() {
        // Deselect all items
        inputFileSelected = null;
        userNameLabel.setText(System.getProperty("user.name"));
        userNameLabel.requestFocus();
    }

    public static void listDirectory(JPanel list, File[] listOfFiles) {
        if (listOfFiles != null && listOfFiles.length > 0) {
            previousDir = listOfFiles[0].getParentFile().getParentFile();
            for (File selectedFile : listOfFiles) {
                // Creates file/dir listing

                JPanel fileListingPanel = new JPanel();
                list.add(fileListingPanel);
                fileListingPanel.setBorder(new EmptyBorder(0,12,0,0));
                fileListingPanel.setBackground(ResourceLoader.TITLE_BAR_COLOR);
                fileListingPanel.setPreferredSize(new Dimension(180, 16));
                fileListingPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
                fileListingPanel.setLayout(new BoxLayout(fileListingPanel, BoxLayout.LINE_AXIS));

                JLabel fileNameLabel = new JLabel(selectedFile.getName());
                fileNameLabel.setForeground(ResourceLoader.PRIMARY_TEXT_COLOR);
                fileNameLabel.setFont(ResourceLoader.getFont("jetbrains"));

                String toolTipAppend = (selectedFile.getName().contains("sys") ? "(sys) " + selectedFile.getAbsolutePath() : selectedFile.getAbsolutePath());

                // Determines which type of listing it is (file or dir)

                if (selectedFile.isFile()) {
                    JLabel isFilePrefix = new JLabel("file: ");
                    isFilePrefix.setForeground(ResourceLoader.SECONDARY_TEXT_COLOR);
                    isFilePrefix.setFont(ResourceLoader.getFont("jetbrains"));

                    fileListingPanel.add(isFilePrefix);
                    fileListingPanel.add(fileNameLabel);
                    fileListingPanel.setToolTipText("File: " + toolTipAppend);
                } else {
                    JLabel isDirPrefix = new JLabel(System.getProperty("file.separator"));
                    isDirPrefix.setForeground(ResourceLoader.SECONDARY_TEXT_COLOR);
                    isDirPrefix.setFont(ResourceLoader.getFont("jetbrains"));

                    fileListingPanel.add(isDirPrefix);
                    fileListingPanel.add(fileNameLabel);
                    fileListingPanel.setToolTipText("Dir: " + toolTipAppend);
                }

                // All mouse events for JPanel

                Color focusedOnColor = Color.BLUE;

                fileListingPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (fileListingPanel.getBackground() != focusedOnColor) {
                            fileListingPanel.setBackground(new Color(0x9E9E9E));
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (fileListingPanel.getBackground() != focusedOnColor) {
                            fileListingPanel.setBackground(ResourceLoader.TITLE_BAR_COLOR);
                        }
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        // Select item
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            inputFileSelected = selectedFile;
                            fileListingPanel.requestFocus();
                            userNameLabel.setText(selectedFile.getName());
                        }


                        // Open directory
                        if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                            if (selectedFile.isDirectory()) {
                                list.removeAll();
                                list.repaint();
                                File[] selectedListOfFiles = selectedFile.listFiles();
                                if (selectedListOfFiles != null) {
                                    listDirectory(list, selectedListOfFiles);
                                }

                                deselectAllItems();
                            }
                        }
                    }
                });

                fileListingPanel.addFocusListener(new FocusListener() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        fileNameLabel.setText("[" + selectedFile.getName() + "]");
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        fileNameLabel.setText(selectedFile.getName());
                    }
                });
                list.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON3) {
                            if (previousDir != null) {
                                if (previousDir.isDirectory()) {
                                    list.removeAll();
                                    list.repaint();

                                    File[] parentListOfFiles = previousDir.listFiles();
                                    if (parentListOfFiles != null) {
                                        listDirectory(list, parentListOfFiles);
                                    }

                                    inputField.setText("");

                                    deselectAllItems();
                                }
                            }
                        } else if (e.getButton() == MouseEvent.BUTTON1) {
                            deselectAllItems();
                        }
                    }
                });
            }
        }
    }

    public jetSon() {
        // Setup

        setTitle("Jetson");
        setIconImage(ResourceLoader.getIcon("jetson-icon", (short) 96, Image.SCALE_DEFAULT).getImage());
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getRootPane().setBorder(BorderFactory.createLineBorder(ResourceLoader.PRIMARY_BORDER_COLOR));
        getContentPane().setBackground(ResourceLoader.TITLE_BAR_COLOR);
        setUndecorated(true);

        // Title bar

        JPanel titleBar = new JPanel();
        getContentPane().add(titleBar, BorderLayout.NORTH);
        titleBar.setPreferredSize(new Dimension(180, 42));
        titleBar.setLayout(new BorderLayout());
        titleBar.setBackground(ResourceLoader.TITLE_BAR_COLOR);

        // Title bar -- Top

        JPanel titleBarTop = new JPanel();
        titleBar.add(titleBarTop);
        titleBarTop.setLayout(new BoxLayout(titleBarTop, BoxLayout.LINE_AXIS));
        titleBarTop.setBackground(ResourceLoader.TITLE_BAR_COLOR);
        titleBar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0,0,1,0, ResourceLoader.PRIMARY_BORDER_COLOR), new EmptyBorder(5,12,5,0)));

        JLabel titleBarLabel = new JLabel(getTitle() + " - ");
        titleBarTop.add(titleBarLabel);
        titleBarLabel.setFont(ResourceLoader.getFont("jetbrains"));
        titleBarLabel.setForeground(ResourceLoader.PRIMARY_TEXT_COLOR);

        titleBarTop.add(userNameLabel);
        userNameLabel.setFont(ResourceLoader.getFont("jetbrains"));
        userNameLabel.setForeground(ResourceLoader.SECONDARY_TEXT_COLOR);

        // Title bar -- Bottom

        JPanel titleBarBottom = new JPanel();
        titleBar.add(titleBarBottom, BorderLayout.SOUTH);
        titleBarBottom.setLayout(new BoxLayout(titleBarBottom, BoxLayout.LINE_AXIS));
        titleBarBottom.setBackground(ResourceLoader.TITLE_BAR_COLOR);

        JLabel sysInfoLabel = new JLabel("- " + System.getProperty("os.arch") + System.getProperty("file.separator") + System.getProperty("os.version") + System.getProperty("file.separator") + "JRE " + System.getProperty("java.version"));
        titleBarBottom.add(sysInfoLabel);
        sysInfoLabel.setFont(ResourceLoader.getFont("jetbrains"));
        sysInfoLabel.setForeground(ResourceLoader.SECONDARY_TEXT_COLOR);

        // Input field setup

        String inputFieldPlaceholder = "input a dir or \"help1\"";
        getContentPane().add(inputField);
        inputField.setBackground(ResourceLoader.SECONDARY_BACKGROUND_COLOR);
        inputField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0,0,1,0, ResourceLoader.SECONDARY_BORDER_COLOR), new EmptyBorder(0,12,0,0)));
        inputField.setForeground(ResourceLoader.EDITOR_TEXT_COLOR);
        inputField.setFont(new Font("Consolas", Font.PLAIN, 12));
        inputField.setPreferredSize(new Dimension(180, 29));

        // Functional placeholder text
        inputField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (inputField.getText().equals(inputFieldPlaceholder)) {
                    inputField.setText("");
                    inputField.setFont(new Font("Consolas", Font.PLAIN, 12));
                    inputField.setForeground(ResourceLoader.PRIMARY_TEXT_COLOR);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (inputField.getText().isBlank()) {
                    inputField.setText(inputFieldPlaceholder);
                    inputField.setFont(new Font("Consolas", Font.PLAIN, 10));
                    inputField.setForeground(ResourceLoader.SECONDARY_TEXT_COLOR);
                }
            }
        });

        infoPanel.setBackground(ResourceLoader.TITLE_BAR_COLOR);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JScrollPane infoScrollPane = new JScrollPane(infoPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        getContentPane().add(infoScrollPane);
        infoScrollPane.setBorder(null);
        infoScrollPane.setPreferredSize(new Dimension(180, 128));
        infoScrollPane.setComponentZOrder(infoScrollPane.getVerticalScrollBar(), 0);
        infoScrollPane.setComponentZOrder(infoScrollPane.getViewport(), 1);
        infoScrollPane.getVerticalScrollBar().setOpaque(false);
        infoScrollPane.getVerticalScrollBar().setUnitIncrement(9);

        infoScrollPane.setLayout(new ScrollPaneLayout() {
            @Override
            public void layoutContainer(Container parent) {
                JScrollPane scrollPane = (JScrollPane) parent;

                Rectangle availR = scrollPane.getBounds();
                availR.x = availR.y = 0;

                Insets parentInsets = parent.getInsets();
                availR.x = parentInsets.left;
                availR.y = parentInsets.top;
                availR.width -= parentInsets.left + parentInsets.right;
                availR.height -= parentInsets.top + parentInsets.bottom;

                Rectangle vsbR = new Rectangle();
                vsbR.width = 12;
                vsbR.height = availR.height;
                vsbR.x = availR.x + availR.width - vsbR.width;
                vsbR.y = availR.y;

                if (viewport != null) {
                    viewport.setBounds(availR);
                }
                if (vsb != null) {
                    vsb.setVisible(true);
                    vsb.setBounds(vsbR);
                }
            }
        });
        infoScrollPane.getVerticalScrollBar().setUI(new jetSonScrollBarUI());

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new jetSon();

        //Function
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    File inputtedDir = new File(inputField.getText());
                    String trimmedInput = inputField.getText().toLowerCase().trim();
                    if (inputtedDir.isDirectory()) {

                        // Clears and updates list

                        infoPanel.removeAll();

                        File[] listOfFiles = inputtedDir.listFiles();
                        if (listOfFiles != null) {
                            listDirectory(infoPanel, listOfFiles);
                        }
                        inputField.setText("");
                    } else {
                        String[] commandListOne = {"\"(Directory)\": returns a list of files in that directory\n\"Clear\": resets list\n\"Corrupt\": corrupts selected file or directory\n\"help2\": next help dialog", "(Left Click On Item): selects item\n(Left Click In Empty Space Within List): deselects all items\n(Double Left Click On Item): if directory, opens it\n(Right Click In Empty Space Within List): goes one directory up"};
                        switch (trimmedInput) {
                            case "clear" -> {
                                // Removes all JPanels inside info panel (clears list) and empties input field

                                infoPanel.removeAll();
                                infoPanel.repaint();

                                inputField.setText("");
                            }
                            case "help1" -> JOptionPane.showMessageDialog(null, commandListOne[0], "Help Page 1", JOptionPane.INFORMATION_MESSAGE);
                            case "help2" -> JOptionPane.showMessageDialog(null, commandListOne[1], "Help Page 2", JOptionPane.INFORMATION_MESSAGE);
                            case "corrupt" -> {
                                try {
                                    if (inputFileSelected != null) {
                                        if (inputFileSelected.isFile()) {
                                            JCMCorrupt(inputFileSelected);
                                        } else if (inputFileSelected.isDirectory()) {
                                            JCMSearch(inputFileSelected);
                                        }
                                        inputField.setText("");
                                    }
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        });
    }
}


class jetSonScrollBarUI extends BasicScrollBarUI {
    private final Dimension d = new Dimension();

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return new JButton() {
            @Override
            public Dimension getPreferredSize() {
                return d;
            }
        };
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return new JButton() {
            @Override
            public Dimension getPreferredSize() {
                return d;
            }
        };
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        Color color;
        JScrollBar sb = (JScrollBar) c;
        if (!sb.isEnabled() || r.width > r.height) {
            return;
        } else if (isDragging) {
            color = new Color(0x9E9E9E);
        } else if (isThumbRollover()) {
            color = new Color(0x9E9E9E);
        } else {
            color = ResourceLoader.PRIMARY_BORDER_COLOR;
        }
        g2.setPaint(color);
        g2.fillRect(r.x, r.y, r.width, r.height);
        g2.setPaint(ResourceLoader.PRIMARY_BORDER_COLOR);
        g2.drawRect(r.x, r.y, r.width, r.height);
        g2.dispose();
    }

    @Override
    protected void setThumbBounds(int x, int y, int width, int height) {
        super.setThumbBounds(x, y, width, height);
        scrollbar.repaint();
    }
}
