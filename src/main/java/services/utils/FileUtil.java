package services.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {
  public static String readFile(String filename) throws Exception {
    String content = null;
    File file = new File(filename);
    FileReader reader = null;
    try {
      reader = new FileReader(file);
      char[] chars = new char[(int) file.length()];
      reader.read(chars);
      content = new String(chars);
    } catch (Exception e) {
      throw e;
    } finally {
      if(reader != null){
        reader.close();
      }
    }
    return content;
  }

  public static void writeFile(String filename, String text) throws Exception{
    FileWriter writer = null;
    try {
      writer = new FileWriter(filename);
      writer.write(text);
    } catch (IOException e) {
      throw e;
    } finally {
      if(writer != null){
        writer.close();
      }
    }
  }
}
