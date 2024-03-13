package edu.ncsu.csc.CoffeeMaker.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import edu.ncsu.csc.CoffeeMaker.models.enums.Role;

/**
 * Controller class for the URL mappings for CoffeeMaker. The controller returns
 * the approprate HTML page in the /src/main/resources/templates folder. For a
 * larger application, this should be split across multiple controllers.
 *
 * @author Kai Presler-Marshall
 */
@Controller
public class MappingController {

    /**
     * On a GET request to /index, the IndexController will return
     * /src/main/resources/templates/index.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/index", "/" } )
    public String index ( final Model model ) {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if ( auth.getAuthorities().contains( new SimpleGrantedAuthority( Role.MANAGER.name() ) ) ) {
            return management( model );

        }

        if ( auth.getAuthorities().contains( new SimpleGrantedAuthority( Role.BARISTA.name() ) ) ) {
            return barista( model );

        }

        if ( auth.getAuthorities().contains( new SimpleGrantedAuthority( Role.CUSTOMER.name() ) ) ) {
            return customerPage( model );
        }

        return "/";

    }

    /**
     * On a GET request to /management, the MappingController will return
     * /src/main/resources/templates/management.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/management", "/management.html" } )
    public String management ( final Model model ) {
        return "management";
    }

    /**
     * On a GET request to /barista, the MappingController will return
     * /src/main/resources/templates/barista.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/barista", "/barista.html" } )
    public String barista ( final Model model ) {
        return "barista";
    }

    /**
     * On a GET request to /recipe, the RecipeController will return
     * /src/main/resources/templates/recipe.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/recipe", "/recipe.html" } )
    public String addRecipePage ( final Model model ) {
        return "recipe";
    }

    /**
     * On a GET request to /deleterecipe, the DeleteRecipeController will return
     * /src/main/resources/templates/deleterecipe.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/deleterecipe", "/deleterecipe.html" } )
    public String deleteRecipeForm ( final Model model ) {
        return "deleterecipe";
    }

    /**
     * On a GET request to /editrecipe, the EditRecipeController will return
     * /src/main/resources/templates/editrecipe.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/editrecipe", "/editrecipe.html" } )
    public String editRecipeForm ( final Model model ) {
        return "editrecipe";
    }

    /**
     * Handles a GET request for inventory. The GET request provides a view to
     * the client that includes the list of the current ingredients in the
     * inventory and a form where the client can enter more ingredients to add
     * to the inventory.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/inventory", "/inventory.html" } )
    public String inventoryForm ( final Model model ) {
        return "inventory";
    }

    /**
     * Handles a GET request for add ingredient. The GET request provides a view
     * to the client that lets the client add new ingredients to the inventory.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/addingredient", "/addingredient.html" } )
    public String addIngredientForm ( final Model model ) {
        return "addingredient";
    }

    /**
     * On a GET request to /makecoffee, the MakeCoffeeController will return
     * /src/main/resources/templates/makecoffee.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/makecoffee", "/makecoffee.html" } )
    public String makeCoffeeForm ( final Model model ) {
        return "makecoffee";
    }

    /**
     * On a GET request to /testpage, the MakeCoffeeController will return
     * /src/main/resources/templates/testpage.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/testpage", "/testpage.html" } )
    public String testPageForm ( final Model model ) {
        return "testpage";
    }

    /**
     * On a GET request to /logIn, the MakeCoffeeController will return
     * /src/main/resources/templates/LogIn.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/logIn", "/logIn.html" } )
    public String logInForm ( final Model model ) {
        return "logIn";
    }

    /**
     * On a GET request to /signUp, the MakeCoffeeController will return
     * /src/main/resources/templates/signUp.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/signUp", "/singUp.html" } )
    public String signUpForm ( final Model model ) {
        return "signUp";
    }

    /**
     * On a GET request to /customer, the MappingController will return
     * /src/main/resources/templates/customer.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/customer", "/customer.html" } )
    public String customerPage ( final Model model ) {
        return "customer";
    }

    /**
     * On a GET request to /fulfill, the MappingController will return
     * /src/main/resources/templates/fulfill.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/fulfill", "/fulfill.html" } )
    public String fulfillPage ( final Model model ) {
        return "fulfill";
    }

    /**
     * On a GET request to /orderHistory, the MappingController will return
     * /src/main/resources/templates/orderHistory.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/orderHistory", "/orderHistory.html" } )
    public String orderHistoryPage ( final Model model ) {
        return "orderHistory";
    }

}
