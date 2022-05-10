package dbpack;
import dbpack.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.event.*;
import java.sql.*;
import java.time.format.TextStyle;
import java.util.Properties;
import java.io.*;

public class SearchDatabase extends JFrame implements ActionListener {

    JPanel searchCenter = new JPanel(new GridLayout(0,1));

    JPanel guiPush;

    JButton selectButton = new JButton("Select");
    JButton advSearchAButton = new JButton("(a)");
    JButton advSearchBButton = new JButton("(b)");
    JButton advSearchCButton = new JButton("(c)");
    JButton advSearchDButton = new JButton("(d)");
    JButton advSearchEButton = new JButton("(e)");
    JButton advSearchFButton = new JButton("(f)");
    JButton advSearchGButton = new JButton("(g)");
    JButton advSearchHButton = new JButton("(h)");
    JButton advSearchIButton = new JButton("(i)");
    JButton advSearchJButton = new JButton("(j)");
    JButton backButton = new JButton("Back");

    JComboBox<String> selectList;

    String passwordp = "";
    String userp = "";


    public SearchDatabase(JPanel gui, JPanel center, String password, String user){
        passwordp = password;
        userp = user;

        guiPush = gui;

        center.setVisible(false);

        searchCenter.setBackground(Color.BLACK);
        searchCenter.setBorder(new EmptyBorder(30, 20, 0, 20)); 
        gui.add(searchCenter, BorderLayout.CENTER);

        JLabel headerMessage = new JLabel("Search Options", SwingConstants.CENTER);
        headerMessage.setFont(new Font("Courier New", Font.BOLD, 40));
        headerMessage.setForeground(Color.WHITE);
        headerMessage.setVerticalAlignment(SwingConstants.BOTTOM);

        JLabel advise = new JLabel("Please select a search option...", SwingConstants.CENTER);
        advise.setFont(new Font("Courier New", Font.BOLD, 20));
        advise.setForeground(Color.WHITE);
        advise.setVerticalAlignment(SwingConstants.TOP);

        JPanel topMessage = new JPanel(new GridLayout(0,1));
        topMessage.setBackground(Color.BLACK);

        JPanel squish = new JPanel();
        squish.setBackground(Color.BLACK);

        topMessage.add(squish);
        topMessage.add(headerMessage);
        topMessage.add(advise);

        searchCenter.add(topMessage);

        buttonStyle(selectButton);
        buttonStyle(advSearchAButton);
        buttonStyle(advSearchBButton);
        buttonStyle(advSearchCButton);
        buttonStyle(advSearchDButton);
        buttonStyle(advSearchEButton);
        buttonStyle(advSearchFButton);
        buttonStyle(advSearchGButton);
        buttonStyle(advSearchHButton);
        buttonStyle(advSearchIButton);
        buttonStyle(backButton);
        addActionEvent();

        JLabel srchBy = new JLabel ("Search: ", SwingConstants.RIGHT);
        JLabel advSrch = new JLabel("Advanced Search: ", SwingConstants.LEFT);

        textStyle(srchBy);
        textStyle(advSrch);

        //dropdown list
        String[] searchStrings = {"Single Route", "Combo Route"};
        selectList = new JComboBox<String>();

        for (int i = 0; i< searchStrings.length; i++){
            selectList.addItem(searchStrings[i]);
        }

        selectList.setBackground(Color.BLACK);
        selectList.setForeground(Color.WHITE);
        selectList.setFont(new Font("Courier New", Font.BOLD, 20));
        //selectList.LineBorder(Color.WHITE);

        JPanel squish4 = new JPanel();
        squish4.setBackground(Color.BLACK);
        JPanel squish5 = new JPanel();
        squish5.setBackground(Color.BLACK);
        JPanel squish6 = new JPanel();
        squish6.setBackground(Color.BLACK);

        JPanel formPanel1 = new JPanel(new GridLayout(0,2));
        formPanel1.setBackground(Color.BLACK);
        formPanel1.setBackground(Color.BLACK);
        formPanel1.setBorder(new EmptyBorder(0, 250, 0, 250));

        formPanel1.add(srchBy);
        formPanel1.add(selectList);
        formPanel1.add(squish4);
        formPanel1.add(selectButton);
        formPanel1.add(squish5);
        formPanel1.add(squish6);
        formPanel1.add(advSrch);

        searchCenter.add(formPanel1);

        JPanel formPanel2 = new JPanel(new GridLayout(0,3));
        formPanel2.setBackground(Color.BLACK);
        formPanel2.setBackground(Color.BLACK);
        formPanel2.setBorder(new EmptyBorder(0, 250, 0, 250));

        formPanel2.add(advSearchAButton);
        formPanel2.add(advSearchBButton);
        formPanel2.add(advSearchCButton);
        formPanel2.add(advSearchDButton);
        formPanel2.add(advSearchEButton);
        formPanel2.add(advSearchFButton);
        formPanel2.add(advSearchGButton);
        formPanel2.add(advSearchHButton);
        formPanel2.add(advSearchIButton);
        formPanel2.add(backButton);
        
        searchCenter.add(formPanel2);

        JPanel squish7 = new JPanel(new GridLayout(0,3));
        squish7.setBackground(Color.BLACK);

        searchCenter.add(squish7);


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
        selectButton.addActionListener(this);
        advSearchAButton.addActionListener(this);
        advSearchBButton.addActionListener(this);
        advSearchCButton.addActionListener(this);
        advSearchDButton.addActionListener(this);
        advSearchEButton.addActionListener(this);
        advSearchFButton.addActionListener(this);
        advSearchGButton.addActionListener(this);
        advSearchHButton.addActionListener(this);
        advSearchIButton.addActionListener(this);
        backButton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e)  {
        Object source = e.getSource();  

        if (source == selectButton) {
            String searchOptionSelected = selectList.getSelectedItem().toString();

           if (searchOptionSelected.equals("Single Route")) {
                System.out.println("single rt selected");

                SingleRoute newSingleRt = new SingleRoute(guiPush, searchCenter, passwordp, userp); ///////// uncomment when testing single route

            } else if (searchOptionSelected.equals("Combo Route")){
                System.out.println("combo rt selected");

                ComboRoute newComboRt = new ComboRoute(guiPush, searchCenter, passwordp, userp);
            }


        } else if (source == advSearchAButton) {
            AdvA newsearchA = new AdvA(guiPush, searchCenter, passwordp, userp);
        } else if (source == advSearchBButton){
            AdvB newsearchB = new AdvB(guiPush, searchCenter, passwordp, userp);
        } else if (source == advSearchCButton) {
            AdvC newsearchC = new AdvC(guiPush, searchCenter, passwordp, userp);
        }  else if (source == advSearchDButton) {
            AdvD newsearchD = new AdvD(guiPush, searchCenter, passwordp, userp);
        }  else if (source == advSearchEButton) {
            AdvE newsearchE = new AdvE(guiPush, searchCenter, passwordp, userp);
        }  else if (source == advSearchFButton) {
            AdvF newsearchF = new AdvF(guiPush, searchCenter, passwordp, userp);
        }  else if (source == advSearchGButton) {
            AdvG newsearchG = new AdvG(guiPush, searchCenter, passwordp, userp);
        }  else if (source == advSearchHButton) {
            AdvH newsearcH = new AdvH(guiPush, searchCenter, passwordp, userp);
        }  else if (source == advSearchIButton) {
            AdvI newsearcH = new AdvI(guiPush, searchCenter, passwordp, userp);
        }  else if (source == backButton){
            AgentScreen agentS = new AgentScreen(guiPush, searchCenter, passwordp, userp);
        }

    }

}
