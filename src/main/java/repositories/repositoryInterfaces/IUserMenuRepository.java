package repositories.repositoryInterfaces;

import models.Menu;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Kaempe on 28-03-2017.
 */
public interface IUserMenuRepository extends IRepository<Menu>
{
    Collection<Menu> getAll(Map<String,String> search);
    Collection<Menu> getAll(String userId);
    boolean buyMenu(int menuId, String userId);
    boolean removeMenu(int menuId, String userId);
    boolean toggleFavoriteMenu(int menuId, String userId);
    boolean isMenuOwned(int menuId);
}
