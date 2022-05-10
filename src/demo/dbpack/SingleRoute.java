package dbpack;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
//import javax.swing.text.html.FormSubmitEvent;

//import javafx.print.PageLayout;

//import javafx.scene.control.ButtonBase;

import java.awt.event.*;
//import java.math.BigDecimal;
//import java.math.RoundingMode;
import java.sql.*;
//import java.time.format.TextStyle;
import java.util.Properties;

/* This Single Route Trip Search finds all routes that stop 
   at the specified Departure station and then at the 
   specified Destination station on the specified day of the week.
*/

public class SingleRoute extends JFrame implements ActionListener {
    JPanel singleCenter = new JPanel(new BorderLayout());
    JButton searchButton = new JButton("Search");
    JButton backButton = new JButton("Back");
    JButton nextButton = new JButton("Next >");

    JComboBox<String> orderByList;

    JPanel guiPush;
    Statement st;

    JTextField startA;
    JTextField endB;
    JTextField day;

    ResultSet resultSet;

    JTextArea returnText;
    JTextArea txtheader;

    String passwordp = "";
    String userp = "";

    Connection conn;

    public SingleRoute(JPanel gui, JPanel center, String password, String user){
        passwordp = password;
        userp = user;

        guiPush = gui;

        center.setVisible(false);

        singleCenter.setBackground(Color.BLACK);
        singleCenter.setBorder(new EmptyBorder(0, 20, 0, 20));
        gui.add(singleCenter, BorderLayout.CENTER);

        JPanel squish = new JPanel();
        squish.setBackground(Color.BLACK);

        JLabel headerMessage = new JLabel("Single Route Search");

        headerMessage.setFont(new Font("Courier New", Font.BOLD, 40));
        headerMessage.setForeground(Color.WHITE);
        headerMessage.setVerticalAlignment(SwingConstants.BOTTOM);

        JPanel topMessage = new JPanel(new GridLayout(0,1));
        //topMessage.add(squish);
        topMessage.add(headerMessage);
        topMessage.setBackground(Color.BLACK);

        txtheader = new JTextArea("Route: |  Time: |   Day:  |  TrainID: | Seats: | Start: | End:");
        txtheader.setBounds(10,30,200,200);
        txtheader.setBorder(new EmptyBorder(15, 10, 0, 0));
        txtheader.setBackground(Color.BLUE);
        txtheader.setForeground(Color.WHITE);
        txtheader.setFont(new Font("Courier New", Font.BOLD, 20));
        topMessage.add(txtheader);

        buttonStyle(searchButton);
        buttonStyle(backButton);
        buttonStyle(nextButton);
        addActionEvent();

        JLabel stA = new JLabel("Start Station(as ID):", SwingConstants.RIGHT);
        JLabel stB = new JLabel("End Station(as ID):", SwingConstants.RIGHT);
        JLabel dy = new JLabel("Day:", SwingConstants.RIGHT);
        JLabel ord = new JLabel("Order By:", SwingConstants.RIGHT);

        textStyle(stA);
        textStyle(stB);
        textStyle(dy);
        textStyle(ord);

        String[] searchParameters = {"Lowest Price", "Shortest Time", "Fewest Stops", "Fewest Stations"};
        orderByList = new JComboBox<String>();


        for (int i = 0; i < searchParameters.length; i++){
            orderByList.addItem(searchParameters[i]);
        }

        orderByList.setBackground(Color.BLACK);
        orderByList.setForeground(Color.WHITE);
        orderByList.setFont(new Font("Courier New", Font.BOLD, 20));

        startA = new JTextField();
        endB = new JTextField();
        day = new JTextField();

        formStyle(startA);
        formStyle(endB);
        formStyle(day);

        JPanel formPanel = new JPanel(new GridLayout(0,2));
        //add to form panel

        JPanel squish2 = new JPanel();
        squish2.setBackground(Color.BLACK);
        JPanel squish16 = new JPanel();
        squish16.setBackground(Color.BLACK);
       
        formPanel.add(squish16);
        formPanel.add(nextButton);
        formPanel.add(stA);
        formPanel.add(startA);
        formPanel.add(stB);
        formPanel.add(endB);
        formPanel.add(dy);
        formPanel.add(day);
        formPanel.add(ord);
        formPanel.add(orderByList);
        formPanel.add(backButton);
        formPanel.add(searchButton);

        formPanel.setBackground(Color.BLACK);

        returnText = new JTextArea();
        returnText.setBounds(10,30,200,200);
        returnText.setBorder(new EmptyBorder(0, 10, 0, 0));
        returnText.setBackground(Color.BLUE);
        returnText.setForeground(Color.WHITE);
        returnText.setFont(new Font("Courier New", Font.BOLD, 20));

        singleCenter.add(topMessage, BorderLayout.PAGE_START);
        singleCenter.add(returnText, BorderLayout.CENTER);

        //topMessage.add(formPanel);
        singleCenter.add(formPanel, BorderLayout.PAGE_END);

    }

    private static void textStyle(JLabel b){
        b.setForeground(Color.WHITE);
        //b.setBackground(Color.BLACK);
        b.setFont(new Font("Courier New", Font.BOLD, 20));
    }

    private static void formStyle(JTextField b){
        b.setBackground(Color.BLACK);
        b.setFont(new Font("Courier New", Font.BOLD, 18));
        b.setForeground(Color.WHITE);
    }

    private static void buttonStyle(JButton b){
        b.setForeground(Color.WHITE);
        b.setBackground(Color.BLACK);
        b.setBorder(new LineBorder(Color.WHITE));
        b.setFont(new Font("Courier New", Font.BOLD, 20));
    }

    // sets the program to listen for clicks on any of these buttons
    public void addActionEvent(){
        searchButton.addActionListener(this);
        backButton.addActionListener(this);
        nextButton.addActionListener(this);
    }

    // Necessary for connection to db
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

    public void actionPerformed(ActionEvent e)  {
        Object source = e.getSource();  

        if (source == searchButton) {
            connectionDB();

            String searchOptionSelected = orderByList.getSelectedItem().toString();

            StringBuilder createViewA = new StringBuilder();
            createViewA.append("CREATE OR REPLACE VIEW rt_stA AS SELECT * FROM RouteInclude ");
            createViewA.append("WHERE station_id = '" + startA.getText() + "' AND stop = 'true';");

            StringBuilder createViewB = new StringBuilder();
            createViewB.append("CREATE OR REPLACE VIEW rt_stB AS SELECT * FROM RouteInclude ");
            createViewB.append("WHERE station_id = '" + endB.getText() + "' AND stop = 'true';");

            System.out.println(createViewA);
            System.out.println(createViewB);

            StringBuilder singleRTF = new StringBuilder();
            singleRTF.append("CALL singleRouteTripFunction();");

            System.out.println(singleRTF);

            StringBuilder createSDM = new StringBuilder();
            createSDM.append("CREATE OR REPLACE VIEW stopDayMatch AS SELECT twoStopMatch.routeID ");
            createSDM.append("AS routeID, stationa, stationb, day, time, train_id FROM twoStopMatch ");
            createSDM.append("INNER JOIN Route_Schedules ON twoStopMatch.routeID = Route_Schedules.routeid ");
            createSDM.append("AND day = '" + day.getText() + "';");

            System.out.println(createSDM);

            StringBuilder rtwithseats = new StringBuilder();
            rtwithseats.append("CALL routeWithSeatsFunction();");

            System.out.println(rtwithseats);

            try { 
                try{
                    st.executeUpdate(createViewA.toString());
                    st.executeUpdate(createViewB.toString());
                    st.executeUpdate(singleRTF.toString());
                    st.executeUpdate(createSDM.toString());
                    st.executeUpdate(rtwithseats.toString());

                } catch (NullPointerException np1){
                    System.out.println(np1);
                }
               
            } catch (SQLException e1) {
                System.out.println(e1);
            }

            resultSet = null;

            if (searchOptionSelected.equals("Lowest Price")) {
                System.out.println("Lowest Price selected");
                returnText.setText("");

                StringBuilder lowestPriceRequest = new StringBuilder();
                lowestPriceRequest.append("SELECT * FROM priceFunctionality;");

                try { 
                    try{
                        resultSet = st.executeQuery(lowestPriceRequest.toString());
                    } catch (NullPointerException np1){
                        System.out.println(np1);
                    }
                   
                } catch (SQLException e1) {
                    System.out.println(e1);
                }

            } else if (searchOptionSelected.equals("Shortest Time")) {
                System.out.println("time selected");
                returnText.setText("");
                

                StringBuilder shortestTimeRequest = new StringBuilder();
                shortestTimeRequest.append("SELECT * FROM timeFunctionality;");

                try { 
                    try{
                        resultSet = st.executeQuery(shortestTimeRequest.toString());
                    } catch (NullPointerException np1){
                        System.out.println(np1);
                    }
                   
                } catch (SQLException e1) {
                    System.out.println(e1);
                }

            } else if (searchOptionSelected.equals("Fewest Stops")){
                System.out.println("stops selected");
                returnText.setText("");

                StringBuilder fewestStopsRequest = new StringBuilder();
                fewestStopsRequest.append("SELECT * FROM fewestStopsView;");

                try { 
                    try{
                        resultSet = st.executeQuery(fewestStopsRequest.toString());
                    } catch (NullPointerException np1){
                        System.out.println(np1);
                    }
                   
                } catch (SQLException e1) {
                    System.out.println(e1);
                }



            } else if (searchOptionSelected.equals("Fewest Stations")){
                System.out.println("stations selected");
                returnText.setText("");

                StringBuilder fewestStationsRequest = new StringBuilder();
                fewestStationsRequest.append("SELECT * FROM fewestStationsView;");

                try { 
                    try{
                        resultSet = st.executeQuery(fewestStationsRequest.toString());
                    } catch (NullPointerException np1){
                        System.out.println(np1);
                    }
                   
                } catch (SQLException e1) {
                    System.out.println(e1);
                }
            }

            String returnRoute = "error";
            String returnTime = "error";
            String returnDay = "error";
            String returnTrainID = "error";
            String returnSeats = "error";
            String returnStart = startA.getText();
            String returnEnd = endB.getText();
    
            
            int i = 0;
            do {
                try{
                    resultSet.next();
                    
                    returnRoute = resultSet.getString("routeid");
                    returnTime = resultSet.getString("time");
                    returnDay = resultSet.getString("day");
                    returnTrainID = resultSet.getString("train_id");
                    returnSeats = resultSet.getString("seat_count");
                    returnStart = resultSet.getString("stationa");
                    returnEnd = resultSet.getString("stationb");

                } catch (SQLException r){
                        System.out.println(r);
                }

                    StringBuilder sb = new StringBuilder();
                    String str1 = String.format("%-7s|", returnRoute);
                    String str2 = String.format("%-5s|", returnTime);
                    String str3 = String.format("%-5s|   ", returnDay);
                    String str4 = String.format("%-8s|   ", returnTrainID);
                    String str5 = String.format("%-5s|   ", returnSeats);
                    String str6 = String.format("%-5s|   ", returnStart);
                    String str7 = String.format("%-5s\n", returnEnd);

                    returnText.append(str1 + str2 + str3 + str4 + str5 + str6 + str7); 
                    i++;
            } while (i < 10);
            

        } else if (source == backButton){
            SearchDatabase agentS = new SearchDatabase(guiPush, singleCenter, passwordp, userp);
        } else if (source == nextButton) {
            int i = 0;
            String returnRoute = "error";
            String returnTime = "error";
            String returnDay = "error";
            String returnTrainID = "error";
            String returnSeats = "error";
            String returnStart = startA.getText();
            String returnEnd = endB.getText();

            if (resultSet != null){
                returnText.setText("");
                do {
                    try{
                        if (resultSet.next() == false){
                            break;
                        }
                        resultSet.next();

                        returnRoute = resultSet.getString("routeid");
                        returnTime = resultSet.getString("time");
                        returnDay = resultSet.getString("day");
                        returnTrainID = resultSet.getString("train_id");
                        returnSeats = resultSet.getString("seat_count");
                        returnStart = resultSet.getString("stationa");
                        returnEnd = resultSet.getString("stationb");
                        } catch (SQLException r){
                            System.out.println(r);
                        }
    
                        StringBuilder sb = new StringBuilder();
                        String str1 = String.format("%-7s|", returnRoute);
                        String str2 = String.format("%-5s|", returnTime);
                        String str3 = String.format("%-5s|   ", returnDay);
                        String str4 = String.format("%-8s|   ", returnTrainID);
                        String str5 = String.format("%-5s|   ", returnSeats);
                        String str6 = String.format("%-5s|   ", returnStart);
                        String str7 = String.format("%-5s\n", returnEnd);
    
                        returnText.append(str1 + str2 + str3 + str4 + str5 + str6 + str7); 
                        i++;
                } while (i < 10);
            }

            
        }



    }
    
}
