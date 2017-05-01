package controllers;

import org.sql2o.Sql2o;
import repositories.UserRecipeRepository;
import repositories.repositoryInterfaces.IUserRecipeRepository;

/**
 * Created by Kaempe on 04-04-2017.
 */
public class RecipeController
{
    private IUserRecipeRepository recipeRepository;

    public RecipeController(Sql2o sql2o){
        recipeRepository = new UserRecipeRepository(sql2o);


    }
}
