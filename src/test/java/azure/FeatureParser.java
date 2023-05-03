package azure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FeatureParser {

    public static List<Map<String, Object>> parseFeatureFile(String filePath) throws IOException {
        String featureText = new String(Files.readAllBytes(Paths.get(filePath)));
        List<Map<String, Object>> scenarios = new ArrayList<>();

        // Find all scenarios in the feature file
        String[] scenarioTexts = featureText.split("(?=Scenario:|Scenario Outline:|\\z)");
        for (String scenarioText : scenarioTexts) {
            if (scenarioText.contains("Scenario Outline:")) {
                // Parse scenario outline and examples
                String[] scenarioOutlineAndExamples = scenarioText.split("Examples:");
                String scenarioOutline = scenarioOutlineAndExamples[0];
                String examples = scenarioOutlineAndExamples[1];
                String[] scenarioSteps = scenarioOutline.split("\n");
                String scenarioName = scenarioSteps[0].substring("Scenario Outline:".length()).trim();
                scenarioSteps = Arrays.copyOfRange(scenarioSteps, 1, scenarioSteps.length);
                String[] examplesLines = examples.split("\n");
                String[] examplesHeaders = Arrays.stream(examplesLines[0].split("\\|"))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toArray(String[]::new);
                List<String[]> examplesData = Arrays.stream(examplesLines)
                        .skip(1)
                        .filter(line -> !line.trim().isEmpty())
                        .map(line -> Arrays.stream(line.split("\\|"))
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .toArray(String[]::new))
                        .collect(Collectors.toList());
                // Combine the scenario steps with the examples data to create new scenarios
                for (String[] example : examplesData) {
                    Map<String, Object> exampleScenario = new HashMap<>();
                    exampleScenario.put("name", getScenarioName(scenarioName, example, examplesHeaders));
                    exampleScenario.put("steps", Arrays.stream(scenarioSteps).map(step -> String.format(step, (Object[]) example)).toArray(String[]::new));
                    exampleScenario.put("headers", examplesHeaders);
                    exampleScenario.put("data", example);
                    scenarios.add(exampleScenario);
                }
            } else if (scenarioText.startsWith("Scenario:")) {
                // Parse scenario
                String[] scenarioLines = scenarioText.split("\n");
                String scenarioName = scenarioLines[0].substring("Scenario:".length()).trim();
                String[] scenarioSteps = Arrays.copyOfRange(scenarioLines, 1, scenarioLines.length);
                Map<String, Object> scenario = new HashMap<>();
                scenario.put("name", scenarioName);
                scenario.put("steps", scenarioSteps);
                scenario.put("headers", null);
                scenario.put("data", null);
                scenarios.add(scenario);
            }
        }
        return scenarios;
    }

    private static String getScenarioName(String scenarioName, String[] example, String[] examplesHeaders) {
        String exampleName = IntStream.range(0, examplesHeaders.length)
                .mapToObj(i -> examplesHeaders[i] + "=" + example[i])
                .collect(Collectors.joining(", "));
        String scenarioNameWithExample = scenarioName + exampleName;
        String[] scenarioNameTokens = scenarioName.split("\\(");
        if (scenarioNameTokens.length > 1) {
            String oldExampleName = scenarioNameTokens[1].replaceAll("\\)", "").trim();
            scenarioNameWithExample = scenarioNameWithExample.replace(oldExampleName, exampleName);
        }
        return scenarioNameWithExample;
    }

    private static String getExampleName(String[] headers, Object[] data) {
//        String test =  IntStream.range(0, headers.length)
//                .mapToObj(i -> headers[i] + "=" + data[i])
//                .collect(Collectors.joining(", "));
//        System.out.println(test+"**************");
        String dataArray = Arrays.toString(data);
        String headerArray = Arrays.toString(headers);
        return dataArray;
    }


    private static void printScenario(Map<String, Object> scenario) {
        String name = (String) scenario.get("name");
        String[] headers = (String[]) scenario.get("headers");
        Object[] data = (Object[]) scenario.get("data");
        String exampleString = "";

        if (headers != null && data != null) {

            exampleString = name + " " + getExampleName(headers, data);
        } else {
            exampleString = name;
        }

        String title = exampleString;

        String[] scenarioSteps = (String[]) scenario.get("steps");
        System.out.println(title);

        for (String step : scenarioSteps) {
            System.out.println("\t" + step);
        }
    }

    public static String getScenarioTitle(Map<String, Object> scenario) {
        String name = (String) scenario.get("name");
        String[] headers = (String[]) scenario.get("headers");
        Object[] data = (Object[]) scenario.get("data");
        String exampleString = "";

        if (headers != null && data != null) {

            exampleString = name + " " + getExampleName(headers, data);
        } else {
            exampleString = name;
        }

        String title = exampleString;
        return title;
    }

    public static String[] getScenarioSteps(Map<String, Object> scenario) {

        String[] scenarioSteps = (String[]) scenario.get("steps");
        for (String step : scenarioSteps) {
            System.out.println("\t" + step);
        }
        return scenarioSteps;
    }


    public static void main(String[] args) throws IOException {

        List<Map<String, Object>> scenarios = parseFeatureFile("src/test/java/azure/test.feature");

        for (Map<String, Object> scenario : scenarios) {
            printScenario(scenario);
        }

    }
}