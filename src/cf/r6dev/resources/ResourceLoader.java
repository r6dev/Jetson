package cf.r6dev.resources;

import java.io.File;
public abstract class ResourceLoader {
    public File RESOURCE_FOLDER;

    public ResourceLoader(File resourceFolder) {
        RESOURCE_FOLDER = resourceFolder;
    }

    public File getResourceFolder() {
        return RESOURCE_FOLDER;
    }
}