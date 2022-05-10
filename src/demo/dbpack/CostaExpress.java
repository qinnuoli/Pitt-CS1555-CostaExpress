package dbpack;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.EmptyBorder;
import java.awt.image.BufferedImage;
import javax.imageio.*;
import java.io.IOException;
import javax.swing.border.LineBorder;


public class CostaExpress extends JFrame implements ActionListener {

    JButton agentButton = new JButton("Agent");
    JButton adminButton = new JButton("Admin");
    JButton exitButton = new JButton("Exit");
    JPanel center = new JPanel(new GridLayout(0,1));
    JPanel gui = new JPanel(new BorderLayout(5,5));

    public CostaExpress() {
        initGUI();
        addActionEvent();
    }

    public void initGUI() {

        JPanel buttonPanel = new JPanel(new GridLayout(0,1));
        buttonPanel.setBackground(Color.BLACK);

        buttonPanel.setBorder(new EmptyBorder(50, 250, 100, 250));
        
        gui.setBorder(new EmptyBorder(50, 50, 50, 50));
        gui.setBackground(Color.BLACK);
        gui.setPreferredSize(new Dimension(1200, 800));

        JFrame frame = new JFrame("CostaExpress Demo");
        frame.setContentPane(gui);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel sideBar = new JPanel();
        sideBar.setPreferredSize(new Dimension(0,200));
        sideBar.setBackground(Color.BLACK);
        JPanel sideBar2 = new JPanel();
        sideBar2.setPreferredSize(new Dimension(0,200));
        sideBar2.setBackground(Color.BLACK);

        try{
            BufferedImage costaImage = ImageIO.read(new File("costa-01-01-01.png"));
            JLabel costaImageLabel = new JLabel(new ImageIcon(costaImage));
            costaImageLabel.setPreferredSize(new Dimension(500,500));
            
            center.add(costaImageLabel);
            
        } catch (IOException e){
            // do nothing
        }

        buttonStyle(agentButton);
        buttonStyle(adminButton);
        buttonStyle(exitButton);

        buttonPanel.add(agentButton);
        buttonPanel.add(adminButton);
        buttonPanel.add(exitButton);

        center.add(buttonPanel);

        center.setBackground(Color.BLACK);
        
        gui.add(sideBar2, BorderLayout.WEST);
        gui.add(sideBar, BorderLayout.EAST);
        gui.add(center, BorderLayout.CENTER);

        frame.setBounds(50, 50, 500, 500);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);

    }

    public void addActionEvent(){
        agentButton.addActionListener(this);
        adminButton.addActionListener(this);
        exitButton.addActionListener(this);
    }

    private static void buttonStyle(JButton b){
        b.setForeground(Color.WHITE);
        b.setBackground(Color.BLACK);
        b.setBorder(new LineBorder(Color.WHITE));
        b.setFont(new Font("Courier New", Font.BOLD, 20));
    }

    public void actionPerformed(ActionEvent e){
        Object source = e.getSource();
        //String inputRes = null;

        if (source == agentButton){
            AgentLogin agentS = new AgentLogin(gui, center);
        } else if (source == adminButton){
            AdminLogin adminS = new AdminLogin(gui, center);
        } else if (source == exitButton){
            System.exit(0);
        }
    }

    public static void main(String[] args){
        CostaExpress runProgram = new CostaExpress();

    }

}
