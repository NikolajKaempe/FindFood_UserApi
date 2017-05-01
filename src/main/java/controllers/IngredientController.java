package controllers;

import models.Allergy;
import models.Ingredient;
import models.wrapper_models.Allergies;
import models.wrapper_models.Ingredients;
import netscape.security.ForbiddenTargetException;
import org.sql2o.Sql2o;
import repositories.UserIngredientRepository;
import repositories.repositoryInterfaces.IUserIngredientRepository;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static JsonUtil.JsonUtil.fromJson;
import static JsonUtil.JsonUtil.json;
import static JsonUtil.JsonUtil.toJson;
import static spark.Spark.*;

/**
 * Created by Kaempe on 03-04-2017.
 */
public class IngredientController
{
    private IUserIngredientRepository ingredientRepository;

    public IngredientController(Sql2o sql2o)
    {
        this.ingredientRepository = new UserIngredientRepository(sql2o);

        get("/ingredients", (req, res) ->
        {
            Collection<Ingredient> ingredients = ingredientRepository.getAll();
            if (ingredients.size() != 0){
                res.status(200);
                return new Ingredients(ingredients);
            }
            res.status(204);
            return new String("No ingredients found in the database");
        }, json());

        get("/ingredients/search/:name", (req, res) ->
        {
            Map<String, String> search = new HashMap<>();
            String nameSearch = null;
            try{
                nameSearch = req.params(":name");

            }catch (Exception e){
                return new String("Invalid search parameter");
            }
            if (nameSearch == null) return new String("Invalid parameter name found in the request");
            search.put("name", nameSearch);
            Collection<Ingredient> ingredients = ingredientRepository.getAll(search);
            if (ingredients.size() != 0){
                res.status(200);
                return new Ingredients(ingredients);
            }
            res.status(204);
            return new String("No ingredients found with the name:  " + nameSearch);
        }, json());

        get("/ingredients/dislikes", (req, res) ->
        {
            String userId = req.attribute("userId");
            Collection<Ingredient> ingredients = ingredientRepository.getAllDislikes(userId);
            if (ingredients.size() != 0){
                res.status(200);
                return new Ingredients(ingredients);
            }
            res.status(204);
            return new String("No dislikes found for the user with id " + userId);
        }, json());

        get("/ingredients/favorites", (req, res) ->
        {
            String userId = req.attribute("userId");

            Collection<Ingredient> ingredients = ingredientRepository.getAllFavorites(userId);
            if (ingredients.size() != 0){
                res.status(200);
                return new Ingredients(ingredients);
            }
            res.status(204);
            return new String("No favorite ingredients found for the user with id " + userId);
        }, json());

        get("/ingredients/:id", (req, res) -> {
            int id ;
            try{
                id = Integer.parseInt(req.params(":id"));
            }catch (Exception e)
            {
                res.status(400);
                return new String("The id must be an integer");
            }
            Ingredient ingredient = ingredientRepository.get(id);

            if (ingredient != null) {
                res.status(200);
                return ingredient;
            }
            res.status(204);
            return new String("No ingredient with id "+ id +" found");
        }, json());

        get("/ingredients/:id/allergies", (req, res) -> {
            int id ;
            try{
                id = Integer.parseInt(req.params(":id"));
            }catch (Exception e)
            {
                res.status(400);
                return new String("The id must be an integer");
            }

            Collection<Allergy> allergies = ingredientRepository.getAllergiesFor(id);

            if (allergies.size() > 0) {
                res.status(200);
                return new Allergies(allergies);
            }
            res.status(204);
            return new String("No allergies found for ingredient with id "+ id);
        }, json());

        put("/ingredients/dislikes",(req,res) -> {
            String userId = req.attribute("userId");
            Ingredients ingredients;
            try{
                ingredients = fromJson(req.body(),Ingredients.class);
            }catch (Exception e){
                return new String("Invalid request body");
            }

            boolean result = ingredientRepository.updateDislikes(userId,ingredients.getIngredients());
            if (result)
            {
                res.status(200);
                return new String("dislikes for the user with id " + userId + " updated");
            }
            res.status(400);
            return new String("Could'nt update dislikes for the user with id " + userId);
        },json());

        put("/ingredients/favorites",(req,res) -> {
            String userId = req.attribute("userId");
            Ingredients ingredients;
            try{
                ingredients = fromJson(req.body(),Ingredients.class);
            }catch (Exception e){
                return new String("Invalid request body");
            }

            boolean result = ingredientRepository.updateFavorites(userId,ingredients.getIngredients());
            if (result)
            {
                res.status(200);
                return new String("Favorite ingredients for the user with id " + userId + " updated");
            }
            res.status(400);
            return new String("Could'nt update favorite ingredients for the user with id " + userId);
        },json());

        exception(IllegalAccessException.class, (e, req, res) -> {
            res.status(401);
            res.body(toJson(e.getMessage()));
            res.type("application/json");
        });

    }
}
