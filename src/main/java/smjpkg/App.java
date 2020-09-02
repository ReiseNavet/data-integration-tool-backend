package smjpkg;

import io.javalin.Javalin;

public class App 
{
    public static void main( String[] args )
    {
        Javalin app = Javalin.create(config -> {
            config.enableCorsForAllOrigins();
        }).start(7000);


        /**
         * Demo endpoint for use in frontend testing.
         */
        app.get("/", ctx -> {
            ctx.result("{ \"value\": 42 }");
        });
    }
}
