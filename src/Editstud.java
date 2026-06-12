import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Editstud extends JFrame {
    private JTextField rollField, nameField, surnameField;
    private JComboBox<String> groupBox;
    private JButton searchBtn, updateBtn, deleteBtn, backBtn, deleteGroupBtn, addGroupBtn;
    private Connection con;

    public Editstud(Connection con) {
        this.con = con;

        setTitle("Update Student Data");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // ===== FULL BACKGROUND IMAGE =====
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/images/editbg.jpg"));
        Image bgImage = bgIcon.getImage();
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // ===== HEADER =====
        ImageIcon editIconRaw = new ImageIcon(getClass().getResource("/images/edit.png"));
        Image editImg = editIconRaw.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
        JLabel header = new JLabel(" Update Student Data", new ImageIcon(editImg), SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setForeground(Color.WHITE);
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        backgroundPanel.add(header, BorderLayout.NORTH);

        // ===== MAIN PANEL =====
        JPanel bgPanel = new JPanel(new GridBagLayout());
        bgPanel.setOpaque(false);
        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 15);
        Color labelColor = Color.WHITE;
        Color fieldBg = new Color(200, 225, 255);
        Color borderColor = new Color(150, 190, 240);

        // ===== FIELDS =====
        JLabel rollLabel = new JLabel("Roll No:");
        rollLabel.setFont(labelFont); rollLabel.setForeground(labelColor);
        rollField = createRoundedField(fieldFont, fieldBg, borderColor);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(labelFont); nameLabel.setForeground(labelColor);
        nameField = createRoundedField(fieldFont, fieldBg, borderColor);

        JLabel surnameLabel = new JLabel("Surname:");
        surnameLabel.setFont(labelFont); surnameLabel.setForeground(labelColor);
        surnameField = createRoundedField(fieldFont, fieldBg, borderColor);

        gbc.gridx = 0; gbc.gridy = 0; cardPanel.add(rollLabel, gbc);
        gbc.gridx = 1; cardPanel.add(rollField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; cardPanel.add(nameLabel, gbc);
        gbc.gridx = 1; cardPanel.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; cardPanel.add(surnameLabel, gbc);
        gbc.gridx = 1; cardPanel.add(surnameField, gbc);

        // ===== GROUP DROPDOWN =====
        JLabel groupLabel = new JLabel("Group:");
        groupLabel.setFont(labelFont); groupLabel.setForeground(labelColor);
        gbc.gridx = 0; gbc.gridy = 3;
        cardPanel.add(groupLabel, gbc);

        groupBox = new JComboBox<>();
        groupBox.setFont(fieldFont);
        groupBox.setPreferredSize(new Dimension(300, 28));
        loadGroups();

        // Add + Delete Group Buttons
        ImageIcon addIcon = new ImageIcon(new ImageIcon(getClass().getResource("/images/plus.png")).getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH));
        addGroupBtn = createStyledButton(" Add", new Color(70, 130, 180), addIcon);
        addGroupBtn.addActionListener(e -> addNewGroup());

        ImageIcon delIcon = new ImageIcon(new ImageIcon(getClass().getResource("/images/delete.png")).getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH));
        deleteGroupBtn = createStyledButton(" Delete", new Color(220, 20, 60), delIcon);
        deleteGroupBtn.addActionListener(e -> deleteGroup());

        JPanel groupBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        groupBtnPanel.setOpaque(false);
        groupBtnPanel.add(addGroupBtn);
        groupBtnPanel.add(deleteGroupBtn);

        JPanel groupPanel = new JPanel(new BorderLayout(5, 5));
        groupPanel.setOpaque(false);
        groupPanel.add(groupBox, BorderLayout.CENTER);
        groupPanel.add(groupBtnPanel, BorderLayout.EAST);

        gbc.gridx = 1; gbc.gridy = 3;
        cardPanel.add(groupPanel, gbc);

        // ===== BUTTON PANEL =====
        JPanel btnWrapper = new JPanel(new GridLayout(2, 2, 15, 15));
        btnWrapper.setOpaque(false);

        ImageIcon searchIcon = new ImageIcon(new ImageIcon(getClass().getResource("/images/search.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
        ImageIcon updateIcon = new ImageIcon(new ImageIcon(getClass().getResource("/images/update.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
        ImageIcon deleteIcon = new ImageIcon(new ImageIcon(getClass().getResource("/images/delete.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
        ImageIcon backIcon = new ImageIcon(new ImageIcon(getClass().getResource("/images/back.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));

        searchBtn = createStyledButton(" Search", new Color(65, 105, 225), searchIcon);
        updateBtn = createStyledButton(" Update", new Color(46, 139, 87), updateIcon);
        deleteBtn = createStyledButton(" Delete", new Color(220, 20, 60), deleteIcon);
        backBtn = createStyledButton(" Back", new Color(255, 140, 0), backIcon);

        btnWrapper.add(searchBtn);
        btnWrapper.add(updateBtn);
        btnWrapper.add(deleteBtn);
        btnWrapper.add(backBtn);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        cardPanel.add(btnWrapper, gbc);

        bgPanel.add(cardPanel, new GridBagConstraints());
        backgroundPanel.add(bgPanel, BorderLayout.CENTER);

        // ===== BUTTON ACTIONS =====
        searchBtn.addActionListener(e -> searchStudent());
        updateBtn.addActionListener(e -> updateStudent());
        deleteBtn.addActionListener(e -> deleteStudent());
        backBtn.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void searchStudent() {
        String roll = rollField.getText().trim();
        if (roll.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Roll Number to search.");
            return;
        }
        try {
            String query = "SELECT * FROM students WHERE roll_no = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, roll);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                surnameField.setText(rs.getString("surname"));
                int grpId = rs.getInt("grp_id");
                for (int i = 0; i < groupBox.getItemCount(); i++) {
                    if (groupBox.getItemAt(i).startsWith(grpId + " -")) {
                        groupBox.setSelectedIndex(i);
                        break;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "No student found with Roll No: " + roll);
            }
            pst.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching student: " + e.getMessage());
        }
    }

    private void updateStudent() {
        String roll = rollField.getText().trim();
        String name = nameField.getText().trim();
        String surname = surnameField.getText().trim();

        if (roll.isEmpty() || name.isEmpty() || surname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }

        if (!name.matches("[A-Za-z ]+")) {
            JOptionPane.showMessageDialog(this, "❌ Name must contain only letters!");
            return;
        }
        if (!surname.matches("[A-Za-z ]+")) {
            JOptionPane.showMessageDialog(this, "❌ Surname must contain only letters!");
            return;
        }

        try {
            String selectedGroup = (String) groupBox.getSelectedItem();
            int grpId = Integer.parseInt(selectedGroup.split(" - ")[0]);

            String query = "UPDATE students SET name=?, surname=?, grp_id=? WHERE roll_no=?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, name);
            pst.setString(2, surname);
            pst.setInt(3, grpId);
            pst.setString(4, roll);
            int rows = pst.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "✅ Student updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "No student found with Roll No: " + roll);
            }
            pst.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating student: " + e.getMessage());
        }
    }

    private void deleteStudent() {
        String roll = rollField.getText().trim();
        if (roll.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Roll Number to delete.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete student with Roll No: " + roll + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            String query = "DELETE FROM students WHERE roll_no = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, roll);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "✅ Student deleted successfully!");
                rollField.setText("");
                nameField.setText("");
                surnameField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "No student found with Roll No: " + roll);
            }
            pst.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting student: " + e.getMessage());
        }
    }

    private void deleteGroup() {
        if (groupBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "⚠️ No group selected!");
            return;
        }

        String selected = groupBox.getSelectedItem().toString();
        int grpId = Integer.parseInt(selected.split(" - ")[0]);
        String projectName = selected.split(" - ")[1];

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete group \"" + projectName + "\"?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            PreparedStatement check = con.prepareStatement("SELECT COUNT(*) FROM students WHERE grp_id = ?");
            check.setInt(1, grpId);
            ResultSet rs = check.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            check.close();

            if (count > 0) {
                JOptionPane.showMessageDialog(this, "❌ Cannot delete this group because it has students assigned!");
                return;
            }

            PreparedStatement pst = con.prepareStatement("DELETE FROM student_groups WHERE grp_id = ?");
            pst.setInt(1, grpId);
            int rows = pst.executeUpdate();
            pst.close();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "✅ Group \"" + projectName + "\" deleted successfully!");
                loadGroups();
            } else {
                JOptionPane.showMessageDialog(this, "⚠️ Group not found.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Error deleting group: " + ex.getMessage());
        }
    }

    private void addNewGroup() {
        JTextField groupNameField = new JTextField();
        int choice = JOptionPane.showConfirmDialog(this, groupNameField, "Enter New Project Name", JOptionPane.OK_CANCEL_OPTION);
        if (choice == JOptionPane.OK_OPTION) {
            String groupName = groupNameField.getText().trim();
            if (groupName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Project name cannot be empty!");
                return;
            }
            try {
                PreparedStatement check = con.prepareStatement("SELECT COUNT(*) FROM student_groups WHERE project_name = ?");
                check.setString(1, groupName);
                ResultSet crs = check.executeQuery(); crs.next();
                if (crs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, "⚠️ Group already exists!");
                    check.close();
                    return;
                }
                check.close();

                PreparedStatement nextPst = con.prepareStatement("SELECT COALESCE(MAX(grp_no), 0) + 1 AS next_no FROM student_groups");
                ResultSet nrs = nextPst.executeQuery(); nrs.next();
                int nextGrpNo = nrs.getInt("next_no");
                nextPst.close();

                PreparedStatement pst = con.prepareStatement(
                        "INSERT INTO student_groups (grp_no, project_name) VALUES (?, ?)");
                pst.setInt(1, nextGrpNo);
                pst.setString(2, groupName);
                pst.executeUpdate();
                pst.close();

                JOptionPane.showMessageDialog(this, "✅ New group added: " + groupName);
                loadGroups();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error adding group: " + ex.getMessage());
            }
        }
    }

    // ===== STYLING HELPERS =====
    private JTextField createRoundedField(Font font, Color bgColor, Color borderColor) {
        JTextField field = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2.dispose();
            }
            @Override public void setOpaque(boolean isOpaque) { super.setOpaque(false); }
        };
        field.setFont(font);
        field.setForeground(Color.DARK_GRAY);
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        field.setOpaque(false);
        return field;
    }

    private JButton createStyledButton(String text, Color color, ImageIcon icon) {
        JButton btn = new JButton(text, icon);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { btn.setBackground(color.darker()); }
            public void mouseExited(MouseEvent evt) { btn.setBackground(color); }
        });
        return btn;
    }

    private void loadGroups() {
        try {
            String query = "SELECT grp_id, project_name FROM student_groups ORDER BY grp_no";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            groupBox.removeAllItems();
            while (rs.next()) {
                int id = rs.getInt("grp_id");
                String name = rs.getString("project_name");
                groupBox.addItem(id + " - " + name);
            }
            pst.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading groups: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/student_management", "root", "root");
            new Editstud(con).setVisible(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "DB Connection failed: " + ex.getMessage());
        }
    }
}
