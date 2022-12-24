package cf.r6dev.jetson.utils;

import cf.r6dev.jetson.Jetson;
import cf.r6dev.jetson.ui.JetsonScrollBarUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public final class JetWriter extends Jetson {
    public JetWriter(@NotNull File file) {
        // Setup
        wipe();
        setLayout(new BorderLayout());

        // Writer text area setup
        JTextArea writerInput = new JTextArea();
        writerInput.setBackground(JetRL.SECONDARY_BACKGROUND_COLOR);
        writerInput.setForeground(JetRL.EDITOR_TEXT_COLOR);
        writerInput.setFont(new Font("Consolas", Font.PLAIN, 12));

        writerInput.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                close();
            }
        });

        // Wrapper setup
        JScrollPane writerWrapper = new JScrollPane(writerInput, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        getContentPane().add(writerWrapper, BorderLayout.CENTER);
        writerWrapper.getVerticalScrollBar().setOpaque(false);
        writerWrapper.getVerticalScrollBar().setLayout(JetsonScrollBarUI.newLayout());
        writerWrapper.getVerticalScrollBar().setUI(new JetsonScrollBarUI());

        writerInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (file.isFile()) {
                        try {
                            if (!write(file, writerInput.getText())) {
                                JOptionPane.showMessageDialog(null, "Failed to write to " + file.getName(), "Could not write", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Given variable " + file.getName() + " is not a file", "Not a file", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    public static boolean write(@NotNull File file, String data) throws IOException {
        if (file.isFile() && file.canWrite()) {
            FileWriter writer = new FileWriter(file);
            writer.write(data);
            writer.flush();
            writer.close();
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        // Initialize
        try {
            new JetWriter(getJetsonTempTxt());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
