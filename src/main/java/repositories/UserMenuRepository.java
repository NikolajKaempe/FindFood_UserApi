package repositories;

import models.Menu;
import org.sql2o.Sql2o;
import repositories.repositoryInterfaces.IUserMenuRepository;

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
        return null;
    }

    @Override
    public Menu get(int id) {
        return null;
    }

    @Override
    public boolean exists(int id) {
        return false;
    }

    @Override
    public Collection<Menu> getAll(Map<String, String> search) {
        return null;
    }

    @Override
    public Collection<Menu> getAll(String userId) {
        return null;
    }

    @Override
    public boolean buyMenu(int recipeId, String userId) {
        return false;
    }

    @Override
    public boolean removeMenu(int recipeId, String userId) {
        return false;
    }

    @Override
    public boolean toggleFavoriteMenu(int recipeId, String userId) {
        return false;
    }

    @Override
    public boolean isMenuOwned(int menuId) {
        return false;
    }
}
