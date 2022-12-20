package com.rsix.JetSon;

import com.rsix.Resources.ResourceLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class jetSon extends JFrame {
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

        JLabel userNameLabel = new JLabel(System.getProperty("user.name"));
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

        JTextField inputField = new JTextField();
        String inputFieldPlaceholder = "path to directory";
        getContentPane().add(inputField);
        inputField.setBackground(ResourceLoader.SECONDARY_BACKGROUND_COLOR);
        inputField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0,0,1,0, ResourceLoader.PRIMARY_BORDER_COLOR), new EmptyBorder(0,12,0,0)));
        inputField.setForeground(ResourceLoader.EDITOR_TEXT_COLOR);
        inputField.setFont(new Font("Consolas", Font.PLAIN, 12));
        inputField.setPreferredSize(new Dimension(180, 29));

        // Functional placeholder text
        inputField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (inputField.getText().equals(inputFieldPlaceholder)) {
                    inputField.setText("");
                    inputField.setForeground(ResourceLoader.PRIMARY_TEXT_COLOR);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (inputField.getText().isBlank()) {
                    inputField.setText(inputFieldPlaceholder);
                    inputField.setForeground(ResourceLoader.SECONDARY_TEXT_COLOR);
                }
            }
        });

        // Get info from inputted path

        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(ResourceLoader.TITLE_BAR_COLOR);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JScrollPane infoScrollPane = new JScrollPane(infoPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        getContentPane().add(infoScrollPane);
        infoScrollPane.setBorder(null);
        infoScrollPane.setPreferredSize(new Dimension(180, 128));
        infoScrollPane.setComponentZOrder(infoScrollPane.getVerticalScrollBar(), 0);
        infoScrollPane.setComponentZOrder(infoScrollPane.getViewport(), 1);
        infoScrollPane.getVerticalScrollBar().setOpaque(false);

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
        infoScrollPane.getVerticalScrollBar().setUI(new MyScrollBarUI());

        // Function
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String trimmedInput = inputField.getText().toLowerCase().trim();
                    if (trimmedInput.equals("clear") || trimmedInput.equals("reset")) {
                        infoPanel.removeAll();
                        infoPanel.repaint();
                        inputField.setText("");
                    } else {
                        File inputtedDir = new File(inputField.getText());
                        if (inputtedDir.isDirectory()) {
                            infoPanel.removeAll();

                            File[] listOfFiles = inputtedDir.listFiles();
                            if (listOfFiles != null) {
                                for (File selectedFile : listOfFiles) {
                                    JPanel fileListingPanel = new JPanel();
                                    infoPanel.add(fileListingPanel);
                                    fileListingPanel.setBorder(new EmptyBorder(0,12,0,0));
                                    fileListingPanel.setBackground(ResourceLoader.TITLE_BAR_COLOR);
                                    fileListingPanel.setPreferredSize(new Dimension(180, 16));
                                    fileListingPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
                                    fileListingPanel.setLayout(new BoxLayout(fileListingPanel, BoxLayout.LINE_AXIS));

                                    JLabel fileNameLabel = new JLabel(selectedFile.getName());
                                    fileNameLabel.setForeground(ResourceLoader.PRIMARY_TEXT_COLOR);
                                    fileNameLabel.setFont(ResourceLoader.getFont("jetbrains"));

                                    String toolTipAppend = (selectedFile.getName().contains("sys") ? "(sys) " + selectedFile.getAbsolutePath() : selectedFile.getAbsolutePath());

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

                                    fileListingPanel.addMouseListener(new MouseAdapter() {
                                        @Override
                                        public void mouseEntered(MouseEvent e) {
                                            fileListingPanel.setBackground(new Color(0x9E9E9E));
                                        }

                                        @Override
                                        public void mouseExited(MouseEvent e) {
                                            fileListingPanel.setBackground(ResourceLoader.TITLE_BAR_COLOR);
                                        }
                                    });
                                }
                            }
                            inputField.setText("");
                        }
                    }
                }
            }
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new jetSon();
    }
}


class MyScrollBarUI extends BasicScrollBarUI {
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
