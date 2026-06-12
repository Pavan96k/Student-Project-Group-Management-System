import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.net.URL;
import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.FontFactory;

public class Viewstud extends JFrame {
    private JTable table;
    private Connection con;
    private boolean darkMode = false;

    private JPanel headerPanel, bottomWrap;
    private JLabel headerLabel;
    private JButton btnRefresh, btnBack, btnExport;
    private JToggleButton toggleDark;
    private ImageIcon darkIcon, lightIcon;

    public Viewstud(Connection con) {
        this.con = con;

        setTitle("View All Students");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // === Load toggle icons ===
        darkIcon = loadIcon("/images/dark.png", 24, 24);
        lightIcon = loadIcon("/images/light.png", 24, 24);

        // === HEADER PANEL (Label + Toggle in same row) ===
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        headerPanel.setOpaque(true);

        URL headerImgURL = getClass().getResource("/images/student.png");
        ImageIcon headerIcon = null;
        if (headerImgURL != null) {
            Image headerImg = new ImageIcon(headerImgURL).getImage()
                    .getScaledInstance(36, 36, Image.SCALE_SMOOTH);
            headerIcon = new ImageIcon(headerImg);
        }
        headerLabel = new JLabel(" Student Data ", headerIcon, SwingConstants.LEFT);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerLabel.setOpaque(false);
        headerLabel.setForeground(Color.BLACK);

        // === Toggle Button ===
        toggleDark = new JToggleButton();
        toggleDark.setFont(new Font("Segoe UI", Font.BOLD, 18));
        toggleDark.setFocusPainted(false);
        toggleDark.setBorderPainted(false);
        toggleDark.setContentAreaFilled(false);
        toggleDark.setOpaque(false);
        toggleDark.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleDark.setPreferredSize(new Dimension(50, 35));

        // === Set Default Icon (Light Theme) ===
        URL iconURL = getClass().getResource("/images/light.png");
        if (iconURL != null) {
            Image img = new ImageIcon(iconURL).getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
            toggleDark.setIcon(new ImageIcon(img));
        }

        // === Add Listener for Theme Switch ===
        toggleDark.addItemListener(e -> {
            darkMode = toggleDark.isSelected();

            URL iconURL2 = getClass().getResource(darkMode ? "/images/dark.png" : "/images/light.png");
            if (iconURL2 != null) {
                Image img2 = new ImageIcon(iconURL2).getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
                toggleDark.setIcon(new ImageIcon(img2));
            }

            applyTheme(darkMode);
        });

        // Add both on same line
        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.add(toggleDark, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // === TABLE ===
        table = new JTable() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(darkMode ? Color.WHITE : Color.BLACK);
                g2.setStroke(new BasicStroke(3.0f));
                int rowHeight = getRowHeight();
                for (int i = 0; i < getRowCount(); i++) {
                    Object srValue = getValueAt(i, 0);
                    if (srValue != null && !srValue.toString().trim().isEmpty()) {
                        int y = i * rowHeight;
                        g2.drawLine(0, y, getWidth(), y);
                    }
                }
                g2.dispose();
            }
        };

        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setRowHeight(28);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        add(scroll, BorderLayout.CENTER);

        // === BOTTOM BUTTONS ===
        bottomWrap = new JPanel(new BorderLayout());
        bottomWrap.setOpaque(true);

        JPanel centerBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 12));
        btnRefresh = createIconButton(" Refresh", "/images/refresh.png", new Color(60, 179, 113));
        btnBack = createIconButton(" Back", "/images/back.png", new Color(220, 20, 60));
        btnRefresh.addActionListener(e -> loadData());
        btnBack.addActionListener(e -> dispose());
        centerBtns.add(btnRefresh);
        centerBtns.add(btnBack);

        JPanel rightBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 12));
        btnExport = createIconButton(" Export PDF", "/images/pdf.png", new Color(70, 130, 180));
        btnExport.addActionListener(e -> exportToPDF());
        rightBtnPanel.add(btnExport);

        bottomWrap.add(centerBtns, BorderLayout.CENTER);
        bottomWrap.add(rightBtnPanel, BorderLayout.EAST);
        add(bottomWrap, BorderLayout.SOUTH);

        loadData();
        applyTheme(false);
        setVisible(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private ImageIcon loadIcon(String path, int w, int h) {
        URL url = getClass().getResource(path);
        if (url != null) {
            Image img = new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }
        return null;
    }

    private JButton createIconButton(String text, String resourcePath, Color bg) {
        URL imgURL = getClass().getResource(resourcePath);
        ImageIcon icon = null;
        if (imgURL != null) {
            Image img = new ImageIcon(imgURL).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            icon = new ImageIcon(img);
        }
        JButton btn = new JButton(text, icon);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(145, 40));
        return btn;
    }

    // === Updated loadData() with Roll No sorting ===
    private void loadData() {
        try {
            String sql = "SELECT s.roll_no, s.name, s.surname, COALESCE(g.project_name, 'No Group') AS project_name " +
                    "FROM students s LEFT JOIN student_groups g ON s.grp_id = g.grp_id " +
                    "ORDER BY project_name ASC, s.roll_no ASC";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"SR No", "Roll No", "Name", "Surname", "Project Group"}, 0);

            String lastProject = null;
            int groupIndex = 0;

            while (rs.next()) {
                String roll = rs.getString("roll_no");
                String name = rs.getString("name");
                String surname = rs.getString("surname");
                String project = rs.getString("project_name");
                if (project == null) project = "No Group";

                String srToShow;
                if (lastProject == null || !lastProject.equals(project)) {
                    groupIndex++;
                    srToShow = String.valueOf(groupIndex);
                    lastProject = project;
                } else {
                    srToShow = "";
                }

                model.addRow(new Object[]{srToShow, roll, name, surname, project});
            }

            table.setModel(model);

            // === Enable sorting with custom comparator for Roll No ===
            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
            sorter.setComparator(1, (a, b) -> {
                try {
                    int numA = Integer.parseInt(a.toString().replaceAll("\\D", ""));
                    int numB = Integer.parseInt(b.toString().replaceAll("\\D", ""));
                    return Integer.compare(numA, numB);
                } catch (Exception e) {
                    return a.toString().compareTo(b.toString());
                }
            });
            table.setRowSorter(sorter);

            pst.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage());
        }
    }

    private void exportToPDF() {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save PDF");
            chooser.setSelectedFile(new java.io.File("students.pdf"));
            int userChoice = chooser.showSaveDialog(this);
            if (userChoice != JFileChooser.APPROVE_OPTION) return;

            String path = chooser.getSelectedFile().getAbsolutePath();
            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream(path));
            doc.open();
            doc.add(new Paragraph("Students Data\n\n", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));

            PdfPTable pdfTable = new PdfPTable(table.getColumnCount());
            pdfTable.setWidthPercentage(100);
            for (int c = 0; c < table.getColumnCount(); c++)
                pdfTable.addCell(new PdfPCell(new Phrase(table.getColumnName(c))));
            for (int r = 0; r < table.getRowCount(); r++)
                for (int c = 0; c < table.getColumnCount(); c++)
                    pdfTable.addCell(table.getValueAt(r, c) == null ? "" : table.getValueAt(r, c).toString());

            doc.add(pdfTable);
            doc.close();
            JOptionPane.showMessageDialog(this, "✅ PDF exported: " + path);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error exporting PDF: " + ex.getMessage());
        }
    }

    private void applyTheme(boolean dark) {
        Color blackDeep = new Color(15, 15, 15);
        Color grayDark = new Color(35, 35, 35);
        Color textColor = dark ? Color.WHITE : Color.BLACK;

        headerPanel.setBackground(dark ? grayDark : new Color(100,149,237));
        headerLabel.setForeground(textColor);
        bottomWrap.setBackground(dark ? blackDeep : new Color(240,248,255));

        for (Component comp : bottomWrap.getComponents()) {
            if (comp instanceof JPanel) {
                comp.setBackground(dark ? blackDeep : new Color(240,248,255));
            }
        }

        btnRefresh.setBackground(dark ? grayDark : new Color(60,179,113));
        btnBack.setBackground(dark ? grayDark : new Color(220,20,60));
        btnExport.setBackground(dark ? grayDark : new Color(70,130,180));

        table.setBackground(dark ? blackDeep : Color.WHITE);
        table.setForeground(textColor);
        table.setGridColor(dark ? Color.GRAY : Color.BLACK);
        table.getTableHeader().setBackground(dark ? grayDark : new Color(72,61,139));
        table.getTableHeader().setForeground(textColor);

        JScrollPane sp = (JScrollPane) table.getParent().getParent();
        sp.getViewport().setBackground(dark ? blackDeep : Color.WHITE);
        sp.setBackground(dark ? blackDeep : Color.WHITE);

        getContentPane().setBackground(dark ? blackDeep : Color.WHITE);
        repaint();
    }

    public static void main(String[] args) {
        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/student_management", "admin", "12345");
            new Viewstud(con);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "DB Connection failed: " + ex.getMessage());
        }
    }
}
