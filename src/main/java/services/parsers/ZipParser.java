package services.parsers;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import net.lingala.zip4j.core.ZipFile;

public class ZipParser {
    /**
     * Unzips and returns the location of the unzipped folder.
     */
    public static String Unzip(String path) throws Exception{
        ZipFile zipFile = new ZipFile(path);
        path = FilenameUtils.removeExtension(path);
        zipFile.extractAll(path);
        File file = new File(path);
        String[] subFilesNames = file.list();
        if (subFilesNames.length == 1){
            String subFilePath = path + "\\" + subFilesNames[0];
            if (new File(subFilePath).isDirectory()){
                path = subFilePath;
            }
        }
        return path;
    }

}
