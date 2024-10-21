Hi! My name is Palina Patsankova, I am AQA. I wrote this project for collecting metrics using info from Jira.
I prepared instruction for you how to use this code.

How to download IDE:

1. Open official website https://www.jetbrains.com/ru-ru/idea/download/other.html .
2. You need IntelliJ IDEA Community Edition, choose your OS and click on link which will start download IDE
3. Install IDE

How to prepare IDE:

1. Open IDE
2. In Menu bar click on File -> Project Structure
3. in 'SDK' dropdown choose 'download JDK', choose '17' version 'Amazon Corretto' vendor, click on download button
4. Click on 'Apply' and 'OK' buttons
5. In Menu bar click on File -> Settings
6. find 'Plugins' option in the left menu
7. using search field find 'Lombok' plugin (pepper icon) and install it
8. Click on 'Apply' and 'OK' buttons
9. if IDE ask to reload PC, do it

How to clone repository:

1. Open IDE
2. In Menu bar click on File -> New -> Project from version control system
3. Open github with the project https://github.com/palina-patsankova/MetricsCollector
4. Click on '<> Code' green button
5. Click on 'copy' icon to copy url
6. return to IDE
7. paste URL to 'URL' field
8. Click on 'Clone' button
9. If IDE ask you to login with your GitHub credentials, do it
10. Ypu can have a dialog window with options 'Trust project', 'Open in safe mode', You can click on 'Trust project'

How to add needed properties to the project:

1. Open src/test/resources package
2. Right mouse click on 'resources' folder -> New -> File
3. Name of the file MUST BE 'test.properties'
4. Click on 'Create'
5. if IDE ask you to add it to GIT, don't add it
6. Fill in file according to example below. (Don't forget to delete all slashes if you see them at the end of lines)

Example of 'test.properties' file \
{START OF FILE}

email={your email from Jira} \
apiToken=Open Jira, in right upper corner click on settings, choose 'Atlassian account settings', open Security tab,
in 'API tokens' block click on 'Create and manage API tokens' link, click on 'Create API token' button, write any name,
click on 'create' button, copy token and paste here\
jiraBaseUrl=https://baseUrl.com/ \
jiraBaseUiUrl=https://baseUrl.com/browse/

#copy from board URL
#baseurl/jira/software/c/projects/QAT/boards/116 \
projectLetters=QAT \
boardId=116 \
component=componentName \
responsibleQas='Ivan Ivanou', 'Petr Petrou', 'Sergey Sergeeu', 'Name LastName', 'Name lastName' \
sprint=last closed \
#sprint=active

{END OF FILE}

How to collect data using project:

1. Open src/test/java/metricsRunner package (folder)
2. Open QaLeadReport file (double click will open this file)
3. Click on 'Play' icon near 'public class QaLeadReport' line (line 18)
4. Choose 'Run 'QaLeadReport'' option
5. Wait until tests will finish
6. Open src/test/resources/htmlReports package
7. Double click on html file
8. In right upper corner you will see options with different browser icons, choose browser you want
9. If you don't see see options with different browser icons, you can click on right mouse button on .html file with
   report Open In -> Browser -> {choose browser you want}

When do we need to add new variables to properties file?

- when values of variables is confidential information (tokens, credentials)
- information which is different for different teams (IDs, names and so on)

There is no need to add new variable when you need to add new task type or some default field which are the same for all
teams

How to add another values which you want to use in JQL:

1. Open test.properties file
2. In new line you need to provide name to new variable in the following format {nameOfTheField=expectedValue}
3. Open QaLeadReport file
4. Find needed test which you want to edit
5. In a new line of existing jql add your operator and using '+' add value of new added field.
6. Example: " AND nameOfTheField='" + propertyManager.getProperty("nameOfTheField") + "'" +

What should I do if I don't need any parameter from default code?

- You need just delete this line, but be careful, jql should be finished with ';'
- For example you have jql: String jql = "project =" + propertyManager.getProperty("projectLetters") +
  " AND component='" + propertyManager.getProperty("component") + "'" +
  " AND sprint='" + getSprintInfo().getValues().get(getSprintInfo().getValues().size() - 1).getName() + "'";
- if you don't need component in jql, delete it and it should looks like: String jql = "project =" +
  propertyManager.getProperty("projectLetters") +
  " AND sprint='" + getSprintInfo().getValues().get(getSprintInfo().getValues().size() - 1).getName() + "'";

What should I do if I don't need some metrics from default code?

- you can delete them at all
- you can comment them: Fully highlight needed code and use hotkeys 'Ctrl + /'
- if after commenting code is still running, do following actions: in the menu bar choose Build -> Build Project

What should I do if I want to add my metrics?

1. Copy code from any previous test
2. Modify jql
3. Give new name for 
   - metricsValues.put("place yor new name here", value); 
   - and metricsJqls.put("place yor new name here", value);
4. Change @Order(value = {place next number})
   - Example @Order(value = 12)