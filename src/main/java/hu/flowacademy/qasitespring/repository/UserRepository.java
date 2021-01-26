package hu.flowacademy.qasitespring.repository;

import hu.flowacademy.qasitespring.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
/**
 * JpaRepository<U,ID> provides us a lot of method to access to database
 * and manage the specified entity's table
 * First generic always should be the type of the @Entity class
 * The second should be the same type as the @Entity's @Id type
 */
public interface UserRepository extends JpaRepository<User, String> {
    /**
     * This will create a SQL query like this:
     * SELECT * FROM users WHERE username=?
     * Where ? is the value of username param
     * @param username
     * @return
     */
    Optional<User> findByUsername(String username);
}

