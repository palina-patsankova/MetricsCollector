package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

public class HTMLTableGenerator {

    public void generateHtml(String sprintName, LinkedHashMap<String, Object> metricsValues,
                             LinkedHashMap<String, Object> metricsJqls) {
        String filePath = "src/test/resources/htmlReports/" + sprintName + ".html";

        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<!DOCTYPE html>\n");
        htmlContent.append("<html>\n");
        htmlContent.append("<head>\n");
        htmlContent.append("<title>Metrics Table</title>\n");
        htmlContent.append("<style>\n");
        htmlContent.append("body {font-family: Arial, sans-serif; background-color: #f4f4f9; margin: 50px;}\n");
        htmlContent.append(
                "table {width: 60%; margin: auto; border-collapse: collapse; box-shadow: 0 2px 3px rgba(0,0,0,0.1);" +
                        "}\n");
        htmlContent.append("th, td {border: 1px solid #ddd; padding: 12px; text-align: left;}\n");
        htmlContent.append("th {background-color: #4CAF50; color: white;}\n");
        htmlContent.append("tr:nth-child(even) {background-color: #f2f2f2;}\n");
        htmlContent.append("tr:hover {background-color: #ddd;}\n");
        htmlContent.append("</style>\n");
        htmlContent.append("</head>\n");
        htmlContent.append("<body>\n");
        htmlContent.append("<h2 style='text-align:center;'>Metrics Table</h2>\n");
        htmlContent.append("<table>\n");
        htmlContent.append("<tr>\n");
        htmlContent.append("<th>Metric's Name</th>\n");
        htmlContent.append("<th>Result</th>\n");
        htmlContent.append("<th>JQL</th>\n");
        htmlContent.append("</tr>\n");

        for (String metricName : metricsValues.keySet()) {
            htmlContent.append("<tr>\n");
            htmlContent.append("<td>").append(metricName).append("</td>\n");

            Object resultValue = metricsValues.get(metricName);
            if (metricName.equals("List of tasks or bugs links, which was reopened")) {
                htmlContent.append("<td>\n");
                List<String> reopenedLinks = (List<String>) resultValue;
                for (String link : reopenedLinks) {
                    htmlContent.append("<p>").append(link).append("</p>\n");
                }
                htmlContent.append("</td>\n");
            } else {
                htmlContent.append("<td>").append(resultValue).append("</td>\n");
            }

            Object jqlValue = metricsJqls.get(metricName);
            htmlContent.append("<td>").append(jqlValue != null ? jqlValue.toString() : "").append("</td>\n");
            htmlContent.append("</tr>\n");
        }

        htmlContent.append("</table>\n");
        htmlContent.append("</body>\n");
        htmlContent.append("</html>");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(htmlContent.toString());
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }

        System.out.println("Beautiful HTML file created successfully: " + filePath);
    }
}
