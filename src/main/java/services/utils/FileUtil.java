package services.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.stream.Collectors;

public class FileUtil {
  public static String readFile(String filename) throws IOException {
    BufferedReader reader = null;
    String text = null;
    try{
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename),"UTF-8")); 
      text = reader.lines().collect(Collectors.joining("\r\n"));
    } catch (IOException e){
      throw e;
    } finally {
      if (reader != null){
        reader.close();
      }
    }
    return text;
  }

  public static void writeFile(String filename, String text) throws IOException{
    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
      writer.write(text);
    } catch (IOException e) {
      throw e;
    } finally {
      if (writer != null){
        writer.close();
      }
    }
  }

  public static void createDirectory(String filepath){
    File parentFolder = new File(filepath).getParentFile();
    if(parentFolder != null && !parentFolder.exists()){
      parentFolder.mkdirs();
    }
  }
}
