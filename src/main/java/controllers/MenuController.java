package controllers;

import models.Allergy;
import models.Ingredient;
import models.Menu;
import models.Recipe;
import models.wrapper_models.Allergies;
import models.wrapper_models.Ingredients;
import models.wrapper_models.Menus;
import models.wrapper_models.Recipes;
import org.sql2o.Sql2o;
import repositories.UserMenuRepository;
import repositories.repositoryInterfaces.IUserMenuRepository;

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
public class MenuController
{
    private IUserMenuRepository menuRepository;

    public MenuController(Sql2o sql2o){
        menuRepository = new UserMenuRepository(sql2o);

        get("/menus", (req, res) ->
        {
            Collection<Menu> menu = menuRepository.getAll();
            if (menu.size() != 0){
                res.status(200);
                return new Menus(menu);
            }
            res.status(200);
            return new String("No menus found in the database");
        }, json());

        get("/menus/user", (req, res) ->
        {
            String userId = req.attribute("userId");
            Collection<Menu> menus = menuRepository.getAll(userId);
            if (menus.size() != 0){
                res.status(200);
                return new Menus(menus);
            }
            res.status(200);
            return new String("No menus found in the for the user with id " + userId);
        }, json());

        get("/menus/:id", (req, res) ->
        {
            int id ;
            try{
                id = Integer.parseInt(req.params(":id"));

            }catch (Exception e){
                return new String("id must be an integer");
            }

            Menu menu = menuRepository.get(id);
            if (menu != null){
                res.status(200);
                return menu;
            }
            res.status(200);
            return new String("No menu found with id " + id);
        }, json());

        get("/menus/search/:name", (req, res) ->
        {
            Map<String, String> search = new HashMap<>();
            String nameSearch = null;
            try{
                nameSearch = req.params(":name");

            }catch (Exception e){
                return new String("Invalid search parameter");
            }

            Collection<Menu> menus = menuRepository.getAll(search);
            if (menus.size() != 0){
                res.status(200);
                return new Menus(menus);
            }
            res.status(200);
            return new String("No menus found with the name " + search);
        }, json());

        get("/menus/publisher/:name", (req, res) ->
        {
            String nameSearch = null;
            try{
                nameSearch = req.params(":name");
                if (nameSearch == null) return new String("invalid search parameter");
            }catch (Exception e){
                return new String("Invalid search parameter");
            }

            Collection<Menu> menus = menuRepository.getPublishers(nameSearch);
            if (menus.size() != 0){
                res.status(200);
                return new Menus(menus);
            }
            res.status(200);
            return new String("No menus found with the publisher " + nameSearch);
        }, json());

        get("/menus/:id/recipes", (req, res) ->
        {
            int id ;
            try{
                id = Integer.parseInt(req.params(":id"));

            }catch (Exception e){
                return new String("id must be an integer");
            }
            if (!menuRepository.exists(id)){
                return new String("No menu with id " + id);
            }

            Collection<Recipe> recipes = menuRepository.getRecipesFor(id);
            if (recipes.size() != 0){
                res.status(200);
                return new Recipes(recipes);
            }
            res.status(200);
            return new String("No recipes found for the menu with id " + id);
        }, json());

        get("/menus/:id/ingredients", (req, res) ->
        {
            int id ;
            try{
                id = Integer.parseInt(req.params(":id"));

            }catch (Exception e){
                return new String("id must be an integer");
            }
            if (!menuRepository.exists(id)){
                return new String("No menu with id " + id);
            }

            Collection<Ingredient> ingredients = menuRepository.getIngredientsFor(id);
            if (ingredients.size() != 0){
                res.status(200);
                return new Ingredients(ingredients);
            }
            res.status(200);
            return new String("No ingredients found for the menu with id " + id);
        }, json());

        get("/menus/:id/allergies", (req, res) ->
        {
            int id ;
            try{
                id = Integer.parseInt(req.params(":id"));

            }catch (Exception e){
                return new String("id must be an integer");
            }
            if (!menuRepository.exists(id)){
                return new String("No menu with id " + id);
            }

            Collection<Allergy> allergies = menuRepository.getAllergiesFor(id);
            if (allergies.size() != 0){
                res.status(200);
                return new Allergies(allergies);
            }
            res.status(200);
            return new String("No allergies found for the menu with id " + id);
        }, json());

        post("/menus/buy/:id", (req, res) ->
        {
            int id ;
            try{
                id = Integer.parseInt(req.params(":id"));

            }catch (Exception e){
                return new String("id must be an integer");
            }
            if (!menuRepository.exists(id)){
                return new String("No menu with id " + id);
            }

            String userId = req.attribute("userId");
            boolean updated = menuRepository.buyMenu(id,userId);

            if (updated){
                res.status(200);
                return new String("Menu with id " + id + " has been bought by the user with id " + userId);
            }
            res.status(204);
            return new String("An error occurred and the menu with id " + id + " has not been bought by the user with id " + userId);
        },json());

        delete("/menus/remove/:id", (req, res) ->
        {
            int id ;
            try{
                id = Integer.parseInt(req.params(":id"));

            }catch (Exception e){
                return new String("id must be an integer");
            }
            if (!menuRepository.exists(id)){
                return new String("No menu with id " + id);
            }

            String userId = req.attribute("userId");
            boolean removed = menuRepository.removeMenu(id,userId);

            if (removed){
                res.status(200);
                return new String("Menu with id " + id + " has been removed from the user with id " + userId);
            }
            res.status(204);
            return new String("An error occurred and the meun with id " + id + " has not been removed from the user with id " + userId);
        },json());
    }
}
