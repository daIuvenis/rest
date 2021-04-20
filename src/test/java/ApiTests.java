import com.google.gson.Gson;
import entities.Category;
import entities.Pet;
import entities.User;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.Random;


import static io.restassured.RestAssured.given;


public class ApiTests {
    String BASE_URL = "https://petstore.swagger.io/v2";

    @Test
    public void addNewPetToTheStore() {
        Category catCategory = new Category(1, "Cats");

        System.out.println("I preparing test data...");
        Pet petToAdd = Pet.builder()
                .id(new Random().nextInt(3))
                .category(catCategory)
                .name(RandomForTest.RANDOM_NAME)
                .photoUrls(Collections.singletonList("urls"))
                .tags(null)
                .status("sold")
                .build();
        System.out.println("Body to send: " + new Gson().toJson(petToAdd));
        Response addingPetResponse = given()
                .baseUri(BASE_URL)
                .basePath("/pet")
                .contentType(ContentType.JSON)
                .body(petToAdd)
                .when()
                .post();

        System.out.println("Response: " + addingPetResponse.asString());

        Pet addedPetResponse = addingPetResponse.as(Pet.class);

        Assert.assertEquals("Wrong name:", RandomForTest.RANDOM_NAME,addedPetResponse.getName());

    }
    @Test
    public void addUser() {

        System.out.println("I preparing test data...");
        User userToAdd = User.builder()
                .id(new Random().nextInt(5))
                .username(RandomForTest.randomName())
                .firstName("Andryusha")
                .lastName("Drobot")
                .email("sobaka@gmail.com")
                .password("qwerty")
                .phone("nokia 3310")
                .userStatus(new Random().nextInt(5))
                .build();
        System.out.println("Body to send: " + new Gson().toJson(userToAdd));

        Response addingUserResponse = given()
                .baseUri(BASE_URL)
                .basePath("/user")
                .contentType(ContentType.JSON)
                .body(userToAdd)
                .when()
                .post();

        System.out.println("Response: " + addingUserResponse.asString());
        Response getMail = given()
                .baseUri(BASE_URL)
                .pathParam("phone", "nokia 3310")
                .when()
                .get("/user/{phone}");
        Assert.assertEquals("status code", 200, addingUserResponse.getStatusCode());
        String getUserEmail = userToAdd.getEmail();
        Assert.assertEquals("Email", getUserEmail , userToAdd.getEmail());
    }
}
