package edu.ncsu.csc.CoffeeMaker.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Orders;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.enums.OrderStatus;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.OrderService;

/**
 * Controller for handling requests related to viewing and managing orders.
 */
@RestController
public class ApiViewOrderController extends APIController {

    @Autowired
    private OrderService     orderService;

    @Autowired
    private InventoryService inventoryService;

    /**
     * Fetches a specific order by its ID.
     *
     * @param orderId
     *            The ID of the order.
     * @return ResponseEntity containing the order or an error message.
     */
    @GetMapping ( "/orders/{orderId}" )
    public ResponseEntity< ? > getOrder ( @PathVariable final Long orderId ) {
        final Orders order = orderService.getOrderById( orderId );
        if ( order == null ) {
            return new ResponseEntity<>( errorResponse( "Order not found" ), HttpStatus.NOT_FOUND );
        }
        return new ResponseEntity<>( order, HttpStatus.OK );
    }

    /**
     * Retrieves a list of all orders.
     *
     * @return ResponseEntity containing the list of orders.
     */
    @GetMapping ( "/orders" )
    public ResponseEntity< ? > getOrders () {
        return new ResponseEntity<>( orderService.getAllOrders(), HttpStatus.OK );
    }

    /**
     * Retrieves a list of all completed orders.
     *
     * @return ResponseEntity containing the list of completed orders.
     */
    @GetMapping ( "/orders/completed" )
    public ResponseEntity< ? > getCompletedOrders () {
        return new ResponseEntity<>( orderService.getCompletedOrders(), HttpStatus.OK );
    }

    /**
     * Retrieves a list of all active (not completed) orders.
     *
     * @return ResponseEntity containing the list of active orders.
     */
    @GetMapping ( BASE_PATH + "/orders/active" )
    public ResponseEntity< ? > getActiveOrders () {
        return new ResponseEntity<>( orderService.getActiveOrders(), HttpStatus.OK );
    }

    /**
     * Retrieves a list of all completed and pickup orders.
     *
     * @return ResponseEntity containing the list of completed and pickup
     *         orders.
     */
    @GetMapping ( BASE_PATH + "/orders/CompletedandPickedup" )
    public ResponseEntity< ? > getCompletedandPickedupOrders () {

        return new ResponseEntity<>( orderService.getCompletedandPickedupOrders(), HttpStatus.OK );
    }

    /**
     * Retrieves a list of all orders that are currently placed and not yet
     * completed.
     *
     * @return ResponseEntity containing the list of orders.
     */
    @GetMapping ( "/barista/orders" )
    public ResponseEntity< ? > getBaristaOrders () {
        return new ResponseEntity<>( orderService.getOrdersByStatus( OrderStatus.PLACED ), HttpStatus.OK );
    }

    @PutMapping ( "/barista/orders/{orderId}/status" )
    public ResponseEntity< ? > updateOrderStatus ( @PathVariable final Long orderId,
            @RequestBody final OrderStatus status ) {
        if ( status != OrderStatus.COMPLETED && status != OrderStatus.CANCELLED ) {
            return new ResponseEntity<>( errorResponse( "Invalid status for order update" ), HttpStatus.BAD_REQUEST );
        }

        final boolean updateSuccess = orderService.updateOrderStatus( orderId, status );

        if ( !updateSuccess ) {
            return new ResponseEntity<>( errorResponse( "Failed to update order status" ), HttpStatus.BAD_REQUEST );
        }

        return new ResponseEntity<>( successResponse( "Order status updated successfully" ), HttpStatus.OK );
    }

    @PutMapping ( "/barista/orders/{orderId}/pickedup" )
    public ResponseEntity< ? > markOrderAsPickedUp ( @PathVariable final Long orderId ) {
        final boolean updateSuccess = orderService.updateOrderStatus( orderId, OrderStatus.PICKED_UP );
        if ( !updateSuccess ) {
            return new ResponseEntity<>( errorResponse( "Failed to mark order as picked up" ), HttpStatus.BAD_REQUEST );
        }
        return new ResponseEntity<>( successResponse( "Order marked as picked up successfully" ), HttpStatus.OK );
    }

    @PostMapping ( "barista/orders/fulfill/{orderId}" )
    public ResponseEntity< ? > fulfillOrder ( @PathVariable final Long orderId ) {
        final Orders order = orderService.findById( orderId );
        makeCoffee( order.getRecipe() );
        return new ResponseEntity<>( HttpStatus.OK );

    }

    public boolean makeCoffee ( final Recipe makeRecipe ) {
        final Inventory inventory = inventoryService.getInventory();

        if ( inventory.useIngredients( makeRecipe ) ) {
            inventoryService.save( inventory );
            return true;
        }

        return false;

    }

}