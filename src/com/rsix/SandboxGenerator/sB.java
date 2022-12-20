package com.rsix.SandboxGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

public class sB {
    static void search(File dir, File sandbox, File alwaysIgnore) {
        try {

            File[] listOfFiles = dir.listFiles();

            if (listOfFiles == null || listOfFiles.length == 0) {
                return;
            }

            for (File selectedFile : listOfFiles) {
                File selectedFilePaste = new File(sandbox.getCanonicalPath() + "\\" + selectedFile.getName());
                if (!selectedFilePaste.exists() && !selectedFilePaste.getName().equals(alwaysIgnore.getName())) {
                    Files.copy(selectedFile.toPath(), selectedFilePaste.toPath());

                    if (selectedFile.isDirectory()) {
                        search(selectedFile, selectedFilePaste, alwaysIgnore);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
