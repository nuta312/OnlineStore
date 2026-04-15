package kg.benext.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import static io.restassured.RestAssured.given;

@Slf4j
@Data
public class HttpRequest {
    protected String url;
    protected RequestSpecification requestSpecification;
    protected Response response;

    private static final String SLASH = "/";

    public HttpRequest(String url) {
        this.url = url;
        RestAssured.defaultParser = Parser.JSON;
        this.requestSpecification = given().baseUri(url).contentType(ContentType.JSON).accept(ContentType.JSON);
    }

    public Response get(String endPoint){
        log.info("Performed GET {}", endPoint);
        this.response = given().spec(requestSpecification).get(endPoint);
        logResponse();
        return this.response;
    }

    public Response post(String endPoint, String body){
        log.info("Performed POST {}", endPoint);
        log.info("Body is {}", body);
        this.response = given().spec(requestSpecification).body(body).post(endPoint);
        logResponse();
        return this.response;
    }

    public Response patch(String endPoint, String body){
        log.info("Performed PATCH {}", endPoint);
        log.info("Body is {}", body);
        this.response = given().spec(requestSpecification).body(body).patch(endPoint);
        logResponse();
        return this.response;
    }

    public Response put(String endPoint, String body){
        log.info("Performed PUT {}", endPoint);
        log.info("Body is {}", body);
        this.response = given().spec(requestSpecification).body(body).put(endPoint);
        logResponse();
        return this.response;
    }

    public Response delete(String endPoint){
        log.info("Performed DELETE {}", endPoint);
        this.response = given().spec(requestSpecification).delete(endPoint);
        logResponse();
        return this.response;
    }

    private void logResponse(){
        log.warn("Response is: ");
        log.warn(getResponse().getBody().asPrettyString());
        log.warn("Status code is: {}", getResponse().getStatusCode());
    }

    public String getEndPoint(String... endPoints){
        StringBuilder endPoint = new StringBuilder();

        for (String arg : endPoints){
            endPoint.append(arg).append(SLASH);
        }
        return endPoint.substring(0,endPoint.length()-1);
    }

    public HttpRequest withToken(String token) {
        this.requestSpecification = given()
                .baseUri(url)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + token);
        return this;
    }

}
