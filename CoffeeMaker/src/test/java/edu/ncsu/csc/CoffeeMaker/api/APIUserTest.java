/**
 *
 */
package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.models.enums.Role;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * Test APIUserController
 *
 * @author Tanvi patel (tpatel7)
 * @author Het Gandhi(hmgandh2)
 *
 */

@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
/**
 * test for apiUserController
 */
class APIUserTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    /**
     * context is web context
     */
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserService           service;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        service.deleteAll();
    }

    @Test
    @Transactional
    /**
     * checks for duplicate user
     *
     * @throws Exception
     */
    public void testCreateUserDuplicate () throws Exception {

        final User user1 = new User( "Bob", "Builder", "bobbuilder", "construct", Role.CUSTOMER );
        final User user2 = new User( "Bob", "Builder", "bobbuilder", "construct", Role.CUSTOMER );

        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( user1 ) ) ).andExpect( status().is2xxSuccessful() ).andReturn()
                .getResponse().getContentAsString();

        assertEquals( 1, service.count() );
        assertEquals( user1, service.findByUsername( "bobbuilder" ) );

        final String result = mvc
                .perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( user2 ) ) )
                .andExpect( status().isConflict() ).andReturn().getResponse().getContentAsString();

        System.out.println( "-----------------------------" );
        System.out.println( result );
    }

    /**
     * creates a valid user and performs a post call
     *
     * @throws Exception
     */
    public void testCreateUser () throws Exception {
        final User user1 = new User( "Bob", "Builder", "bobbuilder", "construct", Role.CUSTOMER );

        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( user1 ) ) ).andExpect( status().isCreated() ).andReturn()
                .getResponse().getContentAsString();

        assertEquals( 1, service.count() );
        final User retrievedUser = service.findByUsername( "bobbuilder" );
        assertEquals( user1.getUsername(), retrievedUser.getUsername() );
        assertEquals( user1.getFirstName(), retrievedUser.getFirstName() );
        assertEquals( user1.getLastName(), retrievedUser.getLastName() );
    }

    @Autowired
    private UserService userService;

    /**
     * user already exists
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testCreateUserConflict () throws Exception {
        final User user = new User( "Alice", "Smith", "alice", "password123", Role.CUSTOMER );
        userService.save( user );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( user ) ) ).andExpect( status().isConflict() );
    }

    /**
     * tries to get a user that does not exist
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testGetNonExistingUser () throws Exception {
        final String username = "nonexistentuser";
        assertNull( userService.findByUsername( username ) );
        mvc.perform( get( "/api/v1/users/" + username ) ).andExpect( status().isNotFound() );
    }

    /**
     * gets all the user
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testGetAllUsers () throws Exception {
        userService.save( new User( "Alice", "Smith", "alice", "password123", Role.CUSTOMER ) );
        userService.save( new User( "Bob", "Builder", "bob", "build123", Role.BARISTA ) );
        mvc.perform( get( "/api/v1/users" ) ).andExpect( status().isOk() );
    }

    /**
     * Test Get User
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testGetUser () throws Exception {

        assertEquals( 0, service.count() );

        final User user1 = new User( "Bob", "Builder", "bobbuilder", "construct", Role.CUSTOMER );
        final User user2 = new User( "John", "Apple", "Japple", "Banana", Role.CUSTOMER );

        service.save( user1 );
        service.save( user2 );

        assertEquals( 2, service.count() );

        assertTrue( mvc.perform( get( "/api/v1/users/bobbuilder" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString().contains( "bobbuilder" ) );

        assertTrue( mvc.perform( get( "/api/v1/users/Japple" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString().contains( "Japple" ) );

        assertFalse( mvc.perform( get( "/api/v1/users/Jbanana" ) ).andDo( print() ).andExpect( status().isNotFound() )
                .andReturn().getResponse().getContentAsString().contains( "Jbanana" ) );

        assertEquals( 2, service.count() );
        assertEquals( user1, service.findByUsername( "bobbuilder" ) );

        assertEquals( 2, service.count() );
        assertEquals( user2, service.findByUsername( "Japple" ) );

    }

}
