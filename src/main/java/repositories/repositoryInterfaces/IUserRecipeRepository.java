package repositories.repositoryInterfaces;

import models.*;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Kaempe on 20-03-2017.
 */
public interface IUserRecipeRepository extends IRepository<Recipe>
{
    Collection<Recipe> getAll(Map<String,String> search);
    Collection<Recipe> getAll(String userId);
    Collection<Recipe> getPublishers(String publisherName);
    boolean buyRecipe(int recipeId, String userId);
    boolean removeRecipe(int recipeId, String userId);
    boolean isRecipeOwned(int recipeId, String userId);
    RecipeType getRecipeTypeFor(int id);
    Collection<MeasuredIngredient> getMeasuredIngredientsFor(int id);
    Ingredient getIngredientFor(int id);
    Collection<Allergy> getAllergiesFor(int id);


}
