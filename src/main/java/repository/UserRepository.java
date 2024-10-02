package repository;

import model.User;
import repository.IRepository;

import java.util.List;

public interface UserRepository extends IRepository<User, Integer> {
    boolean authenticate(String username, String password);
    List<User> findByUsername(String username);
}