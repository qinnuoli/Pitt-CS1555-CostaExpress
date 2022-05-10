package dbpack;
import dbpack.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.event.*;
import javax.swing.UIManager;
import java.util.Properties;
import java.sql.*;
//import org.postgresql.copy.CopyManager;
//import org.postgresql.core.BaseConnection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;


public class AdminScreen extends JFrame implements ActionListener {
    
    JPanel adminCenter = new JPanel(new GridLayout(0,1));
    JPanel topMessage = new JPanel(new GridLayout(0,1));

    JLabel fileName;

    Connection conn;
//    CopyManager copyManager;
    Statement st;
    JTextField inputFileName;

    JComboBox<String> selectList;

    JButton importButton = new JButton("Import"); 
    JButton exportButton = new JButton("Export"); 
    JButton delTabButton = new JButton("Delete Database");
    JButton exitButton = new JButton("Logout"); //Log out of program

    JPanel guiPush;
    JPanel centerPush;

    String passwordp;
    String userp = "";

    public AdminScreen(JPanel gui, JPanel center, String password, String user) {

        passwordp = password;
        userp = user;

        center.setVisible(false);

        adminCenter.setBackground(Color.BLACK);
        adminCenter.setBorder(new EmptyBorder(30, 20, 50, 20));
        gui.add(adminCenter, BorderLayout.CENTER);

        JLabel welcomeMessage = new JLabel("Welcome!", SwingConstants.CENTER);
        welcomeMessage.setFont(new Font("Courier New", Font.BOLD, 40));
        welcomeMessage.setForeground(Color.WHITE);

        welcomeMessage.setVerticalAlignment(SwingConstants.BOTTOM);

        //adminCenter.add(welcomeMessage);
        topMessage.add(welcomeMessage);
        topMessage.setBackground(Color.BLACK);

        JLabel advise = new JLabel("Please select a menu option...", SwingConstants.CENTER);
        advise.setFont(new Font("Courier New", Font.BOLD, 20));
        advise.setForeground(Color.WHITE);

        advise.setVerticalAlignment(SwingConstants.TOP);

        topMessage.add(advise);

        adminCenter.add(topMessage);

        JPanel adminButtonPanel = new JPanel(new GridLayout(0,1));
        adminButtonPanel.setBackground(Color.BLACK);
        adminButtonPanel.setBorder(new EmptyBorder(0, 250, 0, 250));

        buttonStyle(importButton);
        buttonStyle(exportButton);
        buttonStyle(delTabButton);
        buttonStyle(exitButton);

        JPanel adminButtonPanel1 = new JPanel(new GridLayout(0,2));
        adminButtonPanel1.setBackground(Color.BLACK);
        adminButtonPanel1.setBorder(new EmptyBorder(0, 250, 0, 250));

        String[] searchStrings = {"AllData.txt", "Customers", "Clock", "LineInclude", "Railroad_lines", "Reservations", "Route_Schedules", "RouteInclude", "Routes", "Stations", "Stop_Seatcount", "Tickets", "Trains"};
        selectList = new JComboBox<String>();

        for (int i = 0; i< searchStrings.length; i++){
            selectList.addItem(searchStrings[i]);
        }

        selectList.setBackground(Color.BLACK);
        selectList.setForeground(Color.WHITE);
        selectList.setFont(new Font("Courier New", Font.BOLD, 20));

        JLabel tableName = new JLabel("Name:", SwingConstants.RIGHT);
        fileName = new JLabel("File Name:", SwingConstants.RIGHT);
        inputFileName = new JTextField();

        formStyle(inputFileName);
        textStyle(tableName);
        textStyle(fileName);

        JPanel squish32 = new JPanel();
        squish32.setBackground(Color.BLACK);

        adminButtonPanel1.add(tableName);
        adminButtonPanel1.add(selectList);
        adminButtonPanel1.add(fileName);
        adminButtonPanel1.add(inputFileName);
        adminButtonPanel1.add(squish32);
        adminButtonPanel1.add(importButton);
        adminButtonPanel.add(exportButton);
        adminButtonPanel.add(delTabButton);
        adminButtonPanel.add(exitButton);

        adminCenter.add(adminButtonPanel1);
        adminCenter.add(adminButtonPanel);

        JPanel squish = new JPanel();
        squish.setBackground(Color.BLACK);
    //   JPanel squish2 = new JPanel();

        adminCenter.add(squish);
     // adminCenter.add(squish2);

        guiPush = gui;
        centerPush = adminCenter;

        addActionEvent();

    }

    private static void buttonStyle(JButton b){
        b.setForeground(Color.WHITE);
        b.setBackground(Color.BLACK);
        b.setBorder(new LineBorder(Color.WHITE));
        b.setFont(new Font("Courier New", Font.BOLD, 20));
    }

    public void addActionEvent(){
        importButton.addActionListener(this);
        exportButton.addActionListener(this);
        delTabButton.addActionListener(this);
        exitButton.addActionListener(this);
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
 //           copyManager =  new CopyManager((BaseConnection) conn);
            st = conn.createStatement(); //SQL statement to run
        } 
        catch (SQLException c){
            System.out.println(c.toString());
        }
             
    }


    public void actionPerformed(ActionEvent e){
        Object source = e.getSource();
        //String inputRes = null;

        if (source == importButton){ //import
            connectionDB();

            StringBuilder importStatement = new StringBuilder();

            String searchOptionSelected = selectList.getSelectedItem().toString();

            if (searchOptionSelected.equals("AllData.txt")) {
                try {
                    ImportAllText newImport = new ImportAllText(passwordp, userp);
                } catch (ClassNotFoundException | SQLException | IOException e1) {
                    e1.printStackTrace();
                }

            } else if (searchOptionSelected.equals("Customers")){
                
                importStatement.append("COPY Customers FROM '" + inputFileName.getText() + "' ( DELIMITER'|');");

                System.out.println(importStatement);
            } else if (searchOptionSelected.equals("Clock")){

                importStatement.append("COPY Clock FROM '" + inputFileName.getText() + "' ( DELIMITER'|');");
            } else if (searchOptionSelected.equals("LineInclude")){

                importStatement.append("COPY LineInclude FROM '" + inputFileName.getText() + "' ( DELIMITER'|');");
            } else if (searchOptionSelected.equals("Railroad_lines")){
                importStatement.append("COPY Railroad_lines FROM '" + inputFileName.getText() + "' ( DELIMITER'|');");
            } else if (searchOptionSelected.equals("Reservations")){
                importStatement.append("COPY Reservations FROM '" + inputFileName.getText() + "' ( DELIMITER'|');");
            } else if (searchOptionSelected.equals("Route_Schedules")){
                importStatement.append("COPY Route_Schedules FROM '" + inputFileName.getText() + "' ( DELIMITER'|');");
            } else if (searchOptionSelected.equals("RouteInclude")){
                importStatement.append("COPY RouteInclude FROM '" + inputFileName.getText() + "' ( DELIMITER'|');");
            } else if (searchOptionSelected.equals("Routes")){
                importStatement.append("COPY Routes FROM '" + inputFileName.getText() + "' ( DELIMITER'|');");
            } else if (searchOptionSelected.equals("Stations")){
                importStatement.append("COPY Stations FROM '" + inputFileName.getText() + "' ( DELIMITER'|');");
            } else if (searchOptionSelected.equals("Stop_Seatcount")){
                importStatement.append("COPY Stop_Seatcount FROM '" + inputFileName.getText() + "' ( DELIMITER'|');");
            } else if (searchOptionSelected.equals("Tickets")){
                importStatement.append("COPY Tickets FROM '" + inputFileName.getText() + "' ( DELIMITER'|');");
            } else if (searchOptionSelected.equals("Trains")){
                importStatement.append("COPY Trains FROM '" + inputFileName.getText() + "' ( DELIMITER'|');");
            } 

            try { 
                try{
                    st.executeUpdate(importStatement.toString());

                } catch (NullPointerException np1){
                    System.out.println(np1);
                }
               
            } catch (SQLException e1) {
                System.out.println(e1);
            }

            
        } else if (source == exportButton){ //export
            connectionDB();

            StringBuilder exportString = new StringBuilder();
            exportString.append("CALL exportData();");

            try { 
                try{
                    st.executeUpdate(exportString.toString());
                } catch (NullPointerException np1){
                    System.out.println(np1);
                }
               
            } catch (SQLException e1) {
                System.out.println(e1);
            }

            
        } else if (source == delTabButton){
            JFrame jFrame = new JFrame();
            int result = JOptionPane.showConfirmDialog(jFrame, "Are you sure you wish to delete all data?");
            

            if (result == 0){
                //System.out.println("You pressed Yes");
                StringBuilder deleteData = new StringBuilder();
                deleteData.append("CALL deleteData();");

                try { 
                    try{
                        st.executeUpdate(deleteData.toString());
    
                    } catch (NullPointerException np1){
                        System.out.println(np1);
                    }
                   
                } catch (SQLException e1) {
                    System.out.println(e1);
                }

            }  else {
               //do nothing
            }
            
            

        }  else if (source == exitButton){
            // return to login 
            AdminLogin agentP = new AdminLogin(guiPush, centerPush);

        } 


    }
}
