package dbpack;
import dbpack.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.awt.image.BufferedImage;
import javax.imageio.*;
import java.io.IOException;
import java.io.*;
import javax.swing.border.LineBorder;
import java.awt.event.ActionEvent;
import java.awt.event.*;

public class AgentLogin extends JFrame implements ActionListener {

    JPanel agentLoginp = new JPanel(new GridLayout(0,1));
    JButton submitButton = new JButton("Login"); 
    JButton adminButton = new JButton("Admin");
    JTextField userID;
    JPasswordField password;
    JPanel guiPass;

    JPanel oldCenter;


    public AgentLogin(JPanel gui, JPanel center) {
        center.setVisible(false);
        oldCenter = center;

        agentLoginp.setBorder(new EmptyBorder(0, 100, 50, 100));
        agentLoginp.setBackground(Color.BLACK);

        JLabel loginMessage = new JLabel("Agent Login", SwingConstants.CENTER);
        loginMessage.setFont(new Font("Courier New", Font.BOLD, 30));
        loginMessage.setForeground(Color.WHITE);
        //loginMessage.setVerticalAlignment(SwingConstants.TOP);

        JPanel topBar = new JPanel();
        topBar.setBackground(Color.BLACK);

        agentLoginp.add(topBar);

        submitButton.setForeground(Color.WHITE);
        submitButton.setBackground(Color.BLACK);
        submitButton.setBorder(new LineBorder(Color.WHITE));
        submitButton.setFont(new Font("Courier New", Font.BOLD, 18));

        adminButton.setForeground(Color.WHITE);
        adminButton.setBackground(Color.BLACK);
        adminButton.setBorder(new LineBorder(Color.WHITE));
        adminButton.setFont(new Font("Courier New", Font.BOLD, 18));

        try{
            BufferedImage userImage = ImageIO.read(new File("user-01-01.png"));
            JLabel userImageLabel = new JLabel(new ImageIcon(userImage));
            userImageLabel.setVerticalAlignment(SwingConstants.TOP);
            
            agentLoginp.add(userImageLabel);
            
        } catch (IOException e){
            // do nothing
        }

        agentLoginp.add(loginMessage);

        JPanel formMenu = new JPanel(new GridLayout(0,2));
        formMenu.setBackground(Color.BLACK);
        formMenu.setBorder(new EmptyBorder(0, 250, 0, 250));


        JPanel squish = new JPanel();
        JPanel squish2 = new JPanel();
        JPanel squish3 = new JPanel(); 
        JPanel squish4 = new JPanel();
        JPanel squish5 = new JPanel();
        JPanel squish6 = new JPanel();
        JPanel squish7 = new JPanel();

        squish.setBackground(Color.BLACK);
        squish2.setBackground(Color.BLACK);
        squish3.setBackground(Color.BLACK);
        squish4.setBackground(Color.BLACK);
        squish5.setBackground(Color.BLACK);
        squish6.setBackground(Color.BLACK);
        squish7.setBackground(Color.BLACK);
        

        userID = new JTextField(""); ////////////userid field
        userID.setBackground(Color.BLACK);
        userID.setFont(new Font("Courier New", Font.BOLD, 18));
        userID.setForeground(Color.WHITE);

        password = new JPasswordField(); ///////password field
        password.setBackground(Color.BLACK);
        password.setFont(new Font("Courier New", Font.BOLD, 18));
        password.setForeground(Color.WHITE);

        JLabel userMessage = new JLabel("Username: ", SwingConstants.RIGHT);
        userMessage.setFont(new Font("Courier New", Font.BOLD, 20));
        userMessage.setForeground(Color.WHITE);

        JLabel passwordMessage = new JLabel("Password: ", SwingConstants.RIGHT);
        passwordMessage.setFont(new Font("Courier New", Font.BOLD, 20));
        passwordMessage.setForeground(Color.WHITE);

        formMenu.add(squish);
        formMenu.add(squish2);
        formMenu.add(userMessage);
        formMenu.add(userID);
        formMenu.add(passwordMessage);
        formMenu.add(password);
        formMenu.add(squish3);
        formMenu.add(squish7);
        formMenu.add(adminButton);
        formMenu.add(submitButton);

        agentLoginp.add(formMenu);

        JPanel bottomBar = new JPanel();
        bottomBar.setBackground(Color.BLACK);

        agentLoginp.add(bottomBar);


        gui.add(agentLoginp);
        guiPass = gui;

        addActionEvent();

    }

    public void addActionEvent(){
        adminButton.addActionListener(this);
        submitButton.addActionListener(this);
    }


    public void actionPerformed(ActionEvent e){
        Object source = e.getSource();
        //String inputRes = null;

        if (source == submitButton){
            //will need to add functionality for login here

            String savedUserName = userID.getText();
            String savedPassword = new String(password.getPassword());

            //System.out.println("Username: " + savedUserName);
            //System.out.println("Password: " + savedPassword);//hidden, do not print
            
            AgentScreen agentS = new AgentScreen(guiPass, agentLoginp, savedPassword, savedUserName);

            
       
        } else if (source == adminButton) { //this doesnt work quite right, returns to prev page if prev page was agentscreen
            AdminLogin newAlogin = new AdminLogin(guiPass, agentLoginp);
        }
    }


    

}
