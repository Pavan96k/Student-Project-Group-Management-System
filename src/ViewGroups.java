import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.net.URL;

public class ViewGroups extends JFrame {
    private Connection con;
    private JPanel groupPanel;

    public ViewGroups(Connection con) {
        this.con = con;

        setTitle("Project Groups Overview");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // === HEADER with icon ===
        URL headerImgURL = getClass().getResource("/images/all.png");
        ImageIcon headerIcon = null;
        if (headerImgURL != null) {
            Image headerImg = new ImageIcon(headerImgURL).getImage()
                    .getScaledInstance(36, 36, Image.SCALE_SMOOTH);
            headerIcon = new ImageIcon(headerImg);
        }
        JLabel header = new JLabel(" All Project Groups", headerIcon, SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        header.setBackground(new Color(70, 130, 180));
        header.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(header, BorderLayout.NORTH);

        // === SCROLL PANEL FOR GROUPS ===
        groupPanel = new JPanel();
        groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.Y_AXIS));
        groupPanel.setBackground(new Color(245, 248, 255));

        JScrollPane scrollPane = new JScrollPane(groupPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // === BOTTOM BUTTONS ===
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(240, 248, 255));

        JButton refreshBtn = createIconButton(" Refresh", "/images/refresh.png", new Color(60, 179, 113), 18);
        JButton backBtn = createIconButton(" Back", "/images/back.png", new Color(220, 20, 60), 18);

        refreshBtn.addActionListener(e -> loadGroups());
        backBtn.addActionListener(e -> dispose());

        bottomPanel.add(refreshBtn);
        bottomPanel.add(backBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // === Load data on start ===
        loadGroups();

        setVisible(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

    }

    private JButton createIconButton(String text, String resourcePath, Color bg, int iconSize) {
        URL imgURL = getClass().getResource(resourcePath);
        ImageIcon icon = null;
        if (imgURL != null) {
            Image img = new ImageIcon(imgURL).getImage()
                    .getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
            icon = new ImageIcon(img);
        }
        JButton btn = new JButton(text, icon);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(150, 40));
        return btn;
    }


    private void loadGroups() {
        groupPanel.removeAll();

        try {
            String query = """
                SELECT g.grp_id, g.project_name, COUNT(s.student_id) AS member_count
                FROM student_groups g
                LEFT JOIN students s ON g.grp_id = s.grp_id
                GROUP BY g.grp_id, g.project_name
                ORDER BY g.grp_id
            """;

            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            boolean found = false;

            while (rs.next()) {
                found = true;
                int grpId = rs.getInt("grp_id");
                String projectName = rs.getString("project_name");
                int memberCount = rs.getInt("member_count");

                JPanel card = createGroupCard(grpId, projectName, memberCount);
                groupPanel.add(card);
                groupPanel.add(Box.createVerticalStrut(10));
            }

            if (!found) {
                JLabel msg = new JLabel("⚠ No project groups found.", SwingConstants.CENTER);
                msg.setFont(new Font("Segoe UI", Font.BOLD, 18));
                msg.setForeground(Color.GRAY);
                msg.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
                groupPanel.add(msg);
            }

            rs.close();
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading groups: " + e.getMessage());
        }

        groupPanel.revalidate();
        groupPanel.repaint();
    }

    private JPanel createGroupCard(int grpId, String projectName, int count) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel title = new JLabel(" " + projectName);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(60, 60, 60));

        JLabel members = new JLabel("Members: " + count + " / 5");
        members.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        members.setForeground(new Color(80, 80, 80));

        JButton viewBtn = createIconButton(" View Members", "/images/view2.png", new Color(100, 149, 237), 24);
        viewBtn.setPreferredSize(new Dimension(180, 35));
        viewBtn.addActionListener(e -> showGroupMembers(grpId, projectName));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);
        top.add(title, BorderLayout.WEST);
        top.add(members, BorderLayout.EAST);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(Color.WHITE);
        bottom.add(viewBtn);

        card.add(top, BorderLayout.NORTH);
        card.add(bottom, BorderLayout.SOUTH);

        return card;
    }

    private void showGroupMembers(int grpId, String projectName) {
        try {
            String query = """
                SELECT name, surname, roll_no
                FROM students
                WHERE grp_id = ?
                ORDER BY roll_no
            """;

            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, grpId);
            ResultSet rs = pst.executeQuery();

            StringBuilder members = new StringBuilder();
            while (rs.next()) {
                members.append("• ")
                        .append(rs.getString("name")).append(" ")
                        .append(rs.getString("surname"))
                        .append(" (Roll: ").append(rs.getString("roll_no"))
                        .append(")\n");
            }

            if (members.length() == 0)
                members.append(" No students in this group yet.");

            JOptionPane.showMessageDialog(
                    this,
                    members.toString(),
                    projectName + " - Members",
                    JOptionPane.INFORMATION_MESSAGE
            );

            pst.close();
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching members: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3307/student", "root", "root"
            );
            new ViewGroups(con);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "DB Connection failed: " + e.getMessage());
        }
    }
}
