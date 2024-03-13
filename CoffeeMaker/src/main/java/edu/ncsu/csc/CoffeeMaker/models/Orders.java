package edu.ncsu.csc.CoffeeMaker.models;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import edu.ncsu.csc.CoffeeMaker.models.enums.OrderStatus;

/**
 * This class represents an order in the coffee maker application.
 */
@Entity
public class Orders extends DomainObject {

    @Id
    @GeneratedValue
    private long          id;
    @ManyToOne
    private User          customer;
    @ManyToOne
    private Recipe        recipe;

    @Enumerated ( EnumType.STRING )
    private OrderStatus   status;

    private LocalDateTime orderTime;

    public Orders () {
        // Default constructor for JPA
    }

    public Orders ( final User customer, final Recipe recipe, final OrderStatus status,
            final LocalDateTime orderTime ) {
        this.customer = customer;
        this.recipe = recipe;
        this.status = status;
        this.orderTime = orderTime;
    }

    // Getters and Setters
    @Override
    public Serializable getId () {
        return id;
    }

    public void setId ( final Long id ) {
        this.id = id;
    }

    public User getCustomer () {
        return customer;
    }

    public void setCustomer ( final User customer ) {
        this.customer = customer;
    }

    public Recipe getRecipe () {
        return recipe;
    }

    public void setRecipe ( final Recipe recipe ) {
        this.recipe = recipe;
    }

    public OrderStatus getStatus () {
        return status;
    }

    public void setStatus ( final OrderStatus status ) {
        this.status = status;
    }

    public LocalDateTime getOrderTime () {
        return orderTime;
    }

    public void setOrderTime ( final LocalDateTime orderTime ) {
        this.orderTime = orderTime;
    }

    // Overridden methods from Object class
    @Override
    public String toString () {
        return String.format( "Order[id=%d, customer='%s', recipe='%s', status='%s', orderTime='%s']", id,
                customer.getUsername(), recipe.getName(), status, orderTime );
    }

}
