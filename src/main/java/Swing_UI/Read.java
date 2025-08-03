package Swing_UI;

import javax.swing.*;
import javax.swing.table.*;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.sql.*;
import java.util.*;

import LongTermDBManagement.DBConnection;

public class Read extends JFrame {
	 
	private static final long serialVersionUID = 1L;
	private JComboBox<String> fetchTypeBox, steelGradeBox, shiftBox, strandBox;
    private JTextField heatNumberField, castSeqField;
    private JDateChooser startDateChooser, endDateChooser;
    private JTable resultTable; //displays the data
    private DefaultTableModel tableModel; //holds the data
 
    public Read() {
        setTitle("ReadScreen");
        setSize(1000, 600);
        setLocationRelativeTo(null);
//        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
 
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
        fetchBtn.addActionListener(e -> fetchData()); //it is a callback function to fetch the data
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
        java.util.Date startDate = startDateChooser.getDate(); //object type
        java.util.Date endDate = endDateChooser.getDate();     //object type

        // Start building SQL query
        StringBuilder sql = new StringBuilder("SELECT * FROM METAL_MASTER WHERE 1=1"); //as string is immutable so we use StringBuilder to append as many times as we want. 1=1 (always true)
        Vector<Object> parameters = new Vector<>(); //re-sizable array

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
            PreparedStatement stmt = conn.prepareStatement(sql.toString());

            // Set parameters
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i)); //all the data are string type but date is object type that's why we us setObject,
                //in sql row starts from table index 1 but in java the index starts from 0 therefore i+1.
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {     //it will execute till there are no rows left
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
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching data with filters");
        }
    }

 
    // === Custom Button Renderer for SlabNumber ===
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
 
		public ButtonRenderer() {
            setOpaque(true);  //color of the button
        }
 
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString()); // Slab number shown
            return this;
        }
    }
 
    // === Custom Button Editor for SlabNumber ===
    class ButtonEditor extends DefaultCellEditor {
        /**
		 * 
		 */
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
 
    private void showSlabDetails(String slabNumber) {
        JDialog dialog = new JDialog(this, "Metal Details for Slab Number: " + slabNumber, true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());

        DefaultTableModel detailsModel = new DefaultTableModel();
        detailsModel.addColumn("Element Name");
        detailsModel.addColumn("Element Quantity");

        JTable detailsTable = new JTable(detailsModel);
        JScrollPane scrollPane = new JScrollPane(detailsTable);
        dialog.add(scrollPane, BorderLayout.CENTER);

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT QTY_H, QTY_Al, QTY_B, QTY_Ga, QTY_In, QTY_Tl FROM METAL_DETAILS WHERE SLAB_NO = ?"
            );
            ps.setString(1, slabNumber);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Map of element column to display name
                Map<String, String> elements = new LinkedHashMap<>();
                elements.put("QTY_H", "H");
                elements.put("QTY_Al", "Al");
                elements.put("QTY_B", "B");
                elements.put("QTY_Ga", "Ga");
                elements.put("QTY_In", "In");
                elements.put("QTY_Tl", "Tl");

                for (Map.Entry<String, String> entry : elements.entrySet()) {
                    double qty = rs.getDouble(entry.getKey());
                    if (qty > 0.0) {
                        detailsModel.addRow(new Object[]{entry.getValue(), qty});
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "No metal details found for this slab.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load slab details.");
        }

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }


 
    public static void main(String[] args) {
        new Read();
    }
}