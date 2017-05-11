package controllers;

import models.Allergy;
import models.MeasuredIngredient;
import models.Recipe;
import models.wrapper_models.Recipes;
import org.sql2o.Sql2o;
import repositories.UserRecipeRepository;
import repositories.repositoryInterfaces.IUserRecipeRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static JsonUtil.JsonUtil.json;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

/**
 * Created by Kaempe on 04-04-2017.
 */
public class RecipeController
{
    private IUserRecipeRepository recipeRepository;

    public RecipeController(Sql2o sql2o){
        recipeRepository = new UserRecipeRepository(sql2o);

        get("/recipes", (req, res) ->
        {
            Collection<Recipe> recipes = recipeRepository.getAll();
            if (recipes.size() != 0){
                res.status(200);
                return new Recipes(recipes);
            }
            res.status(200);
            return new String("No recipes found in the database");
        }, json());

        get("/recipes/user", (req, res) ->
        {
            String userId = req.attribute("userId");
            Collection<Recipe> recipes = recipeRepository.getAll(userId);
            if (recipes.size() != 0){
                res.status(200);
                return new Recipes(recipes);
            }
            res.status(200);
            return new String("No recipes found in the for the user with id " + userId);
        }, json());

        get("/recipes/:id", (req, res) ->
        {
            int id ;
            try{
                id = Integer.parseInt(req.params(":id"));

            }catch (Exception e){
                return new String("id must be an integer");
            }

            Recipe recipe = recipeRepository.get(id);
            if (recipe != null){
                res.status(200);
                return recipe;
            }
            res.status(200);
            return new String("No recipes found with id " + id);
        }, json());

        get("/recipes/search/:name", (req, res) ->
        {
            Map<String, String> search = new HashMap<>();
            String nameSearch = null;
            try{
                nameSearch = req.params(":name");

            }catch (Exception e){
                return new String("Invalid search parameter");
            }

            Collection<Recipe> recipes = recipeRepository.getAll(search);
            if (recipes.size() != 0){
                res.status(200);
                return new Recipes(recipes);
            }
            res.status(200);
            return new String("No recipes found with the name " + search);
        }, json());

        get("/recipes/publisher/:name", (req, res) ->
        {
            String nameSearch = null;
            try{
                nameSearch = req.params(":name");
                if (nameSearch == null) throw new IllegalArgumentException("invalid search parameter");
            }catch (Exception e){
                return new String("Invalid search parameter");
            }

            Collection<Recipe> recipes = recipeRepository.getPublishers(nameSearch);
            if (recipes.size() != 0){
                res.status(200);
                return new Recipes(recipes);
            }
            res.status(200);
            return new String("No recipes found with the publisher " + nameSearch);
        }, json());

        get("/recipes/:id/ingredients", (req, res) ->
        {
            int id ;
            try{
                id = Integer.parseInt(req.params(":id"));

            }catch (Exception e){
                return new String("id must be an integer");
            }

            if (!recipeRepository.exists(id)) return new String("No recipe found with id " + id);

            Collection<MeasuredIngredient> ingredients = recipeRepository.getMeasuredIngredientsFor(id);
            if (ingredients != null){
                res.status(200);
                return ingredients;
            }
            res.status(200);
            return new String("No ingredients found for recipe with id " + id);
        }, json());

        get("/recipes/:id/allergies", (req, res) ->
        {
            int id ;
            try{
                id = Integer.parseInt(req.params(":id"));

            }catch (Exception e){
                return new String("id must be an integer");
            }

            if (!recipeRepository.exists(id)) return new String("No recipe found with id " + id);

            Collection<Allergy> allergies = recipeRepository.getAllergiesFor(id);
            if (allergies != null){
                res.status(200);
                return allergies;
            }
            res.status(200);
            return new String("No allergies found for recipe with id " + id);
        }, json());

        post("/recipes/buy/:id", (req, res) ->
        {
            int id ;
            try{
                id = Integer.parseInt(req.params(":id"));

            }catch (Exception e){
                return new String("id must be an integer");
            }

            String userId = req.attribute("userId");
            boolean updated = recipeRepository.buyRecipe(id,userId);

            if (updated){
                res.status(200);
                return new String("Recipe with id " + id + " has been bought by the user with id " + userId);
            }
            res.status(204);
            return new String("An error occurred and the Recipe with id " + id + " has not been bought by the user with id " + userId);
        },json());

        delete("/recipes/remove/:id", (req, res) ->
        {
            int id ;
            try{
                id = Integer.parseInt(req.params(":id"));

            }catch (Exception e){
                return new String("id must be an integer");
            }

            String userId = req.attribute("userId");
            boolean removed = recipeRepository.removeRecipe(id,userId);

            if (removed){
                res.status(200);
                return new String("Recipe with id " + id + " has been removed from the user with id " + userId);
            }
            res.status(204);
            return new String("An error occurred and the Recipe with id " + id + " has not been removed from the user with id " + userId);
        },json());
    }
}
