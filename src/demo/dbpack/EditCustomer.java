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

/* Half of # 1 on pdf
   Thoughts on Editing the customer:
   In the scenario where a previous customer calls in to book a reservation, we should have multiple
   ways to retrieve their data. Personally, I would not remember my unique customer ID, so we should
   add the option to search by either Customer ID or a unique combination of the information we have.
   Since John Smith Jr and John Smith Sr may live in the same house and share a phone number, a
   combination of name/email address should provide sufficient uniqueness for the purpose of 
   Edit Customer. 

   the EditCustomer class is created when the user presses the 'Edit Customer' button on the Agent Screen
*/

//JFrame is the window, Action Listener is listening for the button 'press'
public class EditCustomer extends JFrame implements ActionListener { 
	
	// the following is just a blank square in the CENTER of the screen.
	// set the color to another color on line 73 and re-run the program if you want to see it.
	JPanel editCenter = new JPanel(new GridLayout(0,1));

	JPanel guiPush; // panel created in Costa Express that covers the ENTIRE screen which
					// is passed through all of the classes. We add the Center panel to this later.
	Statement st;   // connection statement to use in actionPerformed(e)

	JButton getButton = new JButton("Get Customer");
	JButton saveCButton = new JButton("Save");
	JButton backCButton = new JButton("Back");

	// text fields that the agent needs to 'enter' into the system,
	// if the agent enters one combination, we can populate the remaining fields
	JTextField inputCustomerID; //ID OR
	JTextField inputCustomerFN; //FIRST NAME
	JTextField inputCustomerLN; //LAST NAME
	JTextField inputCustomerEM; //EMAIL 

	//text fields returned by the search
	JTextField returnedCustomerPN; // phone number
	JTextField returnedCustomerST; // street
	JTextField returnedCustomerTWN; // town
	JTextField returnedCustomerPC; // postal code

	// demo could be more secure.. it's not great to pass passwords through each
	// class like this
	String passwordp = "";
	String userp = "";

	Connection conn; //db connection
	
	public EditCustomer(JPanel gui, JPanel center, String password, String user) {
		// gui = ENTIRE screen
		// center = CENTER screen of the class that called EditCustomer 
		
		passwordp = password;
		userp = user;

		guiPush = gui; //to pass the ENTIRE screen on to the next class

		center.setVisible(false);   // makes the old center screen(agentscreen) invisible before we
									// add the new editcenter screen

		editCenter.setBackground(Color.BLACK); //sets bg color of the new edit center screen
		editCenter.setBorder(new EmptyBorder(0, 20, 130, 20)); //it has a border because formatting is hard
		gui.add(editCenter, BorderLayout.CENTER);// add the new center screen to the entire screen, in the center

		// create top 'banner' message
		JLabel headerMessage = new JLabel("Edit Customer", SwingConstants.CENTER);
		headerMessage.setFont(new Font("Courier New", Font.BOLD, 40));
		headerMessage.setForeground(Color.WHITE);
		headerMessage.setVerticalAlignment(SwingConstants.BOTTOM);

		// create new grid panel for advise message
		JPanel advisePanel = new JPanel(new GridLayout(0,1));
		advisePanel.setBackground(Color.BLACK);

		JLabel advise = new JLabel("Please enter either (Customer ID)", SwingConstants.CENTER);
		advise.setFont(new Font("Courier New", Font.BOLD, 20));
		advise.setForeground(Color.WHITE);
		advise.setVerticalAlignment(SwingConstants.BOTTOM);

		JLabel advise2 = new JLabel("- OR - (First Name, Last Name and Email)", SwingConstants.CENTER);
		advise2.setFont(new Font("Courier New", Font.BOLD, 20));
		advise2.setForeground(Color.WHITE);
		advise2.setVerticalAlignment(SwingConstants.TOP);

		// create panel to attach the banner message
		JPanel topMessage = new JPanel(new GridLayout(0,1));
		topMessage.setBackground(Color.BLACK);

		// create buffer panel
		JPanel squish = new JPanel();
		squish.setBackground(Color.BLACK);

		advisePanel.add(advise);
		advisePanel.add(advise2);
		//advisePanel.add(advise3);

		// add these to a panel.. we're doing this so many times because
		// I don't know how to use most of the layout options :)
		// topMessage.add(squish);
		topMessage.add(headerMessage);
		topMessage.add(advisePanel);
		
		editCenter.add(topMessage); //add newly created 'banner' to the center of the screen

		// make the buttons *pretty*
		buttonStyle(getButton);
		buttonStyle(saveCButton);
		buttonStyle(backCButton);
		addActionEvent(); // initalize button listeners or else they wont do anything when you click them

		// un-editable text labels that let the agent know which corresponding form field to fill in
		JLabel ctmrid = new JLabel ("Customer ID: ", SwingConstants.RIGHT);
		JLabel fn = new JLabel("First Name: ", SwingConstants.RIGHT);
		JLabel ln = new JLabel("Last Name: ", SwingConstants.RIGHT);
		JLabel eml = new JLabel("Email: ", SwingConstants.RIGHT);
		JLabel pn = new JLabel("Phone Number: ", SwingConstants.RIGHT);
		JLabel str = new JLabel("Street: ", SwingConstants.RIGHT);
		JLabel twn = new JLabel("Town: ", SwingConstants.RIGHT);
		JLabel zip = new JLabel("Postal Code: ", SwingConstants.RIGHT);
		JLabel or = new JLabel("-- OR --", SwingConstants.RIGHT);

		// make them *pretty*
		textStyle(fn);
		textStyle(ln);
		textStyle(eml);
		textStyle(pn);
		textStyle(str);
		textStyle(twn);
		textStyle(zip);
		textStyle(ctmrid);
		textStyle(or);

		// initialize text fields
		inputCustomerID = new JTextField(); 
		inputCustomerFN = new JTextField();
		inputCustomerLN = new JTextField();
		inputCustomerEM = new JTextField();
		returnedCustomerPN = new JTextField("xx"); 
		returnedCustomerST = new JTextField("xx");
		returnedCustomerTWN = new JTextField("xx");
		returnedCustomerPC = new JTextField("xx");

		// make them *pretty*
		formStyle(inputCustomerID);
		formStyle(inputCustomerFN);
		formStyle(inputCustomerLN);
		formStyle(inputCustomerEM);
		formStyle(returnedCustomerPN);
		formStyle(returnedCustomerST);
		formStyle(returnedCustomerTWN);
		formStyle(returnedCustomerPC);

		JPanel squish2 = new JPanel(); // look at line 192 to see the squish buffer in action
		squish2.setBackground(Color.BLACK);
		JPanel squish3 = new JPanel(); 
		squish3.setBackground(Color.BLACK);
		JPanel squish4 = new JPanel(); 
		squish4.setBackground(Color.BLACK);
		JPanel squish5 = new JPanel(); 
		squish5.setBackground(Color.BLACK);


		// make new panel just for form input
		// GridLayout specifies a layout that adds input in the
		// sequence:
		// row1colA, row1colB
		// row2colA, row2colB
		// row3colA, ...etc
		JPanel formPanel = new JPanel(new GridLayout(0,2));
		formPanel.setBackground(Color.BLACK);
		formPanel.setBackground(Color.BLACK);
		formPanel.setBorder(new EmptyBorder(0, 250, 0, 250));

		// add all of the objects to the form panel
		formPanel.add(ctmrid); // row1colA : "Customer ID"
		formPanel.add(inputCustomerID); //row1colB : [*enter id here*]
		formPanel.add(squish2); //row2colA : EMPTY
		formPanel.add(or); //row2colB : " -- OR -- "
		formPanel.add(fn); //row3colA : "First Name: "
		formPanel.add(inputCustomerFN); //row3colB : [*enter fname here*]
		formPanel.add(ln); //row4colA : "Last Name: "
		formPanel.add(inputCustomerLN); //row4colB : [*enter fname here*] ...etc
		formPanel.add(eml);
		formPanel.add(inputCustomerEM);
		formPanel.add(squish3);
		formPanel.add(getButton); 
		formPanel.add(squish4);
		formPanel.add(squish5);
		formPanel.add(pn);
		formPanel.add(returnedCustomerPN);
		formPanel.add(str);
		formPanel.add(returnedCustomerST);
		formPanel.add(twn);
		formPanel.add(returnedCustomerTWN);
		formPanel.add(zip);
		formPanel.add(returnedCustomerPC);
		formPanel.add(backCButton);
		formPanel.add(saveCButton);

		//add the form panel to the center of the screen
		editCenter.add(formPanel);

		// add another squish for centering because these alignment options
		// are beyond me :)
		JPanel squish22 = new JPanel();
		squish22.setBackground(Color.BLACK);

		//editCenter.add(squish22);
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
		backCButton.addActionListener(this);
		saveCButton.addActionListener(this);
		getButton.addActionListener(this);
	}

	// Necessary for connection to db, I didn't write this, it's referenced from the recitation demos.
	// try/catch statements are separated for error tracing isolation
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
			props.setProperty("user", "postgres");
		} else {
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

	/* 
		the following is a function (NEEDS TESTING) that takes the button push
		and then does something depending on which option was selected.
		look at AddCustomer.java to see how to implement each command

	   for reference, these are the JTextFields that need to be set/retrieved:
		inputCustomerID; //ID OR
		inputCustomerFN; //FIRST NAME
		inputCustomerLN; //LAST NAME
		inputCustomerEM; //EMAIL 
		returnedCustomerPN; // phone number
		returnedCustomerST; // street
		returnedCustomerTWN; // town
		returnedCustomerPC; // postal code
	*/
	public void actionPerformed(ActionEvent e)  {
		Object source = e.getSource();  
		if (source == getButton) { // when agent 'clicks' this button, the program will:
			// start db connection
			connectionDB();

			boolean name_and_email = false;
			StringBuilder sb = new StringBuilder();
			// if we have a customer id, get the matching customer attributes for that line
				// generate a string SELECT statement where customers.customerID = inputcustomerID
			if (!inputCustomerID.getText().equals("")) {
				sb.append("SELECT * FROM Customers WHERE customer_id = '" + inputCustomerID.getText() + "';");
			} else if (!(inputCustomerFN.getText().equals("") && inputCustomerLN.getText().equals("") && inputCustomerEM.getText().equals(""))) {
				sb.append("SELECT * FROM Customers WHERE first_name = '" + inputCustomerFN.getText() + "' AND ");
				sb.append("last_name = '" + inputCustomerLN.getText() + "' AND ");
				sb.append("email = '" + inputCustomerEM.getText() + "';");
				name_and_email = true;
			} else {
				System.out.println("Empty fields");
			}
			// System.out.println(sb.toString());

			// create a ResultSet to hold the returned attributes and execute a query with the try/catch block record results.
			ResultSet resultSet = null;
			try {
				try {
					st.executeQuery(sb.toString());
					resultSet = st.executeQuery(sb.toString());
				} catch (NullPointerException np) {
					System.out.println(np);
				}
			} catch (SQLException sq) {
				//System.out.println(sq);
				System.out.println(sq);
			}

			// loop through the resultSet and setText in return fields that match attribute names.
			try {
				while (resultSet.next()) {
					if (name_and_email) {
						inputCustomerID.setText(resultSet.getString("customer_id"));
					} else {
						inputCustomerFN.setText(resultSet.getString("first_name"));
						inputCustomerLN.setText(resultSet.getString("last_name"));
						inputCustomerEM.setText(resultSet.getString("email"));
					}
					returnedCustomerPN.setText(resultSet.getString("phone_no"));
					returnedCustomerST.setText(resultSet.getString("street"));
					returnedCustomerTWN.setText(resultSet.getString("town"));
					returnedCustomerPC.setText(resultSet.getString("postalcode"));
				}
			} catch (SQLException setTextE) {
				System.out.println(setTextE);
			}

			// close connection
			try{
				conn.close();
			} catch (SQLException df){
				System.out.println(df);
			}
		} else if (source == saveCButton){    
			// start db connection
			connectionDB();

			Statement statement = null;
			// generate and execute an batch UPDATE statement based on .getText for all attributes
			try {
				if (inputCustomerID.getText() != "" || inputCustomerID.getText() != null) {
					statement = conn.createStatement();
					//statement.addBatch("UPDATE Customers SET first_name = '" + inputCustomerFN.getText() + "' WHERE customer_id = '" + cust_id + "'");
					//statement.addBatch("UPDATE Customers SET last_name = '" + inputCustomerLN.getText() + "' WHERE customer_id = '" + cust_id + "'");
					//statement.addBatch("UPDATE Customers SET email = '" + inputCustomerEM.getText() + "' WHERE customer_id = '" + cust_id + "'");
					statement.addBatch("UPDATE Customers SET phone_no = '" + returnedCustomerPN.getText() + "' WHERE customer_id = '" + inputCustomerID.getText() + "'");
					statement.addBatch("UPDATE Customers SET street = '" + returnedCustomerST.getText() + "' WHERE customer_id = '" + inputCustomerID.getText() + "'");
					statement.addBatch("UPDATE Customers SET town = '" + returnedCustomerTWN.getText() + "' WHERE customer_id = '" + inputCustomerID.getText() + "'");
					statement.addBatch("UPDATE Customers SET postalcode = '" + returnedCustomerPC.getText() + "' WHERE customer_id = '" + inputCustomerID.getText() + "'");
					int[] recordsAffected = statement.executeBatch();
				}
			} catch (SQLException updE) {
				System.out.println(updE);
			} finally {
				try {
					if (statement != null) statement.close();
				} catch (SQLException stE) {
					System.out.println(stE);
				}
			}

			// close connection
			try{
				conn.close();
			} catch (SQLException df){
				System.out.println(df);
			}
		} else if (source == backCButton) {
			AgentScreen agentS = new AgentScreen(guiPush, editCenter, passwordp, userp);
		}
	}
}
