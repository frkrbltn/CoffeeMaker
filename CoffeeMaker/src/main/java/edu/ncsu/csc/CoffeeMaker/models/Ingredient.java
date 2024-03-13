package edu.ncsu.csc.CoffeeMaker.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Min;

/**
 * Stores a single ingredient and its amount.
 *
 * @author Camden MacIver
 * @author Het Gandhi
 * @author Tanvi Patel
 */
@Entity
public class Ingredient extends DomainObject {

    /** Ingredient id */
    @Id
    @GeneratedValue
    private Long    id;

    /** Ingredient name */
    private String  name;

    /** Ingredient amount */
    @Min ( 0 )
    private Integer amount;

    /**
     * Constructor of Ingredient with name and amount parameters
     *
     * @param name
     *            name of the Ingredient
     * @param amount
     *            number of units of Ingredient
     */
    public Ingredient ( final String name, @Min ( 0 ) final Integer amount ) {
        super();
        // setName( name );
        // setAmount( amount );
        this.name = name;
        this.amount = amount;

    }

    /**
     * empty Constructor for Ingredient
     */
    public Ingredient () {

    }

    @Override
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the Recipe (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    @SuppressWarnings ( "unused" )
    private void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets the name of ingredient
     *
     * @return name of ingredient
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the name of ingredient
     *
     * @param name
     *            of ingredient
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Gets the amount of ingredient
     *
     * @return amount of ingredient
     */
    public Integer getAmount () {
        return amount;
    }

    /**
     * adds the amount of ingredient
     *
     * @param amount
     *            of ingredient to add
     * @return newly added amount of ingredient
     */
    public Integer addAmount ( final Integer amount ) {
        this.amount += amount;
        return amount;
    }

    /**
     * Sets the amount of ingredient
     *
     * @param amount
     *            of ingredient
     */
    public void setAmount ( final Integer amount ) throws IllegalArgumentException {
        if ( amount < 0 ) {
            throw new IllegalArgumentException( "Units of ingredient must be a positive integer" );
        }
        this.amount = amount;
    }

    @Override
    public String toString () {
        return "Ingredient [id=" + id + ", name=" + name + ", amount=" + amount + "]";
    }

}
