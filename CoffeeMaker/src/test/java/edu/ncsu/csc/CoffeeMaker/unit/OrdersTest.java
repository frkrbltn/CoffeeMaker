package edu.ncsu.csc.CoffeeMaker.unit;

import java.util.List;

import javax.validation.ConstraintViolationException;

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
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Orders;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.OrderService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;
import edu.ncsu.csc.CoffeeMaker.services.UserService;
import edu.ncsu.csc.CoffeeMaker.models.enums.OrderStatus;
import edu.ncsu.csc.CoffeeMaker.models.enums.Role;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class OrdersTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private RecipeService recipeService;
    
    

    @BeforeEach
    public void setup() {
        orderService.deleteAll();
        userService.deleteAll();
        recipeService.deleteAll();
    }

    // TestCreateOrder
    @Test
    @Transactional
    public void testGetId () {

        final User user = new User( "test", "test", "test", "test", Role.CUSTOMER );
        userService.save( user );

        final Recipe r1 = new Recipe();
        r1.setName( "Black Coffee" );
        r1.setPrice( 1 );

        r1.addIngredient( new Ingredient( "Coffee", 1 ) );

        recipeService.save( r1 );

        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );
        r2.addIngredient( new Ingredient( "Coffee", 1 ) );
        r2.addIngredient( new Ingredient( "Sugar", 1 ) );
        r2.addIngredient( new Ingredient( "Milk", 1 ) );
        r2.addIngredient( new Ingredient( "Chocolate", 1 ) );
        recipeService.save( r2 );

        final Orders order = new Orders( user, r1, OrderStatus.COMPLETED, null );
        orderService.save( order );

        Assertions.assertEquals( order.getId(), orderService.getOrderById( (Long) order.getId() ).getId() );
    }
    
    @Test
    @Transactional
    public void testGetCustomer () {

        final User user = new User( "test", "test", "test", "test", Role.CUSTOMER );
        userService.save( user );

        final Recipe r1 = new Recipe();
        r1.setName( "Black Coffee" );
        r1.setPrice( 1 );

        r1.addIngredient( new Ingredient( "Coffee", 1 ) );

        recipeService.save( r1 );

        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );
        r2.addIngredient( new Ingredient( "Coffee", 1 ) );
        r2.addIngredient( new Ingredient( "Sugar", 1 ) );
        r2.addIngredient( new Ingredient( "Milk", 1 ) );
        r2.addIngredient( new Ingredient( "Chocolate", 1 ) );
        recipeService.save( r2 );

        final Orders order = new Orders( user, r1, OrderStatus.COMPLETED, null );
        orderService.save( order );

        Assertions.assertEquals( user, order.getCustomer() );
    }
    
    @Test
    @Transactional
    public void testGetRecipe () {

        final User user = new User( "test", "test", "test", "test", Role.CUSTOMER );
        userService.save( user );

        final Recipe r1 = new Recipe();
        r1.setName( "Black Coffee" );
        r1.setPrice( 1 );

        r1.addIngredient( new Ingredient( "Coffee", 1 ) );

        recipeService.save( r1 );

        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );
        r2.addIngredient( new Ingredient( "Coffee", 1 ) );
        r2.addIngredient( new Ingredient( "Sugar", 1 ) );
        r2.addIngredient( new Ingredient( "Milk", 1 ) );
        r2.addIngredient( new Ingredient( "Chocolate", 1 ) );
        recipeService.save( r2 );

        final Orders order = new Orders( user, r1, OrderStatus.COMPLETED, null );
        orderService.save( order );

        Assertions.assertEquals( r1, order.getRecipe() );
    }
    
    @Test
    @Transactional
    public void testGetStatus () {

        final User user = new User( "test", "test", "test", "test", Role.CUSTOMER );
        userService.save( user );

        final Recipe r1 = new Recipe();
        r1.setName( "Black Coffee" );
        r1.setPrice( 1 );

        r1.addIngredient( new Ingredient( "Coffee", 1 ) );

        recipeService.save( r1 );

        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );
        r2.addIngredient( new Ingredient( "Coffee", 1 ) );
        r2.addIngredient( new Ingredient( "Sugar", 1 ) );
        r2.addIngredient( new Ingredient( "Milk", 1 ) );
        r2.addIngredient( new Ingredient( "Chocolate", 1 ) );
        recipeService.save( r2 );

        final Orders order = new Orders( user, r1, OrderStatus.COMPLETED, null );
        orderService.save( order );

        Assertions.assertEquals(  OrderStatus.COMPLETED, order.getStatus() );
    }
    
    @Test
    @Transactional
    public void testGetOrderTime () {

        final User user = new User( "test", "test", "test", "test", Role.CUSTOMER );
        userService.save( user );

        final Recipe r1 = new Recipe();
        r1.setName( "Black Coffee" );
        r1.setPrice( 1 );

        r1.addIngredient( new Ingredient( "Coffee", 1 ) );

        recipeService.save( r1 );

        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );
        r2.addIngredient( new Ingredient( "Coffee", 1 ) );
        r2.addIngredient( new Ingredient( "Sugar", 1 ) );
        r2.addIngredient( new Ingredient( "Milk", 1 ) );
        r2.addIngredient( new Ingredient( "Chocolate", 1 ) );
        recipeService.save( r2 );

        final Orders order = new Orders( user, r1, OrderStatus.COMPLETED, null );
        orderService.save( order );

        Assertions.assertEquals(  null, order.getOrderTime() );
    }

    @Test
    @Transactional
    public void testGetAllOrders () {
            
        final User user = new User( "test", "test", "test", "test", Role.CUSTOMER );
        userService.save( user );

        final Recipe r1 = new Recipe();
        r1.setName( "Black Coffee" );
        r1.setPrice( 1 );

        r1.addIngredient( new Ingredient( "Coffee", 1 ) );

        recipeService.save( r1 );

        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );
        r2.addIngredient( new Ingredient( "Coffee", 1 ) );
        r2.addIngredient( new Ingredient( "Sugar", 1 ) );
        r2.addIngredient( new Ingredient( "Milk", 1 ) );
        r2.addIngredient( new Ingredient( "Chocolate", 1 ) );
        recipeService.save( r2 );

        final Orders order1 = new Orders( user, r1, OrderStatus.COMPLETED, null );
        orderService.save( order1 );
        
        final Orders order2 = new Orders( user, r2, OrderStatus.COMPLETED, null );
        orderService.save( order2 );

        Assertions.assertEquals( 2, orderService.getAllOrders().size() );
    }

    @Test
    @Transactional
    public void testGetCompletedOrders () {
        final User user = new User( "test", "test", "test", "test", Role.CUSTOMER );
        userService.save( user );

        final Recipe r1 = new Recipe();
        r1.setName( "Black Coffee" );
        r1.setPrice( 1 );

        r1.addIngredient( new Ingredient( "Coffee", 1 ) );

        recipeService.save( r1 );

        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );
        r2.addIngredient( new Ingredient( "Coffee", 1 ) );
        r2.addIngredient( new Ingredient( "Sugar", 1 ) );
        r2.addIngredient( new Ingredient( "Milk", 1 ) );
        r2.addIngredient( new Ingredient( "Chocolate", 1 ) );
        recipeService.save( r2 );

        final Orders order1 = new Orders( user, r1, OrderStatus.COMPLETED, null );
        orderService.save( order1 );
        
        final Orders order2 = new Orders( user, r2, OrderStatus.COMPLETED, null );
        orderService.save( order2 );

        Assertions.assertEquals( 2, orderService.getCompletedOrders().size() );
    }
    



    @Test
    @Transactional
    public void testGetOrderByIdAndCustomerId () {
        final User user = new User( "test", "test", "test", "test", Role.CUSTOMER );
        userService.save( user );

        final Recipe r1 = new Recipe();
        r1.setName( "Black Coffee" );
        r1.setPrice( 1 );

        r1.addIngredient( new Ingredient( "Coffee", 1 ) );

        recipeService.save( r1 );

        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );
        r2.addIngredient( new Ingredient( "Coffee", 1 ) );
        r2.addIngredient( new Ingredient( "Sugar", 1 ) );
        r2.addIngredient( new Ingredient( "Milk", 1 ) );
        r2.addIngredient( new Ingredient( "Chocolate", 1 ) );
        recipeService.save( r2 );

        final Orders order1 = new Orders( user, r1, OrderStatus.PLACED, null );
        orderService.save( order1 );
        
        final Orders order2 = new Orders( user, r2, OrderStatus.COMPLETED, null );
        orderService.save( order2 );

        Assertions.assertEquals( order1, orderService.getOrderByIdAndCustomerId((Long) order1.getId(), (Long) user.getId()) );
    }
    
    @Test
    @Transactional
    public void testGetOrderByStatus () {
        final User user = new User( "test", "test", "test", "test", Role.CUSTOMER );
        userService.save( user );

        final Recipe r1 = new Recipe();
        r1.setName( "Black Coffee" );
        r1.setPrice( 1 );

        r1.addIngredient( new Ingredient( "Coffee", 1 ) );

        recipeService.save( r1 );

        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );
        r2.addIngredient( new Ingredient( "Coffee", 1 ) );
        r2.addIngredient( new Ingredient( "Sugar", 1 ) );
        r2.addIngredient( new Ingredient( "Milk", 1 ) );
        r2.addIngredient( new Ingredient( "Chocolate", 1 ) );
        recipeService.save( r2 );

        final Orders order1 = new Orders( user, r1, OrderStatus.PLACED, null );
        orderService.save( order1 );
        
        final Orders order2 = new Orders( user, r2, OrderStatus.COMPLETED, null );
        orderService.save( order2 );

        Assertions.assertEquals( 1, orderService.getOrdersByStatus(OrderStatus.COMPLETED).size() );
    }
    
    @Test
    @Transactional
    public void testGetCompletedandPickedupOrderss () {
        final User user = new User( "test", "test", "test", "test", Role.CUSTOMER );
        userService.save( user );

        final Recipe r1 = new Recipe();
        r1.setName( "Black Coffee" );
        r1.setPrice( 1 );

        r1.addIngredient( new Ingredient( "Coffee", 1 ) );

        recipeService.save( r1 );

        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );
        r2.addIngredient( new Ingredient( "Coffee", 1 ) );
        r2.addIngredient( new Ingredient( "Sugar", 1 ) );
        r2.addIngredient( new Ingredient( "Milk", 1 ) );
        r2.addIngredient( new Ingredient( "Chocolate", 1 ) );
        recipeService.save( r2 );

        final Orders order1 = new Orders( user, r1, OrderStatus.PLACED, null );
        orderService.save( order1 );
        
        final Orders order2 = new Orders( user, r2, OrderStatus.COMPLETED, null );
        orderService.save( order2 );

        Assertions.assertEquals( 1, orderService.getCompletedandPickedupOrders().size() );
    }
    
    @Test
    @Transactional
    public void testCreateOrder () {
        final User user = new User( "test", "test", "test", "test", Role.CUSTOMER );
        userService.save( user );

        final Recipe r1 = new Recipe();
        r1.setName( "Black Coffee" );
        r1.setPrice( 1 );

        r1.addIngredient( new Ingredient( "Coffee", 1 ) );

        recipeService.save( r1 );

        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );
        r2.addIngredient( new Ingredient( "Coffee", 1 ) );
        r2.addIngredient( new Ingredient( "Sugar", 1 ) );
        r2.addIngredient( new Ingredient( "Milk", 1 ) );
        r2.addIngredient( new Ingredient( "Chocolate", 1 ) );
        recipeService.save( r2 );

        final Orders order1 = new Orders( user, r1, OrderStatus.PLACED, null );
        orderService.save( order1 );
        
        final Orders order2 = new Orders( user, r2, OrderStatus.COMPLETED, null );
        orderService.save( order2 );

        Assertions.assertNotNull( orderService.createOrder(user, r1) );
    }
    
    @Test
    @Transactional
    public void testGetActiveOrders () {
        final User user = new User( "test", "test", "test", "test", Role.CUSTOMER );
        userService.save( user );

        final Recipe r1 = new Recipe();
        r1.setName( "Black Coffee" );
        r1.setPrice( 1 );

        r1.addIngredient( new Ingredient( "Coffee", 1 ) );

        recipeService.save( r1 );

        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );
        r2.addIngredient( new Ingredient( "Coffee", 1 ) );
        r2.addIngredient( new Ingredient( "Sugar", 1 ) );
        r2.addIngredient( new Ingredient( "Milk", 1 ) );
        r2.addIngredient( new Ingredient( "Chocolate", 1 ) );
        recipeService.save( r2 );

        final Orders order1 = new Orders( user, r1, OrderStatus.PLACED, null );
        orderService.save( order1 );
        
        final Orders order2 = new Orders( user, r2, OrderStatus.COMPLETED, null );
        orderService.save( order2 );

        Assertions.assertEquals( 1, orderService.getActiveOrders().size() );
    }
    
    @Test
    @Transactional
    public void testUpdateOrders() {
        final User user = new User("testUser", "password", "Test", "User", Role.CUSTOMER);
        userService.save(user);

        final Recipe recipe = new Recipe();
        recipe.setName("Espresso");
        recipe.setPrice(2);
        recipe.addIngredient(new Ingredient("Coffee", 2));
        recipeService.save(recipe);

        // Create an order with status PLACED
        Orders order = orderService.createOrder(user, recipe);

        // Test updating to COMPLETED (valid transition)
        boolean completedUpdateResult = orderService.updateOrderStatus((Long) order.getId(), OrderStatus.COMPLETED);
        Assertions.assertTrue(completedUpdateResult, "Should be able to update to COMPLETED from PLACED");
        Assertions.assertEquals(OrderStatus.COMPLETED, orderService.getOrderById((Long) order.getId()).getStatus(), "Order status should be COMPLETED");

        // Test updating to PICKED_UP (valid transition)
        boolean pickedUpUpdateResult = orderService.updateOrderStatus((Long) order.getId(), OrderStatus.PICKED_UP);
        Assertions.assertTrue(pickedUpUpdateResult, "Should be able to update to PICKED_UP from COMPLETED");
        Assertions.assertEquals(OrderStatus.PICKED_UP, orderService.getOrderById((Long) order.getId()).getStatus(), "Order status should be PICKED_UP");

        // Test updating to a status that is not a valid transition
        boolean invalidUpdateResult = orderService.updateOrderStatus((Long) order.getId(), OrderStatus.PLACED);
        Assertions.assertFalse(invalidUpdateResult, "Should not be able to update to PLACED from PICKED_UP");
        Assertions.assertNotEquals(OrderStatus.PLACED, orderService.getOrderById((Long) order.getId()).getStatus(), "Order status should not be PLACED");

        // Test updating an order that does not exist
        boolean nonExistentOrderResult = orderService.updateOrderStatus(9999L, OrderStatus.CANCELLED);
        Assertions.assertFalse(nonExistentOrderResult, "Should not be able to update a non-existent order");
    }
    

}
