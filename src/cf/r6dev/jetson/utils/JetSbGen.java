package cf.r6dev.jetson.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

public final class JetSbGen {
    public static boolean createDummy(File dir, File sandbox, File alwaysIgnore) throws IOException {
        File[] listOfFiles = dir.listFiles();

        if (listOfFiles == null || listOfFiles.length == 0) {
            return false;
        }

        for (File selectedFile : listOfFiles) {
            File selectedFilePaste = new File(sandbox.getCanonicalPath() + System.getProperty("file.separator") + selectedFile.getName());
            if (!selectedFilePaste.exists() && !selectedFilePaste.getName().equals(alwaysIgnore.getName())) {
                Files.copy(selectedFile.toPath(), selectedFilePaste.toPath());

                if (selectedFile.isDirectory()) {
                    createDummy(selectedFile, selectedFilePaste, alwaysIgnore);
                }
            }
        }
        return true;
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

        try {
            createDummy(dirToSandbox, sandbox, sandbox);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
