package Swing_UI;

import java.awt.*;
import java.sql.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
import LongTermDBManagement.DBConnection;

public class Delete extends JFrame {

    private static final long serialVersionUID = 1L;
    private JComboBox<String> fetchTypeBox, steelGradeBox, shiftBox, strandBox;
    private JTextField heatNumberField, castSeqField;
    private JDateChooser startDateChooser, endDateChooser;
    private JTable resultTable;
    private DefaultTableModel tableModel;

    public Delete() {
        setTitle("Delete Screen");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel filterPanel = new JPanel(new GridLayout(4, 4, 10, 5));

        fetchTypeBox = new JComboBox<>(new String[]{"Daily Data", "Weekly Wise Data", "Monthly Wise Data", "Yearly Wise Data"});
        steelGradeBox = new JComboBox<>(new String[]{"A1012", "A1011"});
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

        String[] columnNames = {
        		"SlabNumber", "HeatNumber", "SteelGrade",
                "CastSequenceNo", "Shift", "StrandNumber", "Date", "Delete"
        };

        tableModel = new DefaultTableModel(columnNames, 0);
        resultTable = new JTable(tableModel);
        resultTable.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer());
        resultTable.getColumnModel().getColumn(7).setCellEditor(new ButtonEditor());

        JScrollPane scrollPane = new JScrollPane(resultTable);
        add(scrollPane, BorderLayout.CENTER);

        JButton fetchBtn = new JButton("Fetch Data");
        fetchBtn.addActionListener(e -> fetchData());
        add(fetchBtn, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void fetchData() {
        tableModel.setRowCount(0);

        String heatNumber = heatNumberField.getText().trim();
        String castSeq = castSeqField.getText().trim();
        String steelGrade = (String) steelGradeBox.getSelectedItem();
        String shift = (String) shiftBox.getSelectedItem();
        String strand = (String) strandBox.getSelectedItem();
        java.util.Date startDate = startDateChooser.getDate();
        java.util.Date endDate = endDateChooser.getDate();

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

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
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
                row.add("Delete");
                tableModel.addRow(row);
            }

            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No matching data found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching data with filters");
        }
    }

    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
		private static final long serialVersionUID = 1L;

		public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            setText((value == null) ? "Delete" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
		private static final long serialVersionUID = 1L;
		private JButton button = new JButton();
        private String slabNumber;
        private int selectedRow;

        public ButtonEditor() {
            super(new JTextField());
            button.setOpaque(true);

            button.addActionListener(e -> {
                fireEditingStopped();

                int confirm = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to delete this row?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    deleteRowFromDatabase(selectedRow);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            selectedRow = row;
            slabNumber = tableModel.getValueAt(row, 0).toString();
            button.setText("Delete");
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "Delete";
        }

        private void deleteRowFromDatabase(int rowIndex) {
            try {
                String heatNumber = tableModel.getValueAt(rowIndex, 1).toString();
                String dateTimeStr = tableModel.getValueAt(rowIndex, 6).toString();

                try (Connection conn = DBConnection.getConnection()) {
                    conn.setAutoCommit(false);

                    // Delete from METAL_DETAILS first
                    String deleteDetails = "DELETE FROM METAL_DETAILS WHERE SLAB_NO = ?";
                    try (PreparedStatement stmtDetails = conn.prepareStatement(deleteDetails)) {
                        stmtDetails.setString(1, slabNumber);
                        stmtDetails.executeUpdate();
                    }

                    // Then delete from METAL_MASTER
                    String deleteMaster = "DELETE FROM METAL_MASTER WHERE SLAB_NO = ? AND HEAT_NO = ? AND DATE_TIME = ?";
                    try (PreparedStatement stmtMaster = conn.prepareStatement(deleteMaster)) {
                        stmtMaster.setString(1, slabNumber);
                        stmtMaster.setString(2, heatNumber);
                        stmtMaster.setTimestamp(3, Timestamp.valueOf(dateTimeStr));

                        int result = stmtMaster.executeUpdate();

                        if (result > 0) {
                            tableModel.removeRow(rowIndex);
                            conn.commit();
                            JOptionPane.showMessageDialog(null, "Row deleted successfully.");
                        } else {
                            conn.rollback();
                            JOptionPane.showMessageDialog(null, "Delete failed. Record not found.");
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error during delete: " + e.getMessage());
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting row: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new Delete();
    }
}