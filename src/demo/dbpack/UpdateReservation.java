package dbpack;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.Properties;


/* # 4 on pdf
   Thoughts on update reservation:
   We allow changing the name on the reservation (and automatically updating the customer table) and
   the payment on the reservation. After entering the input payment and reservation number, they cannot be
   edited, instead you must start a new updateReservation session.

   If anything other than the payment needs to be updated, a new reservation should instead be generated.. 
   Assuming the balance after inputPayment reaches 0, a new ticket should be generated here. 

   We can make this easier by requiring the reservation id here. In the 'Search Database' part of the 
   program, we can get the reservation ID.. here, we're only updating or deleting. 

   the Update Reservation class is created when the user presses the 'Edit Customer' button on the Agent Screen
*/

//JFrame is the window, Action Listener is listening for the button 'press'
public class UpdateReservation extends JFrame implements ActionListener {
	int cust_id = 0;
	// the following is just a blank square in the CENTER of the screen.
	JPanel resEditCenter = new JPanel(new GridLayout(0,1));

	JPanel guiPush; // panel created in Costa Express that covers the ENTIRE screen which
					// is passed through all of the classes. We add the Center panel to this later.
	Statement st;

	JButton getRButton = new JButton("Get Reservation");
	JButton saveRButton = new JButton("Save");
	JButton backRButton = new JButton("Back");
	JButton deleteRButton = new JButton("Delete");

	// text fields that the agent needs to 'enter' into the system,
	// if the agent enters one combination, we can populate the remaining fields
	JTextField inputReservationNumber; //Reserv_no
	JTextField inputPayment;

	//text fields returned by the search
	JTextField returnedCustomerID; // will need to return this as the customer name
	JTextField returnedReservationBAL; 
	JTextField returnedDay;
	JTextField returnedTime;
	JTextField returnedPrice;
	JTextField returnedTicket;
	JTextField returnedFname;
	JTextField returnedLname;

	// demo could be more secure.. it's not great to pass passwords through each
	// class like this
	String passwordp = "";
	String userp = "";

	Connection conn; //db connection

	public UpdateReservation(JPanel gui, JPanel center, String password, String user) {
		// gui = ENTIRE screen
		// center = CENTER screen of the class that called EditCustomer 
		
		passwordp = password;
		userp = user;

		guiPush = gui; //to pass the ENTIRE screen on to the next class

		center.setVisible(false);   // makes the old center screen(agentscreen) invisible before we
									// add the new editcenter screen

		resEditCenter.setBackground(Color.BLACK); //sets bg color of the new edit center screen
		resEditCenter.setBorder(new EmptyBorder(0, 20, 130, 20)); //it has a border because formatting is hard
		gui.add(resEditCenter, BorderLayout.CENTER);// add the new center screen to the entire screen, in the center
							
		// create top 'banner' message
		JLabel headerMessage = new JLabel("Edit Reservation", SwingConstants.CENTER);
		headerMessage.setFont(new Font("Courier New", Font.BOLD, 40));
		headerMessage.setForeground(Color.WHITE);
		headerMessage.setVerticalAlignment(SwingConstants.BOTTOM);
		
		JLabel advise = new JLabel("Please enter the Reservation ID number", SwingConstants.CENTER);
		advise.setFont(new Font("Courier New", Font.BOLD, 20));
		advise.setForeground(Color.WHITE);
		advise.setVerticalAlignment(SwingConstants.CENTER);

		// create panel to attach the banner message
		JPanel topMessage = new JPanel(new GridLayout(0,1));
		topMessage.setBackground(Color.BLACK);

		// create buffer panel
		JPanel squish = new JPanel();
		squish.setBackground(Color.BLACK);

		// add these to a panel.. we're doing this so many times because
		// I don't know how to use most of the layout options :)
		//topMessage.add(squish);
		topMessage.add(headerMessage);
		topMessage.add(advise);

		resEditCenter.add(topMessage); //add newly created 'banner' to the center of the screen

		// make the buttons *pretty*
		buttonStyle(getRButton);
		buttonStyle(saveRButton);
		buttonStyle(backRButton);
		buttonStyle(deleteRButton);
		addActionEvent(); // initalize button listeners or else they wont do anything when you click them

		// un-editable text labels that let the agent know which corresponding form field to fill in
		JLabel resn = new JLabel ("Reservation Number: ", SwingConstants.RIGHT);
		JLabel fn = new JLabel("First Name: ", SwingConstants.RIGHT); //return will display these
		JLabel ln = new JLabel("Last Name: ", SwingConstants.RIGHT); // return will display these
		JLabel prc = new JLabel("Price: ", SwingConstants.RIGHT); 
		JLabel bal = new JLabel("Balance: ", SwingConstants.RIGHT);
		JLabel day = new JLabel("Day: ", SwingConstants.RIGHT);
		JLabel time = new JLabel("Time: ", SwingConstants.RIGHT);
		JLabel tick = new JLabel("Ticket Number: ", SwingConstants.RIGHT);
		JLabel pymt = new JLabel("Payment: ", SwingConstants.RIGHT);

		// make them *pretty*
		textStyle(resn);
		textStyle(ln);
		textStyle(fn);
		textStyle(prc);
		textStyle(bal);
		textStyle(day);
		textStyle(time);
		textStyle(pymt);
		textStyle(tick);

		// initialize text fields
		inputReservationNumber = new JTextField(); 
		inputPayment = new JTextField();
		returnedCustomerID = new JTextField("xx");
		returnedReservationBAL = new JTextField("xx");
		returnedReservationBAL.setEditable(false);
		returnedDay = new JTextField("xx");
		returnedDay.setEditable(false);
		returnedTime = new JTextField("xx"); 
		returnedTime.setEditable(false);
		returnedPrice = new JTextField("xx");
		returnedPrice.setEditable(false);
		returnedTicket = new JTextField("xx");
		returnedTicket.setEditable(false);
		returnedFname = new JTextField("xx");
		returnedLname = new JTextField("xx");

		// make them *pretty*
		formStyle(inputReservationNumber);
		formStyle(returnedCustomerID);
		formStyle(returnedReservationBAL);
		formStyle(returnedDay);
		formStyle(returnedTime);
		formStyle(returnedPrice);
		formStyle(returnedTicket);
		formStyle(inputPayment);
		formStyle(returnedFname);
		formStyle(returnedLname);

		//look at the formPanel.add section to see how these are used
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
		formPanel.setBorder(new EmptyBorder(0, 250, 0, 250));

		// add all of the objects to the form panel
		formPanel.add(resn); // row1colA : "Reservation Number: "
		formPanel.add(inputReservationNumber); //row1colB : [*enter res# here*]
		formPanel.add(pymt); //row2colA : "Payment: "
		formPanel.add(inputPayment); //row2colB : [*enter input payment here*]
		formPanel.add(deleteRButton); //row3colA : delete Reservation button
		formPanel.add(getRButton); //row3colB : get Reservation button
		formPanel.add(squish4); // row4colA: EMPTY
		formPanel.add(squish5); // row4colB: EMPTY
		formPanel.add(fn); //row5colA : "First Name: "
		formPanel.add(returnedFname); //row5colB : [*enter fname here*] ...etc
		formPanel.add(ln);
		formPanel.add(returnedLname);
		formPanel.add(day); 
		formPanel.add(returnedDay); 
		formPanel.add(time);
		formPanel.add(returnedTime);
		formPanel.add(prc);
		formPanel.add(returnedPrice);
		formPanel.add(bal);
		formPanel.add(returnedReservationBAL);
		formPanel.add(tick);
		formPanel.add(returnedTicket);
		formPanel.add(backRButton);
		formPanel.add(saveRButton);


		//add the form panel to the center of the screen
		resEditCenter.add(formPanel);

		// add another squish for centering because these alignment options
		// are beyond me :)
		JPanel squish22 = new JPanel();
		squish22.setBackground(Color.BLACK);

		//resEditCenter.add(squish22);

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
		getRButton.addActionListener(this);
		saveRButton.addActionListener(this);
		backRButton.addActionListener(this);
		deleteRButton.addActionListener(this);
	}

	// Necessary for connection to db, I didn't write this, it's referenced from the recitation demos
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
		the following is a function that takes the button push
		and then does something depending on which option was selected.
	*/
	
	public void actionPerformed(ActionEvent e)  {
		Object source = e.getSource();  
		
		if (source == getRButton) { // when agent 'clicks' this button, the program will:
			// start db connection
			connectionDB();

			// lock inputs
			inputReservationNumber.setEditable(false);
			inputPayment.setEditable(false);

			// PART 1 ****
			// generate a string SELECT statement to get the reservation attributes of the inputReservation number
			StringBuilder sb = new StringBuilder();
			if (!(inputReservationNumber.getText().equals("") && inputPayment.getText().equals(""))) {
				sb.append("SELECT * FROM Reservations WHERE reserv_no = '" + inputReservationNumber.getText() + "';");
			} else {
				System.out.println("Empty field");
			}
			// check
			// System.out.println(sb.toString());

			// create a ResultSet to hold the returned attributes and execute a query with the try/catch block record results
			ResultSet resultSet = null;
			try {
				try {
					resultSet = st.executeQuery(sb.toString());
				} catch (NullPointerException np) {
					System.out.println(np);
				}
			} catch (SQLException sq) {
				System.out.println(sq);
			}

			String str = "";
			// loop through the resultSet and setText in return fields that match attribute names.
			try {
				while (resultSet.next()) {
					returnedDay.setText(resultSet.getString("day"));
					returnedTime.setText(resultSet.getString("time"));
					returnedPrice.setText(resultSet.getString("price"));
					cust_id = resultSet.getInt("customer_id");
					str = resultSet.getString("balance");
				}
			} catch (SQLException setTextE) {
				System.out.println(setTextE);
			}

			// cast the balance field(string) to double and subtract the payment from the balance
			str = str.replaceAll("[^\\d.]", "");
			double newBalance = Double.valueOf(str) - Double.valueOf(inputPayment.getText());
			returnedReservationBAL.setText(String.valueOf(newBalance));

			// PART 2 ****
			// generate a SELECT query to obtain a ticket_no, if one exists
			StringBuilder sbt = new StringBuilder();
			sbt.append("SELECT ticket_no FROM tickets WHERE reserv_no = '" + inputReservationNumber.getText() + "';");
			// System.out.println(sbt);

			// create a ResultSet to hold the ticket_no and execute a query with the try/catch block record results
			ResultSet resultSetTicket = null;
			try {
				try {
					resultSetTicket = st.executeQuery(sbt.toString());
				} catch (NullPointerException np) {
					System.out.println(np);
				}
			} catch (SQLException sq) {
				System.out.println(sq);
			}

			// loop through the resultSet and setText in return fields that match attribute names.
			try {
				while (resultSetTicket.next()) {
					returnedTicket.setEditable(true);
					returnedTicket.setText(resultSetTicket.getString("ticket_no"));
				}
			} catch (SQLException setTextE) {
				System.out.println(setTextE);
			}

			// PART 3 ****
			// using the attributes obtained from the previous functionality, 
			// create another SELECT statement to retrieve the customer's name using their id number
			StringBuilder sbname = new StringBuilder();
			sbname.append("SELECT first_name, last_name FROM Customers WHERE customer_id = '" + cust_id + "';");
			// System.out.println("upper button select: " + sbname.toString());

			// create a ResultSet to hold the returned attributes and execute a query with the try/catch block record results
			ResultSet resultSetname = null;
			try {
				try {
					resultSetname = st.executeQuery(sbname.toString());
				} catch (NullPointerException np) {
					System.out.println(np);
				}
			} catch (SQLException sq) {
				System.out.println(sq);
			}

			// loop through the resultSet and setText in return fields that match attribute names
			try {
				while (resultSetname.next()) {
					returnedFname.setText(resultSetname.getString("first_name"));
					returnedLname.setText(resultSetname.getString("last_name"));
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
		} else if (source == saveRButton){    
			// start db connection
			connectionDB();
			System.out.println(cust_id);
			
			// PART 1 ****
			// check if first/last name was changed and update the customer table accordingly
			StringBuilder custTableSel = new StringBuilder();
			custTableSel.append("SELECT first_name, last_name FROM Customers WHERE customer_id = '" + cust_id + "';");

			// create a ResultSet to hold the returned attributes and execute a query with the try/catch block record results
			ResultSet resultSetname = null;
			try {
				try {
					resultSetname = st.executeQuery(custTableSel.toString());
				} catch (NullPointerException np) {
					System.out.println(np);
				}
			} catch (SQLException sq) {
				System.out.println(sq);
			}

			// loop through the resultSet and check if the first/last name was changed
			boolean fNameChanged = false;
			boolean lNameChanged = false;
			try {
				while (resultSetname.next()) {
					if (!(resultSetname.getString("first_name")).equals(returnedFname.getText())) fNameChanged = true;
					if (!(resultSetname.getString("last_name")).equals(returnedLname.getText())) lNameChanged = true;
				}
			} catch (SQLException setTextE) {
				System.out.println(setTextE);
			}

			Statement nameSt = null;
			try {
				nameSt = conn.createStatement();
				if (fNameChanged && lNameChanged) {
					nameSt.addBatch("UPDATE Customers SET first_name = '" + returnedFname.getText() + "' WHERE customer_id = '" + cust_id + "'");
					nameSt.addBatch("UPDATE Customers SET last_name = '" + returnedLname.getText() + "' WHERE customer_id = '" + cust_id + "'");
					int[] recordsAffected = nameSt.executeBatch();
				} else if (fNameChanged) {
					nameSt.addBatch("UPDATE Customers SET first_name = '" + returnedFname.getText() + "' WHERE customer_id = '" + cust_id + "'");
					int[] recordsAffected = nameSt.executeBatch();
				} else if (lNameChanged) {
					nameSt.addBatch("UPDATE Customers SET last_name = '" + returnedLname.getText() + "' WHERE customer_id = '" + cust_id + "'");
					int[] recordsAffected = nameSt.executeBatch();
				}
			} catch (SQLException updE) {
				System.out.println("Customer update failed: " + updE);
			} finally {
				try {
					if (nameSt != null) nameSt.close();
				} catch (SQLException stE) {
					System.out.println(stE);
				}
			}

			// PART 2 ****
			Statement statement = null;
			// generate and execute an batch UPDATE statement based on .getText for all attributes
			try {
				statement = conn.createStatement();
				statement.addBatch("UPDATE Reservations SET balance = '" + returnedReservationBAL.getText() + "' WHERE reserv_no = '" + inputReservationNumber.getText() + "'");
				statement.addBatch("UPDATE Tickets SET ticket_no = '" + returnedTicket.getText() + "' WHERE reserv_no = '" + inputReservationNumber.getText() + "'");
				int[] recordsAffected = statement.executeBatch();
			} catch (SQLException updE) {
				System.out.println(updE);
			} finally {
				try {
					if (statement != null) statement.close();
				} catch (SQLException stE) {
					System.out.println(stE);
				}
			}

			// if the balance is now 0, generate an INSERT statement to add a new TICKET
			StringBuilder ins = new StringBuilder();
			StringBuilder sel = new StringBuilder();
			if (returnedTicket.getText().equals("xx") && (Double.valueOf(returnedReservationBAL.getText()) == 0.0)) {
				ins.append("INSERT INTO Tickets (reserv_no) VALUES ('" + inputReservationNumber.getText() + "');");
				sel.append("SELECT ticket_no FROM Tickets WHERE reserv_no = '" + inputReservationNumber.getText() + "';");

				// create a ResultSet to hold the returned attributes and execute a query with the try/catch block record results
				ResultSet resultSetTic = null;
				try {
					st.executeUpdate(ins.toString());
					try {
						resultSetTic = st.executeQuery(sel.toString()); 
					} catch (NullPointerException np) {
						System.out.println(np);
					}
				} catch (SQLException sq) {
					System.out.println(sq);
				}
				// System.out.println("Insert ticket successful");

				// loop through the resultSet and setText the ticket number
				try {
					while (resultSetTic.next()) {
						returnedTicket.setText(resultSetTic.getString("ticket_no"));
					}
				} catch (SQLException setTextE) {
					System.out.println(setTextE);
				}
			}

			// close connection
			try{
				conn.close();
			} catch (SQLException df){
				System.out.println(df);
			}
		} else if (source == deleteRButton) {
			// start db connection
			connectionDB();

			// generate a statement to delete reservation where res# = res#
			StringBuilder deleteRes = new StringBuilder();
			deleteRes.append("DELETE FROM Reservations WHERE reserv_no = '" + inputReservationNumber.getText() + "';");

			// check if ticket is present
			StringBuilder ticket = new StringBuilder();
			ticket.append("SELECT ticket_no FROM Tickets WHERE reserv_no = '" + inputReservationNumber.getText() + "';");

			// create a ResultSet to hold the returned attributes and execute a query with the try/catch block record results
			ResultSet resultSetTicket = null;
			try {
				try {
					resultSetTicket = st.executeQuery(ticket.toString());
				} catch (NullPointerException np) {
					System.out.println(np);
				}
			} catch (SQLException sq) {
				System.out.println(sq);
			}

			// loop through the resultSet and check if the first/last name was changed
			boolean ticketPresent = false;
			String myticket = "";
			try {
				while (resultSetTicket.next()) {
					ticketPresent = true;
					myticket = resultSetTicket.getString("ticket_no");
				}
			} catch (SQLException setTextE) {
				System.out.println(setTextE);
			}

			// generate a statement to delete ticket where res# = res#
			StringBuilder deleteTicket = new StringBuilder();
			deleteTicket.append("DELETE FROM Tickets WHERE ticket_no = '" + myticket + "';");
			// System.out.println("ticket present: " + ticketPresent);

			// try/catch block to execute Update
			try {
				st.executeUpdate(deleteRes.toString());
				if (ticketPresent) st.executeUpdate(deleteTicket.toString());
			} catch (SQLException sq) {
				System.out.println(sq);
			}

			// update text fields to appropriate 'deleted' messages
			inputReservationNumber.setText("deleted");
			inputPayment.setText("deleted");
			returnedFname.setText("deleted");
			returnedLname.setText("deleted");
			returnedDay.setText("deleted");
			returnedTime.setText("deleted");
			returnedPrice.setText("deleted");
			returnedReservationBAL.setText("deleted");
			if (ticketPresent) returnedTicket.setText("deleted");

			// close connection
			try{
				conn.close();
			} catch (SQLException df){
				System.out.println(df);
			}
		} else if (source == backRButton) {
			AgentScreen agentS = new AgentScreen(guiPush, resEditCenter, passwordp, userp);
		}
	}
}
