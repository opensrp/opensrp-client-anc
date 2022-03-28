package org.smartregister.anc.library.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by ndegwamartin on 13/11/2018.
 */
public class FileUtil {
    public static File createFileFromPath(String fileLocation) {
        return new File(fileLocation);
    }

    public static OutputStream createFileOutputStream(File file) throws FileNotFoundException {
        return new FileOutputStream(file);
    }
}
