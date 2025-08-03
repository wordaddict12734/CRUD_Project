package Swing_UI;

import javax.swing.*;
import javax.swing.table.*;
import com.toedter.calendar.JDateChooser;//import jar updating time and date

import java.awt.*;
import java.sql.*;
import java.util.Vector;//for dynamic memory allocation

import LongTermDBManagement.DBConnection;//database connection

public class Update extends JFrame {
	 
	private static final long serialVersionUID = 1L;//final means a variable which cannot be changed. \
	//this ensures that the serial version uid should remain fixed for the life of the class. 
	//serial version uid is the conventional variable which uses for storing the objects data of the class in java with serialized(save the file) by uid and at the time of deserialisation(load file) java compares the stored uid with the current access uid. 
	private JComboBox<String> fetchTypeBox, steelGradeBox, shiftBox, strandBox;//GUI components declaration.
    private JTextField heatNumberField, castSeqField;
    private JDateChooser startDateChooser, endDateChooser;
    private JTable resultTable;
    private DefaultTableModel tableModel;//default table model handles the data behind the table.
 
    public Update() {
        setTitle("UpdateScreen");
        setSize(1000, 600);
        setLocationRelativeTo(null);//center window on the screen
//        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());//the layout sets the window into four segments.
 
        // Filter Panel
        JPanel filterPanel = new JPanel(new GridLayout(4, 4, 10, 5));
 
        fetchTypeBox = new JComboBox<>(new String[]{"Daily Data", "Weekly Wise Data", "Monthly Wise Data", "Yearly Wise Data"});
        steelGradeBox = new JComboBox<>(new String[]{"A1011", "A1012"});
        shiftBox = new JComboBox<>(new String[]{"Morning", "Afternoon", "Night"});
        strandBox = new JComboBox<>(new String[]{"1", "2", "3"});
 
        startDateChooser = new JDateChooser();
        startDateChooser.setDateFormatString("yyyy-MM-dd");
 
        endDateChooser = new JDateChooser();
        endDateChooser.setDateFormatString("yyyy-MM-dd");
 
        heatNumberField = new JTextField();
        castSeqField = new JTextField();
 
        filterPanel.add(new JLabel("Fetch Type"));
        filterPanel.add(fetchTypeBox);
        filterPanel.add(new JLabel("Start Date"));
        filterPanel.add(startDateChooser);
 
        filterPanel.add(new JLabel("End Date"));
        filterPanel.add(endDateChooser);
        filterPanel.add(new JLabel("Heat Number"));
        filterPanel.add(heatNumberField);
 
        filterPanel.add(new JLabel("Steel Grade"));
        filterPanel.add(steelGradeBox);
        filterPanel.add(new JLabel("Cast Sequence No"));
        filterPanel.add(castSeqField);
 
        filterPanel.add(new JLabel("Shift"));
        filterPanel.add(shiftBox);
        filterPanel.add(new JLabel("Strand Number"));
        filterPanel.add(strandBox);
 
        add(filterPanel, BorderLayout.NORTH);
 
        // Table Panel
        String[] columnNames = {
            "SlabNumber", "HeatNumber", "SteelGrade",
            "CastSequenceNo", "Shift", "StrandNumber", "Date"
        };
 
        tableModel = new DefaultTableModel(columnNames, 0);
        resultTable = new JTable(tableModel);
 
        // SlabNumber: Custom Renderer & Editor
        resultTable.getColumnModel().getColumn(0).setCellRenderer(new ButtonRenderer());//display buttons inside table cells
        resultTable.getColumnModel().getColumn(0).setCellEditor(new ButtonEditor());//it makes the button functional
 
        JScrollPane scrollPane = new JScrollPane(resultTable);
        add(scrollPane, BorderLayout.CENTER);
 
        // Bottom Button
        JButton fetchBtn = new JButton("Fetch Data");
        fetchBtn.addActionListener(e -> fetchData());
        add(fetchBtn, BorderLayout.SOUTH);
 
        setVisible(true);
    }
 
    private void fetchData() {
        tableModel.setRowCount(0); // clear existing rows

        // Collect filter values
        String heatNumber = heatNumberField.getText().trim();
        String castSeq = castSeqField.getText().trim();
        String steelGrade = (String) steelGradeBox.getSelectedItem();
        String shift = (String) shiftBox.getSelectedItem();
        String strand = (String) strandBox.getSelectedItem();
        java.util.Date startDate = startDateChooser.getDate();
        java.util.Date endDate = endDateChooser.getDate();

        // Start building SQL query
        StringBuilder sql = new StringBuilder("SELECT * FROM METAL_MASTER WHERE 1=1");
        Vector<Object> parameters = new Vector<>();

        if (!heatNumber.isEmpty()) {
            sql.append(" AND HEAT_NO = ?");
            parameters.add(heatNumber);
        }

        if (!castSeq.isEmpty()) {
            sql.append(" AND CAST_SEQUENCE_NO = ?");
            parameters.add(castSeq);
        }

        if (steelGrade != null && !steelGrade.isEmpty()) {
            sql.append(" AND STEEL_GRADE = ?");
            parameters.add(steelGrade);
        }

        if (shift != null && !shift.isEmpty()) {
            sql.append(" AND SHIFT = ?");
            parameters.add(shift);
        }

        if (strand != null && !strand.isEmpty()) {
            sql.append(" AND STRAND_NO = ?");
            parameters.add(strand);
        }

        if (startDate != null) {
            sql.append(" AND DATE_TIME >= ?");
            parameters.add(new java.sql.Date(startDate.getTime()));
        }

        if (endDate != null) {
            sql.append(" AND DATE_TIME <= ?");
            parameters.add(new java.sql.Date(endDate.getTime()));
        }

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql.toString());//is used in JDBC (Java Database Connectivity) to create a precompiled SQL statement that can then be executed against the database.

            // Set parameters
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));//all the data are string type but date is object type that's why we us setObject,
                //in sql row starts from table index 1 but in java the index starts from 0 therefore i+1.
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("SLAB_NO"));
                row.add(rs.getString("HEAT_NO"));
                row.add(rs.getString("STEEL_GRADE"));
                row.add(rs.getString("CAST_SEQUENCE_NO"));
                row.add(rs.getString("SHIFT"));
                row.add(rs.getString("STRAND_NO"));
                row.add(rs.getTimestamp("DATE_TIME"));
                tableModel.addRow(row);
            }
            
            if(tableModel.getRowCount()==0) {
            	JOptionPane.showMessageDialog(this, "No matching data found.");
            }
        } catch (Exception e) {//This is part of a try-catch block.

//It catches any exception that is a subclass of Exception (including SQLException, NullPointerException, etc.).
            e.printStackTrace();//This prints the full error stack trace to the console (standard error).
            JOptionPane.showMessageDialog(this, "Error fetching data with filters");
        }
    }

 
    // === Custom Button Renderer for SlabNumber ===
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
       
		private static final long serialVersionUID = 1L;//serialVersionUID is a unique identifier for a Serializable class.
//		It is used during the de-serialization process to ensure that the sender and receiver of a serialized object have compatible versions of the class.


 
		public ButtonRenderer() {
            setOpaque(true);
        }
 
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString()); // Slab number shown
            return this;
        }
    }
 
    // === Custom Button Editor for SlabNumber ===
    class ButtonEditor extends DefaultCellEditor {
       
		private static final long serialVersionUID = 1L;
		private JButton button = new JButton();
        private String slabNumber;
 
        public ButtonEditor() {
            super(new JTextField());
            button.setOpaque(true);
            button.addActionListener(e -> showSlabDetails(slabNumber));
        }
 
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            slabNumber = (value == null) ? "" : value.toString();
            button.setText(slabNumber);
            return button;
        }
 
        public Object getCellEditorValue() {
            return slabNumber;
        }
    }
 
    //Update main function....
    private void showSlabDetails(String slabNumber) {
        JDialog dialog = new JDialog(this, "Slab Details - " + slabNumber, true);

        // Use GridLayout: 2 columns (label, field), dynamic rows
        JPanel contentPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        // Input fields
        JTextField heatField = new JTextField();
        JTextField gradeField = new JTextField();
        JTextField strandField = new JTextField();
        JTextField shiftField = new JTextField();
        JTextField castSeqField = new JTextField();

        // Store element fields (editable)
        java.util.Map<String, JTextField> elementFields = new java.util.HashMap<>();

        try (Connection conn = DBConnection.getConnection()) {
            // Fetch data from METAL_MASTER
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM METAL_MASTER WHERE SLAB_NO = ?");
            ps.setString(1, slabNumber);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                heatField.setText(rs.getString("HEAT_NO"));
                gradeField.setText(rs.getString("STEEL_GRADE"));
                strandField.setText(rs.getString("STRAND_NO"));
                shiftField.setText(rs.getString("SHIFT"));
                castSeqField.setText(rs.getString("CAST_SEQUENCE_NO"));
            }

            rs.close();
            ps.close();

            // Add fixed fields to layout
            contentPanel.add(new JLabel("Heat Number:"));
            contentPanel.add(heatField);

            contentPanel.add(new JLabel("Steel Grade:"));
            contentPanel.add(gradeField);

            contentPanel.add(new JLabel("Strand Number:"));
            contentPanel.add(strandField);

            contentPanel.add(new JLabel("Shift:"));
            contentPanel.add(shiftField);

            contentPanel.add(new JLabel("Cast Sequence No:"));
            contentPanel.add(castSeqField);

            // Fetch and display METAL_DETAILS with fixed columns
            PreparedStatement ps2 = conn.prepareStatement(
                "SELECT QTY_H, QTY_Al, QTY_B, QTY_Ga, QTY_In, QTY_Tl FROM METAL_DETAILS WHERE SLAB_NO = ?"
            );
            ps2.setString(1, slabNumber);
            ResultSet rs2 = ps2.executeQuery();

            String[] elementLabels = { "H", "Al", "B", "Ga", "In", "Tl" };
            String[] columnNames   = { "QTY_H", "QTY_Al", "QTY_B", "QTY_Ga", "QTY_In", "QTY_Tl" };

            if (rs2.next()) {
                for (int i = 0; i < columnNames.length; i++) {
                    double quantity = rs2.getDouble(columnNames[i]);
                    JTextField quantityField = new JTextField(String.valueOf(quantity), 10);
                    quantityField.setEditable(true);
                    elementFields.put(elementLabels[i], quantityField);

                    contentPanel.add(new JLabel("Quantity for " + elementLabels[i] + ":"));
                    contentPanel.add(quantityField);
                }
            }

            rs2.close();
            ps2.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load slab details.");
            return;
        }

        // Update Button
        JButton updateBtn = new JButton("Update");
        updateBtn.addActionListener(e -> {
            try (Connection updateConn = DBConnection.getConnection()) {
                // Update METAL_MASTER
                PreparedStatement updateMaster = updateConn.prepareStatement(
                    "UPDATE METAL_MASTER SET HEAT_NO=?, STEEL_GRADE=?, STRAND_NO=?, SHIFT=?, CAST_SEQUENCE_NO=? WHERE SLAB_NO=?"
                );
                updateMaster.setString(1, heatField.getText().trim());
                updateMaster.setString(2, gradeField.getText().trim());
                updateMaster.setString(3, strandField.getText().trim());
                updateMaster.setString(4, shiftField.getText().trim());
                updateMaster.setString(5, castSeqField.getText().trim());
                updateMaster.setString(6, slabNumber);
                updateMaster.executeUpdate();
                updateMaster.close();

                // Update METAL_DETAILS (fixed schema)
                PreparedStatement updateDetail = updateConn.prepareStatement(
                    "UPDATE METAL_DETAILS SET QTY_H=?, QTY_Al=?, QTY_B=?, QTY_Ga=?, QTY_In=?, QTY_Tl=? WHERE SLAB_NO=?"
                );
                updateDetail.setDouble(1, Double.parseDouble(elementFields.get("H").getText().trim()));
                updateDetail.setDouble(2, Double.parseDouble(elementFields.get("Al").getText().trim()));
                updateDetail.setDouble(3, Double.parseDouble(elementFields.get("B").getText().trim()));
                updateDetail.setDouble(4, Double.parseDouble(elementFields.get("Ga").getText().trim()));
                updateDetail.setDouble(5, Double.parseDouble(elementFields.get("In").getText().trim()));
                updateDetail.setDouble(6, Double.parseDouble(elementFields.get("Tl").getText().trim()));
                updateDetail.setString(7, slabNumber);
                updateDetail.executeUpdate();
                updateDetail.close();

                JOptionPane.showMessageDialog(dialog, "Updated successfully.");
                dialog.dispose();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Update failed: " + ex.getMessage());
            }
        });

        // Final layout: add everything to dialog
        dialog.getContentPane().setLayout(new BorderLayout(10, 10));
        dialog.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
        dialog.add(updateBtn, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }


    public static void main(String[] args) {
        new Update();
    }
}