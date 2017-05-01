package repositories;

import models.Allergy;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import repositories.repositoryInterfaces.IUserAllergyRepository;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Created by Kaempe on 03-04-2017.
 */
public class UserAllergyRepository implements IUserAllergyRepository {

    private Sql2o sql2o;

    public UserAllergyRepository(Sql2o sql2o)
    {
        this.sql2o = sql2o;
    }

    @Override
    public Collection<Allergy> getAll() {
        Collection<Allergy> allergies;
        String sql =
                "SELECT allergyId, allergyName, allergyDescription " +
                        "FROM Allergies ";
        try{
            Connection con = sql2o.open();
            allergies = con.createQuery(sql)
                    .executeAndFetch(Allergy.class);
        }catch (Exception e)
        {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return allergies;
    }

    @Override
    public Allergy get(int id) {
        if (!this.exists(id)){
            throw new IllegalArgumentException("No allergy found with id " + id);
        }
        Allergy allergy;
        String sql =
                "SELECT allergyId, allergyName, allergyDescription " +
                        "FROM Allergies " +
                        "WHERE allergyId = :id";
        try{
            Connection con = sql2o.open();
            allergy = con.createQuery(sql)
                    .addParameter("id",id)
                    .executeAndFetchFirst(Allergy.class);
        }catch (Exception e)
        {
            e.printStackTrace();
            throw new IllegalArgumentException("No Allergy with id " + id + " found");
        }
        return allergy;
    }

    @Override
    public boolean exists(int id) {
        String sql =
                "SELECT allergyId FROM Allergies " +
                        "WHERE allergyId = :id";
        try{
            Connection con = sql2o.open();
            Integer allergyId = con.createQuery(sql)
                    .addParameter("id",id)
                    .executeAndFetchFirst(Integer.class);
            if (allergyId != null) return true;
            return false;
        }catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Collection<Allergy> getAll(Map<String, String> search)
    {
        String nameToFind = search.get("name");
        if (nameToFind != null && !(nameToFind.equals(""))){
            Collection<Allergy> allergies;
            String sql =
                    "SELECT allergyId, allergyName, allergyDescription " +
                            "FROM Allergies " +
                            "WHERE allergyName LIKE :search";
            try{
                Connection con = sql2o.open();
                allergies = con.createQuery(sql)
                        .addParameter("search","%" + nameToFind + "%")
                        .executeAndFetch(Allergy.class);
            }catch (Exception e)
            {
                e.printStackTrace();
                return new ArrayList<>();
            }
            if (allergies.size() == 0) throw new IllegalArgumentException("No allergies found with the name: " + nameToFind);
            return allergies;
        }
        return this.getAll();
    }

    @Override
    public Collection<Allergy> getAll(String userId) {
        Collection<Allergy> allergies;
        String sql =
                "SELECT allergyId, allergyName, allergyDescription " +
                        "FROM Allergies WHERE allergyId IN(" +
                            "SELECT allergyId FROM UserAllergies " +
                            "WHERE userId = :id" +
                        ")";
        try{
            Connection con = sql2o.open();
            allergies = con.createQuery(sql)
                    .addParameter("id",userId)
                    .executeAndFetch(Allergy.class);
        }catch (Exception e)
        {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return allergies;
    }

    @Override
    public boolean updateRelations(String userId, Collection<Allergy> model) {
        if (model == null) throw new IllegalArgumentException("A List of allergies is required. It may be empty.");
        if (model.size() != 0){
            for(Allergy allergy : model){
                this.failIfInvalidRelation(allergy.getAllergyId());
            }
        }

        String sqlRelationsToDelete =
                "DELETE FROM UserAllergies WHERE " +
                        "userId = :id";

        String sqlRelationsToUpdate =
                "INSERT INTO UserAllergies (allergyId, userId) " +
                        "VALUES (:allergyId, :userId )";

        try{
            Connection con = sql2o.beginTransaction();
            con.createQuery(sqlRelationsToDelete)
                    .addParameter("id",userId)
                    .executeUpdate();
            model.forEach(allergy ->
                con.createQuery(sqlRelationsToUpdate)
                    .addParameter("allergyId",allergy.getAllergyId())
                    .addParameter("userId",userId)
                    .executeUpdate()
            );
            con.commit();
        }catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public void failIfInvalidRelation(int id) {
        if (id == 0) throw new IllegalArgumentException("allergyId " + id + " cannot be 0");
        if (!this.exists(id)) throw new IllegalArgumentException("allergyId " + id + " does not exists");
    }

}
