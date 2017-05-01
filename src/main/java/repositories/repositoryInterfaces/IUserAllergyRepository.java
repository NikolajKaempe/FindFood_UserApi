package repositories.repositoryInterfaces;

import models.Allergy;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Kaempe on 23-02-2017.
 */
public interface IUserAllergyRepository extends IRepository<Allergy>
{

    Collection<Allergy> getAll(Map<String,String> search);
    Collection<Allergy> getAll(String userId);
    boolean updateRelations(String userId, Collection<Allergy> model);
    void failIfInvalidRelation(int id);
}
