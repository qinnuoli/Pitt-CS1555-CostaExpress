package dbpack;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.event.*;
import java.sql.*;
import java.util.Properties;


public class AdvH extends JFrame implements ActionListener {

    JPanel advhCenter = new JPanel(new BorderLayout());
    JButton searchButton = new JButton("Search");
    JButton backButton = new JButton("Back");
    JButton nextButton = new JButton("Next >");

    JPanel guiPush;
    Statement st;

    JTextField routeTextField;


    ResultSet resultSet;

    JTextArea returnText;
    JTextArea txtheader;

    
    String passwordp = "";
    String userp = "";

    Connection conn;

    public AdvH(JPanel gui, JPanel center, String password, String user){
        passwordp = password;
        userp = user;

        guiPush = gui;

        center.setVisible(false);

        advhCenter.setBackground(Color.BLACK);
        advhCenter.setBorder(new EmptyBorder(0, 20, 0, 20));
        gui.add(advhCenter, BorderLayout.CENTER);

        JPanel squish = new JPanel();
        squish.setBackground(Color.BLACK);

        JLabel headerMessage = new JLabel("Advanced Search (H)");

        headerMessage.setFont(new Font("Courier New", Font.BOLD, 40));
        headerMessage.setForeground(Color.WHITE);
        headerMessage.setVerticalAlignment(SwingConstants.BOTTOM);

        JPanel topMessage = new JPanel(new GridLayout(0,1));
        //topMessage.add(squish);
        topMessage.add(headerMessage);
        topMessage.setBackground(Color.BLACK);

        JLabel ins = new JLabel("For a specified route, list the days of departure, departure hours and trains that run it.", SwingConstants.CENTER);
        topMessage.add(ins);

        txtheader = new JTextArea("Route_ID: | Departure_Date: | Departure_Hours: | Trains: ");
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


        JLabel routeLabel = new JLabel("Route(as ID):", SwingConstants.RIGHT);
        
        textStyle(routeLabel);
        textStyle(ins);
        ins.setFont(new Font("Courier New", Font.BOLD, 18));

        routeTextField = new JTextField();

        formStyle(routeTextField);
       

        JPanel formPanel = new JPanel(new GridLayout(0,2));
        //add to form panel

        JPanel squish2 = new JPanel();
        squish2.setBackground(Color.BLACK);
        JPanel squish16 = new JPanel();
        squish16.setBackground(Color.BLACK);
       
        formPanel.add(squish16);
        formPanel.add(nextButton);
        formPanel.add(routeLabel);
        formPanel.add(routeTextField);
        formPanel.add(backButton);
        formPanel.add(searchButton);

        
        formPanel.setBackground(Color.BLACK);

        returnText = new JTextArea();
        returnText.setBounds(10,30,200,200);
        returnText.setBorder(new EmptyBorder(0, 10, 0, 0));
        returnText.setBackground(Color.BLUE);
        returnText.setForeground(Color.WHITE);
        returnText.setFont(new Font("Courier New", Font.BOLD, 20));

        advhCenter.add(topMessage, BorderLayout.PAGE_START);
        advhCenter.add(returnText, BorderLayout.CENTER);

        //topMessage.add(formPanel);
        advhCenter.add(formPanel, BorderLayout.PAGE_END);
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

            StringBuilder sb = new StringBuilder();
            sb.append("SELECT * from advancedSearchH(" + routeTextField.getText());
            sb.append(") AS f(routeid int, day varchar, time time without time zone, train_id int);");

            System.out.println(sb.toString());
            resultSet = null;

            try { 
                try{
                    resultSet = st.executeQuery(sb.toString());
                } catch (NullPointerException np1){
                    System.out.println(np1);
                }
               
            } catch (SQLException e1) {
                System.out.println(e1);
            }
            
            returnText.setText("");

            String returnRoute = "error";
            String returnDay = "error";
            String returnTime = "error";
            String returnTrain = "error";
    
            
            int i = 0;
            do {
                try{
                    if (resultSet.next() == false){
                        break;
                    }
                    //resultSet.next();
                    returnRoute = resultSet.getString("routeid");
                    returnDay = resultSet.getString("day");
                    returnTime = resultSet.getString("time");
                    returnTrain = resultSet.getString("train_id");

                } catch (SQLException r){
                        System.out.println(r);
                }
                    
                    String str1 = String.format("%-7s|", returnRoute);
                    String str2 = String.format("%-12s|", returnDay);
                    String str3 = String.format("%-12s|", returnTime);
                    String str4 = String.format("%-5s\n", returnTrain);

                    returnText.append(str1 + str2 + str3 + str4); 
                    i++;
            } while (i < 10);
            

        } else if (source == backButton){
            SearchDatabase agentS = new SearchDatabase(guiPush, advhCenter, passwordp, userp);
        } else if (source == nextButton) {
            int i = 0;
            String returnRoute = "error";
            String returnDay = "error";
            String returnTime = "error";
            String returnTrain = "error";

            if (resultSet != null){
                returnText.setText("");
                do {
                    try{
                        if (resultSet.next() == false){
                            break;
                        }
                        resultSet.next();

                        returnRoute = resultSet.getString("route_id");
                        returnDay = resultSet.getString("day");
                        returnTime = resultSet.getString("time");
                        returnTrain = resultSet.getString("train_id");
                    } catch (SQLException r){
                            System.out.println(r);
                        }
    
                        String str1 = String.format("%-7s|", returnRoute);
                        String str2 = String.format("%-7s|", returnDay);
                        String str3 = String.format("%-7s|", returnTime);
                        String str4 = String.format("%-5s\n", returnTrain);

                        returnText.append(str1 + str2 + str3 + str4);
                        i++;
                } while (i < 10);
            }

            
        }



    }

}
