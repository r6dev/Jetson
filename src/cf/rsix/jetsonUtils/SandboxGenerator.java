package cf.rsix.jetsonUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

public class SandboxGenerator {
    public static boolean search(File dir, File sandbox, File alwaysIgnore) {
        try {

            File[] listOfFiles = dir.listFiles();

            if (listOfFiles == null || listOfFiles.length == 0) {
                return false;
            }

            for (File selectedFile : listOfFiles) {
                File selectedFilePaste = new File(sandbox.getCanonicalPath() + System.getProperty("file.separator") + selectedFile.getName());
                if (!selectedFilePaste.exists() && !selectedFilePaste.getName().equals(alwaysIgnore.getName())) {
                    Files.copy(selectedFile.toPath(), selectedFilePaste.toPath());

                    if (selectedFile.isDirectory()) {
                        search(selectedFile, selectedFilePaste, alwaysIgnore);
                    }
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.print("Directory: ");

        Scanner dirToSandboxScanner = new Scanner(System.in);
        String dirToSandboxString = dirToSandboxScanner.nextLine();
        File dirToSandbox = new File(dirToSandboxString);

        File sandbox = new File(dirToSandbox.getAbsolutePath() + "\\" + dirToSandbox.getName() + "-sBGenerated-" + Math.random() * 100);
        if (!sandbox.mkdir()) {
            if (!sandbox.exists()) {
                return;
            }
        }

        search(dirToSandbox, sandbox, sandbox);
    }


}
