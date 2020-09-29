import java.io.File;
import java.util.Random;

import org.apache.commons.io.FileUtils;

import fr.inrialpes.exmo.align.impl.URIAlignment;
import io.javalin.Javalin;
import io.javalin.core.util.FileUtil;
import services.Manager;

public class App {

  private static String generateHash() {
    int LENGTH = 10;
    int leftLimit = 97; // letter 'a'
    int rightLimit = 122; // letter 'z'
    Random random = new Random();
 
    String hash = random.ints(leftLimit, rightLimit + 1)
      .limit(LENGTH)
      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
      .toString();
 
    return hash;
  }

  public static void main(String[] args) throws Exception {
    int PORT = 7000;

    Manager manager = new Manager(); 

    Javalin app = Javalin.create(config -> {
      config.enableCorsForAllOrigins();
    }).start(PORT);
    
    /**
     * Demo endpoint for use in frontend testing.
     */

    app.get("/", ctx -> {
      ctx.result("<h1>Welcome to our API! Please POST here to make things work.</h1>");
    });

    app.post("/", ctx -> {
      // Saving the files as strings for now. Might need some exception handling.

      String baseSaveLocation = "temp/upload/" + generateHash();
      String sourceFileLocation = baseSaveLocation + "/source" + ctx.uploadedFile("source").getExtension();
      String targetFileLocation = baseSaveLocation + "/target" + ctx.uploadedFile("target").getExtension();

      // For booleans in formData, they are there if they are true, and not there if false.
      // This means true values are not null, and false values are null.
      boolean useEquivalence = ctx.formParam("equivalence") != null;
      boolean useSubsumption = ctx.formParam("subsumption") != null;


      FileUtil.streamToFile(ctx.uploadedFile("source").getContent(), sourceFileLocation);
      FileUtil.streamToFile(ctx.uploadedFile("target").getContent(), targetFileLocation);

      // For debugging, delete before production.
      System.out.println(sourceFileLocation);
      System.out.println(targetFileLocation);
      System.out.println(useEquivalence);
      System.out.println(useSubsumption);

      URIAlignment result = manager.handle(sourceFileLocation, targetFileLocation, useEquivalence, useSubsumption);


      ctx.result(result.toString());


      // Clean up temporary files.
      File tempDirectory = new File(baseSaveLocation);
      if(tempDirectory.isDirectory()) {
        FileUtils.deleteDirectory(tempDirectory);
      }

    });

    System.out.println("Listening on port: " + PORT);
  }
}
