package services.utils;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import net.lingala.zip4j.core.ZipFile;

public class Unzipper {
  /**
   * Unzips and returns the location of the unzipped folder.
   */
  public static String unzip(String inputPath, String outputPath) throws Exception{
    ZipFile zipFile = new ZipFile(inputPath);
    zipFile.extractAll(outputPath);
    File file = new File(outputPath);
    String[] subFilesNames = file.list();
    if (subFilesNames.length == 1){
      String subFilePath = outputPath + "/" + subFilesNames[0];
      if (new File(subFilePath).isDirectory()){
        outputPath = subFilePath;
      }
    }
    return outputPath;
  }

  /**
   * Unzips and returns the location of the unzipped folder.
   */
  public static String unzip(String path) throws Exception{
    String outputPath = FilenameUtils.removeExtension(path);
    return unzip(path, outputPath);
  }

}
