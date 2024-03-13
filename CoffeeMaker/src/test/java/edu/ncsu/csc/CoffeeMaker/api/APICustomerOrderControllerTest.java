package edu.ncsu.csc.CoffeeMaker.api;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import edu.ncsu.csc.CoffeeMaker.controllers.APICustomerOrderController;
import edu.ncsu.csc.CoffeeMaker.models.Orders;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.models.enums.OrderStatus;
import edu.ncsu.csc.CoffeeMaker.models.enums.Role;
import edu.ncsu.csc.CoffeeMaker.services.OrderService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

@ExtendWith ( SpringExtension.class )
public class APICustomerOrderControllerTest {

    private MockMvc                    mockMvc;

    @Mock
    private OrderService               orderService;

    @InjectMocks
    private APICustomerOrderController APICustomerOrderController;

    private Orders                     sampleOrder;

    @MockBean
    private UserService                userService;
    @MockBean
    private RecipeService              recipeService;

    @BeforeEach
    public void setup () {

        MockitoAnnotations.initMocks( this );

        mockMvc = MockMvcBuilders.standaloneSetup( APICustomerOrderController ).build();

        final User mockUser = new User( "Test", "User", "testUser", "password", Role.CUSTOMER );
        final Recipe mockRecipe = new Recipe();

        when( userService.findByUsername( "testUser" ) ).thenReturn( mockUser );
        when( recipeService.findByName( "Latte" ) ).thenReturn( mockRecipe );

        final Authentication auth = new UsernamePasswordAuthenticationToken( "testUser", "password" );
        final SecurityContext securityContext = Mockito.mock( SecurityContext.class );
        SecurityContextHolder.setContext( securityContext );
        when( securityContext.getAuthentication() ).thenReturn( auth );

        sampleOrder = new Orders();
        sampleOrder.setId( 1L );
        sampleOrder.setOrderTime( LocalDateTime.now() );
        sampleOrder.setCustomer( mockUser );
        sampleOrder.setRecipe( mockRecipe );
        sampleOrder.setStatus( OrderStatus.PLACED );
    }

    @Test
    public void testOrderReadyForPickup () throws Exception {
        sampleOrder.setStatus( OrderStatus.COMPLETED );
        when( orderService.getOrderByIdAndCustomerId( anyLong(), anyLong() ) ).thenReturn( sampleOrder );

        mockMvc.perform( get( "/customer/1/orders/1/pickup" ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.status", is( "COMPLETED" ) ) )
                .andExpect( jsonPath( "$.message", containsString( "Order is ready for pickup" ) ) );
    }

    @Test
    public void testOrderNotReadyForPickup () throws Exception {
        sampleOrder.setStatus( OrderStatus.PLACED );
        when( orderService.getOrderByIdAndCustomerId( anyLong(), anyLong() ) ).thenReturn( sampleOrder );

        mockMvc.perform( get( "/customer/1/orders/1/pickup" ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.status", is( "PLACED" ) ) )
                .andExpect( jsonPath( "$.message", containsString( "Order is not ready for pickup" ) ) );
    }

    @Test
    public void testOrderAlreadyPickedUp () throws Exception {
        sampleOrder.setStatus( OrderStatus.PICKED_UP );
        when( orderService.getOrderByIdAndCustomerId( anyLong(), anyLong() ) ).thenReturn( sampleOrder );

        mockMvc.perform( get( "/customer/1/orders/1/pickup" ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.status", is( "PICKED_UP" ) ) )
                .andExpect( jsonPath( "$.message", containsString( "Order has been picked up" ) ) );
    }

    @Test
    public void testOrderNotFound() throws Exception {
        when(orderService.getOrderByIdAndCustomerId(anyLong(), anyLong())).thenReturn(null);

        mockMvc.perform(get("/customer/1/orders/9999/pickup"))
               .andExpect(status().isNotFound())
               .andExpect(content().string(containsString("Order not found")));
    }

    @Test
    public void testInvalidOrderId () throws Exception {
        mockMvc.perform( get( "/customer/1/orders/invalidOrderId/pickup" ) ).andExpect( status().isBadRequest() );
    }

    @Test
    public void testOrderCancelled () throws Exception {
        sampleOrder.setStatus( OrderStatus.CANCELLED );
        when( orderService.getOrderByIdAndCustomerId( anyLong(), anyLong() ) ).thenReturn( sampleOrder );

        mockMvc.perform( get( "/customer/1/orders/1/pickup" ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.status", is( "CANCELLED" ) ) )
                .andExpect( jsonPath( "$.message", containsString( "Order has been cancelled" ) ) );
    }

    @Test
    public void testGetMultipleCustomerOrders () throws Exception {
        final List<Orders> orders = Arrays.asList( sampleOrder, new Orders() );

        when( orderService.getOrdersByCustomerId( anyLong() ) ).thenReturn( orders );

        mockMvc.perform( get( "/customer/1/orders" ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$", hasSize( 2 ) ) );

    }

    @Test
    public void testNoOrdersForCustomer() throws Exception {
        when(orderService.getOrdersByCustomerId(anyLong())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/customer/1/orders"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testInvalidCustomerIdForOrders () throws Exception {
        mockMvc.perform( get( "/customer/invalidCustomerId/orders" ) ).andExpect( status().isBadRequest() );

    }

    @Test
    public void testGetSpecificCustomerOrderSuccess () throws Exception {
        final Orders existingOrder = new Orders();
        existingOrder.setId( 1L );
        when( orderService.getOrderByIdAndCustomerId( 1L, 1L ) ).thenReturn( existingOrder );

        mockMvc.perform( get( "/customer/1/order/1" ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.id", is( 1 ) ) );

    }

    @Test
    public void testGetSpecificCustomerOrderNotFound() throws Exception {
        when(orderService.getOrderByIdAndCustomerId(1L, 99L)).thenReturn(null);

        mockMvc.perform(get("/customer/1/order/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateOrderSuccess () throws Exception {

        final User customer = new User( "Test", "User", "testUser", "password", Role.CUSTOMER );
        final Recipe recipe = new Recipe();
        recipe.setName( "Latte" );
        recipe.setPrice( 3 );

        when( userService.findByUsername( "testUser" ) ).thenReturn( customer );
        when( recipeService.findByName( "Latte" ) ).thenReturn( recipe );

        final Authentication authentication = new UsernamePasswordAuthenticationToken( "testUser", "password" );
        final SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication( authentication );
        SecurityContextHolder.setContext( securityContext );

        mockMvc.perform( post( "/customer/order/Latte" ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.customer.username", is( "testUser" ) ) )
                .andExpect( jsonPath( "$.recipe.name", is( "Latte" ) ) )
                .andExpect( jsonPath( "$.status", is( "PLACED" ) ) );

        SecurityContextHolder.clearContext();
    }

    @Test
    public void testCreateOrderInvalidUser() throws Exception {

        when(userService.findByUsername("testUser")).thenReturn(null);

        mockMvc.perform(post("/customer/order/Latte"))
               .andExpect(status().isBadRequest())
               .andExpect(content().string(containsString("Invalid customer or recipe ID")));
    }

    @Test
    public void testCreateOrderInvalidRecipe () throws Exception {

        final User customer = new User( "Test", "User", "testUser", "password", Role.CUSTOMER );
        when( userService.findByUsername( "testUser" ) ).thenReturn( customer );
        when( recipeService.findByName( "InvalidRecipe" ) ).thenReturn( null );

        mockMvc.perform( post( "/customer/order/InvalidRecipe" ) ).andExpect( status().isBadRequest() )
                .andExpect( content().string( containsString( "Invalid customer or recipe ID" ) ) );
    }

    @Test
    public void testMarkOrderAsPickedUpSuccess () throws Exception {
        final Orders orderToPickup = new Orders();
        orderToPickup.setId( 1L );
        orderToPickup.setStatus( OrderStatus.COMPLETED );
        when( orderService.getOrderById( 1L ) ).thenReturn( orderToPickup );
        when( orderService.updateOrderStatus( 1L, OrderStatus.PICKED_UP ) ).thenReturn( true );

        mockMvc.perform( put( "/customer/orders/1/pickup" ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.status", is( "success" ) ) )
                .andExpect( jsonPath( "$.message", containsString( "Order marked as picked up successfully" ) ) );
    }

}
