package com.rsix;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

public class sB {
    static void search(File[] parameters) {
        try {
            File dir = parameters[0];
            File sandbox = parameters[1];
            File alwaysIgnore = parameters[2];

            File[] listOfFiles = dir.listFiles();

            if (listOfFiles == null || listOfFiles.length == 0) {
                return;
            }

            for (File selectedFile : listOfFiles) {
                File selectedFilePaste = new File(sandbox.getCanonicalPath() + "\\" + selectedFile.getName());
                if (!selectedFilePaste.exists() && !selectedFilePaste.getName().equals(alwaysIgnore.getName())) {
                    Files.copy(selectedFile.toPath(), selectedFilePaste.toPath());

                    if (selectedFile.isDirectory()) {
                        File[] selectedFileParameters = {selectedFile, selectedFilePaste, alwaysIgnore};
                        search(selectedFileParameters);
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

        File[] parameters = {dirToSandbox, sandbox, sandbox};

        search(parameters);
    }
}
