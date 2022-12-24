package cf.r6dev.jetson;

import cf.r6dev.jetson.ui.JetsonScrollBarUI;
import cf.r6dev.jetson.utils.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class Jetson extends JFrame {
    // Static file vars
    private static final File JETSON_DIRECTORY = new File(System.getProperty("user.home") + System.getProperty("file.separator") + ".jetson");
    private static final File JETSON_RESOURCE_FOLDER = new File(new File(JETSON_DIRECTORY + System.getProperty("file.separator") + "resources").listFiles() != null ? JETSON_DIRECTORY + System.getProperty("file.separator") + "resources" : System.getProperty("user.dir") + System.getProperty("file.separator") + "resources");
    private static final File JETSON_DUMMY_DIRECTORY = new File(JETSON_DIRECTORY + System.getProperty("file.separator") + "dummies");
    private static final File JETSON_TEMP_TXT = new File(JETSON_DIRECTORY + System.getProperty("file.separator") + "temp.txt");
    private static final File JETSON_BLOAT_TXT = new File(JETSON_DIRECTORY + System.getProperty("file.separator") + "bloat.txt");

    // Static vars
    public static JetRL JRL = new JetRL(JETSON_RESOURCE_FOLDER);
    private static final String[] JETSON_ERRS = {"Jetson error [0]: could not open directory", "Jetson error [1]: could not open parent directory", "Jetson error [2]: could not write to file", "Jetson error [3]: could not create file"};
    public static final String OS_NAME = System.getProperty("os.name").toLowerCase();
    public static final boolean IS_WINDOWS = OS_NAME.contains("win");
    public static final boolean IS_LINUX = OS_NAME.contains("nux") || OS_NAME.contains("nix");
    public static final boolean IS_MAC = OS_NAME.contains("mac");

    // Non-static vars
    private int mouseX, mouseY;
    private final JPanel titleBar = new JPanel();
    private final JLabel titleLabel = new JLabel();
    private final JLabel titleLabelSuffix = new JLabel(System.getProperty("user.name"));
    private final JTextField inputField = new JTextField();
    @SuppressWarnings("FieldMayBeFinal") private String inputFieldPlaceholder = "input a dir or \"help1\"";
    private final JPanel listPanel = new JPanel();
    private final JScrollPane listPanelWrapper = new JScrollPane(listPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private File selectedFile;
    private File oneDirectoryUp;
    private final Font monoFont = JRL.createMonoFont();
    @SuppressWarnings("FieldCanBeLocal") private final Font terminalFont = JRL.createTerminalFont();

    // Jetson JFrame
    public Jetson() {
        // Setup

        setTitle("Jetson");
        titleLabel.setText(getTitle() + " - ");
        setIconImage(JRL.getIcon("jetson-icon", (short) 96, Image.SCALE_DEFAULT).getImage());
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getRootPane().setBorder(BorderFactory.createLineBorder(JetRL.PRIMARY_BORDER_COLOR));
        getContentPane().setBackground(JetRL.TITLE_BAR_COLOR);
        setUndecorated(true);

        // Title bar

        getContentPane().add(titleBar, BorderLayout.NORTH);
        titleBar.setPreferredSize(new Dimension(180, 42));
        titleBar.setLayout(new BorderLayout());
        titleBar.setBackground(JetRL.TITLE_BAR_COLOR);

        // Make title bar draggable
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
        titleBarTop.setBackground(JetRL.TITLE_BAR_COLOR);
        titleBar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0,0,1,0, JetRL.PRIMARY_BORDER_COLOR), new EmptyBorder(5,12,5,0)));

        titleBarTop.add(titleLabel);
        titleLabel.setFont(monoFont);
        titleLabel.setForeground(JetRL.PRIMARY_TEXT_COLOR);

        titleBarTop.add(titleLabelSuffix);
        titleLabelSuffix.setFont(monoFont);
        titleLabelSuffix.setForeground(JetRL.SECONDARY_TEXT_COLOR);

        // Title bar -- Bottom

        JPanel titleBarBottom = new JPanel();
        titleBar.add(titleBarBottom, BorderLayout.SOUTH);
        titleBarBottom.setLayout(new BoxLayout(titleBarBottom, BoxLayout.LINE_AXIS));
        titleBarBottom.setBackground(JetRL.TITLE_BAR_COLOR);

        JLabel sysInfoLabel = new JLabel("- " + System.getProperty("os.arch") + System.getProperty("file.separator") + System.getProperty("os.version") + System.getProperty("file.separator") + "JRE " + System.getProperty("java.version"));
        titleBarBottom.add(sysInfoLabel);
        sysInfoLabel.setFont(terminalFont);
        sysInfoLabel.setForeground(JetRL.SECONDARY_TEXT_COLOR);

        // Input field setup

        getContentPane().add(inputField);
        inputField.setBackground(JetRL.SECONDARY_BACKGROUND_COLOR);
        inputField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0,0,1,0, JetRL.SECONDARY_BORDER_COLOR), new EmptyBorder(0,12,0,0)));
        inputField.setForeground(JetRL.EDITOR_TEXT_COLOR);
        inputField.setFont(terminalFont);
        inputField.setPreferredSize(new Dimension(titleBar.getWidth(), 29));
        listPanel.setBackground(JetRL.TITLE_BAR_COLOR);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        // Functional placeholder text
        inputField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (inputField.getText().equals(inputFieldPlaceholder)) {
                    inputField.setText("");
                }
                inputField.setFont(JRL.createTerminalFont(12));
                inputField.setForeground(JetRL.PRIMARY_TEXT_COLOR);
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (inputField.getText().isBlank()) {
                    inputField.setText(inputFieldPlaceholder);
                    inputField.setFont(JRL.createTerminalFont(10));
                    inputField.setForeground(JetRL.SECONDARY_TEXT_COLOR);
                }
            }
        });

        // Scroll pane for list setup
        getContentPane().add(listPanelWrapper);
        listPanelWrapper.setBorder(null);
        listPanelWrapper.setPreferredSize(new Dimension(180, 128));
        listPanelWrapper.setComponentZOrder(listPanelWrapper.getVerticalScrollBar(), 0);
        listPanelWrapper.setComponentZOrder(listPanelWrapper.getViewport(), 1);
        listPanelWrapper.getVerticalScrollBar().setUnitIncrement(9);
        listPanelWrapper.getVerticalScrollBar().setOpaque(false);
        listPanelWrapper.setLayout(JetsonScrollBarUI.newLayout());
        listPanelWrapper.getVerticalScrollBar().setUI(new JetsonScrollBarUI());

        // Keep scroll bar updated
        listPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                updateScrollBar();
            }
        });


        // Finish up
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Main Logic

    public static @NotNull Jetson initialize() {
        // Check if resources are present
        if (!JRL.getResourceFolder().exists()) {
            JOptionPane.showMessageDialog(null, "Could not find resource folder in current working directory, this can result in faulty GUI. You can download it from the rJToolbox GitHub repo", "Resource folder does not exist", JOptionPane.WARNING_MESSAGE);
        }

        // Verify .jetson directory
        try {
            verifyJetsonDirectory();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Initialize Jetson
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

                        // Clears and updates with inputted directory list
                        frame.clearList(frame.listPanel);

                        File[] listOfFiles = inputtedDir.listFiles();
                        frame.listDirectory(frame.listPanel, listOfFiles);
                        frame.clearInputField();
                    } else {

                        // Detect short commands
                        String lowerCaseInput = inputFieldLocal.getText().toLowerCase();
                        String trimmedInput = lowerCaseInput.trim();
                        String[] commandListOne = {"\"(Directory)\": returns a list of files in that directory\n\"Open\": opens selected file or directory externally (SHOULD support all systems)\n\"Up\": goes one directory up\n\"Read\": reads selected file\n\"Corrupt\": corrupts selected file or directory\n\"Bloat\": bloats selected file into oblivion\n\"Dummy\": creates a dummy version of selected directory/duplicates selected directory\n\"Verify\": verifies .jetson directory\n\"Clear\": resets list\n\"Quit\": exit the application\n\"help2\": next help dialog", "(Left Click On Item): selects item\n(Left Click In Empty Space Within List): deselects all items\n(Double Left Click On Item): if directory, opens it internally, if file, opens externally\n(Right Click Anywhere Inside List): goes one directory up"};
                        switch (trimmedInput) {
                            case "clear" -> {
                                frame.clearList(frame.listPanel);
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
                                if (!frame.goOneDirectoryUp()) {
                                    System.err.println(JETSON_ERRS[1]);
                                }
                            }
                            case "open" -> {
                                try {
                                    if (frame.selectedFile != null && JetViewer.view(frame.selectedFile)) {
                                        frame.clearInputField();
                                    }
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                            case "write" -> {
                                if (frame.selectedFile != null && frame.selectedFile.isFile()) {
                                    new JetWriter(frame.selectedFile);
                                }
                            }
                            case "read" -> {
                                try {
                                    if (frame.selectedFile.isFile() && frame.selectedFile.canRead()) {
                                        if (JetViewer.view(JETSON_TEMP_TXT) && JetWriter.write(JETSON_TEMP_TXT, JetReader.readFile(frame.selectedFile))) {
                                            frame.clearInputField();
                                        }
                                    }
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                            case "delete" -> {
                                if (frame.selectedFile != null) {
                                    if (!frame.selectedFile.delete()) {
                                        System.err.println("Could not delete file " + frame.selectedFile.getName());
                                    }
                                    frame.clearInputField();
                                }
                            }
                            case "corrupt" -> {
                                try {
                                    if (JetCorrupter.corrupt(frame.selectedFile)) {
                                        frame.clearInputField();
                                    } else {
                                        System.err.println(JETSON_ERRS[2]);
                                    }
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                            case "bloat" -> {
                                try {
                                    String tempFileName = frame.selectedFile.getName();
                                    for (short i = 1; i <= 32500; i++) {
                                        if (!JetCorrupter.corrupt(frame.selectedFile)) {
                                            System.err.println(JETSON_ERRS[2] + " " + tempFileName);
                                        }
                                    }
                                    JOptionPane.showMessageDialog(null, tempFileName + " has now been bloated to oblivion", "Into oblivion", JOptionPane.INFORMATION_MESSAGE);
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                            case "dummy" -> {
                                try {
                                    if (frame.selectedFile != null && frame.selectedFile.isDirectory()) {
                                        File sandbox = new File( JETSON_DUMMY_DIRECTORY + System.getProperty("file.separator") + frame.selectedFile.getName());
                                        if (sandbox.mkdir()) {
                                            System.out.println("Sandbox of " + frame.selectedFile.getName() + " created");
                                        }

                                        if (JetSbGen.createDummy(frame.selectedFile, sandbox, sandbox)) {
                                            JetViewer.view(sandbox);
                                            frame.clearInputField();
                                        }
                                    }
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                            case "minimize" -> frame.setExtendedState(JFrame.ICONIFIED);
                            case "verify" -> {
                                try {
                                    verifyJetsonDirectory();
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                            case "quit" -> frame.close();
                        }

                    }
                }
            }
        });
        return frame;
    }

    public static void main(String[] args) {
        // Create Jetson JFrame
        System.out.println("Created " + initialize().getName());
    }

    // UI Methods
    public boolean goToDirectory(File dir, JPanel listToUpdate) {
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

    @SuppressWarnings("BooleanMethodIsAlwaysInverted") public boolean goOneDirectoryUp() {
        if (oneDirectoryUp != null) {
            return goToDirectory(oneDirectoryUp, listPanel);
        }
        return false;
    }

    public void clearInputField() {
        inputField.setText("");
    }

    public void clearList(JComponent list) {
        // Removes all JPanels inside info panel (clears list) and empties input field

        if (list != null) {
            list.removeAll();
            list.repaint();
        }
    }

    public void deselectAllItems() {
        // Deselect all items
        selectedFile = null;
        titleLabelSuffix.setText(System.getProperty("user.name"));
        titleLabelSuffix.requestFocus();
    }

    @SuppressWarnings("unused") void initializeTitle(String text) {
        setTitle(text);
        updateTitleLabel();
    }

    public void updateTitleLabel() {
        titleLabel.setText(getTitle() + " - ");
    }

    public void updateScrollBar() {
        listPanelWrapper.getVerticalScrollBar().repaint();
    }

    public void close() {
        try {
            verifyJetsonDirectory();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    synchronized private boolean listDirectory(JPanel list, File[] directoryFiles) {
        if (directoryFiles != null && directoryFiles.length > 0) {
            clearList(list);
            getListPanelWrapper().getVerticalScrollBar().repaint();
            getListPanelWrapper().getHorizontalScrollBar().repaint();

            File parentDirectory = directoryFiles[0].getParentFile();

            boolean oneDirectoryUpIsNull = parentDirectory.getParentFile() == null;
            oneDirectoryUp = !oneDirectoryUpIsNull ? parentDirectory.getParentFile() : parentDirectory;

            for (File selectedDirectoryFile : directoryFiles) {
                // Creates file/dir listing

                JPanel fileListingPanel = new JPanel();
                list.add(fileListingPanel);
                fileListingPanel.setBorder(new EmptyBorder(0,12,0,0));
                fileListingPanel.setBackground(JetRL.TITLE_BAR_COLOR);
                fileListingPanel.setPreferredSize(new Dimension(titleBar.getWidth(), 16));
                fileListingPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
                fileListingPanel.setLayout(new BoxLayout(fileListingPanel, BoxLayout.LINE_AXIS));

                JLabel fileNameLabel = new JLabel(selectedDirectoryFile.getName());
                fileNameLabel.setForeground(JetRL.PRIMARY_TEXT_COLOR);
                fileNameLabel.setFont(monoFont);

                String toolTipAppend = (selectedDirectoryFile.getName().contains("sys") ? "(sys) " + selectedDirectoryFile.getAbsolutePath() : selectedDirectoryFile.getAbsolutePath());

                // Determines which type of listing it is (file or dir)

                if (selectedDirectoryFile.isFile()) {
                    JLabel isFilePrefix = new JLabel("file: ");
                    isFilePrefix.setForeground(JetRL.SECONDARY_TEXT_COLOR);
                    isFilePrefix.setFont(monoFont);

                    fileListingPanel.add(isFilePrefix);
                    fileListingPanel.add(fileNameLabel);
                    fileListingPanel.setToolTipText("File: " + toolTipAppend);
                } else {
                    JLabel isDirPrefix = new JLabel(System.getProperty("file.separator"));
                    isDirPrefix.setForeground(JetRL.SECONDARY_TEXT_COLOR);
                    isDirPrefix.setFont(monoFont);

                    fileListingPanel.add(isDirPrefix);
                    fileListingPanel.add(fileNameLabel);
                    fileListingPanel.setToolTipText("Dir: " + toolTipAppend);
                }

                // All mouse events for JPanel

                Color focusedOnColor = Color.BLUE;

                // Item mouse events
                fileListingPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        // On hover
                        if (fileListingPanel.getBackground() != focusedOnColor) {
                            fileListingPanel.setBackground(new Color(0x9E9E9E));
                            updateScrollBar();
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        // On stopped hovering
                        if (fileListingPanel.getBackground() != focusedOnColor) {
                            fileListingPanel.setBackground(JetRL.TITLE_BAR_COLOR);
                        }
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        int button = e.getButton();

                        // Select item
                        if (button == MouseEvent.BUTTON1) {
                            selectedFile = selectedDirectoryFile;
                            fileListingPanel.requestFocus();
                            titleLabelSuffix.setText(selectedDirectoryFile.getName());
                        } else if (button == MouseEvent.BUTTON3) {
                            // Go one directory up
                            if (!goOneDirectoryUp()) {
                                System.err.println(JETSON_ERRS[1]);
                            }
                        }

                        // Open directory or file on double click
                        if (e.getClickCount() == 2 && button == MouseEvent.BUTTON1) {
                            if (selectedDirectoryFile.isDirectory()) {
                                if (!goToDirectory(selectedDirectoryFile, list)) {
                                    System.err.println(JETSON_ERRS[0]);
                                }
                            } else if (selectedDirectoryFile.isFile()) {
                                try {
                                    if (JetViewer.view(selectedDirectoryFile)) {
                                        clearInputField();
                                    }

                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        }
                    }
                });

                // Hover effects and events
                fileListingPanel.addFocusListener(new FocusListener() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        fileNameLabel.setText("[" + selectedDirectoryFile.getName() + "]");
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        fileNameLabel.setText(selectedDirectoryFile.getName());
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
                            System.err.println(JETSON_ERRS[1]);
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

    @SuppressWarnings("unused") public static File getJetsonDirectory() throws IOException {
        verifyJetsonDirectory();
        return JETSON_DIRECTORY;
    }

    @SuppressWarnings("unused") public static File getJetsonResourceFolder() {
        try {
            verifyJetsonDirectory();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return JETSON_RESOURCE_FOLDER;
    }

    @SuppressWarnings("unused") public static File getJetsonTempTxt() throws IOException {
        verifyJetsonDirectory();
        return JETSON_TEMP_TXT;
    }

    @SuppressWarnings("unused") public static File getJetsonDummyDirectory() throws IOException {
        verifyJetsonDirectory();
        return JETSON_DUMMY_DIRECTORY;
    }

    @SuppressWarnings("unused") public JPanel getListPanel() {
        return listPanel;
    }

    @SuppressWarnings("unused") public JScrollPane getListPanelWrapper() {
        return listPanelWrapper;
    }

    @SuppressWarnings("unused") public JTextField getInputField() {
        return inputField;
    }

    @SuppressWarnings("unused") public void wipe() {
        for (Component component : getContentPane().getComponents()) {
            remove(component);
        }
    }

    @SuppressWarnings("unused") public JPanel getTitleBar() {return titleBar;}

    @SuppressWarnings("unused") public JLabel getTitleLabel() {
        return titleLabel;
    }

    @SuppressWarnings("unused") public JLabel getTitleLabelSuffix() {
        return titleLabelSuffix;
    }

    @SuppressWarnings("unused") public File getSelectedFile() {
        return selectedFile;
    }

    @SuppressWarnings("unused") public void setSelectedFile(File selectedFile) {
        this.selectedFile = selectedFile;
    }

    @SuppressWarnings("unused") public File getParentDirectory() {
        return oneDirectoryUp;
    }

    // Public API Methods
    public static void verifyJetsonDirectory() throws IOException {
        ArrayList<File> verifiedJetsonFiles = new ArrayList<>(2);
        verifiedJetsonFiles.add(JETSON_DIRECTORY);
        verifiedJetsonFiles.add(JETSON_DUMMY_DIRECTORY);
        verifiedJetsonFiles.add(JETSON_TEMP_TXT);
        verifiedJetsonFiles.add(JETSON_BLOAT_TXT);
        verifiedJetsonFiles.add(JETSON_RESOURCE_FOLDER);

        String bloatToWrite = """
                    ‰PNG
                    \u001A
                      \s
                    IHDR  \u0002   \u0002 \b\u0006   ôxÔú   \u0001sRGB ®Î\u001Cé   \u0004gAMA  ±\u008F\u000Büa\u0005   \tpHYs  \u000EÃ  \u000EÃ\u0001Ço¨d  \u001FuIDATx^íÝK¬Uåù\u0007àeÿI…\u000E\f8 è ^š\u00144©\\\u0006rIj¡iä2h\u0081\u0081‚\u0003\u000B\u000EP\u001Cˆ:h‘\u0081B\u0007X;\u0010p hRD“R ©`\u0007€\u001D ’F.I\u0015Lê¥i•8ðÂ@\u008Di‚6Møû.ÖÖ#\u001Eà\\öÙg\u00ADõ>O²sÖÚ§IÃqïµ~ëûÞïý.+Šâì—/  ‘ïT?\u0001€D\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004 €A\u00183fLu\u0004Í&  \fPÜü'L˜ \u0004Ð
                    —}ù:{î\u0010€K‰›ÿçŸ\u007F^\u009DAs\t  \u0090\u0090)  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  HH  €„\u0004  Hè²/_gÏ\u001D\u0002@Q\\{íµå+Ìœ9³\u00183fLy\u001C~ô£\u001F\u0015\u0013'N¬Îú7nÜ¸bÚ´iÕÙà|ðÁ\u0007Å[o½U\u009D]ÜÑ£G‹ÿþ÷¿åñ›o¾Y|ôÑGåñ§Ÿ~Zœ8q¢<æÂ\u0004 €\u0004â&\u001E7óÐ÷\u0006\u007FóÍ7—¿\u001BÎM»î\u000E\u001E<X\u001D\u0015e¸8}úty\u001Caãí·ßN\u001B\u0018\u0004 €–˜<yrùt>gÎœbÂ„\tåùÔ©S‹ñãÇWÿ\u000B.¦3úÐ\t\tGŽ\u001C)ÃÁ±cÇªÿE»\b Ð@ëÖ\u00AD«ŽÚ©íÿ¾áŠ§õ¸±Ç\u008Dþ¦›n*oú³fÍª~ËHˆ0\u0010£\u00041bpòäÉâÐ¡CÕošK €\u0006:{¶Ý_ÛË.‹K\u0013\u001Dq³Ÿ1cFùŠaü\u001Bn¸¡ú
                    £éµ×^+CÁK/½T¼üòËÅ‡\u001F~Xý¦\u0019\u0004 h \u0001 Ýbè~Þ¼yåkÁ‚\u0005Õ»Ô]\u0004‚\u0018\u0019è\u0004‚Ï?ÿ¼úM=\t Ð@\u0002@»D\u0011Þüùó‹[o½µüyÝu×U¿¡ÉöíÛWìÚµ«Ø³gOYKP7\u0002 4\u0090 Ð|qÓ_ºtiqÛm·yÊo¹3gÎ”! \u0013\u0006êB €\u0006\u0012 š+
                    ÷î¼óÎòæ?vìØê]²øä“OŠ\u001D;v\u0014Ï<óÌ¨/=\u0014  \u0081\u0004€f‰5÷+W®,oú†÷éˆþ\u0004\u008F=öXY30\u001A\u0004 h \u0001 \u0019âiÿ¾ûî+\u0016/^\\½\u0003ß\u0016ÅƒO<ñD±mÛ¶ê\u009DÞ\u0010  \u0081\u0004€z‹êýßüæ7ÅÜ¹s«wàÒÞ}÷ÝbóæÍå«\u0017\u0004 h \u0001 ž\u0016-ZT<üðÃ\u00ADm©KoÄˆÀªU«F¼\u0003¡ \s
                    $ ÔK4èyê©§ÜøéªøL\u00AD]»vÄ–\u0010Ú\u000E\u0018`ˆ¢%ï“O>YîJçæO·Å(À\u001Bo¼Q,_¾¼z§»Œ @\u0003\u0019\u0001\u0018}qQÞ°aCqÕUWUïÀÈÙ½{wqÇ\u001Dwtµ»  \s
                    $ ŒžXÒ·uëV\u0005~ô\\Ô\u0006,Y²¤8uêTõÎð\b Ð@\u0002Àèˆe}/¼ð‚íu\u00195ÑH(B@7v#T\u0003 0 wß}wÙ¸ÅÍŸÑ\u0014Ÿ¿ø\u001CÆçq¸\u0004 €KˆB¿-[¶Tg0úâó¸nÝºêlhL\u0001@\u0003™\u0002è\u008D¨ò\u008F!\u007FóýÔÕúõë‡\u001C\u0004\u0004 h \u0001`äÅn}\u0007\u000E\u001C(fÍšU½\u0003õ´bÅŠ!µ\u00116\u0005 pž¸ùÇ“¿›?Mðì³Ï\u000E©W€\u0011 h # #kûöíÅ²eËª3¨¿3gÎ”›N
                    fgA\u0001 \u001AH \u00189Qð\u0017\u001DØ i"\u0004Üxã\u008D\u0003î\u0013`
                      ²fÍ\u001A7\u007F\u001AkìØ±e“ª\u00812\u0002\s
                    d\u0004 û¦N\u009DZ¼òÊ+åE\u0014šìþûï\u001FÐ–Â\u0002 4\u0090 Ð}qóWôG\u001BÄTÀôéÓ‹·Þz«z§\u007F¦ €ôV¯^íæOk\ft*À\b 4\u0090\u0011€î‰Í}bËUCÿ´Í¥¦\u0002\u0004 h \u0001 {¢Ù\u008FN\u007F´Ñ\u0007\u001F|P\\\u007Fýõ\u0017ÜBØ\u0014 \u0090ÖŒ\u00193Üüi\u00AD«®ºê¢›\u0006\u0019\u0001€\u00062\u0002Ð\u001DÑí/š§@[]l\u0014À\b \u0090ÒÄ‰\u0013‹ùóçWgÐN\u0017\u001B\u0005\u0010 €”î»ï>…\u007F¤ð«_ýª:ú&S Ð@¦ †'6ûyç\u009DwÊ§#È ¦ºöìÙS\u009D\u009D# @\u0003\t Ã\u0013;§Å\u000EjŒœƒ\u0007\u000FVG\u00037sæL£2#d÷îÝÅ’%Kª³s\u0004 h \u0001`x\u0014ÿ
                    Í‘#GÊb²Ã‡\u000F—?\u008F\u001E=Zý¦(Nœ8Q|úé§ÕÙðEkæqãÆ•ÇS¦L)Æ\u008F\u001F_ÜrË-å{Ó¦M+ßgà¢;à•W^ù\u008Db@\u0001 \u001AH \u0018ž\u0018þ¿îºëª3Î÷î»ï–7ôãÇ\u008F—7ùnßÜ»!\u001A8EHˆP0gÎ\u001C¡` –.]ZìÜ¹³:\u0013  ‘\u0004€¡‹\u001BGÜàøZ,\u0015Û±cG¹—ü±cÇjw³\u001Fˆ\u0018\u0019ˆ pë\u00AD·–7º\u00181à›bÚë®»îªÎ\u0004 h$\u0001`èÌÿŸ\u0013CÂqÓ\u008F'Â¸ñ·I\u0014yF\bX¹r¥=\u001Eúøä“OŠ«¯¾ú«i Ë \u0081TbÈ8³xÚ¿çž{Êùàx\u001AlÛÍ?Ä
                    nÛ¶mÅìÙ³‹\u001Bn¸¡,€£(GE¢Ð²C  R‰yã¬6nÜXÜxã\u008DÅÓO?ý\u008Db°6‹-q£ú=Z>¿öÚkÕ»yÝvÛmÕ‘  $’µ‚<ª÷ãßýàƒ\u000F6r~¿\u001B\u000E\u001D:Tî‘\u001F;äÅPxV}G Ô @\u0003©\u0001\u0018šŒ\u0005€ûöí+Ÿ€³<ñ\u000FÄäÉ“‹½{÷¦\\\t\u0012µ\u001FßûÞ÷Êc# @\u001A\u0011 2‰'\u007F7ÿo‹i\u0081¨\u000FÈ8%\u0010\u008D–:Ó`\u0002 \u0090Æå—_^\u001Dµ_<éE‘Ÿ›\u007Fÿ>üðÃ2\u0004Ä\bI6“&M*\u007F
                     @\u001A™zÿÇ\u0012¿xÒåÂ"\u001CÅ\bÉ›o¾Y½“CtV\f\u0002 \u0090Fl\u0001œÅóÏ?_\u001Dq1\u009D\u0010\u0010#&Y˜\u0002 Ò‰\u00061Yxú\u001F¸ø[=ðÀ\u0003ÕYûu\u0002€U Ð@V\u0001\fÍÖ\u00AD[‹\u0015+VTgí6Òû)´Ñ“O>Y®\u0010È`áÂ…\u0002 4‘ 04\u008F?þxš'½èú–uÍ?\u0003c
                     HãôéÓÕQûEÃ\u001B¸\u0018\u0001 H#–~eñÈ#\u008F”#\u001E™ê\u001E\u0018\u001C\u0001 HãÔ©SÕQ\u000E1ÝqàÀ\u0081r›\\8Ÿ\u001A h 5 C\u0013\u0005^ÙÖ|wD\u000Bäè
                    \u0010Ë\u0003\u00AD\u0010 \b Ð@\u0002ÀÐÄf@™7‚éˆ5ïG\u008F\u001E-\u000E\u001F>\\œ8q¢|e\u001B\u001DA €F\u0012 †î\u009DwÞI¹\tÌ@|ðÁ\u0007åè@¬\u001Exýõ×Ë÷b?\u0081/¾ø¢<ŽÐ µp{\b Ð@\u0002ÀÐmØ°¡xè¡‡ª3†*‚A'\fÄÏãÇ\u008F—Ç!‚ÄÛo¿]\u009D}M€¨\u0017\u0001 \u001AH \u0018º\u00193f”7"ê!j2Î_\u009D\u0011Ó\u0011ï½÷^uvî¼ï\u0014Åùç\f\u008D \s
                    $ \fÏûï¿Ÿjc 6ëL[„¾S\u00171ÒÐ\tzQópìØ±ò˜¯\t Ð@\u0002ÀðDË×U«VUgdsðàÁòg\u0014?~öÙg_Õ9\u001C:t¨|?\u000B\u0001 \u001AH \u0018žX\u0017ß¹\t@_\u009D\u0015\u0012\u009Dº†X5ròäÉrš¢mË'\u0005€šh{eòH_Ð³\u0011 †ï…\u0017^(\u0016/^\\\u009DÁÀD?…¨?ˆÑƒ˜nˆ\u001A†¦N/\b 5! 0\u0018\u0002Àð]{íµÅ\u001Bo¼QŒ\u001D;¶z\u0007†.¦\u0011"\u0014Äê‡\u00181hÂt‚ P\u0013\u0002 ƒ! t‡%\u0081Œ¤˜fŠfK\u0011\u0006ê\u0018\b\u0004€š\u0010 \u0018\f\u0001 ;b£œøîY\u0011@/t\u0002Á¾}ûj1m  Ô„ À`\b Ý3oÞ¼bÿþýÕ\u0019ôF,_Ü³gO±k×®Q\u001B\u001D\u0010 jB `0\u0004€îZ³fMñè£\u008FVgÐ[£\u0015\u0006\u0004€š\u0010 \u0018\f\u0001 ûÔ\u0003P\u0007±Êà™gž)¶lÙR66\u001AI\u0002@M\b \f† 02¶nÝZ¬X±¢:ƒÑ\u0013ý\bbûæßÿþ÷#Ö\u007Fà;ÕO€ôîºë®²@\u000BF[,O\u008D0\u001A}\u0006\u000E\u001C8PÖªt›  ÐÇ’%KŠ\u008D\u001B7Vg0úæÎ\u009D[\u0016ªîÝ»·˜:ujõîð\t  }D\u000BØ\u0007\u001F|°XºtiÙ\u0006\u0016êbÁ‚\u0005Åk¯½Vîe1qâÄêÝ¡\u0013  ú±sçÎböìÙå\u0010,ÔIld\u0015],cõÊp\b  \u0017\u0010ÅWÓ§O/ž}öÙê\u001D¨‡ñãÇ—KW£>`¨£\u0001\u0002 ÀEÄ”@\u0014\u0007Îœ9³ì÷\u000Eu\u0012õ\u0001¯¾új1cÆŒê\u009D\u0081\u0013 è‰Øx\u0005š,Z·Æ”@Ô\u0006Dã\u0016¨‹he\u001D[\u0018¯^½ºzg`\u0004 €AˆÚ€ë¯¿¾X¿~}¹V\u001BêbÓ¦Må6×±ÇÅ@\b ôÄ@?\u0090\\š¿åè‹i\u0081uëÖ\u0015W_}uqÏ=÷”ÝÛ \u000E\u0016/^\\6´\u001A\b\u0001€žèÆ’\u0015Îñ·¬\u008FhÕúôÓO—#\u0002óçÏ×DˆZX¶lY¹TðR\u0004 zbÜ¸qÕ\u0011ÃåoYO/½ôR±páÂ²¥w4\u00122*ÀhŠ¥‚1Ju1\u0002@Mœ:uª:j§k®¹¦:b¸\u0014TÖ[|—£‘PŒ
                    L›6M\u0018`Ô<òÈ#ÅòåË«³o\u0013 è‰I“&UG\f×äÉ“«£vjÓÍòÄ‰\u0013ß
                    \u0003\u001A\u000BÑK1\u0015p¡‡\u0006\u0001 &¢¨¨Í<µvÏ\u000F~ðƒêˆ&é„\u0081\u001Bo¼±\\¶\u0015Ë\tŸzê©²µ+Œ”ØTèñÇ\u001F¯Î¾ÉvÀ5ÑömH£§zTL·=èôB´ ½á†\u001Bª³ö‰\u001BbtßË$ê:æÌ™SÜrË-åf/ÑÜ\u0005º)V\u0007ìÙ³§:;G ¨‰\fû\u0090G•t\u0014J1t1üßö!äƒ\u0007\u000F\u0016?ûÙÏª³¼â¿õ”)SÊWt!Œ`\u0010í_a(âº\u0011ÁºïC˜)€šˆåDmwûí·WG\fU\f\u001B·\u009DQ¢sb\u001F‚h:´víÚ2\u0010]yå•å
                    ƒ\u0018\u001DˆÞ\u0003Ñˆ(–\u001DF`‚K‰QÃ»ï¾»:;Ç\b@MÄr\u008D¨Øl3Ó Ã×öáÿ\u0010\u001BïDï}\u0006'\u001ADÅHAL'ÄhAÔŠDíMŒ$DÍ\u0001D\u0081m\u0014¤v\b 5‘! „ûï¿¿Ø¼ysuÆ`,Z´¨Ø½{wuÖ^\u0002ÀÈˆP\u0010á BA§(÷Š+®(ßïP{Ð~\u0011\u0012c_‹  ÔD\fÍlÙ²¥:k¯ó\u0013(\u0003\u0017Û~f¸@Ç\u0016§1ìÍèê\u0004†Ð74|÷»ß-o"\u001D1ò0kÖ¬êŒºëûý\u0012 j"¶rŒÝœ2ˆùËhŸÊÀÍ›7¯Ø¿\u007F\u007FuÖnQç\u0010sß4W\\ÏbùY_QÌx~\u0011ã„\t\u0013¾Õ×"BGôL`dô}\b\u0013 j">ô1GžAì \u0016O²\u009Da(..ž¼b¿ï,\u0015àqñ\u008F5ó\u0010úŽ>„X.Ù\u0011Ë&;L_\f\\g\u001A@ ¨‘÷ß\u007F?M±Nì§\u001E{«·½\u0005òpE0|å•WZ_ø×W<9*\u0014e¨:\u0001¡ó³Ó[Á\u0012Ê¯=ôÐCÅï~÷;\u0001 NöîÝ[,X° :k¿#GŽ”Ë›\\ì/,ööŽ\u0006\u001EYÄZåè”\u0007ÝÖé«\u0010Ó\u0013QP\u001BK*³ê\u0014Úê\u0003P#±î7“(\u001C\u001AÈ–•YÅÊ\u0090L7ÿ\u0090í;@ïtú*töfˆaðhÅœeêµ¯Î–â\u0002@\u008D¼ýöÛÕQ\u001EÑý0º vª\u008D9WU½aÃ†\u0014ËBÏ' Ð+1\u0007~ï½÷–#NÙš)\t 5”µð)B@ÌsÇÐ\\v1L\u0019ËýbŽ.£“'OVGÐ\u001B\u001F~øa9\u0015\u0019Ëã²\u0010 j(ã\b@G\u0014¹Å2Èxò\u008D'àŒÖ¬YSVûg^S\u009Dù;ÀèŠµñYvfì\u0014›+\u0002¬™\f\u00AD^/%
                    ÁbT Ë2Á¨PŽZˆìÍTb.6úÝ\u008F¤\u0018aé<ý´Ý¡C‡ª#\u0006*¶Í}à\u0081\u0007ª³vë\u0014AF ðªÉëË'à³œóÊ+¯œ]¾|ùÙ1cÆôû·jò+þMño‹\u007F#çlß¾½ß¿U7_kÖ¬©þßÚoÝºuýþ
                    ¼.üú2ˆW\u007F½ö\u001B7nÜYS 5ó×¿þµ:"žˆc¹JôGˆ'äó;†5Qü\u001Bâß\u0012ÿ¦ø·i¡úµ^|ö3\u0015\u0019F\u0011iß¦9\\Zß†Cm\u0017;Ðš\u0002¨¡\u008F?þXÓŠ\u000Bˆ9º\u0018ÚŒé\u0081xÕ½‘P\f7Çr£›o¾¹˜?\u007F¾\u0016§\u0017\u0011Ÿù‘Þ\u0016;.ðÑ
                    5‹h¸\u0015Uî\u0019¶\u001B\u001F®X‰\u0014Áüü\u0016Æm\u0014Ÿ‹Ø™U ¨¡X\u0016\u0017sà\\Z|\u0090£xðøñãåÏXI1Z\u0017»¸€Ä|~Üðoºé¦ògæf#ƒ\u0011Ë°¢\u0012»\u0017²\u0005ì}ûö\u0015K–,Ñpë\u0012V¯^]lÚ´©:k·NÃ-\u0001 †n¿ýöbÇŽ\u001DÕ\u0019C\u0011Oy1:\u0010aàõ×_/ßë¯(*– ]hXøB\u0005c\u009DaÕ¸ÉÇM?ž*Ýè‡§Óš´\u0017bÉi¶©—\bX\u0011\u0002Œ\u0004ô/¾ÃQ€\u009Dáé?t\u0002· PC™†¢ ÄÊ—^ÍÏgªôî+žú\u0016.\\hÿ\u008DóÄõ6zodšžÛ½{w\u0019\b\u0015\u0001ÖP¤ô,[\u0003CŒÖô²8/k¯\u0081\bY\u001An}SŒòEï\u008Dlµ9\u009D‘ \u0001 ¦^|ñÅê\bÚ\u00AD×Ó]™·\u001AŽ\u00060ñp±}ûö4ý\u0010ú\u0013OýÑt,FE2Nßu:nš\u0002¨©è†÷Î;ï¤Ù\u001E˜œÎœ9SnÌ\u0012µ\u0018½d¥Í¹¿ýo\u007FûÛ²ð-S\u0081`ÔXmÜ¸1õµ5
                    ”c\u0015•\u0011€šŠ/äc\u008F=V\u009DA;mÙ²¥ç7ÿ°gÏžê(¯¨1Šþ÷ñ \u0011OÃm^\u0003\u001F£\u001DQå\u001F…~1â”ùæ\u001FÁÏ\b@\u0003Ä‡6¾œŠ\u0001i«^\u0016ÿõ\u0015ûÁG!\u0014ß\u0014K\u0006Ÿ{î¹rÛÜ¦‹aþøï|ç\u009Dw\u0016sçÎ\u00ADÞ¥ï’[\u0001 æ²V,Ó~\u009DJäÑ\u0010Sl1
                     \\÷/ž\u0012cÙìáÃ‡Ë\u001BF\u0013öåˆ‚¾NÓ\u00ADø©éVÿÖ¯__¬[·®<\u0016 jÎ( m\u0015\u0017èÑ,ÈÛ»wo±`Á‚êŒ‹‰@Ði´uúôéò8–\u0013ŽÆ’ÂèÃqùå——½\u001C®¸âŠ²ùV¼tO\u001D˜èHúÒK/•Ç\u0002@\u0003¼ðÂ\u000BÅâÅ‹«3h¾^vþ»\u0090åË——û10<GŽ\u001Cùªˆ0\u0002ÂgŸ}öÕñ`\u001B\u000FMš4é«ùùÎÍ½#žê=\b
                    O\u0004¹h\u0001Üùï" 4@|\tbý®\u000F?m\u0011s²£½]mŒ®E+iÈâüi7« \u001A ’t–\u001EÕ´_<u×a¯úX} \u0010\u0090LÎ_Yf\u0004 !¢h):VEÕ44UÝv§‹ÑµØa\u0012Ú.>çÓ§O¯ÎÎ1\u0002Ð\u00101Çvï½÷VgÐL±¢¥.7ÿ\u0010£kF\u0001Èà‰'ž¨Ž¾f\u0004 až|òÉbÕªUÕ\u00194Çh.û»\u0018£ ´]Œ¼EÇÍó;>\u001A\u0001h˜µk×*\\¢q>ùä“ÚŽ`\u0019\u0005 í¶mÛÖo»g\u0001 abøTc š&öû\u001F\u008D–¿\u0003\u0015=ñ¡\u008Db·Í\u000B}¾ÿïË×¹–@4Æ?þñ\u008F²\u0011ÆO~ò“ê\u001D¨¯§žzªö7Ø\b'±\u0006ýÇ?þqõ\u000E´CL»ýë_ÿªÎ¾I
                    @ƒi\u0010DÝEoù¸ õ7üX7Ñ;>6‹±\u0003'm\u0011Knïºë®êìÛ\u0004€\u0006‹¥\u0081\u0007\u000E\u001C([bBÝÄ^ë³gÏ®UÕÿ¥Ì›7¯Ø¿\u007F\u007Fu\u0006Í5\u0090%·j \u001A,žªâé*æx Nââ³páÂFÝüCôH\u008F)\u000Bhº\u0081,¹5\u0002Ð\u0002±\u000BV´
                    ¶\u0019\u0006u\u0010ýÆ£Õo\u0013v\u0090ë\u008F¦[4Ý£\u008F>Z®\u0018»\u0014# -\u0010û©/[¶¬¼ðÂhŠÏ`Ô¥4õæ\u001FbdmÅŠ\u0015¾O4RÌû\u000Fäæ\u001FŒ ´H44‰-N\u001511\u001Ab\u00AD\u007FLIÕ¡Ï\u007F7D=@ô\u0007°\t\u0017M\u0011E·1õ6PF Z$\u001AšDÑU\u0014_A/E\u001DJlïÛ–›\u007Fˆz í·iŠÎŠ›Á0\u0002ÐB±œ)F\u0002¬\u000E \u0017¢\u008Dn\\xN\u009D:U½Ó.wß}w±eË–ê\fê'¾ƒ\u0011À\u0007[t«\u0011P\u000BÅ\u001Cæ\u001FÿøÇâ‡?ü¡Æ&Œ¨ƒ\u0007\u000F–CŽuîò7\\\u007Fÿûß‹Ë.»¬˜3gNõ\u000EÔÇŸþô§â\u0017¿øEñŸÿü§zgà\u0004€–úßÿþWüùÏ\u007FÖ1\u0090\u0011\u0013ÅFwÜqÇ\u0090.<M\u0013S\u001B_|ñEñóŸÿ¼z\u0007Fßý÷ß_üú×¿.¯÷Ca
                     \u0081\u00193f”\u0017kËšè†Xã\u001FUò1GžÍ¢E‹ŠíÛ·+\fdTEÁmtøÛ³gOõÎÐ\b IÄÚæØJ8.Ü0TQh\u0014OýMkðÓM±Ú&Úp_wÝuÕ;Ð;Qä\u001D57±ü{¸¬\u0002H"ê\u0002"1ÎŸ?ßvÂ\fZ<qDxlbw¿n‹Õ6Ó§O/ë\u001F W¢/E4ø‰Ï^7nþA
                    @2ÿþ÷¿‹?üá\u000FÅ÷¿ÿýbÚ´iÕ»pañÔ\u007Fë\u00AD·\u0016\u007FûÛßªwˆ@ýÜsÏ©±¡'Ž\u001C9R†ï\u009D;w\u000Ey¾¿?F \u0012Š'¸\u0018
                    ˆ à)†\u000B‰¥E1bÔö*ÿáˆŽkQ[\u0013\u0017hè¶ÎÈ[ôwéÖS\u007F_\u0002@b1”\u0019kG£o»\u000B\u0018\u001DÑÔ'.:1Ô˜±Ðo°âÂ\u001C\u0017èø›Å\u0005\u001B†+†û7nÜXîæ·mÛ¶êÝ‘\u0011E€^^g\u0017-ZtöÕW_=KN\u001F\u007FüñÙÕ«WŸ\u001D3fL¿Ÿ\u000F¯K¿Æ\u008D\u001BwvëÖ\u00ADÕ_\u0014\u0006'¾ƒ\u001B6l8;qâÄ~?_Ý~Y\u0005À·,_¾¼\\[jÙ`\u000EQ\u0014úÌ3Ï\u0014›6mJ_à×-×^{mñðÃ\u000F\u0017K—.µd\u0090KŠïà\u0013O<QvœìåwP à‚¢\u007F@´Au\u0011k§˜öÙ¼ysYXÄÈøòI®\fÓ\u0011ªm×Íù¢Îæé§Ÿ.\u000BJ£°´×\u0004 .).bq\u0001[¹r¥µÏ
                    \u0017sÔ;vì(Ÿ6F¢¨ˆþÅþ\u001CÑµ-¾CvëÌ-žöã;\u0018£n£ý\u001D\u0014 \u0018”Ø"5F\u0005¢:Ü¨@sÄj\u008FçŸ\u007F¾¼ðŒÆ“\u0006_‹ïÐí·ß^v\u00154*\u0090C\u0014õE×¾]»v
                    »{_7\t \fIt\u0016Œ\u0010\u0010›PÄOO5õ\u0013{Ùÿå/\u007F)/8æöë'¾C¿üå/Ë×²eËªwi‹xÒß¿\u007F\u007F¹’&^uü\u000E
                     tEÔ\u000BÄSM„\u0001Åƒ£#†÷ã‚óâ‹/–/OúÍ\u0011S\u000412ðÓŸþ´ü\u000E™jk¦hš\u00157û—_~¹\\f]w\u0002 ]7yòäbæÌ™Å-·ÜRþ\u0014\bFF\f+Æ.uÇ\u008F\u001F/\u007FÆ‹vˆU\u0004}\u0003\u0081©‚ú‰ïßÑ£G‹Ã‡\u000F—\u0005µqÓoZè\u0016 \u0018qQDØ7\u0010Ìš5«ú
                    ƒ\u0011
                    zâ‚sìØ±Æ<aÐ\u001D±\u0001Q¼nºé¦òg|\u008FÔàôVTìÇ÷/\u0002wülC\u0011\u00AD @ÏÅÜg\\À&MšT\\sÍ5åqŒ\u001A¨#øZ\u0014í\u009D:uªxï½÷Ê'û8Ž\u0017tÄwfÊ”)å+¾C\u0011´\u008D¶
                    _|÷¢õõ?ÿùÏ2dÇ\\~„î6\u0012 ¨\u008Dþ‚AˆŸm|Ú‰m=ãBãFO7E0èŒºM˜0¡\u001C1ˆï–‘·sOñQŒ\u0017Cõñ$\u001F:ß¹x¢Ï¶ç… @ct.l1?\u001A¯+®¸¢¼¸uÄñhÏ•væ\u0005;â¢rúôé²@ïäÉ“å{ñ{\u0005zŒ¦(Úí„êÎ÷)œÿ\u009DŠ}Bê.¾[\u009Dé°Î÷-tjbâ¦Þ†áú‘  ÐZ}/r}Å\bÃ`§\u001BúÞÀÏ§øŽ\f"\u0018Äj…\u0081Š-Ç\u0007:%\u0011£a\u001F}ôQuÖ¿xrW÷ÒMEñÿ\u0017qù¨CKÑ{    IEND®B`‚""";

        if (!JETSON_DIRECTORY.exists()) {
            if (JETSON_DIRECTORY.mkdir()) {
                System.out.println("Successfully created " + JETSON_DIRECTORY.getAbsolutePath());
            }
        }

        if (!JETSON_RESOURCE_FOLDER.exists()) {
            if (JETSON_RESOURCE_FOLDER.mkdir()) {
                System.out.println("Successfully created " + JETSON_RESOURCE_FOLDER.getAbsolutePath());
            }
        }

        if (!JETSON_DUMMY_DIRECTORY.exists()) {
            if (JETSON_DUMMY_DIRECTORY.mkdir()) {
                System.out.println("Successfully created " + JETSON_DUMMY_DIRECTORY.getAbsolutePath());
            }
        }

        for (File verifiedFile : verifiedJetsonFiles) {
            if (!verifiedFile.exists()) {
                if (verifiedFile == JETSON_BLOAT_TXT || verifiedFile == JETSON_TEMP_TXT) {
                    if (verifiedFile.createNewFile()) {
                        System.out.println("Successfully created " + verifiedFile.getName());
                    }
                }
            }
        }

        if (!JetWriter.write(JETSON_BLOAT_TXT, bloatToWrite)) {
            System.err.println(JETSON_ERRS[2] + " " + JETSON_BLOAT_TXT.getAbsolutePath());
        }

        // Clean up of .jetson directory
        if (!JetWriter.write(JETSON_TEMP_TXT, "")) {
            System.err.println(JETSON_ERRS[2]);
        }

        File[] jetsonListOfFiles = JETSON_DIRECTORY.listFiles();
        if (jetsonListOfFiles != null) {
            for (File jetsonFile : jetsonListOfFiles) {
                if (!verifiedJetsonFiles.contains(jetsonFile)) {
                    String tempName = jetsonFile.getName();
                    if (jetsonFile.delete()) {
                        System.out.println("Removed unverified Jetson file " + tempName);
                    }
                }
            }
        }
    }
}