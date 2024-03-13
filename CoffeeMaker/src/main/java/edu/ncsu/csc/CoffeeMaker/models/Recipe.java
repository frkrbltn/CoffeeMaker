package edu.ncsu.csc.CoffeeMaker.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Recipe for the coffee maker. Recipe is tied to the database using Hibernate
 * libraries. See RecipeRepository and RecipeService for the other two pieces
 * used for database support.
 *
 * @author Kai Presler-Marshall
 */
@Entity
public class Recipe extends DomainObject {

    /** Recipe id */
    @Id
    @GeneratedValue
    private Long             id;

    /** Recipe name */
    private String           name;

    /** Recipe price */
    @Min ( 0 )
    private Integer          price;

    /**
     * one to many relationship. List of Ingredients
     */
    @JsonProperty ( "ingredients" )
    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private List<Ingredient> ingredients;

    /**
     * Creates a default recipe for the coffee maker.
     */
    public Recipe () {
        this.name = "";
        this.ingredients = new ArrayList<Ingredient>();
    }

    /**
     * Adds Ingredient
     *
     * @param ingredient
     *            new Ingredient to add
     */
    public void addIngredient ( final Ingredient ingredient ) {
        this.ingredients.add( ingredient );
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
            // find the matching ingredient in the recipe
            if ( match.getName().equals( name ) ) {
                return match;
            }
        }
        return null; // Ingredient does not exist in recipe
    }

    /**
     * Copies all values from the parameter recipe except the name. ** THIS MAY
     * BE REDUNDANT LATER **
     *
     * @param r
     *            recipe to copy from
     */
    public void updateRecipe ( final Recipe r ) {
        ingredients = r.getAllIngredients();
        price = r.getPrice();
    }

    /**
     * Get the ID of the Recipe
     *
     * @return the ID
     */
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
     * Returns name of the recipe.
     *
     * @return Returns the name.
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the recipe name.
     *
     * @param name
     *            The name to set.
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Returns the price of the recipe.
     *
     * @return Returns the price.
     */
    public Integer getPrice () {
        return price;
    }

    /**
     * Sets the recipe price.
     *
     * @param price
     *            The price to set.
     */
    public void setPrice ( final Integer price ) {
        this.price = price;
    }

    /**
     * Returns the name of the recipe.
     *
     * @return String
     */
    @Override
    public String toString () {
        String s = this.name + " with ingredients [ ";
        for ( int i = 0; i < this.ingredients.size(); i++ ) {
            s += this.ingredients.get( i ).toString();

            if ( i != this.ingredients.size() - 1 ) {
                s += ", ";
            }
            else {
                s += "]";
            }
        }

        return s;
    }

    // @Override
    // public int hashCode () {
    // final int prime = 31;
    // Integer result = 1;
    // result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
    // return result;
    // }

    @Override
    public boolean equals ( final Object obj ) {
        if ( this == obj ) {
            return true;
        }
//        if ( obj == null ) {
//            return false;
//        }
//        if ( getClass() != obj.getClass() ) {
//            return false;
//        }
//        final Recipe other = (Recipe) obj;
//        if ( name == null ) {
//            if ( other.name != null ) {
//                return false;
//            }
//        }
//        else if ( !name.equals( other.name ) ) {
//            return false;
//        }
        return false;
    }

}
