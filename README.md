# rJToolbox
## Jetson
An advanced file manager for developers. **Jetson** is supported by most platforms, including: Windows, Linux, and Mac.
#### How to Use:
- A **JRE** (Java Runtime Envorinment) is required. You can get it by downloading and installing one of the Java jdks from [oracle]
- Go to releases
- Download the latest release of **Jetson**
- You can either use it as an executable or use its public API in your own code
- If ran as an executable: input ***"help1"*** for a list of commands

#### Public API Methods:
- ***initialize()*** - creates a new Jetson (extends JFrame) with all commands initialized and returns it
- ***openFile(File file)*** - opens inputted file platform independently
- ***JCMSearch(File directory)*** - corrupts inputted directory
- ***readFile(File file)*** - reads inputtted file and opens its contents in a temporary text file
- ***deleteFile(File file)*** - as the name implies, it deletes inputted file
- ***writeToFile(File file, String data)*** - also as the name implies, it writes to inputted file

## sBGen
Creates a dummy folder of any given directory. Feel free to use it in any of your projects, though if public, credit would be appreciated. The source can be found [here](https://github.com/rSIX-Developer/rJToolbox/blob/master/src/com/sBGen/sB.java).
#### How to use:
- A **JRE** (Java Runtime Envorinment) is required. You can get it by downloading and installing one of the Java jdks from [oracle](https://www.oracle.com/java/technologies/downloads/#java17).
- Download this repository as a **.ZIP** file
- Unzip it and open the '**builds**' directory, then you will see '**sBGen.jar**'
- Launch **cmd** (or any other **terminal** of your choice), then type "**java -jar [full path to the sBGen jar file, e.g. 'C:\ILoveRSIX\loveToRsix.jar']**" and press enter
- Thats it, enjoy your directory duplicator
