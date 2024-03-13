package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.models.enums.Role;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class APIRecipeTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private RecipeService service;

    @MockBean
    private UserService userService;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();

        service.deleteAll();
    }

    @Test
    @Transactional
    public void ensureRecipe() throws Exception {
        service.deleteAll();

        final Recipe r = new Recipe();

        r.addIngredient(new Ingredient("Coffee", 5));
        r.addIngredient(new Ingredient("Sugar", 3));
        r.addIngredient(new Ingredient("Milk", 4));
        r.addIngredient(new Ingredient("Chocolate", 8));

        r.setPrice(10);
        r.setName("Mocha");

        mvc.perform(post("/api/v1/recipes").contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(r))).andExpect(status().isOk());

        final Recipe r1 = service.findByName("Mocha");
        Assertions.assertEquals(r1.getName(), "Mocha");
        Assertions.assertEquals(4, r1.getAllIngredients().size());
        // Assertions.assertEquals( null, );

    }

    @Test
    @Transactional
    public void testRecipeAPI() throws Exception {

        service.deleteAll();

        final Recipe r = new Recipe();
        r.setName("Delicious Not-Coffee");

        r.addIngredient(new Ingredient("Coffee", 1));
        r.addIngredient(new Ingredient("Milk", 5));
        r.addIngredient(new Ingredient("Sugar", 20));
        r.addIngredient(new Ingredient("Chocolate", 10));

        r.setPrice(5);

        mvc.perform(post("/api/v1/recipes").contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(r)));

        Assertions.assertEquals(1, (int) service.count());
        final Recipe r1 = service.findByName("Delicious Not-Coffee");
        Assertions.assertEquals(r1.getName(), "Delicious Not-Coffee");
        Assertions.assertEquals(4, r1.getAllIngredients().size());

    }

    @Test
    @Transactional
    public void testAddRecipe2() throws Exception {

        /* Tests a recipe with a duplicate name to make sure it's rejected */

        Assertions.assertEquals(0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker");
        final String name = "Coffee";
        final Recipe r1 = createRecipe(name, 50, 3, 1, 1, 0);

        service.save(r1);

        final Recipe r2 = createRecipe(name, 50, 3, 1, 1, 0);
        mvc.perform(post("/api/v1/recipes").contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(r2))).andExpect(status().is4xxClientError());

        Assertions.assertEquals(1, service.findAll().size(), "There should only one recipe in the CoffeeMaker");

        final Recipe r3 = service.findByName("Coffee");
        Assertions.assertEquals(r3.getName(), "Coffee");
        Assertions.assertEquals(4, r3.getAllIngredients().size());
    }

    @Test
    @Transactional
    public void testAddRecipe15() throws Exception {

        /* Tests to make sure that our cap of 3 recipes is enforced */

        Assertions.assertEquals(0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker");

        final Recipe r1 = createRecipe("Coffee", 50, 3, 1, 1, 0);
        service.save(r1);
        final Recipe r2 = createRecipe("Mocha", 50, 3, 1, 1, 2);
        service.save(r2);
        final Recipe r3 = createRecipe("Latte", 60, 3, 2, 2, 0);
        service.save(r3);

        Assertions.assertEquals(3, service.count(),
                "Creating three recipes should result in three recipes in the database");

        final Recipe r4 = createRecipe("Hot Chocolate", 75, 0, 2, 1, 2);

        mvc.perform(post("/api/v1/recipes").contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(r4))).andExpect(status().isInsufficientStorage());

        Assertions.assertEquals(3, service.count(), "Creating a fourth recipe should not get saved");
    }

    /**
     * Test to get a specific Recipe
     *
     * @throws Exception
     * @author Het Gandhi
     */
    @Test
    @Transactional
    public void testGetRecipe() throws Exception {

        /* Tests to make sure that get a specific Recipe works */

        Assertions.assertEquals(0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker");

        final Recipe r1 = createRecipe("Coffee", 50, 3, 1, 1, 0);
        service.save(r1);
        final Recipe r2 = createRecipe("Latte", 60, 3, 2, 2, 0);
        service.save(r2);

        Assertions.assertEquals(2, service.count(), "Creating a recipes should result in the database");

        assertTrue(mvc.perform(get("/api/v1/recipes/Coffee")).andDo(print()).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString().contains("Coffee"));

        assertTrue(mvc.perform(get("/api/v1/recipes/Latte")).andDo(print()).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString().contains("Latte"));

        final Recipe r3 = service.findByName("Coffee");
        Assertions.assertEquals(r3.getName(), "Coffee");
        Assertions.assertEquals(4, r3.getAllIngredients().size());

        final Recipe r4 = service.findByName("Latte");
        Assertions.assertEquals(r4.getName(), "Latte");
        Assertions.assertEquals(4, r4.getAllIngredients().size());

    }

    @Test
    @Transactional
    public void testDeleteRecipe() throws Exception {
        Assertions.assertEquals(0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker");

        final Recipe r1 = createRecipe("Coffee", 50, 3, 1, 1, 0);
        service.save(r1);
        final Recipe r2 = createRecipe("Mocha", 50, 3, 1, 1, 2);
        service.save(r2);

        Assertions.assertEquals(2, service.findAll().size(), "There should be two Recipes in the CoffeeMaker");

        mvc.perform(delete("/api/v1/recipes/Coffee"));
        Assertions.assertEquals(1, service.findAll().size(), "There should be one Recipes in the CoffeeMaker");

        final Recipe r3 = createRecipe("Latte", 60, 3, 2, 2, 0);
        service.save(r3);
        final Recipe r4 = createRecipe("Chocolate Milk", 60, 0, 3, 2, 5);
        service.save(r4);
        Assertions.assertEquals(3, service.findAll().size(), "There should be three Recipes in the CoffeeMaker");

        mvc.perform(delete("/api/v1/recipes/Latte"));
        Assertions.assertEquals(2, service.findAll().size(), "There should be two Recipes in the CoffeeMaker");

        mvc.perform(delete("/api/v1/recipes/Chocolate Milk"));
        Assertions.assertEquals(1, service.findAll().size(), "There should be one Recipes in the CoffeeMaker");

        mvc.perform(delete("/api/v1/recipes/Mocha"));
        Assertions.assertEquals(0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker");

    }

    @Test
    @Transactional
    private Recipe createRecipe(final String name, final Integer price, final Integer coffee, final Integer milk,
            final Integer sugar, final Integer chocolate) {
        final Recipe recipe = new Recipe();
        recipe.setName(name);
        recipe.setPrice(price);

        recipe.addIngredient(new Ingredient("Coffee", coffee));
        recipe.addIngredient(new Ingredient("Sugar", sugar));
        recipe.addIngredient(new Ingredient("Milk", milk));
        recipe.addIngredient(new Ingredient("Chocolate", chocolate));

        return recipe;
    }

    @Test
    @Transactional
    public void testGetNonExistentRecipe() throws Exception {
        final String nonExistentName = "NonExistent";
        mvc.perform(get("/api/v1/recipes/" + nonExistentName))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains("No recipe found with name " + nonExistentName)));
    }

    @Test
    @Transactional
    public void testEditNonExistentRecipe() throws Exception {
        final Recipe nonExistentRecipe = new Recipe();
        nonExistentRecipe.setName("NonExistent");
        nonExistentRecipe.setPrice(10);

        mvc.perform(MockMvcRequestBuilders.put("/api/v1/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(nonExistentRecipe)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains("No recipe found for name " + nonExistentRecipe.getName())));
    }

    @Test
    @Transactional
    public void testCreateRecipeInvalidJson() throws Exception {
        String invalidJson = "{invalidJson}";

        mvc.perform(post("/api/v1/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void testDeleteRecipeTwice() throws Exception {
        final Recipe newRecipe = new Recipe();
        newRecipe.setName("ExistingRecipe");
        newRecipe.setPrice(10);
        mvc.perform(post("/api/v1/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(newRecipe)))
                .andExpect(status().isOk());
        mvc.perform(delete("/api/v1/recipes/" + newRecipe.getName()))
                .andExpect(status().isOk());
        mvc.perform(delete("/api/v1/recipes/" + newRecipe.getName()))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void testCreateUserException() throws Exception {
        User user = new User("Test", "User", "testuser", "password", Role.CUSTOMER);
        Mockito.when(userService.findByUsername(user.getUsername())).thenReturn(null);
        Mockito.doThrow(new RuntimeException("Test Exception")).when(userService).save(Mockito.any(User.class));
        mvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(user)))
                .andExpect(status().isBadRequest());
    }

}
