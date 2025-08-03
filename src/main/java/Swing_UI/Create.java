package Swing_UI;

import LongTermDBManagement.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class Create extends JFrame {

	private static final long serialVersionUID = 1L;
	private JComboBox<String> steelGradeField, shiftField,strandField;
	private JTextField slabNumberField, heatNumberField, castSeqField;
	private JTextField qtyHField, qtyAlField, qtyBField, qtyGaField, qtyInField, qtyTlField;

	public Create() {
		setTitle("Create Metal Entry");
		setSize(400, 450);
//		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new GridLayout(14, 2, 8, 8));
		
		
		// Master Table Inputs
		add(new JLabel("Slab Number:"));
		slabNumberField = new JTextField();
		add(slabNumberField);

		add(new JLabel("Heat Number:"));
		heatNumberField = new JTextField();
		add(heatNumberField);

		add(new JLabel("Steel Grade:"));
		String[] steelGrades= {"A1012", "A1011", "A3012"};
		steelGradeField = new JComboBox<>(steelGrades);
//		steelGradeField = new JTextField();
		add(steelGradeField);

		add(new JLabel("Cast Sequence No:"));
		castSeqField = new JTextField();
		add(castSeqField);

		add(new JLabel("Shift:"));
		String[] shifts ={ "Morning", "Afternoon", "Evening" };
        shiftField = new JComboBox<>(shifts);
		add(shiftField);

		add(new JLabel("Strand Number:"));
		String[] strandNumbers ={ "1", "2", "3","4","5","6","7"};
		strandField = new JComboBox<>(strandNumbers);
//		strandField = new JTextField();
		add(strandField);

		// Details Table Inputs
		add(new JLabel("Quantity of H:"));
		qtyHField = new JTextField();
		add(qtyHField);
		
		add(new JLabel("Quantity of Al:"));
		qtyAlField = new JTextField();
		add(qtyAlField);
		
		add(new JLabel("Quantity of B:"));
		qtyBField = new JTextField();
		add(qtyBField);
		
		add(new JLabel("Quantity of Ga:"));
		qtyGaField = new JTextField();
		add(qtyGaField);
		
		add(new JLabel("Quantity of In:"));
		qtyInField = new JTextField();
		add(qtyInField);
		
		add(new JLabel("Quantity of Tl:"));
		qtyTlField = new JTextField();
		add(qtyTlField);


		JButton submitBtn = new JButton("Submit");
		add(submitBtn);

		JButton clearBtn = new JButton("Clear");
		add(clearBtn);

		submitBtn.addActionListener(e -> insertData());//ActionListener,actionPerformed
		clearBtn.addActionListener(e -> clearFields());

		setVisible(true);
	}

	private void insertData() {
		String slab = slabNumberField.getText();
		
		String heat = heatNumberField.getText();
		String grade = (String)steelGradeField.getSelectedItem();
		String cast = castSeqField.getText();
		String shift = (String)shiftField.getSelectedItem();
		String strand = (String)strandField.getSelectedItem();
		if(slab.isEmpty()||heat.isEmpty()||cast.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Field cannot be empty!");
			return;
		}
		
		double qtyH = safeParseDouble(qtyHField.getText());//safeParseDouble helps avoid crashes and safely return 0.0 if the input is invalid or empty.
	    double qtyAl = safeParseDouble(qtyAlField.getText());
	    double qtyB = safeParseDouble(qtyBField.getText());
	    double qtyGa = safeParseDouble(qtyGaField.getText());
	    double qtyIn = safeParseDouble(qtyInField.getText());
	    double qtyTl = safeParseDouble(qtyTlField.getText());


		try (Connection conn = DBConnection.getConnection()) {
			// Insert into MetalMaster
			String sqlMaster = "INSERT INTO METAL_MASTER (SLAB_NO, HEAT_NO, STEEL_GRADE, CAST_SEQUENCE_NO, SHIFT, STRAND_NO) VALUES (?, ?, ?, ?, ?, ?)";
			PreparedStatement psMaster = conn.prepareStatement(sqlMaster);
			psMaster.setString(1, slab);
			psMaster.setString(2, heat);
			psMaster.setString(3, grade);
			psMaster.setString(4, cast);
			psMaster.setString(5, shift);
			psMaster.setString(6, strand);
			psMaster.executeUpdate();

			// Insert into MetalDetails
			String sqlDetails = "INSERT INTO METAL_DETAILS (SLAB_NO, QTY_H,QTY_Al, QTY_B, QTY_Ga, QTY_In, QTY_Tl) VALUES(?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement psDetails = conn.prepareStatement(sqlDetails);
			psDetails.setString(1, slab);
			psDetails.setDouble(2, qtyH);
			psDetails.setDouble(3, qtyAl);
			psDetails.setDouble(4, qtyB);
			psDetails.setDouble(5, qtyGa);
			psDetails.setDouble(6, qtyIn);
			psDetails.setDouble(7, qtyTl);
			psDetails.executeUpdate();

			JOptionPane.showMessageDialog(this, "Data inserted successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error inserting data: " + e.getMessage());
		}
	}
	
	 private double safeParseDouble(String text) {
	        if (text == null || text.trim().isEmpty()) {
	            return 0.0;
	        }
	        try {
	            return Double.parseDouble(text.trim());
	        } catch (NumberFormatException e) {
	            return 0.0;
	        }
	    }


	private void clearFields() {
		slabNumberField.setText("");
		heatNumberField.setText("");
		steelGradeField.setSelectedItem(0);
		castSeqField.setText("");
		shiftField.setSelectedItem("");
		strandField.setSelectedItem("");
		qtyHField.setText("");
	    qtyAlField.setText("");
	    qtyBField.setText("");
	    qtyGaField.setText("");
	    qtyInField.setText("");
	    qtyTlField.setText("");
	}

	public static void main(String[] args) {
		new Create();
	}
}