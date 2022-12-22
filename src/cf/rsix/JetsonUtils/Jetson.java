package cf.rsix.JetsonUtils;

import cf.rsix.Resources.ResourceLoader;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Jetson extends JFrame {
    // Jetson dir files
    public static File jetSonDir = new File(System.getProperty("user.home") + System.getProperty("file.separator") + ".jetson");
    public static File jetSonTempTxt = new File(jetSonDir.getAbsolutePath() + System.getProperty("file.separator") + "temp.txt");

    // Public Variables
    private int mouseX, mouseY;
    public static String osName = System.getProperty("os.name").toLowerCase();
    public static Boolean isWindows = osName.contains("win");
    public static Boolean isLinux = osName.contains("nux") || osName.contains("nix");
    public static Boolean isMac = osName.contains("mac");
    public static final JTextField inputField = new JTextField();
    public static JLabel userNameLabel = new JLabel(System.getProperty("user.name"));
    public static final JPanel infoPanel = new JPanel();
    public static File inputFileSelected;
    public static File previousDir;

    // Jetson JFrame
    public Jetson() {
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

        titleBar.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                setLocation(getX() + e.getX() - mouseX, getY() + e.getY() - mouseY);
            }
        });
        titleBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseY = e.getY();
                mouseX = e.getX();
            }
        });

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
        infoScrollPane.getVerticalScrollBar().setUI(new JetsonScrollBarUI());

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Main Logic
    public static void main(String[] args) throws IOException {
        // Check for Jetson directories and files
        JetSonDirCheck();

        // Create Jetson JFrame
        System.out.println(initialize().getTitle());
        listDirectory(infoPanel, new File(System.getProperty("user.home")).listFiles());
    }

    // UI Methods
    public static Jetson initialize() {
        Jetson frame = new Jetson();

        // Take command inputs
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    File inputtedDir = new File(inputField.getText());
                    if (inputtedDir.isDirectory()) {

                        // Clears and updates list
                        clearList(null);

                        File[] listOfFiles = inputtedDir.listFiles();
                        listDirectory(infoPanel, listOfFiles);
                        clearInputField();
                    } else {

                        // Detect short commands
                        String lowerCaseInput = inputField.getText().toLowerCase();
                        String trimmedInput = lowerCaseInput.trim();
                        String[] commandListOne = {"\"(Directory)\": returns a list of files in that directory\n\"Open\": opens selected file or directory externally (SHOULD support all systems)\n\"Read\": reads selected file\n\"Corrupt\": corrupts selected file or directory\n\"Clear\": resets list\n\"Quit\": exit the application\n\"help2\": next help dialog", "(Left Click On Item): selects item\n(Left Click In Empty Space Within List): deselects all items\n(Double Left Click On Item): if directory, opens it, if file, opens it externally\n(Right Click Anywhere Inside List): goes one directory up"};
                        switch (trimmedInput) {
                            case "clear" -> {
                                clearList(null);
                                clearInputField();
                            }
                            case "help1" -> {
                                JOptionPane.showMessageDialog(null, commandListOne[0], "Help Page 1", JOptionPane.INFORMATION_MESSAGE);
                                clearInputField();
                            }
                            case "help2" -> {
                                JOptionPane.showMessageDialog(null, commandListOne[1], "Help Page 2", JOptionPane.INFORMATION_MESSAGE);
                                clearInputField();
                            }
                            case "open" -> {
                                try {
                                    if (inputFileSelected != null) {
                                        openFile(inputFileSelected);
                                        clearInputField();
                                    }
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            case "read" -> {
                                try {
                                    if (inputFileSelected.isFile() && inputFileSelected.canRead()) {
                                        writeToFile(jetSonTempTxt, readFile(inputFileSelected));
                                        openFile(jetSonTempTxt);
                                        clearInputField();
                                    }
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            case "delete" -> {
                                if (inputFileSelected != null) {
                                    if (!deleteFile(inputFileSelected)) {
                                        System.err.println("Could not delete file " + inputFileSelected.getName());
                                    }
                                    clearInputField();
                                }
                            }
                            case "corrupt" -> {
                                try {
                                    if (inputFileSelected != null) {
                                        if (inputFileSelected.isFile()) {
                                            JCMCorrupt(inputFileSelected);
                                        } else if (inputFileSelected.isDirectory()) {
                                            JCMSearch(inputFileSelected);
                                        }
                                        clearInputField();
                                    }
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            case "bloat" -> {
                                if (inputFileSelected != null) {
                                    String data = "Jetson operation: " + Math.random() / 100000000;
                                    try {
                                        if (inputFileSelected.isFile()) {
                                            writeToFile(inputFileSelected, data);
                                        }
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                    clearInputField();
                                }
                            }
                            case "minimize" -> frame.setExtendedState(JFrame.ICONIFIED);
                            case "quit" -> System.exit(0);
                        }
                    }
                }
            }
        });
        return frame;
    }

    public static void goToDirectory(File dir, JComponent listToUpdate) {
        if (dir != null) {
            if (dir.isDirectory()) {
                clearList(listToUpdate);
                clearInputField();
                deselectAllItems();

                File[] parentListOfFiles = dir.listFiles();
                if (parentListOfFiles != null) {
                    listDirectory(listToUpdate, parentListOfFiles);
                }

                deselectAllItems();
            }
        }
    }

    static void clearInputField() {
        inputField.setText("");
    }

    public static void clearList(JComponent list) {
        // Removes all JPanels inside info panel (clears list) and empties input field

        if (list == null) {
            infoPanel.removeAll();
            infoPanel.repaint();
        } else {
            list.removeAll();
            list.repaint();
        }
    }

    public static void deselectAllItems() {
        // Deselect all items
        inputFileSelected = null;
        userNameLabel.setText(System.getProperty("user.name"));
        userNameLabel.requestFocus();
    }

    public static void listDirectory(JComponent list, File[] listOfFiles) {
        if (listOfFiles != null && listOfFiles.length > 0) {
            clearList(list);

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
                        int button = e.getButton();

                        // Select item
                        if (button == MouseEvent.BUTTON1) {
                            inputFileSelected = selectedFile;
                            fileListingPanel.requestFocus();
                            userNameLabel.setText(selectedFile.getName());
                        } else if (button == MouseEvent.BUTTON3) {
                            goToDirectory(previousDir, list);
                        }

                        // Open directory in Jetson or file externally
                        if (e.getClickCount() == 2 && button == MouseEvent.BUTTON1) {
                            if (selectedFile.isDirectory()) {
                                goToDirectory(selectedFile, list);
                            } else if (selectedFile.isFile()) {
                                try {
                                    openFile(selectedFile);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
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
                            goToDirectory(previousDir, list);
                        } else if (e.getButton() == MouseEvent.BUTTON1) {
                            deselectAllItems();
                        }
                    }
                });
            }
        }
    }

    // Functions
    public static void JetSonDirCheck() throws IOException {
        if (!jetSonDir.exists()) {
            if (!jetSonDir.mkdir()) {
                System.err.println("Attempt to create " + jetSonDir.getAbsolutePath() + " resulted in error");
            }
        }

        if (jetSonTempTxt.createNewFile()) {
            System.out.println("Successfully created " + jetSonTempTxt.getAbsolutePath());
        }
    }
    public static void JCMSearch(@NotNull File dir) {
        try {
            File[] listOfFiles = dir.listFiles();

            if (listOfFiles != null) {
                for (File selectedFile : listOfFiles) {
                    if (selectedFile.isFile()) {
                        FileWriter corrupter = new FileWriter(selectedFile);
                        if (selectedFile.canWrite()) {
                            corrupter.write("Jetson Operation: " + Math.random());
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

    public static void JCMCorrupt(@NotNull File file) throws IOException {
        FileWriter corrupter = new FileWriter(file);
        corrupter.write("Jetson Operation: " + Math.floor(Math.random() / 100));
        corrupter.close();
    }

    public static void openFile(@NotNull File file) throws IOException {
        if (isWindows && Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(file);
            clearInputField();
        } else if (isLinux || isMac) {
            Runtime.getRuntime().exec(new String[]{"/usr/bin/open", file.getAbsolutePath()});
        } else {
            // Unknown OS?
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
        }
        clearInputField();
    }

    public static @NotNull String readFile(@NotNull File file) throws IOException {
        if (file.isFile() && file.canRead()) {
            BufferedReader fileReader = new BufferedReader(new FileReader(file));
            String data;
            String returnString = "";
            while ((data = fileReader.readLine()) != null) {
                returnString = data;
            }
            if (!returnString.isEmpty()) {
                return returnString;
            }
        }
        return "";
    }

    public static boolean deleteFile(@NotNull File file) {
        if (file.isFile()) {
            return file.delete();
        }
        return false;
    }

    public static void writeToFile(@NotNull File file, String data) throws IOException {
        if (file.isFile() && file.canWrite()) {
            FileWriter tempTxtWriter = new FileWriter(jetSonTempTxt);
            tempTxtWriter.write(data);
            tempTxtWriter.flush();
            tempTxtWriter.close();
        }
    }
}


class JetsonScrollBarUI extends BasicScrollBarUI {
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
