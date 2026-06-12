import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Dashboard extends JFrame {
    private Connection con;

    public Dashboard(Connection con) {
        this.con = con;

        setTitle("Student Group Management Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        // ===== MAIN BACKGROUND PANEL =====
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/images/okh2.jpg"));
        Image bgImage = bgIcon.getImage();

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this); // full-page background
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

     // ===== HEADER LABEL (Transparent on background) =====
        ImageIcon dashIconRaw = new ImageIcon(getClass().getResource("/images/dashboard2.png"));
        Image dashImg = dashIconRaw.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
        ImageIcon dashIcon = new ImageIcon(dashImg);

        JLabel titleLabel = new JLabel(" Dashboard", dashIcon, SwingConstants.CENTER); // added icon
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE); // white text for visibility
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // padding
        titleLabel.setOpaque(false); // remove any background
        backgroundPanel.add(titleLabel, BorderLayout.NORTH);



        // ===== CENTER PANEL (Buttons) =====
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false); // transparent to show background

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 30, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 60, 50, 60));

        Font btnFont = new Font("Segoe UI", Font.BOLD, 16);
        Dimension btnSize = new Dimension(200, 60);

        JButton addBtn = createButtonWithIcon("Add", "/images/plus.png", new Color(46, 125, 50), btnFont, btnSize);
        JButton editBtn = createButtonWithIcon("Edit", "/images/edit.png", new Color(245, 124, 0), btnFont, btnSize);
        JButton viewBtn = createButtonWithIcon("View", "/images/view.png", new Color(2, 136, 209), btnFont, btnSize);
        JButton groupBtn = createButtonWithIcon("Groups Overview", "/images/groups.png", new Color(81, 45, 168), btnFont, btnSize);

        JButton[] buttons = { addBtn, editBtn, viewBtn, groupBtn };
        for (JButton btn : buttons) buttonPanel.add(btn);

        centerPanel.add(buttonPanel);
        backgroundPanel.add(centerPanel, BorderLayout.CENTER);

        // ===== ACTION LISTENERS =====
        addBtn.addActionListener(e -> new Addstud(con));
        editBtn.addActionListener(e -> new Editstud(con).setVisible(true));
        viewBtn.addActionListener(e -> new Viewstud(con).setVisible(true));
        groupBtn.addActionListener(e -> new ViewGroups(con).setVisible(true));

        setVisible(true);
    }

    // ===== HELPER METHOD =====
    private JButton createButtonWithIcon(String text, String iconPath, Color bgColor, Font font, Dimension size) {
        ImageIcon rawIcon = new ImageIcon(getClass().getResource(iconPath));
        Image scaled = rawIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        JButton btn = new JButton(text, new ImageIcon(scaled));
        btn.setFont(font);
        btn.setPreferredSize(size);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return btn;
    }

    // ===== MAIN METHOD =====
    public static void main(String[] args) {
        try {
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3307/student_management", "root", "root");
            new Dashboard(con);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "DB Connection failed: " + ex.getMessage());
        }
    }
}
