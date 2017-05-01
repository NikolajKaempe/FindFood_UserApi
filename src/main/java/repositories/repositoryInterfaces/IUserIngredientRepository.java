package repositories.repositoryInterfaces;

import models.Allergy;
import models.Ingredient;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Kaempe on 24-02-2017.
 */
public interface IUserIngredientRepository extends IRepository<Ingredient>
{
    Collection<Ingredient> getAll(Map<String,String> search);
    Collection<Ingredient> getAllDislikes(String userId);
    Collection<Ingredient> getAllFavorites(String userId);
    boolean updateDislikes(String userId, Collection<Ingredient> model);
    boolean updateFavorites(String userId, Collection<Ingredient> model);
    void failIfInvalidRelation(int id);
    Collection<Allergy> getAllergiesFor(int id);
}
