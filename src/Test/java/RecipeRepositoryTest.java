import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import repositories.UserRecipeRepository;

/**
 * Created by Kaempe on 02-05-2017.
 */
public class RecipeRepositoryTest
{
    private final String DB_URL = "mysql://kaempe.club:3306/FindFood_User";
    private final String DB_USER = "FF_User";
    private final String DB_PASS = "Dr4X8gvT";

    /*
    @Test
    public void testCreateInvalidRecipeRelation(){
        // Arrange
        Sql2o sql2o = new Sql2o(DB_URL,DB_USER,DB_PASS);
        int id;
        String sql =
                "INSERT INTO UserRecipes (userId, recipeId) " +
                        "VALUES (:userId, :recipeId)";
        // Act
        try{
            Connection con = sql2o.open();
            id = Integer.parseInt(con.createQuery(sql)
                    .addParameter("userId","asdfg")
                    .addParameter("recipeId",1234)
                    .executeUpdate().getKey().toString());
        }catch (Exception e)
        {
            e.printStackTrace();
            throw new IllegalArgumentException("recipe relation not created");
        }

        // Assert
        System.out.println(id);
    }

*/
    /*
    @Test
    public void testDeleteInvalidRecipeRelation(){
        // Arrange
        Sql2o sql2o = new Sql2o(DB_URL,DB_USER,DB_PASS);
        String sql =
                "DELETE FROM UserRecipes " +
                    "WHERE userId = :userId " +
                    "AND recipeId = :recipeId";
        // Act
            try{
                Connection con = sql2o.open();
                con.createQuery(sql)
                        .addParameter("userId","asdfg")
                        .addParameter("recipeId",1234)
                        .executeUpdate();
            }catch (Exception e)
            {
                e.printStackTrace();
                throw new IllegalArgumentException("user does not own the recipe with id " + 122);
            }

        // Assert
    }
/*
*/
}
