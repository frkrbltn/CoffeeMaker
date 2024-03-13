package edu.ncsu.csc.CoffeeMaker.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import edu.ncsu.csc.CoffeeMaker.models.Ingredient;

public class IngredientTest {
	
	private Ingredient ingredient;

    @BeforeEach
    public void setUp() {
        ingredient = new Ingredient();
    }

    @Test
    public void testConstructorWithParameters() {
        Ingredient ingredient = new Ingredient("Sugar", 10);
        Assertions.assertNotNull(ingredient);
        Assertions.assertEquals("Sugar", ingredient.getName());
        Assertions.assertEquals(10, ingredient.getAmount());
    }


    @Test
    public void testDefaultConstructor() {
        Assertions.assertNotNull(ingredient);
        Assertions.assertNull(ingredient.getName());
        Assertions.assertNull(ingredient.getAmount());
    }
    
    @Test
    public void testSetName() {
        ingredient.setName("Milk");
        Assertions.assertEquals("Milk", ingredient.getName());
    }

    @Test
    public void testSetAmount() {
        ingredient.setAmount(5);
        Assertions.assertEquals(5, ingredient.getAmount());
    }
    
    @Test
    public void testGetId() {
        Assertions.assertEquals(null, ingredient.getId());
    }

}
