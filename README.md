# C195_Scheduling-App

### Purpose of the Application

### Author Information
Author : Alex Fair 

Contact Info : afair26@wgu.edu

application version: 1.0.1

date: 04/13/2023

### IDE and version number
IntelliJ IDEA 2022.3.3 (Community Edition)
Build #IC-223.8836.41, built on March 9, 2023,
Runtime version: 17.0.6+1-b653.34 amd64
VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o.


#### JavaJDK : Java 17.0.5
#### Javafx : openjfx-17.0.2
#### mysql connector: mysql-connector-j-8.0.32

### Program directions
When the program launches the login screen is displayed. The user needs to enter a valid user name and password (from the
mysql database) and hit login. Once they are logged in they are presented with 4 buttons; 
appointments, customers, reports and exit which will lead the user to their respected menus.

### Additional Report
For my additional report I displayed the busiest first level division by name with its number of appointments in a label and 
below that there is a table showing all of the first level divisions with their number of appointments. I intially based it off
of the total appointments by month and by type but added in the first level divisions so it was different. 
It goes through and iterates through each appointment finding the customerID, then based off the customer ID it goes and gets
that customers division name. If that current division name already exists in the division observablelist it adds 1 to the count.

It was more difficult than I had expected it to be until I found the comparator method to sort the list.
