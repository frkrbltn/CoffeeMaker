package edu.ncsu.csc.CoffeeMaker.controllers;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Orders;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.models.enums.OrderStatus;
import edu.ncsu.csc.CoffeeMaker.services.OrderService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * Controller for handling requests related to customer orders.
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APICustomerOrderController extends APIController {

    @Autowired
    private OrderService  orderService;
    @Autowired
    private UserService   userService;
    @Autowired
    private RecipeService recipeService;

    /**
     * Fetches a specific order for a specific customer.
     *
     * @param customerId
     *            The ID of the customer.
     * @param orderId
     *            The ID of the order.
     * @return ResponseEntity containing the order or an error message.
     */
    @GetMapping ( "/customer/{customerId}/order/{orderId}" )
    public ResponseEntity< ? > getCustomerOrder ( @PathVariable final Long customerId,
            @PathVariable final Long orderId ) {
        final Orders order = orderService.getOrderByIdAndCustomerId( orderId, customerId );
        if ( order == null ) {
            return new ResponseEntity<>( errorResponse( "Order not found for the given customer" ),
                    HttpStatus.NOT_FOUND );
        }
        return new ResponseEntity<>( order, HttpStatus.OK );
    }

    /**
     * Retrieves a list of all orders placed by a specific customer.
     *
     * @param customerId
     *            The ID of the customer.
     * @return ResponseEntity containing the list of orders or an error message.
     */
    @GetMapping ( "/customer/{customerId}/orders" )
    public ResponseEntity< ? > getCustomerOrders ( @PathVariable final Long customerId ) {
        return new ResponseEntity<>( orderService.getOrdersByCustomerId( customerId ), HttpStatus.OK );
    }

    @GetMapping ( "/customerName" )
    public ResponseEntity getCustomer () {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final User current = userService.findByUsername( auth.getName() );

        return new ResponseEntity( current, HttpStatus.OK );
    }

    /**
     * Checks if a customer's order is ready for pickup.
     *
     * @param customerId
     *            The ID of the customer.
     * @param orderId
     *            The ID of the order.
     * @return ResponseEntity indicating if the order is ready for pickup or
     *         not.
     */
    @GetMapping ( "/customer/{customerId}/orders/{orderId}/pickup" )
    public ResponseEntity< ? > checkOrderStatusForPickup ( @PathVariable final Long customerId,
            @PathVariable final Long orderId ) {
        final Orders order = orderService.getOrderByIdAndCustomerId( orderId, customerId );
        if ( order == null ) {
            return ResponseEntity.status( HttpStatus.NOT_FOUND ).body( errorResponse( "Order not found" ) );
        }

        final OrderStatus status = order.getStatus();
        String message;
        switch ( status ) {
            case COMPLETED:
                message = "Order is ready for pickup";
                break;
            case PLACED:
                message = "Order is not ready for pickup";
                break;
            case CANCELLED:
                message = "Order has been cancelled";
                break;
            case PICKED_UP:
                message = "Order has been picked up";
                break;
            default:
                return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR )
                        .body( errorResponse( "Unknown order status" ) );
        }
        return ResponseEntity.ok( new OrderStatusResponse( status.toString(), message ) );
    }

    static class OrderStatusResponse {
        private final String status;
        private final String message;

        OrderStatusResponse ( final String status, final String message ) {
            this.status = status;
            this.message = message;
        }

        public String getStatus () {
            return status;
        }

        public String getMessage () {
            return message;
        }
    }

    @PostMapping ( "/customer/order/{recipeName}" )
    public ResponseEntity< ? > createOrder ( @PathVariable final String recipeName ) {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println( "\n\n\n" + auth.getName() + "\n\n\n\n" );
        final User customer = userService.findByUsername( auth.getName() );
        final Recipe recipe = recipeService.findByName( recipeName );

        if ( customer == null || recipe == null ) {
            return ResponseEntity.status( HttpStatus.BAD_REQUEST )
                    .body( errorResponse( "Invalid customer or recipe ID" ) );
        }

        final Orders newOrder = new Orders( customer, recipe, OrderStatus.PLACED, LocalDateTime.now() );
        orderService.save( newOrder );

        return ResponseEntity.ok( newOrder );
    }

    @PutMapping ( "/customer/orders/{orderId}/pickup" )
    public ResponseEntity< ? > markOrderAsPickedUp ( @PathVariable final Long orderId ) {
        final boolean updateSuccess = orderService.updateOrderStatus( orderId, OrderStatus.PICKED_UP );
        if ( !updateSuccess ) {
            return new ResponseEntity<>( errorResponse( "Failed to mark order as picked up" ), HttpStatus.BAD_REQUEST );
        }
        return new ResponseEntity<>( successResponse( "Order marked as picked up successfully" ), HttpStatus.OK );
    }
}
