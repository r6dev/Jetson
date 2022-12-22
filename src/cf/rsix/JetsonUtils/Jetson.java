package cf.rsix.JetsonUtils;

import cf.rsix.Resources.ResourceLoader;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class Jetson extends JFrame {
    // Jetson dir files
    public static File JETSON_DIRECTORY = new File(System.getProperty("user.home") + System.getProperty("file.separator") + ".jetson");
    public static File JETSON_TEMP_TXT = new File(JETSON_DIRECTORY.getAbsolutePath() + System.getProperty("file.separator") + "temp.txt");

    // Public Variables
    public static String[] JETSON_ERR_MESSAGES = {"Jetson error: could not open directory", "Jetson error: could not open parent directory"};
    private int mouseX, mouseY;
    public static String osName = System.getProperty("os.name").toLowerCase();
    public static final boolean isWindows = osName.contains("win");
    public static final boolean isLinux = osName.contains("nux") || osName.contains("nix");
    public static final boolean isMac = osName.contains("mac");
    public final JTextField inputField = new JTextField();
    private final JLabel secondTitleBarLabel = new JLabel(System.getProperty("user.name"));
    private final JPanel listPanel = new JPanel();
    private File SELECTED_FILE;
    private File ONE_DIRECTORY_UP;

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

        titleBarTop.add(secondTitleBarLabel);
        secondTitleBarLabel.setFont(ResourceLoader.getFont("jetbrains"));
        secondTitleBarLabel.setForeground(ResourceLoader.SECONDARY_TEXT_COLOR);

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

        listPanel.setBackground(ResourceLoader.TITLE_BAR_COLOR);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        JScrollPane infoScrollPane = new JScrollPane(listPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
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
        JetsonDirectoryCheck();

        // Create Jetson JFrame
        Jetson newJetson = initialize();
        System.out.println("Initialized " + newJetson.getName() + " " + getJetsonDirectory().getAbsolutePath() + " " + newJetson.getParentDirectory().getName() + " " + " " + newJetson.getListPanel().getName());
        if (newJetson.getSelectedFile() != null) {
            System.out.println("File selected on launch?");
        }
    }

    public static Jetson initialize() {
        Jetson frame = new Jetson();
        frame.goToDirectory(new File(System.getProperty("user.home")), frame.listPanel);

        JTextField inputFieldLocal = frame.inputField;

        // Take command inputs
        inputFieldLocal.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    File inputtedDir = new File(inputFieldLocal.getText());
                    if (inputtedDir.isDirectory()) {

                        // Clears and updates list
                        clearList(frame.listPanel);

                        File[] listOfFiles = inputtedDir.listFiles();
                        frame.listDirectory(frame.listPanel, listOfFiles);
                        frame.clearInputField();
                    } else {

                        // Detect short commands
                        String lowerCaseInput = inputFieldLocal.getText().toLowerCase();
                        String trimmedInput = lowerCaseInput.trim();
                        String[] commandListOne = {"\"(Directory)\": returns a list of files in that directory\n\"Open\": opens selected file or directory externally (SHOULD support all systems)\n\"Up\": goes one directory up\n\"Read\": reads selected file\n\"Corrupt\": corrupts selected file or directory\n\"Bloat\": bloats selected file into oblivion\n\"Dummy\": creates a dummy version of selected directory/duplicates selected directory\n\"Clear\": resets list\n\"Quit\": exit the application\n\"help2\": next help dialog", "(Left Click On Item): selects item\n(Left Click In Empty Space Within List): deselects all items\n(Double Left Click On Item): if directory, opens it, if file, opens it externally\n(Right Click Anywhere Inside List): goes one directory up"};
                        switch (trimmedInput) {
                            case "clear" -> {
                                clearList(frame.listPanel);
                                frame.clearInputField();
                            }
                            case "help1" -> {
                                JOptionPane.showMessageDialog(null, commandListOne[0], "Help Page 1", JOptionPane.INFORMATION_MESSAGE);
                                frame.clearInputField();
                            }
                            case "help2" -> {
                                JOptionPane.showMessageDialog(null, commandListOne[1], "Help Page 2", JOptionPane.INFORMATION_MESSAGE);
                                frame.clearInputField();
                            }
                            case "up" -> {
                                if (frame.goOneDirectoryUp()) {
                                    System.err.println(JETSON_ERR_MESSAGES[1]);
                                }
                            }
                            case "open" -> {
                                try {
                                    System.out.println(frame.SELECTED_FILE.getName());
                                    if (frame.SELECTED_FILE != null) {
                                        openFile(frame.SELECTED_FILE);
                                        frame.clearInputField();
                                    }
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                            case "read" -> {
                                try {
                                    if (frame.SELECTED_FILE.isFile() && frame.SELECTED_FILE.canRead()) {
                                        writeToFile(JETSON_TEMP_TXT, readFile(frame.SELECTED_FILE));
                                        if (openFile(JETSON_TEMP_TXT)) {
                                            frame.clearInputField();
                                        }
                                    }
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                            case "delete" -> {
                                if (frame.SELECTED_FILE != null) {
                                    if (!frame.SELECTED_FILE.delete()) {
                                        System.err.println("Could not delete file " + frame.SELECTED_FILE.getName());
                                    }
                                    frame.clearInputField();
                                }
                            }
                            case "corrupt" -> {
                                try {
                                    if (frame.SELECTED_FILE != null) {
                                        Corrupt(frame.SELECTED_FILE);
                                        frame.clearInputField();
                                    }
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                            case "bloat" -> {
                                if (frame.SELECTED_FILE != null) {
                                    String data = "Jetson Operation: " + Math.random() / 100000000;
                                    try {
                                        if (frame.SELECTED_FILE.isFile()) {
                                            writeToFile(frame.SELECTED_FILE, data);
                                        }
                                    } catch (IOException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                    frame.clearInputField();
                                }
                            }
                            case "dummy" -> {
                                try {
                                    if (frame.SELECTED_FILE != null && frame.SELECTED_FILE.isDirectory()) {
                                        File sandbox = new File(JETSON_DIRECTORY + System.getProperty("file.separator") + frame.SELECTED_FILE.getName());
                                        if (SandboxGenerator.search(frame.SELECTED_FILE, sandbox, sandbox)) {
                                            openFile(sandbox);
                                            frame.clearInputField();
                                        }
                                    }
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                            case "minimize" -> frame.setExtendedState(JFrame.ICONIFIED);
                            case "quit" -> {
                                try {
                                    JetsonDirectoryCheck();
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                            }
                        }
                    }
                }
            }
        });
        return frame;
    }

    // UI Methods
    public boolean goToDirectory(File dir, JComponent listToUpdate) {
        if (dir != null) {
            if (dir.isDirectory()) {
                clearInputField();
                deselectAllItems();

                File[] parentListOfFiles = dir.listFiles();
                if (parentListOfFiles != null) {
                    if (listDirectory(listToUpdate, parentListOfFiles)) {
                        return true;
                    }
                }

                deselectAllItems();
            }
        }
        return false;
    }

    public boolean goOneDirectoryUp() {
        if (ONE_DIRECTORY_UP != null) {
            System.out.println(ONE_DIRECTORY_UP.getName());
            return goToDirectory(ONE_DIRECTORY_UP, listPanel);
        }
        return false;
    }

    public void clearInputField() {
        inputField.setText("");
    }

    public static void clearList(JComponent list) {
        // Removes all JPanels inside info panel (clears list) and empties input field

        if (list != null) {
            list.removeAll();
            list.repaint();
        }
    }

    public void deselectAllItems() {
        // Deselect all items
        SELECTED_FILE = null;
        secondTitleBarLabel.setText(System.getProperty("user.name"));
        secondTitleBarLabel.requestFocus();
    }

    private synchronized boolean listDirectory(JComponent list, File[] directoryFiles) {
        if (directoryFiles != null && directoryFiles.length > 0) {
            clearList(list);

            File parentDirectory = directoryFiles[0].getParentFile();

            boolean oneDirectoryUpIsNull = parentDirectory.getParentFile() == null;
            ONE_DIRECTORY_UP = !oneDirectoryUpIsNull ? parentDirectory.getParentFile() : parentDirectory;

            for (File selectedFile : directoryFiles) {
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
                            SELECTED_FILE = selectedFile;
                            fileListingPanel.requestFocus();
                            secondTitleBarLabel.setText(selectedFile.getName());
                        } else if (button == MouseEvent.BUTTON3) {
                            // Go one directory up
                            if (!goOneDirectoryUp()) {
                                System.err.println(JETSON_ERR_MESSAGES[1]);
                            }
                        }

                        // Open directory in Jetson or file externally
                        if (e.getClickCount() == 2 && button == MouseEvent.BUTTON1) {
                            if (selectedFile.isDirectory()) {
                                if (!goToDirectory(selectedFile, list)) {
                                    System.err.println(JETSON_ERR_MESSAGES[0]);
                                }
                            } else if (selectedFile.isFile()) {
                                try {
                                    if (openFile(selectedFile)) {
                                        clearInputField();
                                    }

                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
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
            }

            // Remove all previous mouse listeners
            for (MouseListener listener : list.getMouseListeners()) {
                list.removeMouseListener(listener);
            }

            list.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        // Go one directory up
                        if (!goOneDirectoryUp()) {
                            System.err.println(JETSON_ERR_MESSAGES[1]);
                        }
                    } else if (e.getButton() == MouseEvent.BUTTON1) {
                        // Deselect all items
                        deselectAllItems();
                    }
                }
            });

            return true;
        }
        return false;
    }

    // Setters & Getters

    public static File getJetsonDirectory() {
        return JETSON_DIRECTORY;
    }

    public JPanel getListPanel() {
        return listPanel;
    }

    public File getSelectedFile() {
        return SELECTED_FILE;
    }

    public File getParentDirectory() {
        return ONE_DIRECTORY_UP;
    }

    // API Methods
    private static void JetsonDirectoryCheck() throws IOException {
        ArrayList<File> verifiedJetsonFiles = new ArrayList<>(2);
        verifiedJetsonFiles.add(JETSON_TEMP_TXT);
        verifiedJetsonFiles.add(JETSON_DIRECTORY);

        if (!JETSON_DIRECTORY.exists()) {
            if (!JETSON_DIRECTORY.mkdir()) {
                System.err.println("Attempt to create " + JETSON_DIRECTORY.getAbsolutePath() + " resulted in error");
            }
        }

        if (!JETSON_TEMP_TXT.exists()) {
            if (JETSON_TEMP_TXT.createNewFile()) {
                System.out.println("Successfully created " + JETSON_TEMP_TXT.getAbsolutePath());
            }
        } else {
            // Clear .jetson directory of any unwanted files
            writeToFile(JETSON_TEMP_TXT, "");
            File[] jetsonListOfFiles = JETSON_DIRECTORY.listFiles();
            if (jetsonListOfFiles != null) {
                for (File jetsonFile : jetsonListOfFiles) {
                    if (!verifiedJetsonFiles.contains(jetsonFile)) {
                        if (jetsonFile.delete()) {
                            System.out.println("Removed unverified Jetson file");
                        }
                    }
                }
            }
        }
    }
    public static void Corrupt(@NotNull File file) throws IOException {
        if (file.isDirectory()) {
            File[] listOfFiles = file.listFiles();

            if (listOfFiles != null) {
                for (File selectedFile : listOfFiles) {
                    if (selectedFile.isFile()) {
                        FileWriter corrupter = new FileWriter(selectedFile);
                        if (selectedFile.canWrite()) {
                            corrupter.write("Jetson Operation: " + Math.random());
                            corrupter.close();
                        }
                    } else {
                        Corrupt(selectedFile);
                    }
                }
            }
        } else if (file.isFile() && file.canWrite()) {
            FileWriter corrupter = new FileWriter(file);
            corrupter.write("Jetson Operation: " + Math.floor(Math.random() / 100));
            corrupter.close();
        }
    }

    public static boolean openFile(@NotNull File file) throws IOException {
        if (isWindows && Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(file);
            return true;
        } else if (isLinux || isMac) {
            Runtime.getRuntime().exec(new String[]{"/usr/bin/open", file.getAbsolutePath()});
            return true;
        } else {
            // Unknown OS?
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
                return true;
            }
        }
        return false;
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

    public static void writeToFile(@NotNull File file, String data) throws IOException {
        if (file.isFile() && file.canWrite()) {
            FileWriter tempTxtWriter = new FileWriter(JETSON_TEMP_TXT);
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
