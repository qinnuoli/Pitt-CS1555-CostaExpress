package dbpack;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.event.*;
import java.sql.*;
import java.util.Properties;

public class AdvD extends JFrame implements ActionListener {
    JPanel advdCenter = new JPanel(new BorderLayout());

    JButton backButton = new JButton("Back");
    JButton nextButton = new JButton("Next >");

    JPanel guiPush;
    Statement st;

  
    ResultSet resultSet;

    JTextArea returnText;
    JTextArea txtheader;

    
    String passwordp = "";
    String userp = "";

    Connection conn;

    public AdvD(JPanel gui, JPanel center, String password, String user){
        passwordp = password;
        userp = user;

        guiPush = gui;

        center.setVisible(false);

        advdCenter.setBackground(Color.BLACK);
        advdCenter.setBorder(new EmptyBorder(0, 20, 0, 20));
        gui.add(advdCenter, BorderLayout.CENTER);

        JPanel squish = new JPanel();
        squish.setBackground(Color.BLACK);

        JLabel headerMessage = new JLabel("Advanced Search (D)");

        headerMessage.setFont(new Font("Courier New", Font.BOLD, 40));
        headerMessage.setForeground(Color.WHITE);
        headerMessage.setVerticalAlignment(SwingConstants.BOTTOM);

        JPanel topMessage = new JPanel(new GridLayout(0,1));
        //topMessage.add(squish);
        topMessage.add(headerMessage);
        topMessage.setBackground(Color.BLACK);

        JLabel ins = new JLabel("Find routes that pass through the same stations but don't have the same stops.", SwingConstants.CENTER);
        topMessage.add(ins);

        txtheader = new JTextArea("Route_ID: ");

        txtheader.setBounds(10,30,200,200);
        txtheader.setBorder(new EmptyBorder(15, 10, 0, 0));
        txtheader.setBackground(Color.BLUE);
        txtheader.setForeground(Color.WHITE);
        txtheader.setFont(new Font("Courier New", Font.BOLD, 20));
        topMessage.add(txtheader);

        buttonStyle(backButton);
        buttonStyle(nextButton);
        addActionEvent();

        textStyle(ins);
        ins.setFont(new Font("Courier New", Font.BOLD, 18));

        JPanel formPanel = new JPanel(new GridLayout(0,2));

        JPanel squish2 = new JPanel();
        squish2.setBackground(Color.BLACK);
        JPanel squish16 = new JPanel();
        squish16.setBackground(Color.BLACK);
       
        formPanel.add(squish16);
        formPanel.add(nextButton);
        formPanel.add(backButton);

        formPanel.setBackground(Color.BLACK);

        returnText = new JTextArea();
        returnText.setBounds(10,30,200,200);
        returnText.setBorder(new EmptyBorder(0, 10, 0, 0));
        returnText.setBackground(Color.BLUE);
        returnText.setForeground(Color.WHITE);
        returnText.setFont(new Font("Courier New", Font.BOLD, 20));

        advdCenter.add(topMessage, BorderLayout.PAGE_START);
        advdCenter.add(returnText, BorderLayout.CENTER);

        advdCenter.add(formPanel, BorderLayout.PAGE_END);


        // since there is no 'search' button action, the following
        // creates the search results

        connectionDB();

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * from advancedSearchD() AS f(route_id int);");

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

        int i = 0;
        do {
            try{
                if (resultSet.next() == false){
                    break;
                }
       
                returnRoute = resultSet.getString("route_id");

            } catch (SQLException r){
                    System.out.println(r);
            }
                    
                String str1 = String.format("%-7s\n", returnRoute);

                returnText.append(str1); 
                i++;
        } while (i < 10);




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

        if (source == backButton){
            SearchDatabase agentS = new SearchDatabase(guiPush, advdCenter, passwordp, userp);
        } else if (source == nextButton) {
            int i = 0;
            String returnRoute = "error";

            if (resultSet != null){
                returnText.setText("");
                do {
                    try{
                        if (resultSet.next() == false){
                            break;
                        }
                        resultSet.next();

                        returnRoute = resultSet.getString("route_id");

                        } catch (SQLException r){
                            System.out.println(r);
                        }
    
                        String str1 = String.format("%-7s\n", returnRoute);

    
                        returnText.append(str1); 
                        i++;
                } while (i < 10);
            }

            
        }



    }

}
