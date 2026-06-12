import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.*;

public class LoginApp extends JFrame implements ActionListener {
    private JTextField userField;
    private JPasswordField passField;
    private JButton loginBtn;
    private Connection con;

    public LoginApp() {
        // --- Frame setup ---
        setTitle("Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // ✅ Start maximized
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(false);
        setResizable(true);
        setLayout(new BorderLayout());

        // --- Background panel with image ---
        JPanel bgPanel = new JPanel() {
            private Image bg;
            {
                try {
                    bg = ImageIO.read(getClass().getResourceAsStream("/images/bg2.png"));
                } catch (IOException | NullPointerException ex) {
                    bg = null;
                    System.err.println("bg2.png not found in /resources/images/");
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bg != null) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                    g2d.dispose();
                } else {
                    g.setColor(new Color(30, 30, 60));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        bgPanel.setLayout(new GridBagLayout());

        // --- Frosted glass card ---
        JPanel loginCard = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.58f));
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);

                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.16f));
                g2.setStroke(new BasicStroke(2f));
                g2.setColor(Color.WHITE);
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 40, 40);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        loginCard.setOpaque(false);
        loginCard.setBorder(new EmptyBorder(30, 40, 30, 40));
        loginCard.setPreferredSize(new Dimension(520, 380));

        // --- Layout constraints ---
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Centered Welcome title ---
        JLabel smallTitle = new JLabel("Welcome", SwingConstants.CENTER);
        smallTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        smallTitle.setForeground(new Color(30, 30, 60));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 12, 25, 12); // ✅ move slightly up from username
        loginCard.add(smallTitle, gbc);

        // --- Username label + field ---
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(new Color(30, 30, 60));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        loginCard.add(userLabel, gbc);

        userField = new JTextField();
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        userField.setBorder(new EmptyBorder(10, 14, 10, 14));
        userField.setPreferredSize(new Dimension(400, 44)); // ✅ slightly longer
        userField.setOpaque(false);
        userField.setBackground(new Color(255, 255, 255, 0));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        loginCard.add(wrapRoundedField(userField), gbc);

        // --- Password label + field ---
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passLabel.setForeground(new Color(30, 30, 60));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        loginCard.add(passLabel, gbc);

        passField = new JPasswordField();
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passField.setBorder(new EmptyBorder(10, 14, 10, 14));
        passField.setPreferredSize(new Dimension(400, 44));
        passField.setOpaque(false);
        passField.setBackground(new Color(255, 255, 255, 0));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        loginCard.add(wrapRoundedField(passField), gbc);

        // --- Login button ---
        loginBtn = new JButton("Login") {
            @Override
            protected void paintComponent(Graphics g) {
                int arc = getHeight();
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = new Color(65, 105, 225);
                if (getModel().isPressed()) base = base.darker();
                g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            public boolean isOpaque() {
                return false;
            }
        };
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setContentAreaFilled(false);
        loginBtn.setPreferredSize(new Dimension(220, 48));
        loginBtn.addActionListener(this);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(30, 12, 12, 12);
        loginCard.add(loginBtn, gbc);

        // Add to background
        bgPanel.add(loginCard);
        add(bgPanel, BorderLayout.CENTER);

        // Connect DB
        connectDB();

        setVisible(true);
    }

    // --- Rounded field wrapper ---
    private JComponent wrapRoundedField(final JComponent comp) {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 220));
                RoundRectangle2D rr = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 26, 26);
                g2.fill(rr);
                g2.setColor(new Color(200, 200, 200, 120));
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(rr);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 0, 0, 0));
        p.add(comp, BorderLayout.CENTER);
        p.setPreferredSize(comp.getPreferredSize());
        return p;
    }

    // --- DB connection ---
    void connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3307/student_management", "root", "root"
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Database connection failed:\n" + e.getMessage());
            con = null;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginBtn) {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "⚠️ Enter both username & password.");
                return;
            }

            if (con == null) {
                JOptionPane.showMessageDialog(this, "❌ No database connection.");
                return;
            }

            try {
                String query = "SELECT * FROM users WHERE username = ? AND password = ?";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setString(1, username);
                pst.setString(2, password);
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "✅ Login successful!");
                    this.dispose();
                    new Dashboard(con);
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Invalid username or password!");
                }
                pst.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "❌ SQL Error: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginApp::new);
    }
}
