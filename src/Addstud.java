import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Addstud extends JFrame implements ActionListener {
    private JTextField nameField, surnameField, rollField;
    private JComboBox<String> groupDropdown;
    private JButton addBtn, backBtn, addGroupBtn;
    private Connection con;

    public Addstud(Connection con) {
        this.con = con;

        setTitle("+ Add New Student");
        setSize(550, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // ===== FULL BACKGROUND IMAGE =====
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/images/add1.jpg"));
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
        ImageIcon studentIconRaw = new ImageIcon(getClass().getResource("/images/student.png"));
        Image studentImg = studentIconRaw.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        JLabel header = new JLabel(" Add New Student", new ImageIcon(studentImg), SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setForeground(Color.WHITE);
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        backgroundPanel.add(header, BorderLayout.NORTH);

        // ===== MAIN FORM PANEL =====
        JPanel bgPanel = new JPanel(new GridBagLayout());
        bgPanel.setOpaque(false);

        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 15);

        Color labelColor = Color.WHITE;
        Color fieldBg = new Color(200, 225, 255);
        Color borderColor = new Color(150, 190, 240);

        // ===== Fields =====
        JLabel nameLabel = new JLabel("First Name:");
        nameLabel.setFont(labelFont);
        nameLabel.setForeground(labelColor);
        nameField = createRoundedField(fieldFont, fieldBg, borderColor);

        JLabel surnameLabel = new JLabel("Surname:");
        surnameLabel.setFont(labelFont);
        surnameLabel.setForeground(labelColor);
        surnameField = createRoundedField(fieldFont, fieldBg, borderColor);

        JLabel rollLabel = new JLabel("Roll No:");
        rollLabel.setFont(labelFont);
        rollLabel.setForeground(labelColor);
        rollField = createRoundedField(fieldFont, fieldBg, borderColor);

        JLabel groupLabel = new JLabel("Select Group:");
        groupLabel.setFont(labelFont);
        groupLabel.setForeground(labelColor);
        groupDropdown = new JComboBox<>();
        groupDropdown.setFont(fieldFont);
        groupDropdown.setBackground(new Color(230, 230, 250));
        groupDropdown.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 220), 2));

        // + Add Group Button
        ImageIcon plusIconRaw = new ImageIcon(getClass().getResource("/images/plus.png"));
        Image plusImg = plusIconRaw.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
        addGroupBtn = createFlatButton(" Add Group", new Color(70, 130, 180), new ImageIcon(plusImg));
        addGroupBtn.setPreferredSize(new Dimension(130, 40));
        addGroupBtn.addActionListener(e -> addNewGroup());

        JPanel groupPanel = new JPanel(new BorderLayout(5, 0));
        groupPanel.setOpaque(false);
        groupPanel.add(groupDropdown, BorderLayout.CENTER);
        groupPanel.add(addGroupBtn, BorderLayout.EAST);

        loadGroups();

        // ===== Add form fields =====
        gbc.gridx = 0; gbc.gridy = 0; cardPanel.add(nameLabel, gbc);
        gbc.gridx = 1; cardPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; cardPanel.add(surnameLabel, gbc);
        gbc.gridx = 1; cardPanel.add(surnameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; cardPanel.add(rollLabel, gbc);
        gbc.gridx = 1; cardPanel.add(rollField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; cardPanel.add(groupLabel, gbc);
        gbc.gridx = 1; cardPanel.add(groupPanel, gbc);

        // ===== Buttons =====
        ImageIcon addIconRaw = new ImageIcon(getClass().getResource("/images/addstud.png"));
        Image addImg = addIconRaw.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
        addBtn = createFlatButton(" Add Student", new Color(60, 179, 113), new ImageIcon(addImg));

        ImageIcon backIconRaw = new ImageIcon(getClass().getResource("/images/back.png"));
        Image backImg = backIconRaw.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
        backBtn = createFlatButton(" Back", new Color(255, 140, 0), new ImageIcon(backImg));
        backBtn.setPreferredSize(addBtn.getPreferredSize());
        backBtn.addActionListener(e -> dispose());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));
        btnPanel.setOpaque(false);
        btnPanel.add(addBtn);
        btnPanel.add(backBtn);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        cardPanel.add(btnPanel, gbc);

        bgPanel.add(cardPanel, new GridBagConstraints());
        backgroundPanel.add(bgPanel, BorderLayout.CENTER);

        addBtn.addActionListener(this);
        setVisible(true);
    }

    // ======= Helper: Rounded Text Field =======
    private JTextField createRoundedField(Font font, Color bgColor, Color borderColor) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
            }

            @Override
            public void setOpaque(boolean isOpaque) {
                super.setOpaque(false);
            }
        };
        field.setFont(font);
        field.setForeground(Color.DARK_GRAY);
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        field.setOpaque(false);
        return field;
    }

    // ======= Helper: Flat (Tab-like) Button =======
    private JButton createFlatButton(String text, Color color, ImageIcon icon) {
        JButton btn = new JButton(text, icon);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(color.darker(), 2));
        btn.setPreferredSize(new Dimension(150, 45));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(color.darker()); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(color); }
        });
        return btn;
    }

    private void loadGroups() {
        groupDropdown.removeAllItems();
        try {
            String sql = "SELECT grp_id, project_name FROM student_groups ORDER BY grp_no";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            boolean hasGroups = false;
            while (rs.next()) {
                int grpId = rs.getInt("grp_id");
                String projectName = rs.getString("project_name");
                groupDropdown.addItem(grpId + " - " + projectName);
                hasGroups = true;
            }
            if (!hasGroups) groupDropdown.addItem("⚠️ No groups available");
            rs.close(); pst.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading groups: " + ex.getMessage());
            groupDropdown.addItem("⚠️ Error loading groups");
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
                        "INSERT INTO student_groups (grp_no, project_name) VALUES (?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                pst.setInt(1, nextGrpNo);
                pst.setString(2, groupName);
                pst.executeUpdate();

                JOptionPane.showMessageDialog(this, "✅ New group added: " + groupName);
                loadGroups();

                for (int i = 0; i < groupDropdown.getItemCount(); i++)
                    if (groupDropdown.getItemAt(i).endsWith(groupName)) {
                        groupDropdown.setSelectedIndex(i);
                        break;
                    }

                pst.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error adding group: " + ex.getMessage());
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String name = nameField.getText().trim();
        String surname = surnameField.getText().trim();
        String roll = rollField.getText().trim();

        // === Input Validation ===
        if (name.isEmpty() || surname.isEmpty() || roll.isEmpty() || groupDropdown.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
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

        if (!roll.matches("[A-Za-z0-9]+")) {
            JOptionPane.showMessageDialog(this, "❌ Roll number can contain only letters and digits!");
            return;
        }

        if (groupDropdown.getSelectedItem().toString().contains("⚠️")) {
            JOptionPane.showMessageDialog(this, "Please create a group first.");
            return;
        }

        int grpId = Integer.parseInt(groupDropdown.getSelectedItem().toString().split(" - ")[0]);
        try {
            String sql = "INSERT INTO students (name, surname, roll_no, grp_id) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, name);
            pst.setString(2, surname);
            pst.setString(3, roll);
            pst.setInt(4, grpId);
            pst.executeUpdate();
            pst.close();
            JOptionPane.showMessageDialog(this, "✅ Student added successfully!");
            nameField.setText("");
            surnameField.setText("");
            rollField.setText("");
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "⚠️ This roll number already exists!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Database Error: " + ex.getMessage());
        }
    }
}
