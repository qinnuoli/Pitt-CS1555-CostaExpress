package dbpack;
import dbpack.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.event.*;
import java.sql.*;
import java.util.Properties;
import java.io.*;


public class AddCustomer extends JFrame implements ActionListener { 

    JPanel customerCenter = new JPanel(new GridLayout(0,1));
    JButton backButton = new JButton("Back");
    JButton addCButton = new JButton("Add");
    
    JPanel guiPush;
    Statement st;

    JTextField firstName;
    JTextField lastName;
    JTextField email;
    JTextField phone_no;
    JTextField street;
    JTextField town;
    JTextField postalCode;
    JTextField customerID;

    String passwordp = "";
    String userp = "";

    Connection conn;

    public AddCustomer(JPanel gui, JPanel center, String password, String user) {

        passwordp = password;
        userp = user;

        guiPush = gui;

        center.setVisible(false);

        customerCenter.setBackground(Color.BLACK);
        customerCenter.setBorder(new EmptyBorder(30, 20, 150, 20));
        gui.add(customerCenter, BorderLayout.CENTER);

        JPanel squish = new JPanel();
        squish.setBackground(Color.BLACK);

        JLabel headerMessage = new JLabel("Customer Entry", SwingConstants.CENTER);
        headerMessage.setFont(new Font("Courier New", Font.BOLD, 40));
        headerMessage.setForeground(Color.WHITE);
        headerMessage.setVerticalAlignment(SwingConstants.BOTTOM);

        JPanel topMessage = new JPanel(new GridLayout(0,1));
        //topMessage.add(squish);
        topMessage.add(headerMessage);
        topMessage.setBackground(Color.BLACK);
        
        JLabel advise = new JLabel("Please enter the following...", SwingConstants.CENTER);
        advise.setFont(new Font("Courier New", Font.BOLD, 20));
        advise.setForeground(Color.WHITE);

        advise.setVerticalAlignment(SwingConstants.TOP);

        topMessage.add(advise);

        customerCenter.add(topMessage);

        buttonStyle(addCButton);
        buttonStyle(backButton);
        addActionEvent();

        JLabel fn = new JLabel("First Name: ", SwingConstants.RIGHT);
        JLabel ln = new JLabel("Last Name: ", SwingConstants.RIGHT);
        JLabel eml = new JLabel("Email: ", SwingConstants.RIGHT);
        JLabel pn = new JLabel("Phone Number: ", SwingConstants.RIGHT);
        JLabel str = new JLabel("Street: ", SwingConstants.RIGHT);
        JLabel twn = new JLabel("Town: ", SwingConstants.RIGHT);
        JLabel zip = new JLabel("Postal Code: ", SwingConstants.RIGHT);
        JLabel ctmrid = new JLabel ("Customer ID: ", SwingConstants.RIGHT);

        textStyle(fn);
        textStyle(ln);
        textStyle(eml);
        textStyle(pn);
        textStyle(str);
        textStyle(twn);
        textStyle(zip);
        textStyle(ctmrid);
/*
        JTextField firstName;
        JTextField lastName;
        JTextField email;
        JTextField phone_no;
        JTextField street;
        JTextField town;
        JTextField postalCode;
*/
        firstName = new JTextField();
        lastName = new JTextField();
        email = new JTextField();
        phone_no = new JTextField();
        street = new JTextField();
        town = new JTextField();
        postalCode = new JTextField();
        customerID = new JTextField("xx");
        customerID.setEditable(false);

        formStyle(firstName);
        formStyle(lastName);
        formStyle(email);
        formStyle(phone_no);
        formStyle(street);
        formStyle(town);
        formStyle(postalCode);
        formStyle(customerID);


        JPanel squish2 = new JPanel();
        squish2.setBackground(Color.BLACK);
        JPanel squish3 = new JPanel();
        squish3.setBackground(Color.BLACK);

        JPanel formPanel = new JPanel(new GridLayout(0,2));
        formPanel.setBackground(Color.BLACK);
        formPanel.setBorder(new EmptyBorder(0, 250, 0, 250));
    
        formPanel.add(fn);
        formPanel.add(firstName);
        formPanel.add(ln);
        formPanel.add(lastName);
        formPanel.add(eml);
        formPanel.add(email);
        formPanel.add(pn);
        formPanel.add(phone_no);
        formPanel.add(str);
        formPanel.add(street);
        formPanel.add(twn);
        formPanel.add(town);
        formPanel.add(zip);
        formPanel.add(postalCode);
        formPanel.add(ctmrid);
        formPanel.add(customerID); //to be updated on 'add'
        formPanel.add(squish2);
        formPanel.add(squish3);
        formPanel.add(backButton);
        formPanel.add(addCButton);

        formPanel.setBackground(Color.BLACK);

        customerCenter.add(formPanel);
        

        //customerCenter.add(squish);



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

        //System.out.println("user: " + userp + " pass: " + passwordp);

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

    public void addActionEvent(){
        backButton.addActionListener(this);
        addCButton.addActionListener(this);
        
    }

    public void actionPerformed(ActionEvent e)  {
        Object source = e.getSource();

        if (source == addCButton) {

            connectionDB();

            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO Customers (first_name, last_name, email, phone_no, street,");
            sb.append(" town, postalcode) VALUES ('" + firstName.getText() + "', '" + lastName.getText());
            sb.append("', '" + email.getText() + "', '" + phone_no.getText() + "', '" + street.getText());
            sb.append("', '" + town.getText() + "', '" + postalCode.getText() + "');");

            System.out.println(sb.toString());

            StringBuilder returnQuery = new StringBuilder();
            returnQuery.append("SELECT customer_id FROM Customers WHERE first_name = '" + firstName.getText() + "' AND ");
            returnQuery.append("last_name = '" + lastName.getText() + "' AND email = '" + email.getText() + "' AND ");
            returnQuery.append("phone_no = '" + phone_no.getText() + "' AND street = '" + street.getText() + "' AND ");
            returnQuery.append("town = '" + town.getText() + "' AND postalcode = '" + postalCode.getText() + "';");

            System.out.print(returnQuery);
            ResultSet resultSet = null;
 
            
            try { 
                try{
                    st.executeUpdate(sb.toString()); //adds new customer to db
                    resultSet = st.executeQuery(returnQuery.toString());
                } catch (NullPointerException np1){
                    System.out.println(np1);
                }
               
            } catch (SQLException e1) {
                System.out.println(e1);
            }

            String returnedID = "error"; //if id cannot be found
            
            try {
                while(resultSet.next()){
                    returnedID = resultSet.getString("customer_id");
                }     
            } catch (SQLException res) {
                System.out.println(res);
            }

            customerID.setText(returnedID);
            customerID.setForeground(Color.GREEN);

            //close connection
            try{
                conn.close();
            } catch (SQLException df){
                System.out.println(df);
            }
            

        } else if (source == backButton) {
            AgentScreen agentS = new AgentScreen(guiPush, customerCenter, passwordp, userp);
        }
    }





    
}
