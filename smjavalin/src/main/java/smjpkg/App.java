package smjpkg;

import io.javalin.Javalin;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        Javalin app = Javalin.create().start(9640);
        app.get("/", ctx -> ctx.result("Hello World"));
    }
}
