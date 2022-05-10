package dbpack;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.event.*;
import java.sql.*;
import java.util.Properties;


public class AdvF extends JFrame implements ActionListener {

    JPanel advfCenter = new JPanel(new BorderLayout());
    JButton searchButton = new JButton("Search");
    JButton backButton = new JButton("Back");
    JButton nextButton = new JButton("Next >");

    JPanel guiPush;
    Statement st;

    JTextField station;

    
    ResultSet resultSet;

    JTextArea returnText;
    JTextArea txtheader;

    
    String passwordp = "";
    String userp = "";

    Connection conn;

    public AdvF(JPanel gui, JPanel center, String password, String user){
        passwordp = password;
        userp = user;

        guiPush = gui;

        center.setVisible(false);

        advfCenter.setBackground(Color.BLACK);
        advfCenter.setBorder(new EmptyBorder(0, 20, 0, 20));
        gui.add(advfCenter, BorderLayout.CENTER);

        JPanel squish = new JPanel();
        squish.setBackground(Color.BLACK);

        JLabel headerMessage = new JLabel("Advanced Search (F)");

        headerMessage.setFont(new Font("Courier New", Font.BOLD, 40));
        headerMessage.setForeground(Color.WHITE);
        headerMessage.setVerticalAlignment(SwingConstants.BOTTOM);

        JPanel topMessage = new JPanel(new GridLayout(0,1));
        //topMessage.add(squish);
        topMessage.add(headerMessage);
        topMessage.setBackground(Color.BLACK);

        JLabel ins = new JLabel("Find all trains that do not stop at a specific station.", SwingConstants.CENTER);
        topMessage.add(ins);

        txtheader = new JTextArea("Train_ID: ");
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


        JLabel stA = new JLabel("Station(as ID):", SwingConstants.RIGHT);

        textStyle(stA);
        textStyle(ins);
        ins.setFont(new Font("Courier New", Font.BOLD, 18));

        station = new JTextField();

        formStyle(station);

        JPanel formPanel = new JPanel(new GridLayout(0,2));
        //add to form panel

        JPanel squish2 = new JPanel();
        squish2.setBackground(Color.BLACK);
        JPanel squish16 = new JPanel();
        squish16.setBackground(Color.BLACK);
       
        formPanel.add(squish16);
        formPanel.add(nextButton);
        formPanel.add(stA);
        formPanel.add(station);
        formPanel.add(backButton);
        formPanel.add(searchButton);

        
        formPanel.setBackground(Color.BLACK);

        returnText = new JTextArea();
        returnText.setBounds(10,30,200,200);
        returnText.setBorder(new EmptyBorder(0, 10, 0, 0));
        returnText.setBackground(Color.BLUE);
        returnText.setForeground(Color.WHITE);
        returnText.setFont(new Font("Courier New", Font.BOLD, 20));

        advfCenter.add(topMessage, BorderLayout.PAGE_START);
        advfCenter.add(returnText, BorderLayout.CENTER);

        //topMessage.add(formPanel);
        advfCenter.add(formPanel, BorderLayout.PAGE_END);
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
            sb.append("SELECT * from advancedSearchF() AS f(train_id int);");

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

            String returnTrain = "error";
    
            
            int i = 0;

            do {
                try{
                    if (resultSet.next() == false){
                        returnText.setText("Search returns empty results: All trains pass through this station.");
                        break;
                    }
                    //resultSet.next();
                    returnTrain = resultSet.getString("train_id");

                } catch (SQLException r){
                        System.out.println(r);
                }
                    
                    String str = String.format("%-5s\n", returnTrain);

                    returnText.append(str); 
                    i++;
            } while (i < 10);
            

        } else if (source == backButton){
            SearchDatabase agentS = new SearchDatabase(guiPush, advfCenter, passwordp, userp);
        } else if (source == nextButton) {
            int i = 0;
            String returnTrain = "error";

            if (resultSet != null){
                returnText.setText("");
                do {
                    try{
                        if (resultSet.next() == false){
                            break;
                        }
                        resultSet.next();

                        returnTrain = resultSet.getString("train_id");
                        } catch (SQLException r){
                            System.out.println(r);
                        }

                        String str = String.format("%-5s\n", returnTrain);

                        returnText.append(str); 
                        i++;
                } while (i < 10);
            }
        }

    }

}
