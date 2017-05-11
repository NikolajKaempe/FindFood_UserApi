package repositories.repositoryInterfaces;

import models.*;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Kaempe on 28-03-2017.
 */
public interface IUserMenuRepository extends IRepository<Menu>
{
    Collection<Menu> getAll(Map<String,String> search);
    Collection<Menu> getAll(String userId);
    Collection<Menu> getPublishers(String publisherName);
    boolean buyMenu(int menuId, String userId);
    boolean removeMenu(int menuId, String userId);
    MealType getMealTypeFor(int id);
    Collection<Recipe> getRecipesFor(int id);
    Collection<Ingredient> getIngredientsFor(int id);
    Collection<Allergy> getAllergiesFor(int id);
    RecipeType getRecipeTypeFor(int recipeId);
    Collection<MeasuredIngredient> getMeasuredIngredientsFor(int recipeId   );
    Ingredient getIngredientFor(int recipeId);
}
