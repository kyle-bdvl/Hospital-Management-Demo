package com.hospital.ui;

import com.hospital.dao.PatientDAO;
import com.hospital.model.Patient;
import com.hospital.model.User;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PatientManagementFrame extends JFrame {
    private PatientDAO patientDAO;
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton, refreshButton;
    
    // Pagination Variables
    private int currentPage = 1;
    private final int PAGE_SIZE = 10;
    private int totalRecords = 0;
    private JLabel pageInfoLabel;
    private JButton prevButton, nextButton;

    public PatientManagementFrame(User user) {
        this.patientDAO = new PatientDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadPatients(); // Initial paginated load
        
        setTitle("Patient Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        String[] cols = {"ID", "Name", "Age", "Gender", "Phone", "Email", "Disease", "Blood Group", "Admission Date"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        patientTable = new JTable(tableModel);
        patientTable.setRowHeight(25);
        searchField = new JTextField(20);

        addButton = createStyledButton("Add Patient", new Color(40, 167, 69));
        editButton = createStyledButton("Edit Patient", new Color(0, 123, 255));
        deleteButton = createStyledButton("Delete Patient", new Color(220, 53, 69));
        refreshButton = createStyledButton("Refresh", new Color(108, 117, 125));

        prevButton = new JButton("Previous");
        nextButton = new JButton("Next");
        pageInfoLabel = new JLabel("Page 1");
        pageInfoLabel.setFont(new Font("Arial", Font.BOLD, 12));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 123, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel titleLabel = new JLabel("Patient Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Control Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchBar.add(new JLabel("Search: "));
        searchBar.add(searchField);
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> searchPatients());
        searchBar.add(searchBtn);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.add(addButton);
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        actionPanel.add(refreshButton);

        topPanel.add(searchBar, BorderLayout.WEST);
        topPanel.add(actionPanel, BorderLayout.EAST);

        // Pagination Bar
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        paginationPanel.add(prevButton);
        paginationPanel.add(pageInfoLabel);
        paginationPanel.add(nextButton);

        add(headerPanel, BorderLayout.NORTH);
        add(topPanel, BorderLayout.CENTER); // Using BorderLayout.CENTER for the top controls and table
        
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.add(topPanel, BorderLayout.NORTH);
        centerContainer.add(new JScrollPane(patientTable), BorderLayout.CENTER);
        centerContainer.add(paginationPanel, BorderLayout.SOUTH);
        
        add(centerContainer, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        addButton.addActionListener(e -> openAddDialog());
        editButton.addActionListener(e -> openEditDialog());
        deleteButton.addActionListener(e -> deletePatient());
        refreshButton.addActionListener(e -> { currentPage = 1; loadPatients(); });

        prevButton.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                loadPatients();
            }
        });

        nextButton.addActionListener(e -> {
            int maxPage = (int) Math.ceil((double) totalRecords / PAGE_SIZE);
            if (currentPage < maxPage) {
                currentPage++;
                loadPatients();
            }
        });
    }

    private void loadPatients() {
        SwingUtilities.invokeLater(() -> {
            totalRecords = patientDAO.getTotalPatientCount();
            int offset = (currentPage - 1) * PAGE_SIZE;
            List<Patient> list = patientDAO.getPatientsPaginated(PAGE_SIZE, offset);
            
            tableModel.setRowCount(0);
            for (Patient p : list) {
                tableModel.addRow(new Object[]{
                    p.getPatientId(), p.getName(), p.getAge(), p.getGender(), 
                    p.getPhone(), p.getEmail(), p.getDisease(), p.getBloodGroup(), p.getAdmissionDate()
                });
            }
            
            int totalPages = (int) Math.ceil((double) totalRecords / PAGE_SIZE);
            if (totalPages == 0) totalPages = 1;
            pageInfoLabel.setText("Page " + currentPage + " of " + totalPages);
            prevButton.setEnabled(currentPage > 1);
            nextButton.setEnabled(currentPage < totalPages);
        });
    }

    private void searchPatients() {
        String term = searchField.getText().trim();
        if (term.isEmpty()) { loadPatients(); return; }
        
        List<Patient> results = patientDAO.searchPatients(term);
        tableModel.setRowCount(0);
        for (Patient p : results) {
            tableModel.addRow(new Object[]{p.getPatientId(), p.getName(), p.getAge(), p.getGender(), p.getPhone(), p.getEmail(), p.getDisease(), p.getBloodGroup(), p.getAdmissionDate()});
        }
        pageInfoLabel.setText("Search Results (" + results.size() + ")");
        prevButton.setEnabled(false);
        nextButton.setEnabled(false);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton b = new JButton(text);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setBorderPainted(false);
        return b;
    }

    private void openAddDialog() {
        PatientDialog d = new PatientDialog(this, "Add Patient", null);
        d.setVisible(true);
        if (d.isConfirmed() && patientDAO.addPatient(d.getPatient())) loadPatients();
    }

    private void openEditDialog() {
        int row = patientTable.getSelectedRow();
        if (row == -1) return;
        int id = (int) tableModel.getValueAt(row, 0);
        Patient p = patientDAO.getPatientById(id);
        if (p != null) {
            PatientDialog d = new PatientDialog(this, "Edit Patient", p);
            d.setVisible(true);
            if (d.isConfirmed()) {
                Patient up = d.getPatient();
                up.setPatientId(id);
                if (patientDAO.updatePatient(up)) loadPatients();
            }
        }
    }

    private void deletePatient() {
        int row = patientTable.getSelectedRow();
        if (row == -1) return;
        int id = (int) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete patient?") == JOptionPane.YES_OPTION) {
            if (patientDAO.deletePatient(id)) loadPatients();
        }
    }
}