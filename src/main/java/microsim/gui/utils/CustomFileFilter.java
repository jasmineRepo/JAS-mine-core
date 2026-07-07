package microsim.gui.utils;

import javax.swing.filechooser.*;
import java.io.File;
import java.util.ArrayList;

public class CustomFileFilter extends FileFilter {
    String extension, description;
    ArrayList<String> additionalExt = null;

    public CustomFileFilter(String extension, String description) {
        this.extension = extension;
        this.description = description;
    }

    public void addExtension(String extension) {
        if (additionalExt == null)
            additionalExt = new ArrayList<String>();

        additionalExt.add(extension);
    }

    public boolean accept(File f) {
        if (additionalExt == null)
            return f.isDirectory() ||
                    f.getName().endsWith(extension);

        boolean acceptable = f.isDirectory() ||
                f.getName().endsWith(extension);

        for (int i = 0; i < additionalExt.size(); i++)
            if (f.getName().endsWith(additionalExt.get(i).toString()))
                return true;

        return acceptable;
    }

    public String getDescription() {
        return description;
    }

}
