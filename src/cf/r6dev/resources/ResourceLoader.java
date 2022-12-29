package cf.r6dev.resources;

import org.jetbrains.annotations.NotNull;

import java.io.File;
public abstract class ResourceLoader {
    public File RESOURCE_FOLDER;

    public ResourceLoader(File resourceFolder) {
        RESOURCE_FOLDER = resourceFolder;
    }

    public File getResourceFolder() {
        return RESOURCE_FOLDER;
    }

    @SuppressWarnings("unused") public File getFile(@NotNull String fileName) {
        return new File(RESOURCE_FOLDER + System.getProperty("file.separator") + fileName);
    }
}