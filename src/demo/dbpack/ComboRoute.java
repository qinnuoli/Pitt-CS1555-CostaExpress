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

/* This Combination Route Trip Search finds all route combinations
   that stop at the specified Departure station and then at the 
   specified Destination station on the specified day of the week.
*/

public class ComboRoute extends JFrame implements ActionListener {
    
    JPanel comboCenter = new JPanel(new BorderLayout());
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

    public ComboRoute(JPanel gui, JPanel center, String password, String user){
        passwordp = password;
        userp = user;

        guiPush = gui;

        center.setVisible(false);

        comboCenter.setBackground(Color.BLACK);
        comboCenter.setBorder(new EmptyBorder(0, 20, 0, 20));
        gui.add(comboCenter, BorderLayout.CENTER);

        JPanel squish = new JPanel();
        squish.setBackground(Color.BLACK);

        JLabel headerMessage = new JLabel("Combination Route Search");

        headerMessage.setFont(new Font("Courier New", Font.BOLD, 40));
        headerMessage.setForeground(Color.WHITE);
        headerMessage.setVerticalAlignment(SwingConstants.BOTTOM);

        JPanel topMessage = new JPanel(new GridLayout(0,1));
        //topMessage.add(squish);
        topMessage.add(headerMessage);
        topMessage.setBackground(Color.BLACK);

        txtheader = new JTextArea("Day:   |  Time: | Rt: | Train:| Start Station: | End Station: | Avail. Seats:");
        txtheader.setBounds(10,30,200,200);
        txtheader.setBorder(new EmptyBorder(15, 100, 0, 0));
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

        comboCenter.add(topMessage, BorderLayout.PAGE_START);
        comboCenter.add(returnText, BorderLayout.CENTER);

        //topMessage.add(formPanel);
        comboCenter.add(formPanel, BorderLayout.PAGE_END);

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
            
            StringBuilder createViewRtStACombo = new StringBuilder();
            createViewRtStACombo.append("CREATE OR REPLACE VIEW rt_stA_combo AS SELECT * FROM RouteInclude");
            createViewRtStACombo.append(" WHERE station_id = '" + startA.getText() + "' AND ");
            createViewRtStACombo.append("stop = 'true';");

            StringBuilder createViewRtStBCombo = new StringBuilder();
            createViewRtStBCombo.append("CREATE OR REPLACE VIEW rt_stB_combo AS SELECT * FROM RouteInclude");
            createViewRtStBCombo.append(" WHERE station_id = '" + endB.getText() + "' AND ");
            createViewRtStBCombo.append("stop = 'true';");
            
            //System.out.println(createViewRtStACombo);
            //System.out.println(createViewRtStBCombo);

            StringBuilder rtABprocCall = new StringBuilder();
            rtABprocCall.append("CALL rtAB_comboProcedure();");
            //System.out.println(rtABprocCall);

            StringBuilder day_StAComboCall = new StringBuilder();
            day_StAComboCall.append("CREATE OR REPLACE VIEW day_StationA_combo AS ");
            day_StAComboCall.append("SELECT rtA_combo.routeID AS route, start_station, stopsatstation, day, time, train_id ");
            day_StAComboCall.append("FROM rtA_combo INNER JOIN Route_Schedules ON rtA_combo.routeID = Route_Schedules.routeid ");
            day_StAComboCall.append("AND Route_Schedules.day = '" + day.getText() + "';");

            StringBuilder day_StBComboCall = new StringBuilder();
            day_StBComboCall.append("CREATE OR REPLACE VIEW day_StationB_combo AS ");
            day_StBComboCall.append("SELECT rtB_combo.routeID AS route, end_station, stopsatstation, day, time, train_id ");
            day_StBComboCall.append("FROM rtB_combo INNER JOIN Route_Schedules ON rtB_combo.routeID = Route_Schedules.routeid ");
            day_StBComboCall.append("AND Route_Schedules.day = '" + day.getText() + "';");

            //System.out.println(day_StAComboCall.toString());
            //System.out.println(day_StBComboCall.toString());

            StringBuilder comboMatchCall = new StringBuilder();
            comboMatchCall.append("CALL rtAB_comboMatch();");
            System.out.println(comboMatchCall.toString());

            try { 
                try{
                    st.executeUpdate(createViewRtStACombo.toString());
                    st.executeUpdate(createViewRtStBCombo.toString());
                    st.executeUpdate(rtABprocCall.toString());
                    st.executeUpdate(day_StAComboCall.toString());
                    st.executeUpdate(day_StBComboCall.toString());
                    st.executeUpdate(comboMatchCall.toString());

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

                StringBuilder lowestPriceRequestc = new StringBuilder();
                lowestPriceRequestc.append("SELECT * FROM combobyprice;");

                try { 
                    try{
                        resultSet = st.executeQuery(lowestPriceRequestc.toString());
                    } catch (NullPointerException np1){
                        System.out.println(np1);
                    }
                   
                } catch (SQLException e1) {
                    System.out.println(e1);
                }
                

            } else if (searchOptionSelected.equals("Shortest Time")) {
                System.out.println("time selected");
                returnText.setText("");

                StringBuilder lowestPriceRequestc = new StringBuilder();
                lowestPriceRequestc.append("SELECT * FROM combobytime;");

                try { 
                    try{
                        resultSet = st.executeQuery(lowestPriceRequestc.toString());
                    } catch (NullPointerException np1){
                        System.out.println(np1);
                    }
                   
                } catch (SQLException e1) {
                    System.out.println(e1);
                }
                
                

            } else if (searchOptionSelected.equals("Fewest Stops")){
                System.out.println("stops selected");
                returnText.setText("");

                StringBuilder lowestPriceRequestc = new StringBuilder();
                lowestPriceRequestc.append("SELECT * FROM combobystops;");

                try { 
                    try{
                        resultSet = st.executeQuery(lowestPriceRequestc.toString());
                    } catch (NullPointerException np1){
                        System.out.println(np1);
                    }
                   
                } catch (SQLException e1) {
                    System.out.println(e1);
                }
                



            } else if (searchOptionSelected.equals("Fewest Stations")){
                System.out.println("stations selected");
                returnText.setText("");

                StringBuilder lowestPriceRequestc = new StringBuilder();
                lowestPriceRequestc.append("SELECT * FROM combobystations;");

                try { 
                    try{
                        resultSet = st.executeQuery(lowestPriceRequestc.toString());
                    } catch (NullPointerException np1){
                        System.out.println(np1);
                    }
                   
                } catch (SQLException e1) {
                    System.out.println(e1);
                }
                

                
            }

            String returnDay = day.getText(); //day
            String returnTimeA = "error"; //starttime
            String returnStart = startA.getText(); //startstation
            String returnRouteA = "error"; //routea
            String returnTrainIDA = "error"; //traina
            String returnSeatsA = "error"; //trainAseatcount
            String returnLayover = "error"; //layover
            String returnTimeB = "error"; //endtime
            String returnRouteB = "error";//routeb
            String returnTrainB = "error"; //trainb
            String returnSeatsB = "error"; //seat_count (trainb)
            String returnEnd = endB.getText(); // endstation
        
    
            
            int i = 0;
            do {
                try{
                    resultSet.next();
                    
                    returnLayover = resultSet.getString("layover");
                    returnDay = resultSet.getString("day");
                    returnTimeA = resultSet.getString("starttime");
                    returnStart = resultSet.getString("startstation");
                    returnRouteA = resultSet.getString("routea");
                    returnTrainIDA = resultSet.getString("traina");
                    returnSeatsA = resultSet.getString("trainaseatcount");
                    
                    returnTimeB = resultSet.getString("endtime");
                    returnRouteB = resultSet.getString("routeb");
                    returnTrainB = resultSet.getString("trainb");
                    returnEnd = resultSet.getString("endstation");
                    returnSeatsB = resultSet.getString("trainbseatcount");
                    
                    } catch (SQLException r){
                        System.out.println(r);
                    }
                    
                    String str1 = String.format("%-7s|", returnDay);
                    String str2 = String.format("%-5s|   ", returnTimeA);
                    String str3 = String.format("%-5s|  ", returnRouteA);
                    String str4 = String.format("%-5s|     ", returnTrainIDA);
                    String str5 = String.format("%-5s|     ", returnStart);
                    String str6 = String.format("%-8s|   ", returnLayover);
                    String str7 = String.format("%-5s\n", returnSeatsA);

                    String str14 = String.format("%-7s|", returnDay);
                    String str8 = String.format("%-5s|   ", returnTimeB);
                    String str9 = String.format("%-5s|  ", returnRouteB);
                    String str10 = String.format("%-5s|     ", returnTrainB);
                    String str11 = String.format("%-5s|     ", returnLayover);
                    String str12 = String.format("%-8s|   ", returnEnd);
                    String str13 = String.format("%-5s\n", returnSeatsB);
                    

                    returnText.append("1st : " + str1 + str2 + str3 + str4 + str5 + str6 + str7);
                    returnText.append("2nd : " + str14 + str8 + str9 + str10 + str11 + str12 + str13 + "\n"); 
                    i++;
            } while (i < 6);
            

        } else if (source == backButton){
            SearchDatabase agentS = new SearchDatabase(guiPush, comboCenter, passwordp, userp);
        } else if (source == nextButton) {
            int i = 0;
            String returnDay = day.getText(); //day
            String returnTimeA = "error"; //starttime
            String returnStart = startA.getText(); //startstation
            String returnRouteA = "error"; //routea
            String returnTrainIDA = "error"; //traina
            String returnSeatsA = "error"; //trainAseatcount
            String returnLayover = "error"; //layover
            String returnTimeB = "error"; //endtime
            String returnRouteB = "error";//routeb
            String returnTrainB = "error"; //trainb
            String returnSeatsB = "error"; //seat_count (trainb)
            String returnEnd = endB.getText(); // endstation

            if (resultSet != null){
                returnText.setText("");
                do {
                    try{
                        if (resultSet.next() == false){
                            break;
                        }
                        resultSet.next();

                        returnLayover = resultSet.getString("layover");
                        returnDay = resultSet.getString("day");
                        returnTimeA = resultSet.getString("starttime");
                        returnStart = resultSet.getString("startstation");
                        returnRouteA = resultSet.getString("routea");
                        returnTrainIDA = resultSet.getString("traina");
                        returnSeatsA = resultSet.getString("trainaseatcount");
                    
                        returnTimeB = resultSet.getString("endtime");
                        returnRouteB = resultSet.getString("routeb");
                        returnTrainB = resultSet.getString("trainb");
                        returnEnd = resultSet.getString("endstation");
                        returnSeatsB = resultSet.getString("trainbseatcount");
                        } catch (SQLException r){
                            System.out.println(r);
                        }
    
               
                        String str1 = String.format("%-7s|", returnDay);
                        String str2 = String.format("%-5s|   ", returnTimeA);
                        String str3 = String.format("%-5s|  ", returnRouteA);
                        String str4 = String.format("%-5s|     ", returnTrainIDA);
                        String str5 = String.format("%-5s|     ", returnStart);
                        String str6 = String.format("%-8s|   ", returnLayover);
                        String str7 = String.format("%-5s\n", returnSeatsA);

                        String str14 = String.format("%-7s|", returnDay);
                        String str8 = String.format("%-5s|   ", returnTimeB);
                        String str9 = String.format("%-5s|  ", returnRouteB);
                        String str10 = String.format("%-5s|     ", returnTrainB);
                        String str11 = String.format("%-5s|     ", returnLayover);
                        String str12 = String.format("%-8s|   ", returnEnd);
                        String str13 = String.format("%-5s\n", returnSeatsB);
                    

                        returnText.append("1st : " + str1 + str2 + str3 + str4 + str5 + str6 + str7);
                        returnText.append("2nd : " + str14 + str8 + str9 + str10 + str11 + str12 + str13 + "\n"); 
                        i++;
                } while (i < 6);
            }

            
        }



    }

}
