package edu.ncsu.csc.CoffeeMaker.api;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import edu.ncsu.csc.CoffeeMaker.controllers.ApiViewOrderController;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Orders;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.models.enums.OrderStatus;
import edu.ncsu.csc.CoffeeMaker.models.enums.Role;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.OrderService;

@ExtendWith ( SpringExtension.class )
public class APIViewOrderControllerTest {

    private MockMvc                mockMvc;

    @Mock
    private OrderService           orderService;

    @MockBean
    private InventoryService       inventoryService;

    @InjectMocks
    private ApiViewOrderController apiViewOrderController;

    private User                   sampleUser;
    private Recipe                 sampleRecipe;
    private Orders                 sampleOrder;

    @BeforeEach
    public void setup () {
        MockitoAnnotations.initMocks( this );
        mockMvc = MockMvcBuilders.standaloneSetup( apiViewOrderController ).build();

        final Inventory mockInventory = new Inventory();
        when( inventoryService.getInventory() ).thenReturn( mockInventory );

        doNothing().when( inventoryService ).save( any( Inventory.class ) );
        sampleUser = new User( "John", "Doe", "johndoe", "password123", Role.CUSTOMER );
        sampleRecipe = new Recipe();
        sampleRecipe.setName( "Cappuccino" );
        sampleRecipe.setPrice( 5 );
        sampleOrder = new Orders( sampleUser, sampleRecipe, OrderStatus.PLACED, LocalDateTime.now() );
    }

    @Test
    public void testGetBaristaOrders () throws Exception {
        final Orders order = new Orders();
        order.setId( 1L );
        order.setCustomer( sampleUser );
        order.setRecipe( sampleRecipe );
        order.setStatus( OrderStatus.PLACED );
        order.setOrderTime( LocalDateTime.now() );

        final List<Orders> orders = Arrays.asList( order );
        when( orderService.getOrdersByStatus( OrderStatus.PLACED ) ).thenReturn( orders );

        mockMvc.perform( get( "/barista/orders" ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$", hasSize( 1 ) ) ).andExpect( jsonPath( "$[0].id", is( 1 ) ) )
                .andExpect( jsonPath( "$[0].customer.username", is( sampleUser.getUsername() ) ) )
                .andExpect( jsonPath( "$[0].recipe.name", is( sampleRecipe.getName() ) ) )
                .andExpect( jsonPath( "$[0].status", is( OrderStatus.PLACED.toString() ) ) );
    }

    @Test
    public void testUpdateOrderStatus() throws Exception {
        when(orderService.updateOrderStatus(anyLong(), any(OrderStatus.class))).thenReturn(true);

        mockMvc.perform(put("/barista/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"COMPLETED\""))
                .andExpect(status().isOk());

        mockMvc.perform(put("/barista/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"CANCELLED\""))
                .andExpect(status().isOk());

        mockMvc.perform(put("/barista/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"PICKED_UP\""))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateOrderStatusFailure() throws Exception {
        when(orderService.updateOrderStatus(anyLong(), any(OrderStatus.class))).thenReturn(false);

        mockMvc.perform(put("/barista/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"COMPLETED\""))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testInvalidOrderIdForUpdate () throws Exception {
        mockMvc.perform( put( "/barista/orders/9999/status" ).contentType( MediaType.APPLICATION_JSON )
                .content( "\"COMPLETED\"" ) ).andExpect( status().isBadRequest() );
    }

    @Test
    public void testMarkOrderAsPickedUpSuccess() throws Exception {
        when(orderService.updateOrderStatus(anyLong(), any(OrderStatus.class))).thenReturn(true);

        mockMvc.perform(put("/barista/orders/1/pickedup")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testMarkOrderAsPickedUpFailure() throws Exception {
        when(orderService.updateOrderStatus(anyLong(), any(OrderStatus.class))).thenReturn(false);

        mockMvc.perform(put("/barista/orders/1/pickedup")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateOrderStatusToCompleted() throws Exception {

        when(orderService.updateOrderStatus(1L, OrderStatus.COMPLETED)).thenReturn(true);

        mockMvc.perform(put("/barista/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"COMPLETED\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.message", containsString("Order status updated successfully")));
    }

@Test
public void testUpdateOrderStatusToCancelled() throws Exception {
    when(orderService.updateOrderStatus(anyLong(), eq(OrderStatus.CANCELLED))).thenReturn(true);

    mockMvc.perform(put("/barista/orders/{orderId}/status", 1)
            .contentType(MediaType.APPLICATION_JSON)
            .content("\"CANCELLED\""))
            .andExpect(status().isOk());
}

    @Test
    public void testUpdateOrderStatusWithInvalidStatus () throws Exception {
        mockMvc.perform( put( "/barista/orders/{orderId}/status", 1 ).contentType( MediaType.APPLICATION_JSON )
                .content( "\"PICKED_UP\"" ) ).andExpect( status().isBadRequest() );
    }

@Test
public void testUpdateNonExistentOrder() throws Exception {
    when(orderService.updateOrderStatus(anyLong(), any(OrderStatus.class))).thenReturn(false);

    mockMvc.perform(put("/barista/orders/{orderId}/status", 9999)
            .contentType(MediaType.APPLICATION_JSON)
            .content("\"COMPLETED\""))
            .andExpect(status().isBadRequest());
}

@Test
public void testMarkOrderAsPickedUp() throws Exception {
    when(orderService.updateOrderStatus(anyLong(), eq(OrderStatus.PICKED_UP))).thenReturn(true);

    mockMvc.perform(put("/barista/orders/{orderId}/pickedup", 1)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
}

@Test
public void testMarkNonExistentOrderAsPickedUp() throws Exception {
    when(orderService.updateOrderStatus(anyLong(), eq(OrderStatus.PICKED_UP))).thenReturn(false);

    mockMvc.perform(put("/barista/orders/{orderId}/pickedup", 9999)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
}

    @Test
    public void testGetOrderFound () throws Exception {
        final Orders existingOrder = new Orders( sampleUser, sampleRecipe, OrderStatus.PLACED, LocalDateTime.now() );
        existingOrder.setId( 1L );
        when( orderService.getOrderById( 1L ) ).thenReturn( existingOrder );

        mockMvc.perform( get( "/orders/1" ) ).andExpect( status().isOk() ).andExpect( jsonPath( "$.id", is( 1 ) ) )
                .andExpect( jsonPath( "$.status", is( "PLACED" ) ) )
                .andExpect( jsonPath( "$.customer.username", is( sampleUser.getUsername() ) ) )
                .andExpect( jsonPath( "$.recipe.name", is( sampleRecipe.getName() ) ) );
    }

    @Test
    public void testGetOrdersMultiple () throws Exception {
        final Orders orderOne = new Orders( sampleUser, sampleRecipe, OrderStatus.PLACED, LocalDateTime.now() );
        final Orders orderTwo = new Orders( sampleUser, sampleRecipe, OrderStatus.COMPLETED, LocalDateTime.now() );
        final List<Orders> ordersList = Arrays.asList( orderOne, orderTwo );
        when( orderService.getAllOrders() ).thenReturn( ordersList );

        mockMvc.perform( get( "/orders" ) ).andExpect( status().isOk() ).andExpect( jsonPath( "$", hasSize( 2 ) ) )
                .andExpect( jsonPath( "$[0].status", is( "PLACED" ) ) )
                .andExpect( jsonPath( "$[1].status", is( "COMPLETED" ) ) );
    }

    @Test
    public void testUpdateOrderStatusSuccess() throws Exception {
        when(orderService.updateOrderStatus(1L, OrderStatus.COMPLETED)).thenReturn(true);

        mockMvc.perform(put("/barista/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"COMPLETED\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.message", containsString("Order status updated successfully")));
    }

    @Test
    public void testGetOrderByIdFound () throws Exception {
        final Orders existingOrder = new Orders( sampleUser, sampleRecipe, OrderStatus.PLACED, LocalDateTime.now() );
        existingOrder.setId( 1L );
        when( orderService.getOrderById( 1L ) ).thenReturn( existingOrder );

        mockMvc.perform( get( "/orders/1" ) ).andExpect( status().isOk() ).andExpect( jsonPath( "$.id", is( 1 ) ) )
                .andExpect( jsonPath( "$.status", is( "PLACED" ) ) )
                .andExpect( jsonPath( "$.customer.username", is( sampleUser.getUsername() ) ) )
                .andExpect( jsonPath( "$.recipe.name", is( sampleRecipe.getName() ) ) );
    }

    @Test
    public void testGetAllOrders () throws Exception {
        final Orders orderOne = new Orders( sampleUser, sampleRecipe, OrderStatus.PLACED, LocalDateTime.now() );
        final Orders orderTwo = new Orders( sampleUser, sampleRecipe, OrderStatus.COMPLETED, LocalDateTime.now() );
        final List<Orders> ordersList = Arrays.asList( orderOne, orderTwo );
        when( orderService.getAllOrders() ).thenReturn( ordersList );

        mockMvc.perform( get( "/orders" ) ).andExpect( status().isOk() ).andExpect( jsonPath( "$", hasSize( 2 ) ) )
                .andExpect( jsonPath( "$[0].status", is( "PLACED" ) ) )
                .andExpect( jsonPath( "$[1].status", is( "COMPLETED" ) ) );
    }

    @Test
    public void testFulfillOrderFailure () throws Exception {

        final Orders foundOrder = new Orders( sampleUser, sampleRecipe, OrderStatus.PLACED, LocalDateTime.now() );
        when( orderService.findById( 1L ) ).thenReturn( foundOrder );

        mockMvc.perform( post( "/barista/orders/fulfill/1" ) ).andExpect( status().isOk() );
    }

}
