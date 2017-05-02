package repositories;

import models.*;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import repositories.repositoryInterfaces.IUserRecipeRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Created by Kaempe on 04-04-2017.
 */
public class UserRecipeRepository implements IUserRecipeRepository {
    private Sql2o sql2o;

    public UserRecipeRepository(Sql2o sql2o){
        this.sql2o = sql2o;
    }

    @Override
    public Collection<Recipe> getAll() {
        Collection<Recipe> recipes;
        String sql =
                "SELECT recipeId, recipeName, recipeDescription, recipeImageFilePath, publisherName " +
                    "FROM Recipes";
        try{
            Connection con = sql2o.open();
            recipes = con.createQuery(sql)
                    .executeAndFetch(Recipe.class);
            recipes.forEach(recipe -> recipe.setRecipeType(this.getRecipeTypeFor(recipe.getRecipeId())));
            recipes.forEach(recipe -> recipe.setMeasuredIngredients(this.getMeasuredIngredientsFor(recipe.getRecipeId())));
        }catch (Exception e)
        {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return recipes;
    }

    @Override
    public Recipe get(int id) {
        if (!this.exists(id)){
            throw new IllegalArgumentException("No recipe found with id " + id);
        }

        Recipe recipe;
        String sql =
                "SELECT recipeId, recipeName, recipeDescription, recipeImageFilePath, publisherName " +
                    "FROM Recipes WHERE recipeId = :id";
        try{
            Connection con = sql2o.open();
            recipe = con.createQuery(sql)
                    .addParameter("id",id)
                    .executeAndFetchFirst(Recipe.class);
            recipe.setRecipeType(this.getRecipeTypeFor(recipe.getRecipeId()));
            recipe.setMeasuredIngredients(this.getMeasuredIngredientsFor(recipe.getRecipeId()));
        }catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        return recipe;
    }

    @Override
    public boolean exists(int id) {
        Recipe recipe;

        String sql =
            "SELECT recipeId " +
                "FROM Recipes " +
                "WHERE recipeId = :id";
        try{
            Connection con = sql2o.open();
            recipe = con.createQuery(sql)
                    .addParameter("id",id)
                    .executeAndFetchFirst(Recipe.class);
            if (recipe != null) return true;
            return false;
        }catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Collection<Recipe> getAll(Map<String, String> search) {
        String nameToFind = search.get("name");
        if (nameToFind != null && !(nameToFind.equals(""))){
            Collection<Recipe> recipes;
            String sql =
                    "SELECT recipeId, recipeName, recipeDescription, recipeImageFilePath, publisherName " +
                            "FROM Recipes " +
                            "WHERE recipeName LIKE :search";
            try {
                Connection con = sql2o.open();
                recipes = con.createQuery(sql)
                        .addParameter("search","%" + nameToFind + "%")
                        .executeAndFetch(Recipe.class);
                recipes.forEach(recipe -> recipe.setRecipeType(this.getRecipeTypeFor(recipe.getRecipeId())));
                recipes.forEach(recipe -> recipe.setMeasuredIngredients(this.getMeasuredIngredientsFor(recipe.getRecipeId())));
            }catch (Exception e)
            {
                e.printStackTrace();
                return new ArrayList<>();
            }

            return recipes;
        }
        return this.getAll();
    }

    @Override
    public Collection<Recipe> getAll(String userId) {
        Collection<Recipe> recipes;
        String sql =
                "SELECT recipeId, recipeName, recipeDescription, recipeImageFilePath, publisherName " +
                        "FROM Recipes " +
                        "WHERE recipeId in (" +
                            "SELECT recipeId " +
                            "FROM UserRecipes " +
                                "WHERE userId = :id" +
                        ")";
        try {
            Connection con = sql2o.open();
            recipes = con.createQuery(sql)
                    .addParameter("id",userId)
                    .executeAndFetch(Recipe.class);
            recipes.forEach(recipe -> recipe.setRecipeType(this.getRecipeTypeFor(recipe.getRecipeId())));
            recipes.forEach(recipe -> recipe.setMeasuredIngredients(this.getMeasuredIngredientsFor(recipe.getRecipeId())));
        }catch (Exception e)
        {
            e.printStackTrace();
            return new ArrayList<>();
        }

        return recipes;
    }

    @Override
    public Collection<Recipe> getPublishers(String publisherName) {
        String nameToFind = publisherName;
        if (nameToFind != null && !(nameToFind.equals(""))){
            Collection<Recipe> recipes;
            String sql =
                    "SELECT recipeId, recipeName, recipeDescription, recipeImageFilePath, publisherName " +
                        "FROM Recipes " +
                            "WHERE publisherName " +
                            "LIKE :search";
            try {
                Connection con = sql2o.open();
                recipes = con.createQuery(sql)
                        .addParameter("search","%" + nameToFind + "%")
                        .executeAndFetch(Recipe.class);
                recipes.forEach(recipe -> recipe.setRecipeType(this.getRecipeTypeFor(recipe.getRecipeId())));
                recipes.forEach(recipe -> recipe.setMeasuredIngredients(this.getMeasuredIngredientsFor(recipe.getRecipeId())));
            }catch (Exception e)
            {
                e.printStackTrace();
                return new ArrayList<>();
            }

            return recipes;
        }
        return this.getAll();
    }

    @Override
    public boolean buyRecipe(int recipeId, String userId) {
        int id;
        String sql =
                "INSERT INTO UserRecipes (userId, recipeId) " +
                        "VALUES (:userId, :recipeId)";
        // Act
        try{
            Connection con = sql2o.open();
            id = Integer.parseInt(con.createQuery(sql)
                    .addParameter("userId",userId)
                    .addParameter("recipeId",recipeId)
                    .executeUpdate().getKey().toString());
            if (id == 0) return false;
        }catch (Exception e)
        {
            return false;
        }
        return true;
    }

    @Override
    public boolean removeRecipe(int recipeId, String userId) {
        String sql =
                "DELETE FROM UserRecipes " +
                    "WHERE userId = :userId " +
                        "AND recipeId = :recipeId";
        // Act
        try{
            Connection con = sql2o.open();
            con.createQuery(sql)
                    .addParameter("userId",userId)
                    .addParameter("recipeId",recipeId)
                    .executeUpdate();
        }catch (Exception e)
        {
            return false;
        }
        return true;
    }

    @Override
    public boolean isRecipeOwned(int recipeId, String userId) {
       Integer id;
        String sql =
                "SELECT recipeId " +
                    "FROM UserRecipes " +
                        "WHERE recipeId = :recipeId " +
                        "AND userId = :userId";
        try{
            Connection con = sql2o.open();
            id = con.createQuery(sql)
                    .addParameter("recipeId",recipeId)
                    .addParameter("userId",userId)
                    .executeAndFetchFirst(Integer.class);
            if (id == 0) return false;
        }catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public RecipeType getRecipeTypeFor(int id){
        RecipeType recipeType;
        String sql =
                "SELECT recipeTypeId, recipeTypeName " +
                        "FROM RecipeTypes " +
                        "WHERE recipeTypeId IN (" +
                        "SELECT recipeTypeId FROM Recipes " +
                        "WHERE recipeId = :id" +
                        ")";
        try{
            Connection con = sql2o.open();
            recipeType = con.createQuery(sql)
                    .addParameter("id",id)
                    .executeAndFetchFirst(RecipeType.class);
        }catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

        return recipeType;
    }


    @Override
    public Collection<MeasuredIngredient> getMeasuredIngredientsFor(int id){
        Collection<MeasuredIngredient> ingredients;
        String sql =
                "SELECT measuredIngredientId, amount, measure FROM MeasuredIngredients " +
                        "WHERE recipeId = :id";
        try{
            Connection con = sql2o.open();
            ingredients = con.createQuery(sql)
                    .addParameter("id",id)
                    .executeAndFetch(MeasuredIngredient.class);
            ingredients.forEach(ingredient -> ingredient.setIngredient(getIngredientFor(id)));
        }catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        return ingredients;
    }

    @Override
    public Ingredient getIngredientFor(int id){
        Ingredient ingredient;
        String sql =
                "SELECT ingredientId, ingredientName, ingredientDescription " +
                        "FROM Ingredients " +
                        "WHERE ingredientId IN (" +
                        "SELECT ingredientId FROM MeasuredIngredients " +
                        "WHERE recipeId= :id" +
                        ")";
        try{
            Connection con = sql2o.open();
            ingredient = con.createQuery(sql)
                    .addParameter("id",id)
                    .executeAndFetchFirst(Ingredient.class);
        }catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        return ingredient;
    }

    @Override
    public Collection<Allergy> getAllergiesFor(int id){
        Collection<Allergy> allergies;
        String sql =
                "SELECT allergyId, allergyName, allergyDescription " +
                        "FROM Allergies " +
                        "WHERE allergyId in (" +
                        "SELECT allergyId from RecipeAllergies WHERE " +
                        "recipeId = :id" +
                        ")";
        try{
            Connection con = sql2o.open();
            allergies = con.createQuery(sql)
                    .addParameter("id",id)
                    .executeAndFetch(Allergy.class);
        }catch (Exception e)
        {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return allergies;
    }
}
