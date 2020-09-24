import algorithms.ui.BasicSemanticMatcher;
import io.javalin.Javalin;

public class App {
  public static void main(String[] args) throws Exception {
    int PORT = 7000;

    Javalin app = Javalin.create(config -> {
      config.enableCorsForAllOrigins();
    }).start(PORT);

    /**
     * Demo endpoint for use in frontend testing.
     */

    String result = BasicSemanticMatcher.runBasicSemanticMatching();
    app.get("/", ctx -> {
      ctx.result("{ \"value\": \""+ result +"\" }");
    });

    app.post("/", ctx -> {
      // Saving the files as strings for now. Might need some exception handling.
      String source = new String(ctx.uploadedFile("source").getContent().readAllBytes());
      String target = new String(ctx.uploadedFile("target").getContent().readAllBytes());

      System.out.println(source);
      System.out.println(target);

      ctx.result("{ \"value\": \"Files uploaded\" }");
    });

    System.out.println("Listening on port: " + PORT);
  }
}
