# rJ Library
A **JRE** (Java Runtime Envorinment) is required to use any **rJ** tools. You can get one from [oracle](https://www.oracle.com/java/technologies/downloads/#java17)
## Jetson
An advanced file manager for developers. **Jetson** is supported by most platforms, including: Windows, Linux, and Mac.
#### How to Use:
- The **resource** folder is required. You can find it [here](https://github.com/r6dev/rJToolbox/tree/master/builds/Jetson/resources)
- Go to releases
- Download the latest release of **Jetson**
- You can either use it as an executable or use its public API in your own code
- If ran as an executable: input ***"help1"*** for a list of commands

#### Public API Methods:
- ***initialize()*** - returns a new Jetson (extends JFrame) with initialized commands
- ***verifyJetsonDirectory*** - checks if .jetson folder and all verified Jetson files are within
- ***openFile(File file)*** - opens inputted file platform independently
- ***Corrupt(File file)*** - corrupts inputted directory or file
- ***readFile(File file)*** - reads inputtted file and opens its contents in a temporary text file
- ***writeToFile(File file, String data)*** - also as the name implies, it writes to inputted file
#### Utilities
- ***SandboxGenerator***. Creates a dummy folder of any given directory. Feel free to use it in any of your projects. The source can be found [here](https://github.com/rSIX-Developer/rJToolbox/blob/master/src/com/sBGen/sB.java).
