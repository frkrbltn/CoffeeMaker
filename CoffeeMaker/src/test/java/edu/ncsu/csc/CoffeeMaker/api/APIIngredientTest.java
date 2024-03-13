package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
class APIIngredientTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private IngredientService     service;

    @BeforeEach
    void setUp () throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        service.deleteAll();
    }

    @Test
    @Transactional
    void testGetIngredient () throws Exception {
        service.deleteAll();

        final Ingredient coffee = new Ingredient( "Coffee", 2 );

        // Create Ingredient
        final String response = mvc
                .perform( post( "/api/v1//ingredients" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( coffee ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        assertTrue( response.contains( "Coffee" ) );

        // Get Ingredient
        final String response2 = mvc.perform( get( "/api/v1/ingredients/Coffee" ) ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();

        assertTrue( response2.contains( "\"Coffee\",\"amount\":2" ) );

        mvc.perform( get( "/api/v1/ingredients/Sugar" ) ).andExpect( status().isNotFound() );

    }

}
