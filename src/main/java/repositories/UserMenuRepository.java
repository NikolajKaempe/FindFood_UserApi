package repositories;

import models.*;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import repositories.repositoryInterfaces.IUserMenuRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Created by Kaempe on 04-04-2017.
 */
public class UserMenuRepository implements IUserMenuRepository {
    private Sql2o sql2o;

    public UserMenuRepository(Sql2o sql2o){
        this.sql2o = sql2o;
    }

    @Override
    public Collection<Menu> getAll() {
        Collection<Menu> menus;
        String sql =
                "SELECT menuId, menuName, menuDescription, menuImageFilePath, publisherName " +
                        "FROM Menus";
        try {
            Connection con = sql2o.open();
            menus = con.createQuery(sql)
                    .executeAndFetch(Menu.class);
            menus.forEach(menu -> {
                    menu.setRecipes(this.getRecipesFor(menu.getMenuId()));
                    menu.setMealType(this.getMealTypeFor(menu.getMenuId()));
            });
        }catch (Exception e)
        {
            e.printStackTrace();
            return new ArrayList<>();
        }

        return menus;
    }

    @Override
    public Menu get(int id) {
        if (!this.exists(id)){
            throw new IllegalArgumentException("no menu found with id " + id);
        }
        Menu menu;
        String sql =
                "SELECT menuId, menuName, menuDescription, menuImageFilePath, publisherName " +
                        "FROM Menus";
        try {
            Connection con = sql2o.open();
            menu = con.createQuery(sql)
                    .executeAndFetchFirst(Menu.class);
            menu.setRecipes(this.getRecipesFor(menu.getMenuId()));
        }catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        return menu;
    }

    @Override
    public boolean exists(int id) {
        String sql =
                "SELECT menuId " +
                    "FROM Menus " +
                        "WHERE menuId = :menuId";
        try{
            Connection con = sql2o.open();
            id = con.createQuery(sql)
                    .addParameter("menuId",id)
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
    public Collection<Menu> getAll(Map<String, String> search) {
        String nameToFind = search.get("name");
        if (nameToFind != null && !(nameToFind.equals(""))){
            Collection<Menu> menus;
            String sql =
                    "SELECT menuId, menuName, menuDescription, menuImageFilePath, publisherName " +
                            "FROM Menus " +
                            "WHERE menuName " +
                            "LIKE :search";
            try {
                Connection con = sql2o.open();
                menus = con.createQuery(sql)
                        .addParameter("search","%" + nameToFind + "%")
                        .executeAndFetch(Menu.class);
                menus.forEach(menu -> {
                    menu.setRecipes(this.getRecipesFor(menu.getMenuId()));
                    menu.setMealType(this.getMealTypeFor(menu.getMenuId()));
                });
            }catch (Exception e)
            {
                e.printStackTrace();
                return new ArrayList<>();
            }

            return menus;
        }
        return this.getAll();
    }

    @Override
    public Collection<Menu> getAll(String userId) {
            Collection<Menu> menus;
            String sql =
                    "SELECT menuId, menuName, menuDescription, menuImageFilePath, publisherName " +
                        "FROM Menus " +
                            "WHERE menuId IN(" +
                                "SELECT menuId " +
                                "FROM UserMenus " +
                                    "WHERE userId = :userId" +
                            ")";
            try {
                Connection con = sql2o.open();
                menus = con.createQuery(sql)
                        .addParameter("userId",userId)
                        .executeAndFetch(Menu.class);
                menus.forEach(menu -> {
                    menu.setRecipes(this.getRecipesFor(menu.getMenuId()));
                    menu.setMealType(this.getMealTypeFor(menu.getMenuId()));
                });
            }catch (Exception e)
            {
                e.printStackTrace();
                return new ArrayList<>();
            }

            return menus;
    }

    @Override
    public Collection<Menu> getPublishers(String publisherName) {
        String nameToFind = publisherName;
        if (nameToFind != null && !(nameToFind.equals(""))){
            Collection<Menu> menus;
            String sql =
                    "SELECT menuId, menuName, menuDescription, menuImageFilePath, publisherName " +
                            "FROM Menus " +
                            "WHERE publisherName " +
                            "LIKE :search";
            try {
                Connection con = sql2o.open();
                menus = con.createQuery(sql)
                        .addParameter("search","%" + nameToFind + "%")
                        .executeAndFetch(Menu.class);
                menus.forEach(menu -> {
                    menu.setRecipes(this.getRecipesFor(menu.getMenuId()));
                    menu.setMealType(this.getMealTypeFor(menu.getMenuId()));
                });
            }catch (Exception e)
            {
                e.printStackTrace();
                return new ArrayList<>();
            }

            return menus;
        }
        return this.getAll();
    }

    @Override
    public boolean buyMenu(int menuId, String userId) {
        int id;
        String sql =
                "INSERT INTO UserMenus (userId, menuId) " +
                        "VALUES (:userId, :menuId)";
        // Act
        try{
            Connection con = sql2o.open();
            id = Integer.parseInt(con.createQuery(sql)
                    .addParameter("userId",userId)
                    .addParameter("menuId",menuId)
                    .executeUpdate().getKey().toString());
            if (id == 0) return false;
        }catch (Exception e)
        {
            return false;
        }
        return true;
    }

    @Override
    public boolean removeMenu(int menuId, String userId) {
        String sql =
                "DELETE FROM UserMenus " +
                    "WHERE userId = :userId " +
                        "AND menuId = :menuId";
        // Act
        try{
            Connection con = sql2o.open();
            con.createQuery(sql)
                    .addParameter("userId",userId)
                    .addParameter("menuId",menuId)
                    .executeUpdate();
        }catch (Exception e)
        {
            return false;
        }
        return true;
    }

    public boolean isMenuOwned(int menuId, String userId) {
        Integer id;
        String sql =
                "SELECT menuId " +
                        "FROM UserMenus " +
                        "WHERE menuId = :menuId " +
                        "AND userId = :userId";
        try{
            Connection con = sql2o.open();
            id = con.createQuery(sql)
                    .addParameter("menuId",menuId)
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
    public MealType getMealTypeFor(int id) {
        MealType mealType;
        String sql =
                "SELECT mealTypeId, mealTypeName " +
                        "FROM MealTypes " +
                        "WHERE mealTypeId IN (" +
                        "SELECT mealTypeId FROM Menus " +
                        "WHERE menuId = :id" +
                        ")";
        try{
            Connection con = sql2o.open();
            mealType = con.createQuery(sql)
                    .addParameter("id",id)
                    .executeAndFetchFirst(MealType.class);
        }catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

        return mealType;
    }

    @Override
    public Collection<Recipe> getRecipesFor(int id) {
        Collection<Recipe> recipes;
        String sql =
                "SELECT recipeId, recipeName, recipeDescription, recipeImageFilePath, publisherName " +
                        "FROM Recipes " +
                        "WHERE recipeId in (" +
                        "SELECT recipeId FROM MenuRecipes " +
                        "WHERE menuId = :menuId" +
                        ")";
        try{
            Connection con = sql2o.open();
            recipes = con.createQuery(sql)
                    .addParameter("menuId",id)
                    .executeAndFetch(Recipe.class);
            recipes.forEach(recipe ->  {
                recipe.setRecipeType(this.getRecipeTypeFor(recipe.getRecipeId()));
                recipe.setMeasuredIngredients(this.getMeasuredIngredientsFor(recipe.getRecipeId()));
            });
        }catch (Exception e)
        {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return recipes;
    }

    @Override
    public Collection<Ingredient> getIngredientsFor(int id) {
        Collection<Ingredient> ingredients;
        String sql =
                "SELECT ingredientId, ingredientName, ingredientDescription " +
                        "FROM Ingredients " +
                        "WHERE ingredientId in (" +
                        "SELECT ingredientId from MenuIngredients WHERE " +
                        "menuId = :id" +
                        ")";
        try{
            Connection con = sql2o.open();
            ingredients = con.createQuery(sql)
                    .addParameter("id",id)
                    .executeAndFetch(Ingredient.class);
        }catch (Exception e)
        {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return ingredients;
    }

    @Override
    public Collection<Allergy> getAllergiesFor(int id) {
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

    @Override
    public RecipeType getRecipeTypeFor(int recipeId) {
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
                    .addParameter("id",recipeId)
                    .executeAndFetchFirst(RecipeType.class);
        }catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

        return recipeType;
    }

    @Override
    public Collection<MeasuredIngredient> getMeasuredIngredientsFor(int recipeId) {
        Collection<MeasuredIngredient> ingredients;
        String sql =
                "SELECT measuredIngredientId, amount, measure FROM MeasuredIngredients " +
                        "WHERE recipeId = :id";
        try{
            Connection con = sql2o.open();
            ingredients = con.createQuery(sql)
                    .addParameter("id",recipeId)
                    .executeAndFetch(MeasuredIngredient.class);
            ingredients.forEach(ingredient -> ingredient.setIngredient(getIngredientFor(recipeId)));
        }catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        return ingredients;
    }

    @Override
    public Ingredient getIngredientFor(int recipeId) {
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
                    .addParameter("id",recipeId)
                    .executeAndFetchFirst(Ingredient.class);
        }catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        return ingredient;
    }
}
