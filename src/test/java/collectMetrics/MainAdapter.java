package collectMetrics;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataDto.ResponseDto;
import dataDto.SprintInfoResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import utils.PropertyManager;
import utils.RestWrapper;

import static io.restassured.RestAssured.given;

public class MainAdapter {

    PropertyManager propertyManager = new PropertyManager();
    RestWrapper restWrapper = new RestWrapper();
    Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    public ResponseDto getListOfTasksByJql(String jql) {
        RequestSpecification request = given()
                .auth().preemptive()
                .basic(propertyManager.getProperty("email"), propertyManager.getProperty("apiToken"))
                .queryParam("jql", jql);
        Response response = restWrapper.get(propertyManager.getProperty("jiraBaseUrl") + "rest/api/3/search", request);
        return gson.fromJson(response.asString().trim(), ResponseDto.class);
    }

    public ResponseDto getHowManyTimesTaskWasReopened(String taskId) {
        RequestSpecification request = given()
                .auth().preemptive()
                .basic(propertyManager.getProperty("email"), propertyManager.getProperty("apiToken"))
                .queryParam("expand", "changelog");

        Response response =
                restWrapper.get(propertyManager.getProperty("jiraBaseUrl") + "rest/api/3/issue/" + taskId, request);
        return gson.fromJson(response.asString().trim(), ResponseDto.class);
    }

    public SprintInfoResponse getCurrentSprint() {
        RequestSpecification request  = null;
        if (propertyManager.get("sprint").equalsIgnoreCase("active")) {
            request = given()
                    .auth().preemptive()
                    .basic(propertyManager.getProperty("email"), propertyManager.getProperty("apiToken"))
                    .queryParam("state", "active");
        } else if (propertyManager.get("sprint").equalsIgnoreCase("last closed")) {
            request = given()
                    .auth().preemptive()
                    .basic(propertyManager.getProperty("email"), propertyManager.getProperty("apiToken"))
                    .queryParam("state", "closed");
        }
        Response response = restWrapper.get(
                propertyManager.getProperty("jiraBaseUrl") + "rest/agile/1.0/board/" + propertyManager.get(
                        "boardId") + "/sprint", request);

        SprintInfoResponse responseValue = gson.fromJson(response.asString().trim(), SprintInfoResponse.class);

        if (responseValue.getTotal() > 50){
            request = given()
                    .auth().preemptive()
                    .basic(propertyManager.getProperty("email"), propertyManager.getProperty("apiToken"))
                    .queryParam("state", "closed")
                    .queryParam("startAt", responseValue.getTotal() - 5);
            response = restWrapper.get(
                    propertyManager.getProperty("jiraBaseUrl") + "rest/agile/1.0/board/" + propertyManager.get(
                            "boardId") + "/sprint", request);
            return gson.fromJson(response.asString().trim(), SprintInfoResponse.class);
        } else return gson.fromJson(response.asString().trim(), SprintInfoResponse.class);
    }
}
