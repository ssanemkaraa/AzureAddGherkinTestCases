package azure;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class AzureAddTestCases {

    public static void addWorkItemIntoTestPlan(String baseUri, String planId, String suiteId, String titleScenario, String isAutomated, List<String> stepList, String mail, String name, String PAT) throws IOException {

        RestAssured.baseURI = baseUri;

        StringBuilder allSteps = new StringBuilder();

        for (int j = 0; j < stepList.size(); j++) {
            String id = String.valueOf(j + 2);
            String step = stepList.get(j);
            if (step.contains("<") || step.contains(">") || step.contains("&")) {
                step = step.replace("<", "&lt;").replace(">", "&gt;").replace("&", "&amp;");
            }
            String stepFormatted = "<step id=\\\\\\\"" + id + "\\\\\\\" type=\\\\\\\"ActionStep\\\\\\\"><parameterizedString isformatted=\\\\\\\"true\\\\\\\">&lt;DIV&gt;&lt;P&gt;" + step + "&lt;/P&gt;&lt;/DIV&gt;</parameterizedString><parameterizedString isformatted=\\\\\\\"true\\\\\\\">&lt;DIV&gt;&lt;P&gt;&lt;BR/&gt;&lt;/P&gt;&lt;/DIV&gt;</parameterizedString><description/></step>";

            allSteps.append(String.format(stepFormatted, step.trim()));
        }
        allSteps = new StringBuilder(allSteps.toString());

        String addWorkItembody = "{\"contributionIds\":[\"ms.vss-work-web.update-work-items-data-provider\"],\"context\":{\"properties\":{\"updatePackage\":\"[{\\\"id\\\":0,\\\"rev\\\":0,\\\"projectId\\\":\\\"8b932203-9524-4d15-8a3b-4c2cbfe8e44c\\\",\\\"isDirty\\\":true,\\\"tempId\\\":-1,\\\"fields\\\":{\\\"1\\\":\\\"" +
                titleScenario +
                "\\\",\\\"2\\\":\\\"Design\\\",\\\"9\\\":\\\"" + name + " <" + mail + ">\\\",\\\"22\\\":\\\"New\\\",\\\"24\\\":\\\"" + name + " <" + mail + ">\\\",\\\"25\\\":\\\"Test Case\\\",\\\"33\\\":\\\"" + name + " <" + mail + ">\\\",\\\"22352360\\\":{\\\"type\\\":1},\\\"22352361\\\":{\\\"type\\\":1},\\\"22352362\\\":\\\"" + name + " <" + mail + ">\\\",\\\"22352368\\\":2,\\\"22352395\\\":\\\"<steps id=\\\\\\\"0\\\\\\\" last=\\\\\\\"2\\\\\\\">" +
                allSteps +
                "</steps>\\\",\\\"22352404\\\":\\\"" +
                isAutomated +
                "\\\",\\\"-2\\\":332,\\\"-104\\\":494}}]\", \"pageSource\":{\"contributionPaths\":[\"VSS\",\"VSS/Resources\",\"q\",\"knockout\",\"mousetrap\",\"mustache\",\"react\",\"react-dom\",\"react-transition-group\",\"jQueryUI\",\"jquery\",\"OfficeFabric\",\"tslib\",\"@uifabric\",\"VSSUI\",\"ContentRendering\",\"ContentRendering/Resources\",\"WidgetComponents\",\"WidgetComponents/Resources\",\"Charts\",\"Charts/Resources\",\"TFSUI\",\"TFSUI/Resources\",\"TFS\",\"Notifications\",\"Presentation/Scripts/marked\",\"Presentation/Scripts/URI\",\"Presentation/Scripts/punycode\",\"Presentation/Scripts/IPv6\",\"Presentation/Scripts/SecondLevelDomains\",\"highcharts\",\"highcharts/highcharts-more\",\"highcharts/modules/accessibility\",\"highcharts/modules/heatmap\",\"highcharts/modules/funnel\",\"Analytics\",\"ReleaseManagement/Core\"], \"diagnostics\":{ \"sessionId\":\"5e69c062-30aa-422b-973c-2fb1123f2a90\",     \"activityId\":\"5e69c062-30aa-422b-973c-2fb1123f2a90\",     \"bundlingEnabled\":true,     \"cdnAvailable\":true,     \"cdnEnabled\":true,     \"webPlatformVersion\":\"M217\",     \"serviceVersion\":\"Dev19.M217.1 (build: AzureDevOps_M217_20230222.11)\"},     \"navigation\":{   \"topMostLevel\":8, \"area\":\"\", \"currentController\":\"ContributedPage\", \"currentAction\":\"Execute\", \"routeId\":\"ms.vss-test-web.testplans-hub-refresh-route\", \"routeTemplates\":[\"{project}/_testPlans\",\"{project}/_testPlans/{pivots}\",\"{project}/{team}/_testPlans\",\"{project}/{team}/_testManagement\",\"{project}/_testManagement\"], \"routeValues\":{  \"project\":\"RoofXR\",\"controller\":\"ContributedPage\",\"action\":\"Execute\"                        }                        },\"project\":{ \"id\":\"8b932203-9524-4d15-8a3b-4c2cbfe8e44c\",\"name\":\"RoofXR\"                            },\"selectedHubGroupId\":\"ms.vss-test-web.test-hub-group\",\"selectedHubId\":\"ms.vss-test-web.testplans-hub-refresh\",\"url\":\"YOUR_ORGANIZATION_URL/RoofXR/_testPlans/define?planId=" +
                planId +
                "&suiteId=" +
                suiteId +
                "\"},\"sourcePage\":{\"url\":\"YOUR_ORGANIZATION_URL/RoofXR/_testPlans/define?planId=" +
                planId +
                "&suiteId=" +
                suiteId +
                "\",\"routeId\":\"ms.vss-test-web.testplans-hub-refresh-route\",\"routeValues\":{\"project\":\"RoofXR\",\"controller\":\"ContributedPage\",\"action\":\"Execute\"}}}}}";

        Response response = given()
                .header("Accept", "application/json;api-version=5.1-preview.1;excludeUrls=true;enumsAsNumbers=true;msDateFormat=true;noArrayWrap=true")
                .header("Authorization", "Basic " + PAT)
                .header("Content-Type", "application/json")
                .header("Cookie", "VstsSession=%7B%22PersistentSessionId%22%3A%2244463523-d5c4-43ce-ba0d-6f0c69c82312%22%2C%22PendingAuthenticationSessionId%22%3A%2200000000-0000-0000-0000-000000000000%22%2C%22CurrentAuthenticationSessionId%22%3A%2200000000-0000-0000-0000-000000000000%22%2C%22SignInState%22%3A%7B%7D%7D").body(addWorkItembody)
                .when()
                .post("_apis/Contribution/dataProviders/query")
                .then()
                .extract().response();

        System.out.println("-----------------------------------" + response.statusCode());
//        response.prettyPrint();

        String responseString = response.asString();

        JSONObject responseJson = new JSONObject(responseString);
        JSONObject data = responseJson.getJSONObject("data");
        JSONObject updateWorkItemsDataProvider = data.getJSONObject("ms.vss-work-web.update-work-items-data-provider");
        int id = updateWorkItemsDataProvider.getJSONArray("data").getJSONObject(0).getInt("id");
        System.out.println("id : " + id);

        String workItemId = String.valueOf(id);

        Response response2 = given()
                .header("Accept", "application/json;api-version=6.1-preview.3;excludeUrls=true;enumsAsNumbers=true;msDateFormat=true;noArrayWrap=true")
                .header("Authorization", "Basic " + PAT)
                .header("Content-Type", "application/json")
                .header("Cookie", "VstsSession=%7B%22PersistentSessionId%22%3A%2244463523-d5c4-43ce-ba0d-6f0c69c82312%22%2C%22PendingAuthenticationSessionId%22%3A%2200000000-0000-0000-0000-000000000000%22%2C%22CurrentAuthenticationSessionId%22%3A%2200000000-0000-0000-0000-000000000000%22%2C%22SignInState%22%3A%7B%7D%7D").body("[{\"pointAssignments\":[],\"workItem\":{\"id\":" + workItemId + "}}]")
                .when()
                .post("RoofXR/_apis/testplan/Plans/" + planId + "/Suites/" + suiteId + "/TestCase")
                .then()
                .extract().response();

        System.out.println("-----------------------------------" + response2.statusCode());
//        response2.prettyPrint();

        String endOfUrl = "YOUR_ORGANIZATION_URL/RoofXR/_testPlans/define?planId=" + planId + "&suiteId=" + suiteId;

        System.out.println("Eklendi : " + endOfUrl);

    }

    public static void main(String[] args) throws IOException {
        String organizationUrl = "YOUR_ORGANIZATION_URL";
        String planId = "88382";
        String suiteId = "88388";
        String isAutomated = "Not Automated";
        String mail = "sanem.kara@goartmetaverse.com";
        String name = "Sanem Kara";
        // Azure Profilinden Personal Access Token oluşturulabilir. Bu Token ile postmanden istek atılarak tokenın şifrelenmiş hali alınabilir.
        // Request body örneği json dosyasında bulunmaktadır.
        String PAT = "Şifrelenmiş PAT";

        List<Map<String, Object>> scenarios = FeatureParser.parseFeatureFile("src/test/java/azure/test.feature");

        for (Map<String, Object> scenario : scenarios) {
            String title = FeatureParser.getScenarioTitle(scenario);
            System.out.println(title);
            String[] scenarioSteps = FeatureParser.getScenarioSteps(scenario);
            List<String> scenarioStepList = new ArrayList<>(Arrays.asList(scenarioSteps));
            addWorkItemIntoTestPlan(organizationUrl, planId, suiteId, title, isAutomated, scenarioStepList, mail, name, PAT);
        }
    }
}