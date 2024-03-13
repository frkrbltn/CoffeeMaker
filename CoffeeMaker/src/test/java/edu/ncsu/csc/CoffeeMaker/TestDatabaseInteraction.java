package edu.ncsu.csc.CoffeeMaker;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@RunWith ( SpringRunner.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )

/**
 * Test Add Recipe and edit Recipe
 *
 * @author Het Gandhi
 * @author Tanvi Patel
 */
public class TestDatabaseInteraction {

    @Autowired
    private RecipeService recipeService;

    @Test
    @Transactional
    public void testRecipes () {
        recipeService.deleteAll();

        /* We'll fill this out in a bit */
        final Recipe r = new Recipe();

        /* set fields here */
        r.setName( "Coffee Recipe" );
        r.setPrice( 5 );
        r.addIngredient( new Ingredient( "Coffee", 2 ) );
        r.addIngredient( new Ingredient( "Sugar", 1 ) );
        r.addIngredient( new Ingredient( "Milk", 1 ) );
        r.addIngredient( new Ingredient( "Chocolate", 1 ) );

        recipeService.save( r );

        final List<Recipe> dbRecipes = recipeService.findAll();

        assertEquals( 1, dbRecipes.size() );

        final Recipe dbRecipe = dbRecipes.get( 0 );

        assertEquals( r.getName(), dbRecipe.getName() );
        /* Test all of the fields! You can also us assertAll. */
        assertEquals( r.getPrice(), dbRecipe.getPrice() );
        assertEquals( r.searchIngredient( "Coffee" ).getAmount(), dbRecipe.searchIngredient( "Coffee" ).getAmount() );
        assertEquals( r.searchIngredient( "Sugar" ).getAmount(), dbRecipe.searchIngredient( "Sugar" ).getAmount() );
        assertEquals( r.searchIngredient( "Milk" ).getAmount(), dbRecipe.searchIngredient( "Milk" ).getAmount() );
        assertEquals( r.searchIngredient( "Chocolate" ).getAmount(),
                dbRecipe.searchIngredient( "Chocolate" ).getAmount() );

        /**
         * Testing Edit Recipe
         */
        final Recipe dbRecipeByName = recipeService.findByName( "Coffee Recipe" );

        assertEquals( r.searchIngredient( "Chocolate" ).getAmount(),
                dbRecipeByName.searchIngredient( "Chocolate" ).getAmount() );
        dbRecipe.setPrice( 15 );
        dbRecipe.searchIngredient( "Sugar" ).setAmount( 12 );
        recipeService.save( dbRecipe );

        assertEquals( 1, recipeService.count() );

        assertEquals( 15, (int) recipeService.findAll().get( 0 ).getPrice() );

    }
}
