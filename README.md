# Jetson
Jetson is a file manager in GUI form with all Jetson utilities packed with. To use Jetson or any Jetson utilities, the *resource* folder has to be present. You can find it [here](https://github.com/r6dev/Jetson/tree/master/resources)
### Methods:
- ***initialize()*** - Creates and returns a new Jetson GUI instance with all commands ready
- ***verifyJetsonDirectory()*** - Checks for .jetson folder and all verified Jetson files

# Utilities
### Methods
- ***JetViewer.view(file)*** - Opens any file or directory as long as it can be opened
- ***JetReader.readFile(file)*** - Reads any file and returns its data as long as it can be read. Including **.exe** files and such
- ***JetCorrupter.corrupt(file)*** - Corrupts any file or directory
- ***JetWriter.write(file)*** - Writes to any file if it can be written to
- ***JetSbGen.createDummy(file)**** - Creates a dummy folder of any directory
