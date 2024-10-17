package metricsRunner;

import collectMetrics.MainAdapter;
import dataDto.*;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import utils.HTMLTableGenerator;
import utils.PropertyManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class QaLeadReport {

    MainAdapter mainAdapter = new MainAdapter();
    PropertyManager propertyManager = new PropertyManager();
    static String sprintName;
    static String startDate;
    static String endDate;
    static LinkedHashMap<String, Object> metricsValues;
    static LinkedHashMap<String, Object> metricsJqls;
    SprintInfoResponse response;
    List<ValueDto> values;

    @BeforeAll
    public static void beforeAll() {
        metricsValues = new LinkedHashMap<>();
        metricsJqls = new LinkedHashMap<>();
    }

    public SprintInfoResponse getSprintInfo() {
        if (response == null) {
            response = mainAdapter.getCurrentSprint();

            values = response.getValues();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            values.sort(Comparator.comparing(value -> {
                try {
                    return sdf.parse(value.getEndDate());
                } catch (ParseException e) {
                    throw new RuntimeException("Failed to parse date", e);
                }
            }));
            response.setValues(values);
            return response;
        } else return response;

    }

    @Test
    @Order(value = 1)
    public void getBasicSprintInfo() {
        SprintInfoResponse response = getSprintInfo();
        sprintName = response.getValues().get(response.getValues().size() - 1).getName();
        startDate =
                StringUtils.substringBefore(response.getValues().get(response.getValues().size() - 1).getStartDate(),
                        "T");
        endDate = StringUtils.substringBefore(response.getValues().get(response.getValues().size() - 1).getEndDate(),
                "T");
        metricsValues.put("Sprint Name", sprintName);
        metricsJqls.put("Sprint Name", "");
    }

    @Test
    @Order(value = 2)
    public void getTotalNumberOfTicketsInTheSprint() {
        String jql = "project =" + propertyManager.getProperty("projectLetters") +
                " AND component='" + propertyManager.getProperty("component") + "'" +
                " AND sprint='" + getSprintInfo().getValues().get(getSprintInfo().getValues().size() - 1).getName() + "'";
        ResponseDto json = mainAdapter.getListOfTasksByJql(jql);
        metricsValues.put("Total number of tasks in the sprint", json.getIssues().size());
        metricsJqls.put("Total number of tasks in the sprint", jql);
    }

    @Test
    @Order(value = 3)
    public void getTotalNumberOfQaPoints() {
        String jql = "project =" + propertyManager.getProperty("projectLetters") +
                " AND component='" + propertyManager.getProperty("component") + "'" +
                " AND sprint='" + getSprintInfo().getValues().get(getSprintInfo().getValues().size() - 1).getName() + "'" +
                " AND \"QA Points[Number]\" IS NOT EMPTY" +
                " AND status=Done" +
                " AND resolutionDate >= \"" + startDate + "\"" +
                " AND resolutionDate <= \"" + endDate + "\"";
        ResponseDto json = mainAdapter.getListOfTasksByJql(jql);
        double totalNumberOfQaPoints = 0.0;
        for (IssueDto issue : json.getIssues()) {
            Double issueQaPoints = issue.getFields().getQaPoints();
            if (issueQaPoints != null) {
                totalNumberOfQaPoints += issueQaPoints;
            }
        }

        String jql2 = "project =" + propertyManager.getProperty("projectLetters") +
                " AND component='" + propertyManager.getProperty("component") + "'" +
                " AND sprint='" + getSprintInfo().getValues().get(getSprintInfo().getValues().size() - 1).getName() + "'" +
                " AND \"QA Points[Number]\" IS NOT EMPTY";
        ResponseDto json2 = mainAdapter.getListOfTasksByJql(jql2);
        double totalNumberOfQaPoints2 = 0.0;
        for (IssueDto issue : json2.getIssues()) {
            Double issueQaPoints = issue.getFields().getQaPoints();
            if (issueQaPoints != null) {
                totalNumberOfQaPoints2 += issueQaPoints;
            }
        }
        String actualNumber = String.valueOf(totalNumberOfQaPoints).replace(".0", "");
        String expectedNumber = String.valueOf(totalNumberOfQaPoints2).replace(".0", "");
        metricsValues.put("Total number of qa points in the sprint Actual/Expected",
                actualNumber + "/" + expectedNumber);
        metricsJqls.put("Total number of qa points in the sprint Actual/Expected",
                "JQL Actual: " + jql + " <br> <br>JQL Expected: " + jql2);
    }

    @Test
    @Order(value = 4)
    public void getNumberOfTestedTickets() {
        String jql = "project =" + propertyManager.getProperty("projectLetters") +
                " AND component='" + propertyManager.getProperty("component") + "'" +
                " AND sprint='" + getSprintInfo().getValues().get(getSprintInfo().getValues().size() - 1).getName() + "'" +
                " AND 'QA Points[Number]' IS NOT EMPTY" +
                " AND status=Done" +
                " AND resolutionDate >= \"" + startDate + "\"" +
                " AND resolutionDate <= \"" + endDate + "\"";
        ResponseDto json = mainAdapter.getListOfTasksByJql(jql);
        metricsValues.put("Number of tested tickets at the end of the sprint", json.getIssues().size());
        metricsJqls.put("Number of tested tickets at the end of the sprint", jql);
    }

    @Test
    @Order(value = 5)
    public void getNumberOfNotTestedTicketsButTheyAreReadyForTesting() {
        String jql = "project =" + propertyManager.getProperty("projectLetters") +
                " AND component='" + propertyManager.getProperty("component") + "'" +
                " AND status = 'Ready for QA'" +
                " AND sprint='" + getSprintInfo().getValues().get(getSprintInfo().getValues().size() - 1).getName() + "'";
        ResponseDto json = mainAdapter.getListOfTasksByJql(jql);

        metricsValues.put("Number of tickets which are ready for testing, but not tested yet", json.getIssues().size());
        metricsJqls.put("Number of tickets which are ready for testing, but not tested yet", jql);

        String jql2 = "project =" + propertyManager.getProperty("projectLetters") +
                " AND status = 'in QA' " +
                "AND sprint='" + getSprintInfo().getValues().get(getSprintInfo().getValues().size() - 1).getName() + "'";
        ResponseDto json2 = mainAdapter.getListOfTasksByJql(jql2);

        metricsValues.put("Number of tickets which are in testing now, but testing is not finished",
                json2.getIssues().size());
        metricsJqls.put("Number of tickets which are in testing now, but testing is not finished", jql2);
    }

    @Test
    @Order(value = 6)
    public void getNumberOfTicketsWhereTestingIsNotRequired() {
        String jql = "project =" + propertyManager.getProperty("projectLetters") +
                " AND component='" + propertyManager.getProperty("component") + "'" +
                " AND sprint='" + getSprintInfo().getValues().get(getSprintInfo().getValues().size() - 1).getName() + "'" +
                " AND \"QA Points[Number]\" IS EMPTY" +
                " AND status=Done" +
                " AND \"Assigned QA[User Picker (multiple users)]\" = null" +
                " AND resolutionDate >= \"" + startDate + "\"" +
                " AND resolutionDate <= \"" + endDate + "\"";
        ResponseDto json = mainAdapter.getListOfTasksByJql(jql);

        metricsValues.put("Number of tickets where testing was not required", json.getIssues().size());
        metricsJqls.put("Number of tickets where testing was not required", jql);
    }

    @Test
    @Order(value = 7)
    public void getNumberOfTestedTicketsWithoutReopening() {
        String jql = "project =" + propertyManager.getProperty
                ("projectLetters") +
                " AND component='" + propertyManager.getProperty("component") + "'" +
                " AND sprint='" + getSprintInfo().getValues().get(getSprintInfo().getValues().size() - 1).getName() + "'" +
                " AND resolutionDate >= \"" + startDate + "\"" +
                " AND resolutionDate <= \"" + endDate + "\"" +
                " AND \"QA Points[Number]\" IS NOT EMPTY" +
                " AND status=Done" +
                " AND status WAS NOT 'QA FAILED'";
        ResponseDto json = mainAdapter.getListOfTasksByJql(jql);

        metricsValues.put("Number of tickets which was moved to done without reopening", json.getIssues().size());
        metricsJqls.put("Number of tickets which was moved to done without reopening", jql);
    }

    @Test
    @Order(value = 8)
    public void getListOfFailedTickets() {
        String jql = "project=" + propertyManager.getProperty
                ("projectLetters") +
                " AND component='" + propertyManager.getProperty("component") + "'" +
                " AND sprint='" + getSprintInfo().getValues().get(getSprintInfo().getValues().size() - 1).getName() + "'" +
                " AND \"QA Points[Number]\" IS NOT EMPTY" +
                " AND status CHANGED from 'In QA' TO 'QA FAILED' DURING (" + startDate + ", " + endDate + ")";
        ResponseDto json = mainAdapter.getListOfTasksByJql(jql);

        String jql2 = "project =" + propertyManager.getProperty
                ("projectLetters") +
                " AND component='" + propertyManager.getProperty("component") + "'" +
                " AND sprint='" + getSprintInfo().getValues().get(getSprintInfo().getValues().size() - 1).getName() + "'" +
                " AND resolutionDate >= \"" + startDate + "\"" +
                " AND resolutionDate <= \"" + endDate + "\"" +
                " AND \"QA Points[Number]\" IS NOT EMPTY" +
                " AND status=Done" +
                " AND status CHANGED from 'In QA' TO 'QA FAILED' DURING (" + startDate + ", " + endDate + ")";
        ResponseDto json2 = mainAdapter.getListOfTasksByJql(jql2);

        metricsValues.put(
                "Number of task which was reopened during sprint: Total number / Reopened, but moved to Done within " +
                        "the sprint", json.getIssues().size() + "/" + json2.getIssues().size());
        metricsJqls.put(
                "Number of task which was reopened during sprint: Total number / Reopened, but moved to Done within " +
                        "the sprint",
                "JQL for Total number: " + jql + " <br><br>JQL for Reopened, but moved to Done within the sprint: " + jql2);
        List<String> listOfIssues = new ArrayList<>();

        int counter = 0;
        for (int i = 0; i < json.getIssues().size(); i++) {
            String issueId = json.getIssues().get(i).getKey();
            String issueLink = propertyManager.getProperty("jiraBaseUiUrl") + issueId;
            ResponseDto issueResponse = mainAdapter.getHowManyTimesTaskWasReopened(issueId);

            for (HistoryDto history : issueResponse.getChangelog().getHistories()) {
                for (ItemDto item : history.getItems()) {
                    if (item.getField().equals("status") && item.getToString().equals("QA Failed")) {
                        counter++;
                    }
                }
            }
            listOfIssues.add(issueLink + ". Total number of reopens is " + counter);
            counter = 0;
        }

        metricsValues.put("List of tasks or bugs links, which was reopened", listOfIssues);
        metricsJqls.put("List of tasks or bugs links, which was reopened", " ");
    }

    @Test
    @Order(value = 9)
    public void getNumberOfAcceptanceBugsCreatedDuringTheSprint() {
        String jql = "project =" + propertyManager.getProperty("projectLetters") +
                " AND component='" + propertyManager.getProperty("component") + "'" +
                " AND type = 'Acceptance Bug'" +
                " AND created >= \"" + startDate + "\"" +
                " AND created <=  \"" + endDate + "\"";
        ResponseDto response = mainAdapter.getListOfTasksByJql(jql);

        metricsValues.put("Number of acceptance bugs created", response.getIssues().size());
        metricsJqls.put("Number of acceptance bugs created", jql);
    }

    @Test
    @Order(value = 10)
    public void getNumberOfBugsCreatedDuringTheSprint() {
        String jql = "project =" + propertyManager.getProperty("projectLetters") +
                " AND component='" + propertyManager.getProperty("component") + "'" +
                " AND type = 'Bug'" +
                " AND created >= \"" + startDate + "\"" +
                " AND created <=  \"" + endDate + "\"";
        ResponseDto response =
                mainAdapter.getListOfTasksByJql(jql);

        metricsValues.put("Number of bugs created during sprint", response.getIssues().size());
        metricsJqls.put("Number of bugs created during sprint", jql);
    }

    @Test
    @Order(value = 11)
    public void getNumberOfImprovementsCreatedDuringSprint() {
        String jql = "project =" + propertyManager.getProperty
                ("projectLetters") +
                " AND component='" + propertyManager.getProperty("component") + "'" +
                " AND type IN (Task, Story)" +
                " AND creator IN (" + propertyManager.get("responsibleQas") + ")" +
                " AND summary !~ 'Create tests' AND summary !~ 'QA' AND summary !~ 'API' AND summary !~ 'in Postman'" +
                " AND created >= \"" + startDate + "\"" +
                " AND created <=  \"" + endDate + "\"";
        ResponseDto response = mainAdapter.getListOfTasksByJql(jql);

        metricsValues.put("Number of improvements created during sprint", response.getIssues().size());
        metricsJqls.put("Number of improvements created during sprint", jql);
    }

    @AfterAll
    public static void generateHTML() {
        HTMLTableGenerator HTMLTableGenerator = new HTMLTableGenerator();
        HTMLTableGenerator.generateHtml(sprintName.replaceAll(" ", ""), metricsValues, metricsJqls);
    }
}