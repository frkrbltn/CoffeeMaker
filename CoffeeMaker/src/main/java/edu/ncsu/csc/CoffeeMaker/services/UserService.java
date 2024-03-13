/**
 *
 */
package edu.ncsu.csc.CoffeeMaker.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.repositories.UserRepository;

/**
 * @author Tanvi Patel
 */
@Component
@Transactional
public class UserService extends Service<User, Long> {

    /**
     * userRepository, to be autowired in by Spring and provide CRUD operations
     * on User model.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * gets the repository
     */
    @Override
    protected JpaRepository<User, Long> getRepository () {
        return userRepository;
    }

    /**
     * Find a recipe with the provided name
     *
     * @param name
     *            Name of the recipe to find
     * @return found recipe, null if none
     */
    public User findByUsername ( final String username ) {
        return userRepository.findByUsername( username );
    }

}
