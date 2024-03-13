package edu.ncsu.csc.CoffeeMaker.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Inventory for the coffee maker. Inventory is tied to the database using
 * Hibernate libraries. See InventoryRepository and InventoryService for the
 * other two pieces used for database support.
 *
 * @author Kai Presler-Marshall
 */
@Entity
public class Inventory extends DomainObject {

    /** id for inventory entry */
    @Id
    @GeneratedValue
    private Long                   id;

    /**
     * one to many relationship. List of Ingredients
     */
    @JsonProperty ( "ingredients" )
    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private final List<Ingredient> ingredients;

    /**
     * Empty constructor for Hibernate
     */
    public Inventory () {
        // Intentionally empty so that Hibernate can instantiate
        // Inventory object.
        this.ingredients = new ArrayList<Ingredient>();
    }

    /**
     * Use this to create inventory with specified amts.
     *
     * @param ingredients
     *            the list of ALL ingredients
     */
    public Inventory ( final List<Ingredient> ingredients ) {
        this.ingredients = ingredients;
    }

    /**
     * Returns the ID of the entry in the DB
     *
     * @return long
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the Inventory (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets the list of all the ingredients
     *
     * @return list of all the ingredients
     */
    public List<Ingredient> getAllIngredients () {
        return ingredients;
    }

    /**
     * Searches the specific ingredient by name
     *
     * @param name
     *            specific name of the ingredient
     * @return if match found returns the Ingredient or returns null otherwise
     */
    public Ingredient searchIngredient ( final String name ) {
        for ( final Ingredient match : ingredients ) {
            // find the matching ingredient in the inventory
            if ( match.getName().equals( name ) ) {
                return match;
            }
        }
        return null; // Ingredient does not exist in inventory
    }

    /**
     * Adds a completely new ingredient and amount
     *
     * @param i
     *            the ingredient to add
     */
    public void addIngredient ( final Ingredient i ) {
        final Ingredient found = searchIngredient( i.getName() );
        if ( found == null ) {
            ingredients.add( i );
        }
        else {
            throw new IllegalArgumentException( "Ingredient " + i.getName() + " already exists" );
        }

    }

    /**
     * Updates an existing ingredient and amount
     *
     * @param i
     *            the ingredient to add
     */
    public void updateIngredient ( final Ingredient i ) {
        final Ingredient found = searchIngredient( i.getName() );
        if ( found == null ) {
            ingredients.add( i );
        }
        else {
            found.addAmount( i.getAmount() );
        }

    }

    /**
     * Removes the ingredients used to make the specified recipe. Assumes that
     * the user has checked that there are enough ingredients to make
     *
     * @param r
     *            recipe to make
     * @return true if recipe is made.
     */
    public boolean useIngredients ( final Recipe r ) {
        final List<Ingredient> l = r.getAllIngredients();
        for ( final Ingredient recipeI : l ) {
            final Ingredient inventoryI = searchIngredient( recipeI.getName() );

            if ( inventoryI == null || inventoryI.getAmount() < recipeI.getAmount() ) {
                return false; // bail if ingredient doesn't exist or there isn't
                              // enough
            }

            // update the inventory
            inventoryI.setAmount( inventoryI.getAmount() - recipeI.getAmount() );
        }

        return true; // all ingredients where found and subtracted
    }

    /**
     * Returns a string describing the current contents of the inventory.
     *
     * @return String
     */
    @Override
    public String toString () {
        final StringBuffer buf = new StringBuffer();
        for ( final Ingredient i : ingredients ) {
            buf.append( i.getName() + ": " + i.getAmount() + "  " );
        }
        buf.append( "\n" );
        return buf.toString();
    }

}
