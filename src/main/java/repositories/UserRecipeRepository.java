package repositories;

import models.Recipe;
import org.sql2o.Sql2o;
import repositories.repositoryInterfaces.IUserRecipeRepository;

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
        return null;
    }

    @Override
    public Recipe get(int id) {
        return null;
    }

    @Override
    public boolean exists(int id) {
        return false;
    }

    @Override
    public Collection<Recipe> getAll(Map<String, String> search) {
        return null;
    }

    @Override
    public Collection<Recipe> getAll(String userId) {
        return null;
    }

    @Override
    public boolean buyRecipe(int recipeId, String userId) {
        return false;
    }

    @Override
    public boolean removeRecipe(int recipeId, String userId) {
        return false;
    }

    @Override
    public boolean toggleFavoriteRecipe(int recipeId, String userId) {
        return false;
    }

    @Override
    public boolean isRecipeOwned(int recipeId) {
        return false;
    }
}
