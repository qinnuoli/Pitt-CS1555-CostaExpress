package dbpack;
import java.sql.*;
import java.util.Properties;
import java.io.*;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.RoundingMode; 

public class ImportAllText {
    String passwordp = "";
    String userp = "";
    Statement st;
    Connection conn;
    
    public ImportAllText(String password, String user) throws
        SQLException, ClassNotFoundException, IOException {
        passwordp = password;
        userp = user;

		connectionDB();			
		
		String tableName = null;
		String[] attributeNames;
		String attributeReturnString = null;
		
		//parse file
		String file = "/Users/qinnuoli/Desktop/CS1555_CostaExpress/src/Demo/AllData.txt";
        BufferedReader bufferedReader;

        bufferedReader = new BufferedReader(new FileReader(file));

		
		String currentLine;
		

		while ((currentLine = bufferedReader.readLine()) != null){
			
			if (!currentLine.contains("****")){ //if string is not a line of ****
				
				if (currentLine.contains("***")){ //if string contains *** it is either the tableName or format
				
					StringBuilder workingLine = new StringBuilder();
					workingLine.append(currentLine);
					
					if (currentLine.contains(".")){ //if line contains ".", it is the tableName line
						
						workingLine.delete(0, 4); //delete ***
						int periodLocation = workingLine.indexOf("."); //find period
						int lineLength = workingLine.length();
						workingLine.delete(periodLocation, lineLength); //delete everything from . to end of line
						

						tableName = workingLine.toString();
						
						tableName = tableName.replaceAll(" ", "_");
						
						System.out.println("Table: " + tableName); // test if table names are correctly formatted with underscores
						
						
					} else { //if line does not contain ".", it is the formatting line
						
						workingLine.delete(0, 4); //delete ***
						
						if ((tableName.compareToIgnoreCase("Railroad_lines") == 0) || (tableName.compareToIgnoreCase("Routes") == 0)){
							String tmpAttributes = workingLine.toString();
							
							
							//handling for routes and railroad lines
							int carrotLocation = tmpAttributes.indexOf("<");
							
							workingLine.delete(carrotLocation, workingLine.length());
							tmpAttributes = workingLine.toString();
							
							tmpAttributes = tmpAttributes.replaceAll(" ", ", ");
							
							workingLine.delete(0,workingLine.length());
							workingLine = workingLine.append(tmpAttributes);
							workingLine.delete(workingLine.length()-2, workingLine.length());
							
							attributeReturnString = workingLine.toString();
							
							System.out.println("Attributes: " + attributeReturnString); 
							
							
						} else {
							String tmpAttributes = workingLine.toString();
							tmpAttributes = tmpAttributes.replaceAll(" ", "_");
						
							attributeNames = tmpAttributes.split(";", 8);
							int y = 0;
						
							for (String a : attributeNames) { //check for ID presented as "ID" and update with table name
								attributeNames[y] = a.toLowerCase();
								y++;
								if (a.compareToIgnoreCase("ID") == 0){
									StringBuilder changeIDName = new StringBuilder();
									changeIDName = changeIDName.append(tableName.toLowerCase());
									changeIDName = changeIDName.deleteCharAt(changeIDName.length()-1);
									changeIDName = changeIDName.append("_id");
									a = changeIDName.toString();
									attributeNames[0] = a;
									//System.out.println("a: " + a);
								}
							}
							
							//make string with all attributes separated by a comma
							StringBuilder attributeStringBuilder = new StringBuilder();
							
							for (String a: attributeNames) {
								if (a.equals("speed_(km/h)")) {
									attributeStringBuilder.append("top_speed"); // a special case for TRAIN
								}
								else {
									attributeStringBuilder.append(a);
								}
								attributeStringBuilder.append(", ");
							}
							
							attributeStringBuilder.delete(attributeStringBuilder.length()-2, attributeStringBuilder.length());
							
							System.out.println("Attributes: " + attributeStringBuilder);
							
							//need to return string, not sb
							attributeReturnString = attributeStringBuilder.toString();
							
							
						}
						

					}
									
					
				} else { //currentLine is actual input data.
					
					//separate routes and raillines because they have different handling
					if ((tableName.compareToIgnoreCase("Routes") == 0) | (tableName.compareToIgnoreCase("Railroad_lines") == 0)){
						//if the data belongs to either Routes or Railroad_lines
						
						if(tableName.compareToIgnoreCase("Routes") == 0) {
							//format route data
							String[] tmpRouteStopsData = currentLine.split("Stations:"); //tmpRouteLineData[0] = Route: 22; tmpRouteLineData[1] = 1,2,3,... Stops: 1, 2, 3, ...
							
							String[] tmpRouteData = tmpRouteStopsData[0].split(": ");
							
							String routeNumber = tmpRouteData[1];
							routeNumber = routeNumber.replaceAll(" ", "");
							
							
							StringBuilder insertStatementRoute = new StringBuilder();
							insertStatementRoute.append("INSERT INTO ");
							insertStatementRoute.append(tableName);
							insertStatementRoute.append(" (");
							insertStatementRoute.append(attributeReturnString.toLowerCase());
							insertStatementRoute.append(") VALUES (");
							insertStatementRoute.append(routeNumber);
							insertStatementRoute.append(");");
							
							System.out.println(insertStatementRoute); //////////////////////////////////////////////////INSERT STATEMENT
							
							try {
								st.executeUpdate(insertStatementRoute.toString());	
							}
							catch (SQLException e) {
								System.out.println(e.toString());
							}
							
						
							String[] tmpStationStopsData = tmpRouteStopsData[1].split("Stops:");

							
							String[] tmpStopsData = tmpStationStopsData[1].split(","); //container of Stops that this route uses
							for(String a: tmpStopsData){
								a = a.replaceAll(" ", "");
							}
							
							tmpStopsData[tmpStopsData.length-1] = tmpStopsData[tmpStopsData.length-1].replaceAll(" ", "");
							
							String[] tmpStationsData = tmpStationStopsData[0].split(","); //container of Stations that this route passes
							for(String a: tmpStationsData){
								a = a.replaceAll(" ", "");
							}

							tmpStationsData[tmpStationsData.length-1] = tmpStationsData[tmpStationsData.length-1].replaceAll(" ", "");
							
							String stationNumber = null;
							String stopNumber = null;

							
							for(int w = 0; w <= tmpStationsData.length-1; w++) {
								boolean isStop = false;
								stationNumber = tmpStationsData[w];

								
								for(int x = 0; x <= tmpStopsData.length-1; x++){
								
									if (tmpStopsData[x].compareToIgnoreCase(tmpStationsData[w]) == 0){
										isStop = true;
										//break;
									}
								}
								
								StringBuilder insertStatementRouteInclude = new StringBuilder();
								insertStatementRouteInclude.append("INSERT INTO RouteInclude (route_id, station_id, stop) VALUES (");
								insertStatementRouteInclude.append(routeNumber);
								insertStatementRouteInclude.append(", ");
								insertStatementRouteInclude.append(stationNumber);
								insertStatementRouteInclude.append(", ");
								insertStatementRouteInclude.append(isStop);
								insertStatementRouteInclude.append(");");
								
								System.out.println(insertStatementRouteInclude); /////////////////////////////////////////////INSERT STATEMENT
								
								try {
									st.executeUpdate(insertStatementRouteInclude.toString());
								}
								catch (SQLException e) {
									System.out.println(e.toString());
								}
								
							}
								
						}
						
						if(tableName.compareToIgnoreCase("Railroad_lines") == 0){
							//format railroad lines data
							String[] tmpRRLineData = currentLine.split("Stations:"); //tmpRRLineData[0] = LineID and Speed Limit; tmpRRLineData[1] = Station and Distance List

							
							String[] tmpLineIDSpeedLimit = tmpRRLineData[0].split(" ");

							
							String lineID = (tmpLineIDSpeedLimit[2]);
							String speedLimit = (tmpLineIDSpeedLimit[5]);
							
							StringBuilder rrLineInfo = new StringBuilder();
							rrLineInfo.append("(");
							rrLineInfo.append(lineID);
							rrLineInfo.append(", ");
							rrLineInfo.append(speedLimit);
							rrLineInfo.append(")");
							
							String rrLineInfoString = rrLineInfo.toString();
							
							StringBuilder insertStatementRR = new StringBuilder();
							insertStatementRR.append("INSERT INTO ");
							insertStatementRR.append(tableName);
							insertStatementRR.append(" (");
							insertStatementRR.append(attributeReturnString.toLowerCase());
							insertStatementRR.append(") VALUES ");
							insertStatementRR.append(rrLineInfoString);
							insertStatementRR.append(";");
							
							System.out.println(insertStatementRR); /////////////////////////////////////////INSERT STATEMENT
							
							try {
								st.executeUpdate(insertStatementRR.toString());
							}
							catch (SQLException e) {
								System.out.println(e.toString());
							}
							
							
							String[] tmpStationDistances = tmpRRLineData[1].split("Distances:"); //tmpStationDistances[0] = Stations; tmpStationDistances[1] = Distances
							

							String[] tmpStations = tmpStationDistances[0].split(",");
							
							
							for (String a: tmpStations){  //container of stations
								a = a.replaceAll(" ", "");
							}

							String[] tmpDistances = tmpStationDistances[1].split(",");
						
						
							for (String a: tmpDistances){ //container of distances
								a = a.replaceAll(" ", "");
						
							}
							
							String stationA = null;
							String stationB = null;
							String distance = null;
							String startStation = tmpStations[0];
							
							
							for(int k = 0; k < tmpStations.length-1; k++){

								stationA = tmpStations[k];
								stationB = tmpStations[k+1];
								distance = tmpDistances[k+1];
								
								/* removing this part to update LineInclude table only, and remove Distances for easier access
								StringBuilder insertStatementDistances = new StringBuilder();
								insertStatementDistances.append("INSERT INTO Distance (station_a, station_b, miles) VALUES (");
								insertStatementDistances.append(stationA);
								insertStatementDistances.append(", ");
								insertStatementDistances.append(stationB);
								insertStatementDistances.append(", ");
								insertStatementDistances.append(distance);
								insertStatementDistances.append(");");
								
								System.out.println(insertStatementDistances); ////////////////////////////////////////////INSERT STATEMENT
								
								try {
									st.executeUpdate(insertStatementDistances.toString());
								}
								catch (SQLException e) {
									System.out.println(e.toString());
								}
								*/
								
								StringBuilder insertStatementLineInclude = new StringBuilder();
								insertStatementLineInclude.append("INSERT INTO LineInclude (station_a, station_b, line_id, miles) VALUES (");
								insertStatementLineInclude.append(stationA);
								insertStatementLineInclude.append(", ");
								insertStatementLineInclude.append(stationB);
								insertStatementLineInclude.append(", ");
								insertStatementLineInclude.append(lineID);
								insertStatementLineInclude.append(", ");
								insertStatementLineInclude.append(distance);
								insertStatementLineInclude.append(");");
								
								System.out.println(insertStatementLineInclude); //////////////////////////////////////////////INSERT STATEMENT
								
								try {
									st.executeUpdate(insertStatementLineInclude.toString());
								}
								catch (SQLException e) {
									System.out.println(e.toString());
								}
								
							}
							
							
						}
						
						
					} else {
						//everything else
						String dataString = null;	
						
						if (tableName.compareToIgnoreCase("Stations") == 0) { //format for stations
							//System.out.println("Station format:");
							String[] formattedStations = currentLine.split(";"); //container of strings to be formatted
							
							//for (String a: formattedStations) { //container of stationData to be formatted
								//System.out.println(a);
							//}
							
							StringBuilder formattedStationsSB = new StringBuilder();
							formattedStationsSB.append(formattedStations[0] + ", '" + formattedStations[1] + "', '" + formattedStations[2] + "', '");
							formattedStationsSB.append(formattedStations[3] + "', " + formattedStations[4] + ", '" + formattedStations[5] + "', '");
							formattedStationsSB.append(formattedStations[6] + "', '" + formattedStations[7] + "'");
							
							//System.out.println(formattedStationsSB);
							dataString = formattedStationsSB.toString();
							
							
						} else if (tableName.compareToIgnoreCase("Customers") == 0) {
							//System.out.println("Customer format:");
							
							String[] formattedCustomers = currentLine.split(";");
							
							StringBuilder formattedCustomersSB = new StringBuilder();
							formattedCustomersSB.append(formattedCustomers[0] + ", '" + formattedCustomers[1] + "', '" + formattedCustomers[2] + "', '");
							formattedCustomersSB.append(formattedCustomers[3] + "', '" + formattedCustomers[4] + "', '" + formattedCustomers[5] + "'");
							
							//System.out.println(formattedCustomersSB);
							dataString = formattedCustomersSB.toString();
							
						} else if (tableName.compareToIgnoreCase("Trains") == 0){
							//System.out.println("Trains format:");
							String[] formattedTrains = currentLine.split(";");

							int disConversion = Integer.parseInt(formattedTrains[5]);

							BigDecimal bd = new BigDecimal(disConversion * (.621371));
							bd = bd.setScale(2, RoundingMode.CEILING); //ROUNDED UP

							double kmtomilesConversion = bd.doubleValue();
							
							StringBuilder formattedTrainsSB = new StringBuilder();
							formattedTrainsSB.append(formattedTrains[0] + ", '" + formattedTrains[1] + "', '" + formattedTrains[2] + "', ");
							formattedTrainsSB.append(formattedTrains[3] + ", " + formattedTrains[4] + ", " + String.valueOf(kmtomilesConversion));
							
							//System.out.println(formattedTrainsSB);
							dataString = formattedTrainsSB.toString();

							StringBuilder newAttribute = new StringBuilder();
							newAttribute.append("train_id, name, description, seats, top_speed, cost_per_mile");
							attributeReturnString = newAttribute.toString();
							
							
						} else if (tableName.compareToIgnoreCase("Route_Schedules") == 0){
							//System.out.println("Route_Schedules format:");
							
							String[] formattedRouteSchedules = currentLine.split(";");
							
							StringBuilder formattedRouteSchedulesSB = new StringBuilder();
							formattedRouteSchedulesSB.append(formattedRouteSchedules[0] + ", '" + formattedRouteSchedules[1] + "', '" + formattedRouteSchedules[2] + "', ");
							formattedRouteSchedulesSB.append(formattedRouteSchedules[3]);
							
							//System.out.println(formattedRouteSchedulesSB);
							dataString = formattedRouteSchedulesSB.toString();
							
							
						} else {
							System.out.println("Something is wrong..");
						}
						
						
						
						
						currentLine = currentLine.replaceAll(";", ", ");

						StringBuilder insertStatement = new StringBuilder();
						insertStatement.append("INSERT INTO");
						insertStatement.append(" ");
						insertStatement.append(tableName);
						insertStatement.append(" (");
						insertStatement.append(attributeReturnString);
						insertStatement.append(") VALUES (");
						insertStatement.append(dataString);
						insertStatement.append(");");
						
						System.out.println(insertStatement); ////////////////////////////////////////////////INSERTSTATEMENT
						
						try {
							st.executeUpdate(insertStatement.toString());
						}
						catch (SQLException e) {
								System.out.println(e.toString());
						}
						
					}
					
					
				}
				
				
			} else { // if string is a line of ****, skip
				// do nothing, just here to remind me not to do anything
			}
			
			
			
			
		} //end while loop
		
		bufferedReader.close();
		
		/*
        try {
            conn.setAutoCommit(false);
            st.executeUpdate("INSERT INTO recitation9.student (sid, name, class, major) VALUES ('145', 'Marios', 3, 'CS');");
            st.executeUpdate("INSERT INTO recitation9.student (sid, name, class, major) VALUES ('156', 'Andreas', 3, 'CS');");
            conn.commit();
        } catch (SQLException e1) {
            try {
                conn.rollback();
            } catch (SQLException e2) {
                System.out.println(e2.toString());
            }
        }
		*/

    }


    public void connectionDB() {
        try {
            Class.forName("org.postgresql.Driver");
        }
        catch (ClassNotFoundException b) {
            System.out.println(b.toString());
        }
        
        String url = "jdbc:postgresql://localhost:5432/";
        Properties props = new Properties();

        if ((userp.equals("")) || (userp.equals(" "))) {
            //System.out.println("yes");
            props.setProperty("user", "postgres");
        } else {
            //System.out.println("no");
            props.setProperty("user", userp);
        }

        props.setProperty("password", passwordp); 
        
        try{
            conn = DriverManager.getConnection(url, props);
            st = conn.createStatement(); //SQL statement to run
        } 
        catch (SQLException c){
            System.out.println(c.toString());
        }
         
    }

}
