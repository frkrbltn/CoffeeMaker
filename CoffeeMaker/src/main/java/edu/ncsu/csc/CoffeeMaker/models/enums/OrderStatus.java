package edu.ncsu.csc.CoffeeMaker.models.enums;

/**
 * Enumeration representing the possible states of an order.
 */
public enum OrderStatus {
    PLACED,        // Order has been placed but not yet processed
    COMPLETED,     // Order has been completed and ready for pickup
    CANCELLED,     // Order has been cancelled
    PICKED_UP      // Order has been picked up by the customer
}