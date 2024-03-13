package edu.ncsu.csc.CoffeeMaker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.CoffeeMaker.models.User;

/**
 * UserRepository to return and send stuff to database
 *
 * @author Tanvi Patel
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * finds user by username
     *
     * @param username
     *            is the name of the user
     * @return User
     */
    User findByUsername ( String username );
}
