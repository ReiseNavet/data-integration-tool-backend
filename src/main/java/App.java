import java.io.File;
import java.util.Locale;
import java.util.Random;

import org.apache.commons.io.FileUtils;

import fr.inrialpes.exmo.align.impl.URIAlignment;
import io.javalin.Javalin;
import io.javalin.core.util.FileUtil;
import services.ExceptionHandler;
import services.HashGenerator;
import services.Manager;
import services.parsers.CellParser;
import org.semanticweb.owl.align.Cell;

public class App {

  public static void main(String[] args) throws Exception {
    int PORT = 7000;
    Locale.setDefault(Locale.ENGLISH);

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

      String baseSaveLocation = "temp/upload/" + HashGenerator.generateHash();
      String sourceFileLocation = baseSaveLocation + "/source" + ctx.uploadedFile("source").getExtension();
      String targetFileLocation = baseSaveLocation + "/target" + ctx.uploadedFile("target").getExtension();

      // For booleans in formData, they are there if they are true, and not there if false.
      // This means true values are not null, and false values are null.
      boolean useEquivalence = ctx.formParam("equivalence") != null;
      boolean useSubsumption = ctx.formParam("subsumption") != null;


      FileUtil.streamToFile(ctx.uploadedFile("source").getContent(), sourceFileLocation);
      FileUtil.streamToFile(ctx.uploadedFile("target").getContent(), targetFileLocation);

      String json = null;

      try {
        URIAlignment result = manager.handle(sourceFileLocation, targetFileLocation, useEquivalence, useSubsumption, baseSaveLocation);
        json = "[";
        for (Cell cell: result.getArrayElements()) {
          json += String.format("{\"source\": \"%s\", \"target\": \"%s\", \"relation\": \"%s\", \"confidence\": %s},", 
              CellParser.getSource(cell), CellParser.getTarget(cell), CellParser.getRelation(cell), CellParser.getConfidence(cell));
        }
        if (json.length() > 1){
          //Incase there are no cells, dont remove the "["
          json = json.substring(0, json.length() - 1);
        }
        json += "]";
        

      } catch (Exception e){
        ctx.status(500);
        json = ExceptionHandler.getErrorMsg(e);
        System.out.println(json);
        e.printStackTrace();
      }

      try {
        // Clean up temporary files.
        File tempDirectory = new File(baseSaveLocation);
        if(tempDirectory.isDirectory()) {
          FileUtils.deleteDirectory(tempDirectory);
        }

      } catch (Exception e){
        System.out.println("Couldn't delete files..?");
        e.printStackTrace();
      }

      ctx.result(json);

    });

    System.out.println("Listening on port: " + PORT);
  }
}
