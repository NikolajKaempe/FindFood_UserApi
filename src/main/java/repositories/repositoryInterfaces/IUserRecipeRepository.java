package repositories.repositoryInterfaces;

import models.Recipe;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Kaempe on 20-03-2017.
 */
public interface IUserRecipeRepository extends IRepository<Recipe>
{
    Collection<Recipe> getAll(Map<String,String> search);
    Collection<Recipe> getAll(String userId);
    boolean buyRecipe(int recipeId, String userId);
    boolean removeRecipe(int recipeId, String userId);
    boolean toggleFavoriteRecipe(int recipeId, String userId);
    boolean isRecipeOwned(int recipeId);

}
