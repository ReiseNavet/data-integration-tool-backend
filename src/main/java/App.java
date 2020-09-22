import io.javalin.Javalin;
import ui.BasicSemanticMatcher;

public class App {
  public static void main(String[] args) throws Exception {
    Javalin app = Javalin.create(config -> {
      config.enableCorsForAllOrigins();
    }).start(7000);

    /**
     * Demo endpoint for use in frontend testing.
     */

    String result = BasicSemanticMatcher.runBasicSemanticMatching();
    app.get("/", ctx -> {
      ctx.result("{ \"value\": \""+ result +"\" }");
    });
  }
}