package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
class APIInventoryTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private InventoryService      service;

    @BeforeEach
    void setUp () throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        service.deleteAll();
    }

    @Test
    @Transactional
    void testGetInventory () throws Exception {
        service.deleteAll();

        final Inventory i = service.getInventory();
        i.addIngredient( new Ingredient( "Coffee", 50 ) );
        i.addIngredient( new Ingredient( "Milk", 60 ) );
        i.addIngredient( new Ingredient( "Sugar", 70 ) );
        i.addIngredient( new Ingredient( "Chocolate", 80 ) );

        service.save( i );
        final String response = mvc.perform( get( "/api/v1/inventory" ) ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();

        assertTrue( response.contains( "\"Coffee\",\"amount\":50" ) );
        assertTrue( response.contains( "\"Milk\",\"amount\":60" ) );
        assertTrue( response.contains( "\"Sugar\",\"amount\":70" ) );
        assertTrue( response.contains( "\"Chocolate\",\"amount\":80" ) );
    }

    @Test
    @Transactional
    void testUpdateInventory () throws Exception {

        // service.deleteAll();

        assertEquals( 0, service.getInventory().getAllIngredients().size() );

        final Inventory inventory = new Inventory();

        inventory.addIngredient( new Ingredient( "Coffee", 50 ) );
        inventory.addIngredient( new Ingredient( "Milk", 50 ) );
        inventory.addIngredient( new Ingredient( "Sugar", 50 ) );
        inventory.addIngredient( new Ingredient( "Chocolate", 50 ) );

        assertEquals( 4, inventory.getAllIngredients().size() );

        mvc.perform( put( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( inventory ) ) ).andExpect( status().isOk() ).andReturn()
                .getResponse();

        final Inventory i = service.getInventory();

        assertNotNull( i.getAllIngredients() );
        assertNotEquals( 0, i.getAllIngredients().size() );
        assertEquals( inventory.toString(), i.toString() );

    }

    /**
     * updating single ingredient in inventory
     *
     * @throws Exception
     *
     * @author Het Gandhi
     */
    @Test
    @Transactional
    void testUpdateInventorySingle () throws Exception {

        // service.deleteAll();

        assertEquals( 0, service.getInventory().getAllIngredients().size() );

        final Inventory inventory = new Inventory();

        inventory.addIngredient( new Ingredient( "Coffee", 50 ) );

        assertEquals( 1, inventory.getAllIngredients().size() );

        mvc.perform( put( "/api/v1/inventory/single" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new Ingredient( "Coffee", 60 ) ) ) ).andExpect( status().isOk() )
                .andReturn().getResponse();

        final Inventory i = service.getInventory();

        assertEquals( 1, i.getAllIngredients().size() );
        assertEquals( i.searchIngredient( "Coffee" ).getName(), "Coffee" );
        assertEquals( i.searchIngredient( "Coffee" ).getAmount(), 60 );

    }

}
