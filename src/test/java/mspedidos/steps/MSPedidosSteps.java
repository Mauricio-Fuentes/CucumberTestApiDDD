package mspedidos.steps;

import context.World;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import util.RequestSpecificationFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static util.Util.jsonTemplate;

public class MSPedidosSteps {
    private final World world;
    private final Properties envConfig;
    private RequestSpecification request;

    public MSPedidosSteps(World world) {
        this.world = world;
        this.envConfig = World.envConfig;
        this.world.featureContext = World.threadLocal.get();
    }

    @Before
    public void setUp() {
        request = RequestSpecificationFactory.getInstance();
    }

    @Given("a account with valid details")
    public void getProductValidData(@Transpose DataTable dataTable) throws IOException {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        String name = data.get(0).get("name");
        String number = data.get(0).get("number");
        String city = data.get(0).get("city");
        String type = data.get(0).get("type");

        Map<String, Object> valuesToTemplate = new HashMap<>();
        valuesToTemplate.put("name", name);
        valuesToTemplate.put("number", number);
        valuesToTemplate.put("city", city);
        valuesToTemplate.put("type", type);

        String jsonAsString = jsonTemplate(envConfig.getProperty("msaccount-accounts_request"), valuesToTemplate);

        world.scenarioContext.put("requestStr", jsonAsString);
    }

    @When("request is submitted for account creation")
    public void submitProductCreation() {
        String payload = world.scenarioContext.get("requestStr").toString();
        Response response = request
                .accept(ContentType.JSON)
                .body(payload)
                .contentType(ContentType.JSON)
                .when().post(envConfig.getProperty("msaccount-service_url")
                        + envConfig.getProperty("msaccount-account_api"));

        world.scenarioContext.put("response", response);
    }

    @Then("verify that the HTTP response is {int}")
    public void verifyHTTPResponseCode(Integer status) {
        Response response = (Response) world.scenarioContext.get("response");
        Integer actualStatusCode = response.then()
                .extract()
                .statusCode();
        Assert.assertEquals(status, actualStatusCode);
    }

    @Then("a account id is returned")
    public void checkProductId() {
        Response response = (Response) world.scenarioContext.get("response");
        String responseString = response.then().extract().asString();
        Assert.assertNotNull(responseString);
        Assert.assertNotEquals("", responseString);
    }
}
