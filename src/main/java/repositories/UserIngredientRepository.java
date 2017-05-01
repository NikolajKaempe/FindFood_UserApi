package repositories;

import models.Allergy;
import models.Ingredient;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import repositories.repositoryInterfaces.IUserIngredientRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Created by Kaempe on 03-04-2017.
 */
public class UserIngredientRepository implements IUserIngredientRepository {

    private Sql2o sql2o;

    public UserIngredientRepository(Sql2o sql2o) { this.sql2o = sql2o; }

    @Override
    public Collection<Ingredient> getAll() {
        Collection<Ingredient> ingredients;
        String sql =
                "SELECT * FROM Ingredients";
        try{
            Connection con = sql2o.open();
            ingredients = con.createQuery(sql)
                    .executeAndFetch(Ingredient.class);
        }catch (Exception e)
        {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return ingredients;    }

    @Override
    public Ingredient get(int id) {
        if (!this.exists(id)){
            throw new IllegalArgumentException("No ingredient found with id " + id);
        }
        Ingredient ingredient;
        String sql =
                "SELECT * FROM Ingredients " +
                        "WHERE ingredientId = :id";
        try{
            Connection con = sql2o.open();
            ingredient = con.createQuery(sql)
                    .addParameter("id",id)
                    .executeAndFetchFirst(Ingredient.class);
            ingredient.setAllergies(this.getAllergiesFor(id));
        }catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        return ingredient;    }

    @Override
    public boolean exists(int id) {
        String sql =
                "SELECT ingredientId FROM Ingredients " +
                        "WHERE ingredientId = :id";
        try{
            Connection con = sql2o.open();
            Integer allergyId = con.createQuery(sql)
                    .addParameter("id",id)
                    .executeAndFetchFirst(Integer.class);
            if (allergyId != null) return true;
            return false;
        }catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }    }

    @Override
    public Collection<Ingredient> getAll(Map<String, String> search) {
        String nameToFind = search.get("name");
        if (nameToFind != null && !(nameToFind.equals(""))){
            Collection<Ingredient> ingredients;
            String sql =
                    "SELECT * FROM Ingredients " +
                            "WHERE ingredientName LIKE :search";
            try{
                Connection con = sql2o.open();
                ingredients = con.createQuery(sql)
                        .addParameter("search","%" + nameToFind + "%")
                        .executeAndFetch(Ingredient.class);
            }catch (Exception e)
            {
                e.printStackTrace();
                return new ArrayList<>();
            }
            if (ingredients.size() == 0) throw new IllegalArgumentException("No ingredients found with the name: " + nameToFind);
            return ingredients;
        }
        return this.getAll();
    }

    @Override
    public Collection<Ingredient> getAllDislikes(String userId) {
        Collection<Ingredient> ingredients;
        String sql =
                "SELECT * " +
                        "FROM Ingredients WHERE ingredientId IN(" +
                        "SELECT ingredientId FROM UserDislikedIngredients " +
                        "WHERE userId = :id" +
                        ")";
        try{
            Connection con = sql2o.open();
            ingredients = con.createQuery(sql)
                    .addParameter("id",userId)
                    .executeAndFetch(Ingredient.class);
        }catch (Exception e)
        {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return ingredients;
    }

    @Override
    public Collection<Ingredient> getAllFavorites(String userId) {
        Collection<Ingredient> ingredients;
        String sql =
                "SELECT * " +
                        "FROM Ingredients WHERE ingredientId IN(" +
                        "SELECT ingredientId FROM UserFavoritedIngredients " +
                        "WHERE userId = :id" +
                        ")";
        try{
            Connection con = sql2o.open();
            ingredients = con.createQuery(sql)
                    .addParameter("id",userId)
                    .executeAndFetch(Ingredient.class);
        }catch (Exception e)
        {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return ingredients;
    }

    @Override
    public boolean updateDislikes(String userId, Collection<Ingredient> model) {
        if (model == null) throw new IllegalArgumentException("A List of ingredients is required. It may be empty.");
        if (model.size() != 0){
            for(Ingredient ingredient : model){
                this.failIfInvalidRelation(ingredient.getIngredientId());
            }
        }

        String sqlRelationsToDelete =
                "DELETE FROM UserDislikedIngredients WHERE " +
                        "userId = :id";

        String sqlRelationsToUpdate =
                "INSERT INTO UserDislikedIngredients (ingredientId, userId) " +
                        "VALUES (:ingredientId, :userId )";

        try{
            Connection con = sql2o.beginTransaction();
            con.createQuery(sqlRelationsToDelete)
                    .addParameter("id",userId)
                    .executeUpdate();
            model.forEach(ingredient ->
                    con.createQuery(sqlRelationsToUpdate)
                            .addParameter("ingredientId",ingredient.getIngredientId())
                            .addParameter("userId",userId)
                            .executeUpdate()
            );
            con.commit();
        }catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public boolean updateFavorites(String userId, Collection<Ingredient> model) {
        if (model == null) throw new IllegalArgumentException("A List of ingredients is required. It may be empty.");
        if (model.size() != 0){
            for(Ingredient ingredient : model){
                this.failIfInvalidRelation(ingredient.getIngredientId());
            }
        }

        String sqlRelationsToDelete =
                "DELETE FROM UserFavoritedIngredients WHERE " +
                        "userId = :id";

        String sqlRelationsToUpdate =
                "INSERT INTO UserFavoritedIngredients (ingredientId, userId) " +
                        "VALUES (:ingredientId, :userId )";

        try{
            Connection con = sql2o.beginTransaction();
            con.createQuery(sqlRelationsToDelete)
                    .addParameter("id",userId)
                    .executeUpdate();
            model.forEach(ingredient ->
                    con.createQuery(sqlRelationsToUpdate)
                            .addParameter("ingredientId",ingredient.getIngredientId())
                            .addParameter("userId",userId)
                            .executeUpdate()
            );
            con.commit();
        }catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public void failIfInvalidRelation(int id) {
        if (id == 0) throw new IllegalArgumentException("ingredientId " + id + " cannot be 0");
        if (!this.exists(id)) throw new IllegalArgumentException("ingredient with id " + id + " does not exists");
    }

    @Override
    public Collection<Allergy> getAllergiesFor(int id)
    {
        Collection<Allergy> allergies;
        String sql =
                "SELECT * FROM Allergies " +
                        "WHERE allergyId IN (" +
                        "SELECT allergyId FROM IngredientAllergies " +
                        "WHERE ingredientId = :id" +
                        ")";
        try{
            Connection con = sql2o.open();
            allergies = con.createQuery(sql)
                    .addParameter("id",id)
                    .executeAndFetch(Allergy.class);
        }catch (Exception e)
        {
            throw new IllegalArgumentException("No allergies found for ingredient with id "+ id);
        }

        return allergies;
    }
}