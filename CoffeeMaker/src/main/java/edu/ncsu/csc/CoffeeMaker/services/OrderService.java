package edu.ncsu.csc.CoffeeMaker.services;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.models.Orders;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.models.enums.OrderStatus;
import edu.ncsu.csc.CoffeeMaker.repositories.OrderRepository;

/**
 * Service class for handling business logic related to Orders.
 */
@Component
@Transactional
public class OrderService extends Service<Orders, Long> {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    protected JpaRepository<Orders, Long> getRepository () {
        return orderRepository;
    }

    public Orders getOrderById ( final Long orderId ) {
        return findById( orderId );
    }

    public List<Orders> getAllOrders () {
        return findAll();
    }

    public List<Orders> getCompletedOrders () {
        return orderRepository.findByStatus( OrderStatus.COMPLETED );
    }

    public List<Orders> getActiveOrders () {
        return orderRepository.findByStatus( OrderStatus.PLACED );
    }

    public boolean updateOrderStatus ( final Long orderId, final OrderStatus newStatus ) {
        final Orders order = getOrderById( orderId );
        if ( order == null ) {
            return false;
        }

        final OrderStatus currentStatus = order.getStatus();

        // Validate status transitions
        switch ( newStatus ) {
            case PLACED:
                return false;

            case COMPLETED:
                if ( currentStatus != OrderStatus.PLACED ) {
                    return false;
                }
                break;

            case CANCELLED:
                break;

            case PICKED_UP:
                if ( currentStatus != OrderStatus.COMPLETED ) {
                    return false;
                }
                break;

            default:
                return false;
        }

        order.setStatus( newStatus );
        save( order );
        return true;
    }

    public List<Orders> getOrdersByCustomerId ( final Long customerId ) {
        return orderRepository.findByCustomerId( customerId );
    }

    public Orders getOrderByIdAndCustomerId ( final Long orderId, final Long customerId ) {
        return orderRepository.findByIdAndCustomerId( orderId, customerId );
    }

    /**
     * Retrieves a list of orders by their status.
     *
     * @param status
     *            The status of the orders to retrieve.
     * @return A list of orders with the given status.
     */
    public List<Orders> getOrdersByStatus ( final OrderStatus status ) {
        return orderRepository.findByStatus( status );
    }

    /**
     * Retrieves a list of orders by their status of completed and pickup.
     *
     * @param status
     *            The status of the completed and pickup to retrieve.
     * @return A list of orders with the completed and pickup status.
     */
    public List<Orders> getCompletedandPickedupOrders () {
        final List<Orders> merged = new LinkedList<Orders>();

        merged.addAll( orderRepository.findByStatus( OrderStatus.COMPLETED ) );
        merged.addAll( orderRepository.findByStatus( OrderStatus.PICKED_UP ) );
        return merged;
    }

    public Orders createOrder ( final User customer, final Recipe recipe ) {
        final Orders newOrder = new Orders();
        newOrder.setCustomer( customer );
        newOrder.setRecipe( recipe );
        newOrder.setStatus( OrderStatus.PLACED );
        newOrder.setOrderTime( LocalDateTime.now() );

        return orderRepository.save( newOrder );
    }
}
