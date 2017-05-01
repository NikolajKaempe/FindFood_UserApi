package controllers;

import org.sql2o.Sql2o;
import repositories.UserMenuRepository;
import repositories.repositoryInterfaces.IUserMenuRepository;

/**
 * Created by Kaempe on 04-04-2017.
 */
public class MenuController
{
    private IUserMenuRepository menuRepository;

    public MenuController(Sql2o sql2o){
        menuRepository = new UserMenuRepository(sql2o);
    }
}
