package edu.ncsu.csc.CoffeeMaker.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.ncsu.csc.CoffeeMaker.models.Orders;
import edu.ncsu.csc.CoffeeMaker.models.enums.OrderStatus;

/**
 * Repository for performing CRUD operations on Order entities.
 */
@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

    /**
     * Finds an order by its ID.
     *
     * @param orderId
     *            The ID of the order.
     * @return The order, or null if not found.
     */
    @Override
    Optional<Orders> findById ( Long orderId );

    /**
     * Finds all orders with a specific status.
     *
     * @param status
     *            The status of the orders to find.
     * @return A list of orders with the given status.
     */
    List<Orders> findByStatus ( OrderStatus status );

    /**
     * Finds all orders placed by a specific customer.
     *
     * @param customerId
     *            The ID of the customer.
     * @return A list of orders placed by the customer.
     */
    List<Orders> findByCustomerId ( Long customerId );

    /**
     * Finds a specific order by its ID and customer's ID.
     *
     * @param orderId
     *            The ID of the order.
     * @param customerId
     *            The ID of the customer.
     * @return The order, or null if not found.
     */
    Orders findByIdAndCustomerId ( Long orderId, Long customerId );
}
