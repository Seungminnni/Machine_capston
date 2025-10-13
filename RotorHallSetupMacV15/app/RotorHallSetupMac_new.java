import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayDeque;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.text.*;

import com.fazecast.jSerialComm.SerialPort;

// ============= 1) SOZLASH OYNASI =================
public class RotorHallSetupMac extends JFrame {

    // faqat raqam kiritish uchun filter
    public static class NumericFilter extends DocumentFilter {

        @Override
        public void insertString(FilterBypass fb, int offset, String s, AttributeSet a) throws BadLocationException {
            if (s != null && s.matches("[0-9.]*")) {
                super.insertString(fb, offset, s, a);
            }
        }

        @Override
        public void replace(FilterBypass fb, int off, int len, String s, AttributeSet a) throws BadLocationException {
            if (s != null && s.matches("[0-9.]*")) {
                super.replace(fb, off, len, s, a);
            }
        }
    }

    private JTextField tfRadius, tfCaution, tfDanger, tfA, tfB;
    private JComboBox<String> cbPort;

    public RotorHallSetupMac() {
        super("Set up for Centrlfugal Filter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        // root.setBorder(new EmptyBorder(16, 24, 20, 24));
        root.setBorder(new EmptyBorder(16, 0, 20, 0));
        setContentPane(root);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(12, 8, 12, 8);
        g.anchor = GridBagConstraints.BASELINE_LEADING;

        // Radius
        g.gridx = 0;
        g.gridy = 0;
        form.add(lbl("Radius of Rotor", 24f, Font.PLAIN), g);
        g.gridx = 1;
        tfRadius = numField(10, 32, 180);
        form.add(tfRadius, g);
        g.gridx = 2;
        form.add(lbl("(cm)", 20f, Font.PLAIN), g);

        // Caution
        g.gridy = 1;
        g.gridx = 0;
        form.add(lblColor("Caution", new Color(230, 120, 40), 24f, Font.PLAIN), g);
        g.gridx = 1;
        tfCaution = numField(10, 32, 180);
        form.add(tfCaution, g);
        g.gridx = 2;
        form.add(lbl("%", 20f, Font.PLAIN), g);

        // Danger
        g.gridy = 2;
        g.gridx = 0;
        form.add(lblColor("Danger", new Color(220, 20, 20), 24f, Font.PLAIN), g);
        g.gridx = 1;
        tfDanger = numField(10, 32, 180);
        form.add(tfDanger, g);
        g.gridx = 2;
        form.add(lbl("%", 20f, Font.PLAIN), g);

        // Network
        GridBagConstraints g2 = (GridBagConstraints) g.clone();
        g2.gridy = 0;
        g2.gridx = 4;
        form.add(lbl("Network Connection", 24f, Font.PLAIN), g2);
        g2.gridx = 5;
        JComboBox<String> cbNetwork = new JComboBox<>(new String[]{"WiFi", "Bluetooth"});
        cbNetwork.setPreferredSize(new Dimension(130, 28));
        cbNetwork.setFont(cbNetwork.getFont().deriveFont(22f));
        form.add(cbNetwork, g2);

        // Port Name (to'liq path bilan)
        g.gridy = 1;
        g.gridx = 4;
        form.add(lbl("Port Name", 24f, Font.PLAIN), g);
        g.gridx = 5;
        cbPort = new JComboBox<>();
        cbPort.setEditable(true); // qo'lda ham yozish uchun
        cbPort.setPreferredSize(new Dimension(220, 28));
        cbPort.setFont(cbPort.getFont().deriveFont(18f));
        fillPorts(cbPort);
        form.add(cbPort, g);

        // Title
        g.gridy = 3;
        g.gridx = 0;
        g.gridwidth = 6;
        form.add(lbl("Relationship of thickness and hall Voltage", 26f, Font.BOLD), g);

        // Formula
        g.gridy = 4;
        g.gridwidth = 1;
        g.gridx = 0;
        g.anchor = GridBagConstraints.CENTER;
        g.insets = new Insets(12, 130, 12, 2);
        JLabel hLabel = lbl("H =", 32f, Font.BOLD);
        hLabel.setOpaque(true);
        form.add(hLabel, g);
        g.anchor = GridBagConstraints.BASELINE_LEADING;
        g.gridx = 1;
        g.insets = new Insets(12, 10, 12, 0);
        tfA = numField(6, 38, 140);
        form.add(tfA, g);
        g.gridx = 2;
        g.insets = new Insets(12, -80, 12, 8);
        g.anchor = GridBagConstraints.CENTER;
        JLabel tLabel = lbl("T +", 30f, Font.BOLD);
        tLabel.setOpaque(true);
        form.add(tLabel, g);
        g.anchor = GridBagConstraints.BASELINE_LEADING;
        g.gridx = 3;
        g.insets = new Insets(12, -40, 12, 8);
        tfB = numField(6, 38, 140);
        form.add(tfB, g);

        // Legend
        g.gridx = 0;
        g.gridy = 6;
        g.gridwidth = 6;
        JLabel legendLabel = lbl("      H = Hall Voltage,   T = Thickness", 30f, Font.PLAIN);
        legendLabel.setOpaque(true);
        // legendLabel.setBackground(Color.RED);
        legendLabel.setBorder(new EmptyBorder(0, 0, 0, 5)); // 5px right margin
        form.add(legendLabel, g);

        // Button
        g.gridy = 6;
        g.gridwidth = 6;
        form.add(Box.createVerticalStrut(32), g);
        g.gridy = 7;
        JButton btnSet = new JButton("Set");
        btnSet.setPreferredSize(new Dimension(520, 90));
        btnSet.setFont(btnSet.getFont().deriveFont(Font.BOLD, 44f));
        btnSet.setBackground(new Color(120, 200, 120));
        btnSet.setOpaque(true);
        btnSet.setForeground(Color.BLACK);
        btnSet.setBorder(new LineBorder(new Color(80, 120, 80), 2));
        g.anchor = GridBagConstraints.CENTER;
        g.fill = GridBagConstraints.NONE;
        form.add(btnSet, g);

        root.add(form, BorderLayout.CENTER);

        btnSet.addActionListener(e -> {
            try {
                double a = parse(tfA.getText(), 1.0);
                double b = parse(tfB.getText(), 0.0);
                double cautionPct = parse(tfCaution.getText(), 40.0);
                double dangerPct = parse(tfDanger.getText(), 60.0);
                double radiusCm = parse(tfRadius.getText(), 10.0);
                String port = String.valueOf(cbPort.getEditor().getItem()); // tanlangan yoki qo'lda kiritilgan
                GraphWindowFlat gw = new GraphWindowFlat(a, b, cautionPct, dangerPct, radiusCm, port);
                gw.setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Qiymatlarni tekshiring: " + ex.getMessage(),
                        "Xatolik", JOptionPane.ERROR_MESSAGE);
            }
        });

        setPreferredSize(new Dimension(980, 560));
        pack();
        setResizable(true);
    }

    // 맥/Linux 기준 포트만 표시 (윈도우는 참고용으로 주석처리해둠)
    private static void fillPorts(JComboBox<String> combo) {
        combo.removeAllItems();
        SerialPort[] ports = SerialPort.getCommPorts();

        // 현재 개발환경: macOS
        java.util.LinkedHashSet<String> seen = new java.util.LinkedHashSet<>();
        for (SerialPort p : ports) {
            String path = p.getSystemPortPath();
            String name = p.getSystemPortName();
            if (path != null && !path.isBlank()) {
                seen.add(path.trim());                 // 보통 /dev/cu.usbserial-XXXXX
            } else if (name != null && !name.isBlank()) {
                if (!name.startsWith("/dev/")) seen.add("/dev/" + name.trim());
                else seen.add(name.trim());
            }
        }
        if (seen.isEmpty()) seen.add("/dev/cu.usbmodemXXXXXXXX"); // fallback 예시

        for (String s : seen) combo.addItem(s);

        if (combo.getItemCount() > 0) combo.setSelectedIndex(0);

        // Windows
        String os = System.getProperty("os.name", "").toLowerCase();
        boolean isWindows = os.contains("win");

        if (isWindows) {
            java.util.TreeSet<String> names = new java.util.TreeSet<>(
                    (a, b) -> {
                        try {
                            int na = Integer.parseInt(a.replaceAll("\\D+", ""));
                            int nb = Integer.parseInt(b.replaceAll("\\D+", ""));
                            return Integer.compare(na, nb);
                        } catch (Exception e) {
                            return a.compareToIgnoreCase(b);
                        }
                    }
            );
            for (SerialPort p : ports) {
                String n = p.getSystemPortName(); // 예: "COM6"
                if (n != null && !n.isBlank()) names.add(n.trim());
            }
            if (names.isEmpty()) {
                for (int i = 1; i <= 10; i++) combo.addItem("COM" + i);
            } else {
                for (String n : names) combo.addItem(n);
            }
        }
    }

    // helpers
    private static double parse(String s, double def) {
        return (s == null || s.isEmpty()) ? def : Double.parseDouble(s);
    }

    private static JTextField numField(int cols, int h, int w) {
        JTextField tf = new JTextField(cols);
        ((AbstractDocument) tf.getDocument()).setDocumentFilter(new NumericFilter());
        tf.setPreferredSize(new Dimension(w, h));
        tf.setBorder(new LineBorder(Color.GRAY));
        tf.setFont(tf.getFont().deriveFont(14f));
        tf.setHorizontalAlignment(JTextField.CENTER);
        return tf;
    }

    private static JLabel lbl(String s, float size, int style) {
        JLabel l = new JLabel(s);
        l.setFont(l.getFont().deriveFont(size).deriveFont(style));
        return l;
    }

    private static JLabel lblColor(String s, Color c, float size, int style) {
        JLabel l = lbl(s, size, style);
        l.setForeground(c);
        return l;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new RotorHallSetupMac().setVisible(true));
    }
}

// ============== 2) GRAFIK OYNA (real-time serial + gain/baseline) ==============
class GraphWindowFlat extends JFrame {

    private final int V_CHART_W = 450, V_CHART_H = 300;
    private final int T_CHART_W = 450, T_CHART_H = 300;

    private final double a, b, cautionPct, dangerPct;
    private final ChartPanelFlat vChart = new ChartPanelFlat("Voltage – Time Graph", "Time [s]", "Voltage [V]");
    private final ChartPanelFlat tChart = new ChartPanelFlat("Thickness – Time Graph", "Time [s]", "Thickness [cm]");
    private final JComboBox<String> cbDt = new JComboBox<>(new String[]{"0.1", "0.2", "0.5", "1.0"});
    private final JButton btnRun = flatBtn("Run");
    private final JButton btnStop = flatBtn("Stop");
    private final TrafficLight light = new TrafficLight();
    private final GaugePanel gauge = new GaugePanel();
    private final JTextField tfFilling = new JTextField(4);
    private final JTextField tfThick = new JTextField(6);
    private final JTextField tfVolt = new JTextField(6);

    // --- Y-axis range ---
    private final JTextField tfYMin = new JTextField("0.0", 4);
    private final JTextField tfYMax = new JTextField("5.0", 4);
    private final JButton btnApply = new JButton("Apply");

    // Serial
    private final String portName;
    private final int baud = 115200;
    private final SerialReader serial;

    // 시뮬레이션 모드 변수들
    private boolean simulationMode = false;
    private javax.swing.Timer simulationTimer;
    private double simulationTime = 0;

    GraphWindowFlat(double a, double b, double cautionPct, double dangerPct, double radiusCm, String port) {
        super("Graph");
        this.a = a;
        this.b = b;
        this.cautionPct = cautionPct;
        this.dangerPct = dangerPct;
        this.portName = port;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // setSize(1280, 575);
        setSize(1280, 645);
        setResizable(true);
        setLocationRelativeTo(null);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Graph", buildGraphUI());
        tabs.addTab("Manual", new JPanel());
        tabs.addTab("Data", new JPanel());
        setContentPane(tabs);

        // Thresholdlar
        double yC = (cautionPct / 100.0) * 5.0;
        double yD = (dangerPct / 100.0) * 5.0;
        tChart.setThresholds(yC, yD);
        vChart.setThresholds(yC, yD);

        vChart.setBackground(Color.GRAY);
        vChart.setPreferredSize(new Dimension(V_CHART_W, V_CHART_H));
        vChart.setMinimumSize(new Dimension(V_CHART_W, V_CHART_H));
        tChart.setBackground(Color.GRAY);
        tChart.setPreferredSize(new Dimension(T_CHART_W, T_CHART_H));
        tChart.setMinimumSize(new Dimension(T_CHART_W, T_CHART_H));

        // Serial listener
        serial = new SerialReader(Vraw -> {
            double dt = Double.parseDouble((String) cbDt.getSelectedItem());
            double V = clamp(Vraw, 0, 5);

            double T = clamp(a * V - b, 0, 5); // 식 수정 해야할 수 있음

            vChart.push(dt, V);
            tChart.push(dt, T);

            tfVolt.setText(String.format("%.3f", V));
            tfThick.setText(String.format("%.2f", T));

            double yCVal = (cautionPct / 100.0) * 5.0;
            double yDVal = (dangerPct / 100.0) * 5.0;
            if (T >= yDVal) {
                light.setState(TrafficLight.State.RED);
            } else if (T >= yCVal) {
                light.setState(TrafficLight.State.YELLOW);
            } else {
                light.setState(TrafficLight.State.GREEN);
            }

            int perc = (int) Math.round((T / 5.0) * 100.0);
            tfFilling.setText(String.valueOf(perc));
            gauge.setPercent(perc);
        });

        // 시뮬레이션 타이머 설정
        simulationTimer = new javax.swing.Timer(200, e -> {
            if (simulationMode) {
                simulationTime += Double.parseDouble((String) cbDt.getSelectedItem());
                
                // 시뮬레이션 데이터 생성 (사인파 + 노이즈)
                double baseVoltage = 1.8 + 0.2 * Math.sin(simulationTime * 0.5) + (Math.random() - 0.5) * 0.1;
                double V = clamp(baseVoltage, 0, 5);
                double T = clamp(a * V - b, 0, 5);

                vChart.push(Double.parseDouble((String) cbDt.getSelectedItem()), V);
                tChart.push(Double.parseDouble((String) cbDt.getSelectedItem()), T);

                tfVolt.setText(String.format("%.3f", V));
                tfThick.setText(String.format("%.2f", T));

                double yCVal = (cautionPct / 100.0) * 5.0;
                double yDVal = (dangerPct / 100.0) * 5.0;
                if (T >= yDVal) {
                    light.setState(TrafficLight.State.RED);
                } else if (T >= yCVal) {
                    light.setState(TrafficLight.State.YELLOW);
                } else {
                    light.setState(TrafficLight.State.GREEN);
                }

                int perc = (int) Math.round((T / 5.0) * 100.0);
                tfFilling.setText(String.valueOf(perc));
                gauge.setPercent(perc);
            }
        });
    }

    private JPanel buildGraphUI() {
        JPanel left = new JPanel();
        left.setOpaque(false); // 투명하게 변경
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(Box.createVerticalStrut(48));
        left.add(Box.createVerticalGlue());

        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(new EmptyBorder(20, 0, 12, 0));

        JPanel runButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        btnRun.setPreferredSize(new Dimension(140, 40));
        btnRun.setFont(new Font("Dialog", Font.BOLD, 22));
        runButtonPanel.add(btnRun);

        btnStop.setPreferredSize(new Dimension(140, 40));
        btnStop.setFont(new Font("Dialog", Font.BOLD, 22));
        btnStop.setEnabled(false);
        runButtonPanel.add(btnStop);

        runButtonPanel.add(Box.createHorizontalStrut(80));

        JPanel timeIntervalPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        JLabel ti = new JLabel("Time Interval[s]");
        ti.setFont(new Font("Dialog", Font.BOLD, 20));
        timeIntervalPanel.add(ti);

        // cbDt.setPreferredSize(new Dimension(120, 28));
        cbDt.setPreferredSize(new Dimension(120, 28));
        cbDt.setBackground(Color.WHITE); // Make input field white
        cbDt.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("Dialog", Font.PLAIN, 18));
                lbl.setBackground(isSelected ? new Color(170, 200, 235) : new Color(236, 236, 236));
                lbl.setForeground(Color.BLACK);
                lbl.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
                return lbl;
            }
        });
        cbDt.setUI(new BasicComboBoxUI() {
            @Override
            protected ComboPopup createPopup() {
                return new BasicComboPopup((JComboBox) cbDt) {
                    @Override
                    public void show() {
                        list.setBackground(new Color(236, 236, 236));
                        list.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                        super.show();
                    }
                };
            }
        });
        timeIntervalPanel.add(cbDt);
        runButtonPanel.add(timeIntervalPanel);

        // --- CHARTS PANEL ---
        // Change chartsPanel layout to GridLayout for proportional resizing
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        chartsPanel.add(wrapChart(vChart));
        chartsPanel.add(wrapChart(tChart));

        // Y-axis range panel for Voltage – Time Graph
        JPanel yAxisBoxV = new JPanel();
        yAxisBoxV.setLayout(new BoxLayout(yAxisBoxV, BoxLayout.Y_AXIS));
        yAxisBoxV.setBorder(new LineBorder(Color.BLACK, 1));
        JLabel yAxisTitleV = new JLabel("Y Axis Range (Voltage)", SwingConstants.CENTER);
        yAxisTitleV.setFont(new Font("Dialog", Font.BOLD, 18));
        yAxisTitleV.setAlignmentX(Component.CENTER_ALIGNMENT);
        yAxisBoxV.add(Box.createVerticalStrut(2));
        yAxisBoxV.add(yAxisTitleV);
        JPanel yAxisVControls = new JPanel();
        yAxisVControls.setLayout(new GridBagLayout());
        yAxisVControls.setOpaque(false);
        GridBagConstraints gbcV = new GridBagConstraints();
        gbcV.insets = new Insets(0, 0, 0, 0); // LEFT PADDING MATCHED
        gbcV.anchor = GridBagConstraints.WEST; // LEFT ALIGN
        gbcV.gridy = 0;
        gbcV.gridx = 0;
        JLabel lblYMinV = new JLabel("Y Min");
        lblYMinV.setFont(new Font("Dialog", Font.PLAIN, 15));
        lblYMinV.setPreferredSize(new Dimension(50, 28)); // FIXED WIDTH
        yAxisVControls.add(lblYMinV, gbcV);
        gbcV.gridx = 1;
        tfYMin.setPreferredSize(new Dimension(55, 28));
        tfYMin.setFont(new Font("Dialog", Font.PLAIN, 15));
        yAxisVControls.add(tfYMin, gbcV);
        gbcV.gridx = 2;
        JLabel lblYMaxV = new JLabel("Y Max");
        lblYMaxV.setFont(new Font("Dialog", Font.PLAIN, 15));
        lblYMaxV.setPreferredSize(new Dimension(50, 28)); // FIXED WIDTH
        yAxisVControls.add(lblYMaxV, gbcV);
        gbcV.gridx = 3;
        tfYMax.setPreferredSize(new Dimension(55, 28));
        tfYMax.setFont(new Font("Dialog", Font.PLAIN, 15));
        yAxisVControls.add(tfYMax, gbcV);
        gbcV.gridx = 4;
        btnApply.setPreferredSize(new Dimension(70, 28));
        btnApply.setFont(new Font("Dialog", Font.BOLD, 15));
        yAxisVControls.add(btnApply, gbcV);
        yAxisBoxV.add(Box.createVerticalStrut(4));
        yAxisBoxV.add(yAxisVControls);
        yAxisBoxV.add(Box.createVerticalStrut(2));

        // Wrap Voltage box in a container panel with left margin
        JPanel yAxisBoxVWrapper = new JPanel(new BorderLayout());
        yAxisBoxVWrapper.setBorder(new EmptyBorder(0, 17, 0, 0)); // 17px left margin
        yAxisBoxVWrapper.add(yAxisBoxV, BorderLayout.CENTER);

        // Y-axis range panel for Thickness – Time Graph
        JTextField tfYMinT = new JTextField("0.0", 4);
        JTextField tfYMaxT = new JTextField("5.0", 4);
        JButton btnApplyT = new JButton("Apply");
        JPanel yAxisBoxT = new JPanel();
        yAxisBoxT.setLayout(new BoxLayout(yAxisBoxT, BoxLayout.Y_AXIS));
        // yAxisBoxT.setBackground(new Color(200, 220, 255));
        yAxisBoxT.setBorder(new LineBorder(Color.BLACK, 1));
        // yAxisBoxT.setPreferredSize(new Dimension(0, 70));
        JLabel yAxisTitleT = new JLabel("Y Axis Range (Thickness)", SwingConstants.CENTER);
        yAxisTitleT.setFont(new Font("Dialog", Font.BOLD, 18));
        yAxisTitleT.setAlignmentX(Component.CENTER_ALIGNMENT);
        yAxisBoxT.add(Box.createVerticalStrut(2));
        yAxisBoxT.add(yAxisTitleT);
        // Improved layout for Thickness box controls
        JPanel yAxisTControls = new JPanel();
        yAxisTControls.setLayout(new GridBagLayout());
        yAxisTControls.setOpaque(false);
        GridBagConstraints gbcT = new GridBagConstraints();
        gbcT.insets = new Insets(0, 0, 0, 0); // LEFT PADDING MATCHED
        gbcT.anchor = GridBagConstraints.WEST; // LEFT ALIGN
        gbcT.gridy = 0;
        gbcT.gridx = 0;
        JLabel lblYMinT = new JLabel("Y Min");
        lblYMinT.setFont(new Font("Dialog", Font.PLAIN, 15));
        lblYMinT.setPreferredSize(new Dimension(50, 28)); // FIXED WIDTH
        yAxisTControls.add(lblYMinT, gbcT);
        gbcT.gridx = 1;
        tfYMinT.setPreferredSize(new Dimension(55, 28));
        tfYMinT.setFont(new Font("Dialog", Font.PLAIN, 15));
        yAxisTControls.add(tfYMinT, gbcT);
        gbcT.gridx = 2;
        JLabel lblYMaxT = new JLabel("Y Max");
        lblYMaxT.setFont(new Font("Dialog", Font.PLAIN, 15));
        lblYMaxT.setPreferredSize(new Dimension(50, 28)); // FIXED WIDTH
        yAxisTControls.add(lblYMaxT, gbcT);
        gbcT.gridx = 3;
        tfYMaxT.setPreferredSize(new Dimension(55, 28));
        tfYMaxT.setFont(new Font("Dialog", Font.PLAIN, 15));
        yAxisTControls.add(tfYMaxT, gbcT);
        gbcT.gridx = 4;
        btnApplyT.setPreferredSize(new Dimension(70, 28));
        btnApplyT.setFont(new Font("Dialog", Font.BOLD, 15));
        yAxisTControls.add(btnApplyT, gbcT);
        yAxisBoxT.add(Box.createVerticalStrut(4));
        yAxisBoxT.add(yAxisTControls);
        yAxisBoxT.add(Box.createVerticalStrut(2));

        // Wrap Thickness box in a container panel with right margin
        JPanel yAxisBoxTWrapper = new JPanel(new BorderLayout());
        yAxisBoxTWrapper.setBorder(new EmptyBorder(0, 0, 0, 17)); // 17px right margin
        yAxisBoxTWrapper.add(yAxisBoxT, BorderLayout.CENTER);

        // Place the two Y Axis Range boxes side by side at the top
        JPanel yAxisBoxes = new JPanel(new GridLayout(1, 2, 12, 0));
        // yAxisBoxV.setPreferredSize(new Dimension(0, 60));
        yAxisBoxT.setPreferredSize(new Dimension(0, 60));
        yAxisBoxes.add(yAxisBoxVWrapper); // Use wrapper for Voltage box
        yAxisBoxes.add(yAxisBoxTWrapper); // Use wrapper for Thickness box

        // Sludge info
        JPanel sludge = new JPanel(new BorderLayout());
        sludge.setBorder(new LineBorder(Color.BLACK, 1));
        sludge.setPreferredSize(new Dimension(600, 80));
        JLabel sludgeTitle = new JLabel("Sludge", SwingConstants.CENTER);
        sludgeTitle.setFont(new Font("Dialog", Font.BOLD, 22));
        sludge.add(sludgeTitle, BorderLayout.NORTH);
        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        JPanel thicknessPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JLabel lblThick = new JLabel("Thickness");
        lblThick.setFont(new Font("Dialog", Font.BOLD, 22));
        thicknessPanel.add(lblThick);
        tfThick.setPreferredSize(new Dimension(70, 22));
        tfThick.setHorizontalAlignment(JTextField.CENTER);
        tfThick.setEditable(false);
        thicknessPanel.add(tfThick);
        JLabel lblThickUnit = new JLabel("[cm]");
        lblThickUnit.setFont(new Font("Dialog", Font.BOLD, 22));
        thicknessPanel.add(lblThickUnit);
        JPanel voltagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JLabel lblVolt = new JLabel("Voltage");
        lblVolt.setFont(new Font("Dialog", Font.BOLD, 22));
        voltagePanel.add(lblVolt);
        tfVolt.setPreferredSize(new Dimension(70, 22));
        tfVolt.setHorizontalAlignment(JTextField.CENTER);
        tfVolt.setEditable(false);
        voltagePanel.add(tfVolt);
        JLabel lblVoltUnit = new JLabel("[V]");
        lblVoltUnit.setFont(new Font("Dialog", Font.BOLD, 22));
        voltagePanel.add(lblVoltUnit);
        contentPanel.add(thicknessPanel);
        contentPanel.add(voltagePanel);
        sludge.add(contentPanel, BorderLayout.CENTER);
        JPanel sludgeCenterWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sludgeCenterWrapper.add(sludge);
        // South panel: Y Axis boxes on top, sludge below
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.add(Box.createVerticalStrut(8));
        southPanel.add(yAxisBoxes);
        southPanel.add(Box.createVerticalStrut(10));
        southPanel.add(sludgeCenterWrapper);
        southPanel.add(Box.createVerticalStrut(2));

        center.add(runButtonPanel, BorderLayout.NORTH);
        center.add(chartsPanel, BorderLayout.CENTER);
        center.add(southPanel, BorderLayout.SOUTH);

        // RIGHT
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(new EmptyBorder(6, 0, 6, 6));

        JPanel trafficLightWithLabels = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        trafficLightWithLabels.add(light);

        JPanel labelsPanel = new JPanel();
        labelsPanel.setLayout(new BoxLayout(labelsPanel, BoxLayout.Y_AXIS));
        labelsPanel.add(dotLine(new Color(220, 20, 20), " Danger"));
        labelsPanel.add(dotLine(new Color(230, 180, 0), " Caution"));
        labelsPanel.add(dotLine(new Color(20, 160, 60), " Normal"));
        trafficLightWithLabels.add(labelsPanel);
        right.add(trafficLightWithLabels);

        JLabel swl = new JLabel("Sludge Warning light");
        swl.setFont(new Font("Dialog", Font.BOLD, 22));
        swl.setAlignmentX(Component.CENTER_ALIGNMENT);
        right.add(swl);
        right.add(Box.createVerticalStrut(10));

        right.add(centered(gauge));
        right.add(Box.createVerticalStrut(8));

        JPanel fr = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JLabel fillingRateLabel = new JLabel("Filling  Rate");
        fillingRateLabel.setFont(new Font("Dialog", Font.BOLD, 22));
        fr.add(fillingRateLabel);
        tfFilling.setPreferredSize(new Dimension(38, 20));
        tfFilling.setBorder(new LineBorder(Color.BLACK));
        tfFilling.setFont(tfFilling.getFont().deriveFont(16f));
        tfFilling.setHorizontalAlignment(JTextField.CENTER);
        tfFilling.setEditable(false);
        fr.add(tfFilling);
        fr.add(new JLabel("%"));
        right.add(fr);
        right.add(Box.createVerticalStrut(10));

        JPanel page = new JPanel(new BorderLayout(24, 0));
        page.add(left, BorderLayout.WEST);
        page.add(center, BorderLayout.CENTER);
        page.add(right, BorderLayout.EAST);

        btnRun.addActionListener(e -> start());
        btnStop.addActionListener(e -> stop());

        btnApply.addActionListener(e -> {
            try {
                double min = Double.parseDouble(tfYMin.getText());
                double max = Double.parseDouble(tfYMax.getText());
                if (min >= max) {
                    JOptionPane.showMessageDialog(GraphWindowFlat.this, "Y Min must be less than Y Max");
                    return;
                }
                vChart.setYRange(min, max); // Only affect Voltage – Time Graph
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(GraphWindowFlat.this, "Invalid number format");
            }
        });
        btnApplyT.addActionListener(e -> {
            try {
                double min = Double.parseDouble(tfYMinT.getText());
                double max = Double.parseDouble(tfYMaxT.getText());
                if (min >= max) {
                    JOptionPane.showMessageDialog(GraphWindowFlat.this, "Y Min must be less than Y Max");
                    return;
                }
                tChart.setYRange(min, max); // Only affect Thickness – Time Graph
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(GraphWindowFlat.this, "Invalid number format");
            }
        });

        return page;
    }

    private static JPanel dotLine(Color color, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JLabel dot = new JLabel("<html><span style='font-size:44px'>&#9679;</span></html>");
        dot.setForeground(color);
        p.add(dot);
        JLabel lab = new JLabel(text);
        lab.setFont(new Font("Dialog", Font.BOLD, 30));
        p.add(lab);
        return p;
    }

    private static JButton flatBtn(String text) {
        JButton b = new JButton(text);
        b.setPreferredSize(new Dimension(120, 32));
        b.setFocusPainted(false);
        b.setBackground(new Color(220, 220, 220));
        b.setBorder(new LineBorder(new Color(140, 140, 140)));
        b.setFont(b.getFont().deriveFont(18f));
        return b;
    }

    private JPanel wrapChart(ChartPanelFlat panel) {
        Dimension d = panel.getPreferredSize();
        if (d == null || d.width <= 0 || d.height <= 0) {
            d = new Dimension(260, 160);
            panel.setPreferredSize(d);
        }
        panel.setMinimumSize(d);

        JPanel box = new JPanel(new BorderLayout());
        JLabel t = new JLabel(panel.title, JLabel.CENTER);
        t.setFont(new Font("Dialog", Font.BOLD, 20));
        t.setBorder(new EmptyBorder(0, 0, 6, 0));
        box.add(t, BorderLayout.NORTH);

        panel.setBorder(new LineBorder(Color.GRAY));
        box.add(panel, BorderLayout.CENTER);
        return box;
    }

    private void start() {
        // 먼저 시리얼 포트 연결 시도
        String path = portName;
        if (serial.open(path, baud)) {
            serial.start();
            simulationMode = false;
            simulationTimer.stop();
            btnRun.setEnabled(false);
            btnStop.setEnabled(true);
        } else {
            // 포트 연결 실패시 자동으로 시뮬레이션 모드로 전환
            JOptionPane.showMessageDialog(this,
                    "Arduino를 찾을 수 없습니다. 시뮬레이션 모드로 실행합니다.",
                    "시뮬레이션 모드", JOptionPane.INFORMATION_MESSAGE);
            
            simulationMode = true;
            simulationTime = 0;
            simulationTimer.start();
            btnRun.setEnabled(false);
            btnStop.setEnabled(true);
        }
    }

    private void stop() {
        serial.stop();
        simulationMode = false;
        simulationTimer.stop();
        btnRun.setEnabled(true);
        btnStop.setEnabled(false);
    }

    private static Component centered(JComponent c) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p.add(c);
        return p;
    }

    private static double clamp(double x, double lo, double hi) {
        return Math.max(lo, Math.min(hi, x));
    }
}

// ============== 3) Svetofor ==============
class TrafficLight extends JPanel {

    enum State {
        GREEN, YELLOW, RED
    }
    private State state = State.GREEN;

    TrafficLight() {
        setPreferredSize(new Dimension(80, 140));
        setOpaque(true);
    }

    void setState(State s) {
        state = s;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        int r = (h - 40) / 3;
        int cx = w / 2;
        int y = 20;
        paintLamp(g2, cx, y, r, state == State.RED ? new Color(200, 0, 0) : new Color(120, 120, 120));
        y += r + 10;
        paintLamp(g2, cx, y, r, state == State.YELLOW ? new Color(230, 180, 0) : new Color(120, 120, 120));
        y += r + 10;
        paintLamp(g2, cx, y, r, state == State.GREEN ? new Color(0, 180, 0) : new Color(120, 120, 120));
    }

    private void paintLamp(Graphics2D g2, int cx, int y, int r, Color c) {
        g2.setColor(c);
        g2.fillOval(cx - r / 2, y, r, r);
        g2.setColor(Color.BLACK);
        g2.drawOval(cx - r / 2, y, r, r);
    }
}

// ============== 4) Gauge (yarim doira) ==============
class GaugePanel extends JPanel {

    private int percent = 0; // 0..100

    GaugePanel() {
        setPreferredSize(new Dimension(250, 150));
        setOpaque(false); // 투명하게 설정
        setBorder(null); // 테두리 제거
    }

    void setPercent(int p) {
        percent = Math.max(0, Math.min(100, p));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        // 완전히 아무것도 그리지 않고 게이지만 그리기
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int w = getWidth(), h = getHeight();
        
        // 게이지만 그리기 (배경 완전 제거)
        int availableW = w - 44, availableH = h - 15;
        int r = Math.min(availableW, availableH * 2);
        int x = (w - r) / 2, y = (h - r / 2) / 2 + 10;
        int cx = x + r / 2, cy = y + r / 2;
        g2.setStroke(new BasicStroke(9f));
        g2.setColor(new Color(255, 50, 50));
        g2.draw(new Arc2D.Double(x, y, r, r, 0, 60, Arc2D.OPEN));
        g2.setColor(new Color(255, 180, 0));
        g2.draw(new Arc2D.Double(x, y, r, r, 60, 60, Arc2D.OPEN));
        g2.setColor(new Color(50, 255, 50));
        g2.draw(new Arc2D.Double(x, y, r, r, 120, 60, Arc2D.OPEN));
        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4f, 6f}, 0));
        drawRay(g2, cx, cy, Math.toRadians(240), r / 2 - 2);
        drawRay(g2, cx, cy, Math.toRadians(300), r / 2 - 2);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20f));
        drawLabel(g2, "Normal", cx, cy, r / 2 - 44, Math.toRadians(210), new Color(0, 200, 0));
        drawLabel(g2, "Caution", cx, cy, r / 2 - 24, Math.toRadians(270), new Color(255, 165, 0));
        drawLabel(g2, "Danger", cx, cy, r / 2 - 44, Math.toRadians(330), new Color(255, 30, 30));
        double ang2 = Math.toRadians(180 + percent * 1.7);
        int nx2 = (int) (cx + Math.cos(ang2) * (r / 2 + 18)), ny2 = (int) (cy + Math.sin(ang2) * (r / 2 + 18));
        g2.setStroke(new BasicStroke(2.5f));
        g2.setColor(Color.DARK_GRAY);
        g2.drawLine(cx, cy, nx2, ny2);
        g2.setColor(Color.BLACK);
        int baseCenterX = (int) (nx2 - Math.cos(ang2) * 12), baseCenterY = (int) (ny2 - Math.sin(ang2) * 12);
        int[] xP = {nx2, (int) (baseCenterX + Math.cos(ang2 + Math.PI / 2) * 6), (int) (baseCenterX + Math.cos(ang2 - Math.PI / 2) * 6)};
        int[] yP = {ny2, (int) (baseCenterY + Math.sin(ang2 + Math.PI / 2) * 6), (int) (baseCenterY + Math.sin(ang2 - Math.PI / 2) * 6)};
        g2.fillPolygon(xP, yP, 3);
        g2.setColor(Color.BLACK);
        g2.drawPolygon(xP, yP, 3);
        g2.setColor(Color.DARK_GRAY);
        g2.fillOval(cx - 3, cy - 3, 6, 6);
    }

    private static void drawRay(Graphics2D g2, int cx, int cy, double ang, int len) {
        int x2 = (int) (cx + Math.cos(ang) * len);
        int y2 = (int) (cy + Math.sin(ang) * len);
        g2.drawLine(cx, cy, x2, y2);
    }

    private static void drawLabel(Graphics2D g2, String s, int cx, int cy, int r, double ang, Color color) {
        int tx = (int) (cx + Math.cos(ang) * r), ty = (int) (cy + Math.sin(ang) * r);
        Color old = g2.getColor();
        g2.setColor(color);
        FontMetrics fm = g2.getFontMetrics();
        int sw = fm.stringWidth(s), sh = fm.getAscent();
        g2.drawString(s, tx - sw / 2, ty + sh / 2);
        g2.setColor(old);
    }
}

// ============== 5) Grafik panel — FLAT (real-time chizish) ==============
class ChartPanelFlat extends JPanel {

    final String title, xLabel, yLabel;
    private final ArrayDeque<Double> values = new ArrayDeque<>();
    private final ArrayDeque<Double> times = new ArrayDeque<>();
    private final double horizonSec = 5.0;
    private Double cautionY = null, dangerY = null;
    private double yMin = 0, yMax = 5;

    ChartPanelFlat(String title, String xLabel, String yLabel) {
        this.title = title;
        this.xLabel = xLabel;
        this.yLabel = yLabel;
        setBackground(Color.GRAY); // 회색 배경으로 변경
        setPreferredSize(new Dimension(260, 160));
    }

    void setYRange(double min, double max) {
        this.yMin = min;
        this.yMax = max;
        repaint();
    }

    void setThresholds(double c, double d) {
        this.cautionY = c;
        this.dangerY = d;
        repaint();
    }

    void push(double dt, double v) {
        double t = times.isEmpty() ? 0 : times.getLast() + dt;
        times.addLast(t);
        values.addLast(v);
        while (!times.isEmpty() && times.getLast() - times.getFirst() > horizonSec) {
            times.removeFirst();
            values.removeFirst();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int W = getWidth(), H = getHeight();
        int margin = 40;
        int plotW = W - 2 * margin, plotH = H - 2 * margin;
        
        if (plotW <= 0 || plotH <= 0) return;
        
        // 배경
        g2.setColor(Color.GRAY);
        g2.fillRect(margin, margin, plotW, plotH);
        
        // 격자
        g2.setColor(new Color(160, 160, 160));
        g2.setStroke(new BasicStroke(1f));
        for (int i = 0; i <= 10; i++) {
            int y = margin + i * plotH / 10;
            g2.drawLine(margin, y, margin + plotW, y);
        }
        for (int i = 0; i <= 10; i++) {
            int x = margin + i * plotW / 10;
            g2.drawLine(x, margin, x, margin + plotH);
        }
        
        // 임계값 선
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5f, 5f}, 0));
        
        // Caution 선
        if (cautionY != null) {
            double cautionYPos = margin + plotH - (cautionY - yMin) / (yMax - yMin) * plotH;
            if (cautionYPos >= margin && cautionYPos <= margin + plotH) {
                g2.setColor(new Color(255, 165, 0));
                g2.drawLine(margin, (int)cautionYPos, margin + plotW, (int)cautionYPos);
            }
        }
        
        // Danger 선
        if (dangerY != null) {
            double dangerYPos = margin + plotH - (dangerY - yMin) / (yMax - yMin) * plotH;
            if (dangerYPos >= margin && dangerYPos <= margin + plotH) {
                g2.setColor(Color.RED);
                g2.drawLine(margin, (int)dangerYPos, margin + plotW, (int)dangerYPos);
            }
        }
        
        // 데이터 그리기
        if (!values.isEmpty() && !times.isEmpty()) {
            g2.setColor(Color.BLUE);
            g2.setStroke(new BasicStroke(2f));
            
            Double[] valArray = values.toArray(new Double[0]);
            Double[] timeArray = times.toArray(new Double[0]);
            
            if (timeArray.length > 1) {
                double tMin = timeArray[0];
                double tMax = timeArray[timeArray.length - 1];
                double tRange = Math.max(tMax - tMin, 0.1);
                
                for (int i = 1; i < timeArray.length; i++) {
                    int x1 = margin + (int)((timeArray[i-1] - tMin) / tRange * plotW);
                    int y1 = margin + plotH - (int)((valArray[i-1] - yMin) / (yMax - yMin) * plotH);
                    int x2 = margin + (int)((timeArray[i] - tMin) / tRange * plotW);
                    int y2 = margin + plotH - (int)((valArray[i] - yMin) / (yMax - yMin) * plotH);
                    
                    if (x1 >= margin && x1 <= margin + plotW && x2 >= margin && x2 <= margin + plotW) {
                        g2.drawLine(x1, Math.max(margin, Math.min(margin + plotH, y1)), 
                                   x2, Math.max(margin, Math.min(margin + plotH, y2)));
                    }
                }
            }
        }
        
        // 테두리
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1f));
        g2.drawRect(margin, margin, plotW, plotH);
        
        // 축 레이블
        g2.setFont(new Font("Dialog", Font.PLAIN, 12));
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(Color.WHITE);
        
        // Y축 레이블
        for (int i = 0; i <= 5; i++) {
            double val = yMin + (yMax - yMin) * i / 5.0;
            String label = String.format("%.1f", val);
            int y = margin + plotH - i * plotH / 5;
            g2.drawString(label, margin - fm.stringWidth(label) - 5, y + fm.getAscent()/2);
        }
        
        // X축 레이블 (항상 표시)
        double xRange = 10.0;
        double xMin = 0.0;
        if (!times.isEmpty()) {
            Double[] timeArray = times.toArray(new Double[0]);
            if (timeArray.length > 1) {
                xMin = timeArray[0];
                double xMax = timeArray[timeArray.length - 1];
                xRange = Math.max(xMax - xMin, 1.0);
            }
        }
        
        for (int i = 0; i <= 5; i++) {
            double val = xMin + xRange * i / 5.0;
            String label = String.format("%.1f", val);
            int x = margin + i * plotW / 5;
            g2.drawString(label, x - fm.stringWidth(label)/2, margin + plotH + 15);
        }
        
        // 축 제목
        g2.setFont(new Font("Dialog", Font.BOLD, 11));
        FontMetrics titleFm = g2.getFontMetrics();
        
        // Y축 제목 (세로로 회전)
        AffineTransform orig = g2.getTransform();
        g2.rotate(-Math.PI/2, 15, margin + plotH/2);
        g2.drawString(yLabel, 15, margin + plotH/2);
        g2.setTransform(orig);
        
        // X축 제목
        g2.drawString(xLabel, margin + plotW/2 - titleFm.stringWidth(xLabel)/2, getHeight() - 5);
    }

    private static double map(double v, double a1, double a2, double b1, double b2) {
        return b1 + (v - a1) * (b2 - b1) / (a2 - a1);
    }
}

// ============== 6) SerialReader ==============
class SerialReader {

    interface Listener {

        void onVoltage(double V);
    }
    private SerialPort port;
    private Thread thread;
    private volatile boolean running = false;
    private final Listener listener;

    SerialReader(Listener listener) {
        this.listener = listener;
    }

    boolean open(String portName, int baud) {
        close();
        port = SerialPort.getCommPort(portName);
        port.setBaudRate(baud);
        port.setNumDataBits(8);
        port.setParity(SerialPort.NO_PARITY);
        port.setNumStopBits(SerialPort.ONE_STOP_BIT);
        port.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
        return port.openPort();
    }

    void start() {
        if (port == null || !port.isOpen() || running) {
            return;
        }
        running = true;
        thread = new Thread(() -> {
            try (java.io.BufferedReader br = new java.io.BufferedReader(
                    new java.io.InputStreamReader(port.getInputStream(), java.nio.charset.StandardCharsets.US_ASCII))) {
                String line;
                while (running && (line = br.readLine()) != null) {
                    String[] tok = line.trim().split("[,;\\t ]+");
                    if (tok.length >= 1) {
                        try {
                            double V = Double.parseDouble(tok[0]);
                            SwingUtilities.invokeLater(() -> listener.onVoltage(V));
                        } catch (NumberFormatException ignore) {
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                close();
            }
        }, "SerialReader");
        thread.setDaemon(true);
        thread.start();
    }

    void stop() {
        running = false;
        close();
    }

    void close() {
        if (port != null) {
            try {
                port.closePort();
            } catch (Exception ignored) {
            }
        }
        port = null;
    }
}