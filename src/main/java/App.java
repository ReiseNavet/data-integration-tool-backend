import io.javalin.Javalin;
import ui.BasicSemanticMatcher;

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
    System.out.println("Listening on port: " + PORT);
  }
}