import controllers.AllergyController;
import controllers.IngredientController;
import controllers.MenuController;
import controllers.RecipeController;
import firebaseFiles.FireBaseDatabase;
import org.sql2o.Sql2o;

import static spark.Spark.port;

/**
 * Created by Kaempe on 03-04-2017.
 */
public class Main {
    //public final static String DB_URL = "mysql://80.255.6.114:3306/FindFood_User";
    public final static String DB_URL = "mysql://localhost:3306/FindFood_User";
    public final static String DB_USER = "FF_User";
    public final static String DB_PASS = "Dr4X8gvT";

    public static void main( String[] args) {
        port(9654);
        Sql2o sql2o = new Sql2o(DB_URL, DB_USER, DB_PASS);
        new AllergyController(sql2o);
        new IngredientController(sql2o);
        new RecipeController(sql2o);
        new MenuController(sql2o);
        new FireBaseDatabase();
    }
}
