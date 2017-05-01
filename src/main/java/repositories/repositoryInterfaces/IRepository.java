package repositories.repositoryInterfaces;

import java.util.Collection;

/**
 * Created by Kaempe on 23-02-2017.
 */
public interface IRepository<T>
{
    Collection<T> getAll();
    T get(int id);
    boolean exists(int id);
}
