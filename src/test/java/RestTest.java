import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class RestTest {

    private String apiKey ="r0eOnKLOvelRb1cZdyV6bKaFErm0i7iIzD91cWGH";
    private String[] curiosityCameras =new String[] {"FHAZ", "RHAZ", "MAST", "CHEMCAM", "MAHLI", "MARDI", "NAVCAM"};


    @Before
    public void before(){
        RestAssured.baseURI = "https://api.nasa.gov";
    }


    @Test
    public void TenFirstPhotosSunDate(){

        given().log().all().queryParam("api_key", apiKey)
                .queryParam("sol", "1000")
                .queryParam("page", "1")
                .queryParam("per_page", "10")
                .get("/mars-photos/api/v1/rovers/curiosity/photos")
                .then().log().ifError()
                .statusCode(200)
                .body("photos.size()", is(10))
                .and()
                .body("photos[0].sol", equalTo(1000))
                .and()
                .body("photos[0].rover.name", equalTo("Curiosity"));

    }

    @Test
    public void TenFirstPhotosEarthDate(){

        given().log().all().queryParam("api_key", apiKey)
                .queryParam("earth_date", "2015-05-30")
                .queryParam("page", "1")
                .queryParam("per_page", "10")
                .get("/mars-photos/api/v1/rovers/curiosity/photos")
                .then().log().ifError()
                .statusCode(200)
                .body("photos.size()", is(10))
                .and()
                .body("photos[0].earth_date", equalTo("2015-05-30"))
                .and()
                .body("photos[0].rover.name", equalTo("Curiosity"));

    }

    @Test
    public void compareImages(){

        Response responseEarth= given().log().all().queryParam("api_key", apiKey)
                .queryParam("earth_date", "2015-05-30")
                .queryParam("page", "1")
                .queryParam("per_page", "10")
                .get("/mars-photos/api/v1/rovers/curiosity/photos");

        Response responseSol= given().log().all().queryParam("api_key", apiKey)
                .queryParam("sol", "1000")
                .queryParam("page", "1")
                .queryParam("per_page", "10")
                .get("/mars-photos/api/v1/rovers/curiosity/photos");


       Assert.assertEquals(responseEarth.getBody().asString(),responseSol.getBody().asString());
    }

    @Test
    public void validateAmountOfPictures() {

        ArrayList<Integer> curiositySize = new ArrayList<Integer>();
        int otherCamerasSum = 0;

        //photos taken by each camera from curiosity rover
        for (int i = 0; i < curiosityCameras.length; i++) {
            Response response = given().log().all().queryParam("api_key", apiKey)
                    .queryParam("sol", "1000")
                    .queryParam("camera", curiosityCameras[i])
                    .get("/mars-photos/api/v1/rovers/curiosity/photos")
                    .then()
                    .extract()
                    .response();
            int size = response.jsonPath().getList("photos").size();
            curiositySize.add(size);
        }

        //Photos taken by opportunity cameras
        Response response = given().log().all().queryParam("api_key", apiKey)
                .queryParam("sol", "1000")
                .get("/mars-photos/api/v1/rovers/opportunity/photos")
                .then()
                .extract()
                .response();
        otherCamerasSum = otherCamerasSum + response.jsonPath().getList("photos").size();


        //Photos taken by spirit cameras
        response = given().log().all().queryParam("api_key", apiKey)
                .queryParam("sol", "1000")
                .get("/mars-photos/api/v1/rovers/spirit/photos")
                .then()
                .extract()
                .response();
        otherCamerasSum = otherCamerasSum + response.jsonPath().getList("photos").size();

        otherCamerasSum = otherCamerasSum * 10;

        for (int size : curiositySize) {
            System.out.println("Size: " + size + " total photos taken by other cameras " + otherCamerasSum);
            Assert.assertTrue("Curiosity camera took more pictures than 10 times other cameras on same date",
                    size < otherCamerasSum);
        }
    }

            


        




}