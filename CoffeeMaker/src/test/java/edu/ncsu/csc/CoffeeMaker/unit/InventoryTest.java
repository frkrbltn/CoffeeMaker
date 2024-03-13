package edu.ncsu.csc.CoffeeMaker.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class InventoryTest {

    @Autowired
    private InventoryService inventoryService;

    @BeforeEach
    public void setup () {

        final Inventory ivt = inventoryService.getInventory();
        // inventoryService.delete( i );
        // final Inventory ivt = new Inventory();
        ivt.addIngredient( new Ingredient( "Coffee", 500 ) );
        ivt.addIngredient( new Ingredient( "Sugar", 500 ) );
        ivt.addIngredient( new Ingredient( "Milk", 500 ) );
        ivt.addIngredient( new Ingredient( "Chocolate", 500 ) );

        inventoryService.save( ivt );
    }

    @Test
    @Transactional
    public void testConsumeInventory () {
        final Inventory i = inventoryService.getInventory();

        final Recipe recipe = new Recipe();
        recipe.setName( "Delicious Not-Coffee" );

        recipe.addIngredient( new Ingredient( "Coffee", 10 ) );
        recipe.addIngredient( new Ingredient( "Milk", 20 ) );
        recipe.addIngredient( new Ingredient( "Sugar", 5 ) );
        recipe.addIngredient( new Ingredient( "Chocolate", 1 ) );

        recipe.setPrice( 5 );

        i.useIngredients( recipe );

        /*
         * Make sure that all of the inventory fields are now properly updated
         */

        Assertions.assertEquals( 480, (int) i.searchIngredient( "Milk" ).getAmount() );
        Assertions.assertEquals( 495, (int) i.searchIngredient( "Sugar" ).getAmount() );
        Assertions.assertEquals( 499, (int) i.searchIngredient( "Chocolate" ).getAmount() );
    }

    /**
     * Test Consume Inventory when it use all the units of ingredients
     *
     * @author Het Gandhi
     */
    @Test
    @Transactional
    public void testConsumeInventory1 () {
        final Inventory i = inventoryService.getInventory();

        final Recipe recipe = new Recipe();
        recipe.setName( "Delicious Not-Coffee" );
        recipe.addIngredient( new Ingredient( "Chocolate", 500 ) );
        recipe.addIngredient( new Ingredient( "Milk", 500 ) );
        recipe.addIngredient( new Ingredient( "Sugar", 500 ) );
        recipe.addIngredient( new Ingredient( "Coffee", 500 ) );

        recipe.setPrice( 20 );

        i.useIngredients( recipe );

        /*
         * Make sure that all of the inventory fields are now properly updated 0
         * (zero)
         */

        Assertions.assertEquals( 0, (int) i.searchIngredient( "Chocolate" ).getAmount() );
        Assertions.assertEquals( 0, (int) i.searchIngredient( "Milk" ).getAmount() );
        Assertions.assertEquals( 0, (int) i.searchIngredient( "Sugar" ).getAmount() );
        Assertions.assertEquals( 0, (int) i.searchIngredient( "Coffee" ).getAmount() );
    }

    /**
     * Test Consume Inventory when consuming more units than available
     *
     * @author Het Gandhi
     */
    @Test
    @Transactional
    public void testConsumeInventory2 () {
        final Inventory i = inventoryService.getInventory();

        final Recipe recipe = new Recipe();
        recipe.setName( "Delicious Not-Coffee" );
        recipe.addIngredient( new Ingredient( "Chocolate", 510 ) );
        recipe.addIngredient( new Ingredient( "Milk", 510 ) );
        recipe.addIngredient( new Ingredient( "Sugar", 510 ) );
        recipe.addIngredient( new Ingredient( "Coffee", 510 ) );

        recipe.setPrice( 15 );

        i.useIngredients( recipe );

        /*
         * Make sure that all of the inventory fields stayed the same
         */

        Assertions.assertEquals( 500, (int) i.searchIngredient( "Chocolate" ).getAmount() );
        Assertions.assertEquals( 500, (int) i.searchIngredient( "Milk" ).getAmount() );
        Assertions.assertEquals( 500, (int) i.searchIngredient( "Sugar" ).getAmount() );
        Assertions.assertEquals( 500, (int) i.searchIngredient( "Coffee" ).getAmount() );
    }

    /**
     *
     * @author Camden MacIver
     */
    @Test
    @Transactional
    public void testConsumeInventory3 () {
        final Inventory i = inventoryService.getInventory();
        Assertions.assertEquals( null, i.searchIngredient( "Pumpkin Spice" ) );
        Assertions.assertTrue( i.toString().contains( "Coffee: 500  Sugar: 500  Milk: 500  Chocolate: 500" ) );

    }

    @Test
    @Transactional
    public void testAddInventory1 () {
        final Inventory i = inventoryService.getInventory();

        i.searchIngredient( "Coffee" ).addAmount( 5 );
        i.searchIngredient( "Milk" ).addAmount( 3 );
        i.searchIngredient( "Sugar" ).addAmount( 7 );
        i.searchIngredient( "Chocolate" ).addAmount( 2 );

        /* Save and retrieve again to update with DB */
        inventoryService.save( i );

        final Inventory ivt = inventoryService.getInventory();

        Assertions.assertEquals( 505, (int) ivt.searchIngredient( "Coffee" ).getAmount(),
                "Adding to the inventory should result in correctly-updated values for coffee" );
        Assertions.assertEquals( 503, (int) ivt.searchIngredient( "Milk" ).getAmount(),
                "Adding to the inventory should result in correctly-updated values for milk" );
        Assertions.assertEquals( 507, (int) ivt.searchIngredient( "Sugar" ).getAmount(),
                "Adding to the inventory should result in correctly-updated values sugar" );
        Assertions.assertEquals( 502, (int) ivt.searchIngredient( "Chocolate" ).getAmount(),
                "Adding to the inventory should result in correctly-updated values chocolate" );

    }

    @Test
    @Transactional
    public void testAddInventory2 () {
        final Inventory ivt = inventoryService.getInventory();

        try {
            ivt.searchIngredient( "Coffee" ).addAmount( -5 );
            ivt.searchIngredient( "Sugar" ).addAmount( 3 );
            ivt.searchIngredient( "Milk" ).addAmount( 7 );
            ivt.searchIngredient( "Chocolate" ).addAmount( 2 );

        }
        catch ( final IllegalArgumentException iae ) {
            Assertions.assertEquals( 500, (int) ivt.searchIngredient( "Coffee" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- coffee" );
            Assertions.assertEquals( 500, (int) ivt.searchIngredient( "Milk" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- milk" );
            Assertions.assertEquals( 500, (int) ivt.searchIngredient( "Sugar" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- sugar" );
            Assertions.assertEquals( 500, (int) ivt.searchIngredient( "Chocolate" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- chocolate" );
        }
    }

    @Test
    @Transactional
    public void testAddInventory3 () {
        final Inventory ivt = inventoryService.getInventory();

        try {
            ivt.searchIngredient( "Coffee" ).addAmount( 5 );
            ivt.searchIngredient( "Sugar" ).addAmount( -3 );
            ivt.searchIngredient( "Milk" ).addAmount( 7 );
            ivt.searchIngredient( "Chocolate" ).addAmount( 2 );
        }
        catch ( final IllegalArgumentException iae ) {
            Assertions.assertEquals( 500, (int) ivt.searchIngredient( "Coffee" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for milk should result in no changes -- coffee" );
            Assertions.assertEquals( 500, (int) ivt.searchIngredient( "Milk" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for milk should result in no changes -- milk" );
            Assertions.assertEquals( 500, (int) ivt.searchIngredient( "Sugar" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for milk should result in no changes -- sugar" );
            Assertions.assertEquals( 500, (int) ivt.searchIngredient( "Chocoalte" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for milk should result in no changes -- chocolate" );

        }

    }

    @Test
    @Transactional
    public void testAddInventory4 () {
        final Inventory ivt = inventoryService.getInventory();

        try {
            ivt.searchIngredient( "Coffee" ).addAmount( 5 );
            ivt.searchIngredient( "Sugar" ).addAmount( 3 );
            ivt.searchIngredient( "Milk" ).addAmount( -7 );
            ivt.searchIngredient( "Chocolate" ).addAmount( 2 );
        }
        catch ( final IllegalArgumentException iae ) {
            Assertions.assertEquals( 500, (int) ivt.searchIngredient( "Coffee" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for sugar should result in no changes -- coffee" );
            Assertions.assertEquals( 500, (int) ivt.searchIngredient( "Milk" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for sugar should result in no changes -- milk" );
            Assertions.assertEquals( 500, (int) ivt.searchIngredient( "Sugar" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for sugar should result in no changes -- sugar" );
            Assertions.assertEquals( 500, (int) ivt.searchIngredient( "Chocolate" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for sugar should result in no changes -- chocolate" );

        }

    }

    @Test
    @Transactional
    public void testAddInventory5 () {
        final Inventory ivt = inventoryService.getInventory();

        try {
            ivt.searchIngredient( "Coffee" ).addAmount( 5 );
            ivt.searchIngredient( "Sugar" ).addAmount( 3 );
            ivt.searchIngredient( "Milk" ).addAmount( 7 );
            ivt.searchIngredient( "Chocolate" ).addAmount( -2 );
        }
        catch ( final IllegalArgumentException iae ) {
            Assertions.assertEquals( 500, (int) ivt.searchIngredient( "Coffee" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for chocolate should result in no changes -- coffee" );
            Assertions.assertEquals( 500, (int) ivt.searchIngredient( "Milk" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for chocolate should result in no changes -- milk" );
            Assertions.assertEquals( 500, (int) ivt.searchIngredient( "Sugar" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for chocolate should result in no changes -- sugar" );
            Assertions.assertEquals( 500, (int) ivt.searchIngredient( "Chocolate" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for chocolate should result in no changes -- chocolate" );

        }

    }

}
