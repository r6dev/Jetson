# Jetson
#### A file manager containing all Jetson utilities.
### Note:
A **resource** folder containing all fonts and images used by the Jetson file manager is required inside the working directory or the .jetson directory for the Jetson file manager to work. You can download this folder [here (with ads, if you wish to support me)](https://go.rsix.cf/jetson-resources-download), or [here (without ads)](https://minhaskamal.github.io/DownGit/#/home?url=https://github.com/r6dev/rsix-assets/tree/master/resources). This requirement only applies to the Jetson file manager; not Jetson utilities.

### Methods:
#### Public static:
- ***initialize()*** - Returns a new Jetson GUI instance with all commands ready
- ***verifyJetsonDirectory()*** - Checks for .jetson folder and all verified Jetson files

#### Public non-static:
Too many methods to document.

# Utilities
### Methods:
#### Public static:
- JetViewer.***view(file)*** - Opens any file or directory as long as it can be opened
- JetReader.***readFile(file)*** - Reads any file and returns its data as long as it can be read. Including **.exe** files and such
- JetCorrupter.***corrupt(file)*** - Corrupts any file or directory
- JetWriter.***write(file)*** - Writes to any file if it can be written to
- JetSbGen.***createDummy(file)*** - Creates a dummy folder of any directory

#### Public non-static:
- new JetRL(resourceFolder).***registerFont(fontToRegister, size)*** - Registeres font in the local GraphicsEnvironment and returns it
- new JetRL(resourceFolder).***registerFont(fontToRegister)*** - Returns ***registerFont(fontToRegister, 12)***
- new JetRL(resourceFolder).***createTerminalFont(size)*** - Registeres and returns a font based on the system of the user
- new JetRL(resourceFolder).***createTerminalFont()*** - Returns ***createTerminalFont(12)***
- new JetRL(resourceFolder).***createMonoFont(size)*** - Registers and returns JetBrains Mono if users OS is Windows; returns ***createTerminalFont()*** otherwise

### Variables
#### Public static:
- JetRL.***TITLE_BAR_COLOR***
- JetRL.***PRIMARY_BORDER_COLOR***
- JetRL.***SECONDARY_BORDER_COLOR***
- JetRL.***PRIMARY_BACKGROUND_COLOR***
- JetRL.***SECONDARY_BACKGROUND_COLOR***
- JetRL.***PRIMARY_TEXT_COLOR***
- JetRL.***SECONDARY_TEXT_COLOR***
- JetRL.***EDITOR_TEXT_COLOR***
- JetRL.***SCROLL_BAR_HOVER_COLOR***

</ul>

- Jetson.***OS_NAME*** - Name of users operating system
- Jetson.***IS_WINDOWS*** - True if Windows; false otherwise
- Jetson.***IS_LINUX*** - True if Linux; false otherwise
- Jetson.***IS_MAC*** - True if Mac; false otherwise

# UI
- ***JetsonScrollBarUI*** - UI for Jetson scrollbars

### Methods:
#### Public static:
- ***JetsonScrollBarUI.newLayout()*** - Returns a new ScrollPaneLayout suited for the JetsonScrollBarUI
