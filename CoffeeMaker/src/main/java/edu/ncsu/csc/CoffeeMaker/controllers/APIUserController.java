/**
 *
 */
package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * This class has api endpoints representing users
 *
 * @author Tanvi Patel
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIUserController extends APIController {

    /**
     * UserService object, to be autowired in by Spring to allow for
     * manipulating the User model
     */
    @Autowired
    private UserService userService;

    /**
     * creates user and saves it in database
     *
     * @param user
     *            the user that needs to be added to the database
     * @return correct https status code with user object if created
     */
    @PostMapping ( BASE_PATH + "/users" )
    public ResponseEntity createUser ( @RequestBody final User user ) {
        final User db = userService.findByUsername( user.getUsername() );

        if ( null != db ) {
            return new ResponseEntity( db.getFirstName() + " " + db.getLastName() + "'s account already exists",
                    HttpStatus.CONFLICT );
        }

        try {
            userService.save( user );
            return new ResponseEntity( HttpStatus.CREATED );
        }
        catch ( final Exception e ) {
            return new ResponseEntity( "Error creating account for " + user.getUsername(), HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * gets the user from database if exists
     *
     * @param username
     *            is the username of the user
     * @return User if exist
     */
    @GetMapping ( BASE_PATH + "/users/{username}" )
    public ResponseEntity getUser ( @PathVariable final String username ) {
        final User user = userService.findByUsername( username );

        // returns not found if user does not exists
        if ( null == user ) {
            return new ResponseEntity( HttpStatus.NOT_FOUND );
        }

        // user found
        return new ResponseEntity( user, HttpStatus.OK );
    }

    /**
     * gets all the user
     *
     * @return List of all user
     */
    @GetMapping ( BASE_PATH + "/users" )
    public List<User> getAllUsers () {
        return userService.findAll();
    }
}
