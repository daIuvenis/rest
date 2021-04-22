import com.google.gson.Gson;
import entities.Category;
import entities.CustomResponse;
import entities.Pet;
import entities.User;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.given;


public class ApiTests {
    String BASE_URL = "https://petstore.swagger.io/v2";

    @Test
    public void addNewPetToTheStore() {
        Category platypusCategory = new Category(1, "Platypus");

        System.out.println("Creating pet");
        Pet petToAdd = Pet.builder()
                .id(new BigInteger("12345678901234567890"))
                .category(platypusCategory)
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

        CustomResponse cr = addingPetResponse.as(CustomResponse.class);
        System.out.println("response " + addingPetResponse.asString());
        Assert.assertEquals("something bad happened", 500, addingPetResponse.getStatusCode());
        Assert.assertEquals("wops ", cr.getMessage(), "something bad happened");

    }
    @Test
    public void addPetAndDelete() throws InterruptedException {
        Category platypusCategory = new Category(1, "Platypus");
        Pet petToAdd = Pet.builder()
                .id(new BigInteger("2"))
                .category(platypusCategory)
                .name(RandomForTest.RANDOM_NAME)
                .photoUrls(Collections.singletonList("urls"))
                .tags(null)
                .status("available")
                .build();
        System.out.println("Body to send: " + new Gson().toJson(petToAdd));
        Response addNewPet = given()
                .baseUri(BASE_URL)
                .basePath("/pet")
                .contentType(ContentType.JSON)
                .body(petToAdd)
                .when()
                .post();
        System.out.println("response " + addNewPet.asString());
        BigInteger id = petToAdd.getId();
        Response gettingInfoAboutPet = given()
                .baseUri(BASE_URL)
                .pathParam("petId", id)
                .when()
                .get("/pet/{petId}");
        TimeUnit.SECONDS.sleep(5);
        Assert.assertEquals("platypus add ", 200, gettingInfoAboutPet.getStatusCode());
        Response deletePet = given()
                .baseUri(BASE_URL)
                .pathParam("petId", id)
                .when()
                .delete("/pet/{petId}");
        Assert.assertEquals("pet deleted ", 200, deletePet.getStatusCode());
        Response gettingInfoAboutDeleting = given()
                .baseUri(BASE_URL)
                .pathParam("petId", 5)
                .when()
                .get("/pet/{petId}");
        TimeUnit.SECONDS.sleep(5);
        Assert.assertEquals("pet found ", 404, gettingInfoAboutDeleting.getStatusCode());
        CustomResponse mess = gettingInfoAboutDeleting.as(CustomResponse.class);
        Assert.assertEquals("Message doesnt match", "Pet not found", mess.getMessage());
    }
    @Test
    public void addUser() {

        System.out.println("I preparing test data...");
        User userToAdd = User.builder()
                .id(new Random().nextInt(5))
                .username("human")
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
        Assert.assertEquals("status code", 200, addingUserResponse.getStatusCode());
        System.out.println("Response: " + addingUserResponse.asString());
        Response getEmail = given()
                .baseUri(BASE_URL)
                .pathParam("username","human")
                .when()
                .get("/user/{username}")
                .then()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("UserScheme.json"))
                .extract()
                .response();

        System.out.println(getEmail.asString());
    }
    @Test
    public void checkAvailablePlace(){
        int idEmpty = 100;
        for (int i = 1; i <= idEmpty; i++) {
            int gettingPetsResponse = given()
                    .baseUri(BASE_URL)
                    .contentType(ContentType.JSON)
                    .when()
                    .get("/pet/" + i)
                    .then()
                    .extract()
                    .statusCode();

            if (gettingPetsResponse == 404) {
                idEmpty--;
            }
        }
        System.out.println(idEmpty);
    }
    @Test
    public void soldStatus(){

        Category platypusCategory = new Category(1, "Platypus");
        System.out.println("Category ready");
        BigInteger idPet = new BigInteger("2");
        Pet petToAdd = Pet.builder()
                .id(idPet)
                .category(platypusCategory)
                .name(RandomForTest.RANDOM_NAME)
                .photoUrls(Collections.singletonList("urls"))
                .tags(null)
                .status("sold")
                .build();
        System.out.println("Body to send: " + new Gson().toJson(petToAdd));

        Response addingNewPetPost = given()
                .baseUri(BASE_URL)
                .basePath("/pet")
                .contentType(ContentType.JSON)
                .body(petToAdd)
                .when()
                .post();
        System.out.println("Response: " + addingNewPetPost.asString());
        Assert.assertEquals("Status code in not 200", 200, addingNewPetPost.getStatusCode());

        Response gettingPetsWithStatusSold = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .when()
                .get("/pet/findByStatus/?status=sold");

        System.out.println("Request done");

        List<Pet> petsSold = Arrays.stream(gettingPetsWithStatusSold.as(Pet[].class))
                .filter(pet -> pet.getId().equals(petToAdd.getId()))
                .collect(Collectors.toList());

        Assert.assertEquals("Name is false", petToAdd.getName(), petsSold.get(0).getName());
    }
}
