package view;

import controller.GameController;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import javax.swing.Timer;


public class GameFrame extends JFrame {
    private final GameController controller;
    private JLabel bgLabel, playerImgLabel, enemyImgLabel;
    private JProgressBar hpBarPlayer, manaBarPlayer, hpBarEnemy, manaBarEnemy;
    private JLabel playerHP, enemyHP, playerMana, enemyMana, log;
    private JPanel skillPanel;
    private Timer warningTimer;

    public GameFrame() {
        setTitle("⚔️ Pháp Sư Nguyên Tố");
        setSize(950, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // nền
        ImageIcon bgIcon = new ImageIcon("src/img/background.png");
        Image bgScaled = bgIcon.getImage().getScaledInstance(950, 600, Image.SCALE_SMOOTH);
        bgLabel = new JLabel(new ImageIcon(bgScaled));
        bgLabel.setBounds(0, 0, 950, 600);
        bgLabel.setLayout(null);
        setContentPane(bgLabel);

        controller = new GameController(this);
        showStartScreen();
        setResizable(false);

    }

    /**
     * ==================== TRANG KHỞI ĐẦU ====================
     */
    public void showStartScreen() {
        // Reset nền chính
        ImageIcon bgIcon = new ImageIcon("src/img/background.png");
        Image bgScaled = bgIcon.getImage().getScaledInstance(950, 600, Image.SCALE_SMOOTH);
        bgLabel.setIcon(new ImageIcon(bgScaled));

        bgLabel.removeAll();
        bgLabel.repaint();

        CustomButton startBtn = new CustomButton("BẮT ĐẦU");
        startBtn.setBounds(298, 416, 320, 50);

        // Action
        startBtn.addActionListener(e -> controller.startGame());

        // Add vào background
        bgLabel.add(startBtn);

        ImageIcon icon = new ImageIcon("src/img/exit.png");
        Image scaled = icon.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
        icon = new ImageIcon(scaled);

        JButton exitBtn = new JButton(icon);
        exitBtn.setBounds(900 - 35, 10, 35, 35); // Góc phải trên
        exitBtn.setContentAreaFilled(false);
        exitBtn.setBorder(null);
        exitBtn.setFocusPainted(false);
        exitBtn.setOpaque(false);

        exitBtn.addActionListener(e -> controller.exit());

        bgLabel.add(exitBtn);
        bgLabel.setComponentZOrder(exitBtn, 0); // nằm trên cùng

        bgLabel.revalidate();
        bgLabel.repaint();
    }

    public void showWarning(String text) {
        // Nếu đang có warning cũ → hủy trước
        if (warningTimer != null && warningTimer.isRunning()) {
            warningTimer.stop();
        }

        log.setText(text);
        log.setFont(new Font("Serif", Font.BOLD, 20));
        log.setForeground(Color.WHITE);
        ((GlowLabel) log).setGlowColor(new Color(0, 0, 0, 140));
        log.repaint();

        // ⏱️ 2 giây sau tự xóa
        warningTimer = new Timer(2000, e -> {
            log.setText("");
            log.repaint();
        });
        warningTimer.setRepeats(false);
        warningTimer.start();
    }
    /**
     * Khởi tạo giao diện trận đấu
     */
    public void setupBattle(Mage player, Mage enemy) {
        // Set nền mới cho trận đấu
        ImageIcon bgIcon = new ImageIcon("src/img/img.png");
        Image bgScaled = bgIcon.getImage().getScaledInstance(950, 600, Image.SCALE_SMOOTH);
        bgLabel.setIcon(new ImageIcon(bgScaled));
        bgLabel.removeAll();

        // ảnh nhân vật
        // ảnh nhân vật (Nhỏ lại: 220x220)
        playerImgLabel = new JLabel();
        enemyImgLabel = new JLabel();
        playerImgLabel.setBounds(100, 170, 220, 220); // Dịch vào 100, y=170
        enemyImgLabel.setBounds(630, 170, 220, 220); // Dịch vào 630
        playerImgLabel.setIcon(getMageImage(player, true));
        enemyImgLabel.setIcon(getMageImage(enemy, false));
        bgLabel.add(playerImgLabel);
        bgLabel.add(enemyImgLabel);

        // === Thanh máu - mana (Đẩy lên trên cùng) ===
        hpBarPlayer = createBar(Color.RED, 50, 20, 100); // y=20
        manaBarPlayer = createBar(new Color(30, 144, 255), 50, 45, 50); // y=45
        hpBarEnemy = createBar(Color.RED, 600, 20, 100);
        manaBarEnemy = createBar(new Color(0, 191, 255), 600, 45, 50);
        bgLabel.add(hpBarPlayer);
        bgLabel.add(manaBarPlayer);
        bgLabel.add(hpBarEnemy);
        bgLabel.add(manaBarEnemy);

        // label chỉ số
        // label chỉ số (ngay dưới thanh)
        playerHP = createLabel("HP: 100", 50, 70);
        playerMana = createLabel("Mana: 50", 150, 70);
        enemyHP = createLabel("HP: 100", 600, 70);
        enemyMana = createLabel("Mana: 50", 700, 70);
        bgLabel.add(playerHP);
        bgLabel.add(playerMana);
        bgLabel.add(enemyHP);
        bgLabel.add(enemyMana);

        // log (Khởi tạo rỗng, sẽ được showVersus điền vào)
        log = new GlowLabel("", SwingConstants.CENTER); // Mặc định rỗng
        // Cài đặt sẵn màu Tím Hồng (Magenta)
        log.setForeground(Color.MAGENTA);
        ((GlowLabel) log).setGlowColor(new Color(255, 255, 255, 100));
        log.setFont(new Font("Serif", Font.BOLD, 24));

        // Đặt full width
        log.setBounds(0, 200, 950, 80);
        bgLabel.add(log);
        bgLabel.setComponentZOrder(log, 0);

        // skill panel
        skillPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        skillPanel.setOpaque(false);
        skillPanel.setBounds(80, 430, 800, 130);
        bgLabel.add(skillPanel);

        showSkills(player);

        updateBars(player, enemy);

        // Mặc định ẩn nút skill khi mới vào để hiện VS
        enableSkillButtons(false);
        bgLabel.revalidate();
        bgLabel.repaint();
    }

    public void showVersus(String leftName, String rightName) {
        log.setText(leftName + "   VS   " + rightName);

        // 🔽 Font nhỏ lại
        log.setFont(new Font("Serif", Font.BOLD, 22));

        // 🔼 Đẩy lên cao, thu chiều cao
        log.setBounds(0, 120, 950, 50);

        log.setForeground(Color.WHITE);
        ((GlowLabel) log).setGlowColor(new Color(0, 0, 0, 160));
        log.repaint();

        // Ẩn sau 2s (giữ logic cũ nếu bạn đã có)
        Timer t = new Timer(2000, e -> {
            log.setText("");
            log.repaint();
        });
        t.setRepeats(false);
        t.start();
    }


    /**
     * Tạo progress bar máu/mana
     */
    private JProgressBar createBar(Color color, int x, int y, int max) {
        JProgressBar bar = new JProgressBar(0, max);
        bar.setBounds(x, y, 300, 20);
        bar.setForeground(color);
        bar.setBackground(Color.DARK_GRAY);
        bar.setValue(max);
        return bar;
    }

    private JLabel createLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setBounds(x, y, 120, 20);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        return label;
    }

    /**
     * Hiển thị danh sách kỹ năng
     */
    public void showSkills(Mage mage) {
        skillPanel.removeAll();

        for (Skill s : mage.getSkills()) {

            // Tạo panel chứa nút + label
            JPanel wrapper = new JPanel();
            wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
            wrapper.setOpaque(false);
            wrapper.setAlignmentX(Component.CENTER_ALIGNMENT); // <<< căn giữa cả cụm

            // === NÚT TRÒN ===
            CircleButton btn = new CircleButton("");
            btn.setPreferredSize(new Dimension(90, 90));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT); // <<< căn giữa nút

            // Icon kỹ năng
            if (s.getEffectImg() != null) {
                ImageIcon icon = new ImageIcon(s.getEffectImg());
                Image scaled = icon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(scaled));
            }

            // Tooltip giữ nguyên
            btn.setToolTipText(
                    "<html><center>"
                            + s.getName()
                            + "<br>Mana: " + s.getManaCost()
                            + "<br><i>" + s.getDescription() + "</i>"
                            + "</center></html>");

            btn.addActionListener(e -> controller.playerUseSkill(s));

            // === LABEL BÊN DƯỚI NÚT ===
            JLabel info = new JLabel(
                    "<html><center>"
                            + s.getName()
                            + "<br>Mana: " + s.getManaCost()
                            + "</center></html>");
            info.setForeground(Color.WHITE);
            info.setAlignmentX(Component.CENTER_ALIGNMENT);
            info.setAlignmentX(Component.CENTER_ALIGNMENT); // <<< căn giữa label

            // Thêm vào wrapper
            wrapper.add(btn);
            wrapper.add(Box.createVerticalStrut(5));
            wrapper.add(info);

            skillPanel.add(wrapper);
        }

        skillPanel.revalidate();
        skillPanel.repaint();
    }

    /**
     * Cập nhật chỉ số HP/Mana
     */
    public void updateBars(Mage player, Mage enemy) {
        int playerHp = Math.min(player.getHp(), 100);
        int playerMana = Math.min(player.getMana(), 50);
        int enemyHp = Math.min(enemy.getHp(), 100);
        int enemyMana = Math.min(enemy.getMana(), 50);

        hpBarPlayer.setValue(playerHp);
        manaBarPlayer.setValue(playerMana);
        hpBarEnemy.setValue(enemyHp);
        manaBarEnemy.setValue(enemyMana);

        this.playerHP.setText("HP: " + playerHp);
        this.playerMana.setText("Mana: " + playerMana);
        this.enemyHP.setText("HP: " + enemyHp);
        this.enemyMana.setText("Mana: " + enemyMana);
    }

    /**
     * Cập nhật log diễn biến
     */
    /**
     * Cập nhật log diễn biến
     */
    public void updateLog(String text) {
        // Loại bỏ HTML tag nếu có để text sạch đẹp hơn với GlowLabel
        String cleanText = text.replaceAll("<[^>]*>", "");
        log.setText(cleanText);
        log.repaint();
    }

    /**
     * Hiển thị hiệu ứng skill đơn giản
     */
    public void showSkillEffect(Skill skill, boolean targetIsEnemy) {
        String imgPath = skill.getEffectImg();
        if (imgPath == null)
            return;

        JLabel target = targetIsEnemy ? enemyImgLabel : playerImgLabel;

        ImageIcon icon = new ImageIcon(imgPath);
        Image scaled = icon.getImage().getScaledInstance(
                target.getWidth(), target.getHeight(), Image.SCALE_SMOOTH);

        JLabel effect = new JLabel(new ImageIcon(scaled));
        effect.setBounds(target.getX(), target.getY(), target.getWidth(), target.getHeight());
        effect.setOpaque(false);

        bgLabel.add(effect);
        bgLabel.setComponentZOrder(effect, 0);
        bgLabel.revalidate();
        bgLabel.repaint();

        Timer timer = new Timer(700, e -> {
            bgLabel.remove(effect);
            bgLabel.revalidate();
            bgLabel.repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Kết thúc game - Hiện overlay đẹp mắt
     */
    public void showEnd(String result) {
        // Tạo Panel bao phủ toàn bộ screen
        JPanel overlay = new JPanel(null);
        overlay.setBounds(0, 0, 950, 600);
        overlay.setOpaque(false);

        // Chặn click phím phía dưới
        overlay.addMouseListener(new java.awt.event.MouseAdapter() {
        });

        // Panel thông báo ở giữa
        JPanel box = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Nền Trắng Vàng Sáng (Bright White-Gold)
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 255, 250, 240),
                        0, getHeight(), new Color(255, 250, 220, 240));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                // Viền Vàng Kim nổi bật
                g2.setColor(new Color(255, 215, 0));
                g2.setStroke(new BasicStroke(4));
                g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 30, 30);

                // Hiệu ứng lấp lánh (Sparkle decor)
                g2.setColor(new Color(255, 255, 255, 150));
                g2.fillOval(20, 20, 5, 5);
                g2.fillOval(370, 170, 5, 5);
            }
        };
        box.setBounds(275, 200, 400, 200);
        box.setOpaque(false);

        // Chữ Thắng/Thua
        GlowLabel resLabel = new GlowLabel(result, SwingConstants.CENTER);
        resLabel.setBounds(0, 40, 400, 60);
        resLabel.setFont(new Font("Serif", Font.BOLD, 40));

        if (result.contains("THẮNG")) {
            resLabel.setForeground(new Color(255, 215, 0)); // Vàng Gold
            resLabel.setGlowColor(new Color(255, 255, 0, 100));
        } else {
            resLabel.setForeground(new Color(200, 0, 0)); // Đỏ sẫm
            resLabel.setGlowColor(new Color(0, 0, 0, 200));
        }

        // Nút OK/Làm lại
        CustomButton okBtn = new CustomButton("CHƠI LẠI");
        okBtn.setBounds(125, 120, 150, 40);
        okBtn.addActionListener(e -> controller.resetToStart());

        box.add(resLabel);
        box.add(okBtn);
        overlay.add(box);

        bgLabel.add(overlay);
        bgLabel.setComponentZOrder(overlay, 0);
        bgLabel.repaint();
    }

    /**
     * Lấy ảnh nhân vật
     */
    private ImageIcon getMageImage(Mage mage, boolean isPlayer) {
        String path;
        if (mage instanceof HoaLong) {
            path = "src/img/HoaLong.png";
        } else if (mage instanceof PhongVu) {
            path = isPlayer ? "src/img/nguoiChoi/PhongVuUser.png" : "src/img/may/PhongVuMay.png";
        } else {
            path = "src/img/nguoiChoi/ThuyTamUser.png";
        }
        ImageIcon icon = new ImageIcon(path);
        // Scale ảnh vừa vặn 220x220
        Image scaled = icon.getImage().getScaledInstance(220, 220, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private void setBackground(String path) {
        ImageIcon bgIcon = new ImageIcon(path);
        Image bgScaled = bgIcon.getImage().getScaledInstance(950, 600, Image.SCALE_SMOOTH);
        bgLabel.setIcon(new ImageIcon(bgScaled));
    }

    public void enableSkillButtons(boolean enable) {
        if (skillPanel == null)
            return;

        for (Component c : skillPanel.getComponents()) {
            if (c instanceof JButton btn) {
                btn.setEnabled(enable);
            }
        }
    }

    /**
     * Lớp nút bấm tùy chỉnh cho Menu với phong cách hiện đại
     */
    private static class CustomButton extends JButton {
        private boolean isHovered = false;

        public CustomButton(String text) {
            super(text);
            setFont(new Font("Serif", Font.BOLD, 22));
            setForeground(Color.BLACK);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    isHovered = true;
                    setForeground(Color.WHITE);
                    repaint();
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    isHovered = false;
                    setForeground(Color.BLACK);
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            // Hiệu ứng đổ bóng nhẹ hoặc tỏa sáng
            if (isHovered) {
                g2.setColor(new Color(255, 215, 0, 50));
                g2.fillRoundRect(2, 2, width - 4, height - 4, 25, 25);
            }

            // Nền Gradient (Glassmorphism)
            GradientPaint gp;
            if (isHovered) {
                gp = new GradientPaint(0, 0, new Color(255, 215, 0, 180), 0, height, new Color(218, 165, 32, 220));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, width, height, 25, 25);
            }
            // Nếu không hover, không vẽ nền để lộ background frame

            // Viền
            if (isHovered) {
                g2.setColor(new Color(255, 255, 255));
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(0, 0, width - 1, height - 1, 25, 25);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Màn hình chọn nhân vật bằng code vẽ (Java 2D Graphics)
     */
    public void showCharacterSelect(boolean selectingPlayer) {
        // Xóa hết content cũ
        getContentPane().removeAll();

        // Tạo panel vẽ nền tùy chỉnh
        SelectionPanel selectionPanel = new SelectionPanel(selectingPlayer);
        selectionPanel.setLayout(null);
        selectionPanel.setBounds(0, 0, 950, 600);

        // --- NÚT QUAY LẠI ---
        ImageIcon backIcon = new ImageIcon("src/img/muiten.png");
        Image backScaled = backIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        JButton backBtn = new JButton(new ImageIcon(backScaled));
        backBtn.setBounds(20, 20, 40, 40);
        backBtn.setContentAreaFilled(false);
        backBtn.setBorder(null);
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> {
            if (selectingPlayer) {
                // Đang chọn tướng mình -> Quay về màn hình Start
                setContentPane(bgLabel);
                showStartScreen();
            } else {
                // Đang chọn tướng địch -> Quay về chọn tướng mình
                showCharacterSelect(true);
            }
        });
        selectionPanel.add(backBtn);

        // --- TIÊU ĐỀ ---
        // --- TIÊU ĐỀ ---
        JLabel title = new JLabel(
                selectingPlayer ? "CHỌN PHÁP SƯ CỦA BẠN" : "CHỌN PHÁP SƯ ĐỐI THỦ",
                SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Serif", Font.BOLD, 32)); // Giảm size chút cho vừa
        title.setBounds(0, 15, 950, 50); // Đưa lên ngang hàng với nút Back (y=20)
        // Shadow cho text dễ đọc
        title.setBorder(BorderFactory.createEmptyBorder());
        selectionPanel.add(title);

        // --- THÊM 3 NHÂN VẬT VÀO ĐÚNG 3 KHUNG ĐÃ VẼ ---
        // Tọa độ khung (phải khớp với logic vẽ trong SelectionPanel)
        int slotY = 180;
        int slotW = 220;
        int slotH = 300;
        int gap = 60;
        int totalW = (slotW * 3) + (gap * 2);
        int startX = (950 - totalW) / 2;

        // Slot 1: Hỏa Long
        addAutoAlignedChar(selectionPanel, "Hoả Long", "src/img/HoaLong.png",
                startX, slotY, slotW, slotH, selectingPlayer);

        // Slot 2: Phong Vũ
        addAutoAlignedChar(selectionPanel, "Phong Vũ", "src/img/nguoiChoi/PhongVuUser.png",
                startX + slotW + gap, slotY, slotW, slotH, selectingPlayer);

        // Slot 3: Thủy Tâm
        addAutoAlignedChar(selectionPanel, "Thuỷ Tâm", "src/img/nguoiChoi/ThuyTamUser.png",
                startX + (slotW + gap) * 2, slotY, slotW, slotH, selectingPlayer);

        setContentPane(selectionPanel);
        revalidate();
        repaint();
    }

    private void addAutoAlignedChar(JPanel panel, String name, String imgPath,
            int x, int y, int w, int h, boolean selectingPlayer) {
        ImageIcon icon = new ImageIcon(imgPath);
        // Scale ảnh nhân vật nhỏ hơn khung một chút để nằm lọt vào trong
        Image scaled = icon.getImage().getScaledInstance(w - 20, w - 20, Image.SCALE_SMOOTH);

        JButton btn = new JButton(name, new ImageIcon(scaled));
        btn.setBounds(x, y, w, h);
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setFont(new Font("Serif", Font.BOLD, 20));
        btn.setForeground(Color.WHITE);

        // Trong suốt
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setForeground(new Color(255, 215, 0)); // Vàng khi hover
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setForeground(Color.WHITE);
            }
        });

        btn.addActionListener(e -> {
            Mage chosen;
            switch (name) {
                case "Hoả Long" -> chosen = new HoaLong();
                case "Phong Vũ" -> chosen = new PhongVu();
                default -> chosen = new ThuyTam();
            }
            if (selectingPlayer) {
                controller.setPlayerMage(chosen);
                showCharacterSelect(false);
            } else {
                controller.setEnemyMage(chosen);
                // Quay lại contentPane gốc để hiển thị trận đấu
                setContentPane(bgLabel);
                controller.finishCharacterSelect();
            }
        });

        panel.add(btn);
    }

    /**
     * Panel vẽ nền Selection bằng code (Graphics2D)
     */
    private static class SelectionPanel extends JPanel {
        private final boolean isPlayerMode;

        public SelectionPanel(boolean isPlayerMode) {
            this.isPlayerMode = isPlayerMode;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // 1. VẼ NỀN GRADIENT
            GradientPaint bgGradient;
            if (isPlayerMode) {
                // Tông "Celestial" (Thiên giới): Hỗn hợp Xanh Ngọc, Tím Nhạt và Xanh Dương đậm
                // Dùng LinearGradient đơn giản không đủ đẹp, ta giả lập bằng màu nền + phủ màu
                bgGradient = new GradientPaint(0, 0, new Color(0, 20, 60), 0, h, new Color(100, 50, 180));
            } else {
                // Tông "Void" (Hư không): Tím than, Đen và Hồng Đậm (Magenta) -> Sang trọng hơn
                // Đỏ cam
                bgGradient = new GradientPaint(0, 0, new Color(20, 0, 40), 0, h, new Color(180, 0, 100));
            }
            g2.setPaint(bgGradient);
            g2.fillRect(0, 0, w, h);

            // 1.1 VẼ VÒNG SÁNG SAU NÚT BACK (Để icon không bị tối)
            g2.setColor(new Color(255, 255, 255, 50)); // Trắng mờ nhẹ hơn chút để tiệp màu
            g2.fillOval(15, 15, 50, 50);

            // 1.2 VẼ HIỆU ỨNG ÁNH SÁNG PHỦ (Overlay)
            // Phủ một lớp Gradient chéo để màu sắc có chiều sâu (giống artwork xịn)
            GradientPaint overlay = new GradientPaint(0, 0, new Color(255, 255, 255, 40), w, h,
                    new Color(0, 0, 0, 100));
            g2.setPaint(overlay);
            g2.fillRect(0, 0, w, h);

            // 1.3 VẼ HỌA TIẾT NỀN (Magic Patterns)
            drawMagicPatterns(g2, w, h);

            // 2. VẼ SAO / BỤI (Particles)
            drawParticles(g2, w, h);

            // 3. VẼ 3 Ô KHUNG (PEDESTALS)
            int slotY = 180;
            int slotW = 220;
            int slotH = 300;
            int gap = 60;
            int totalW = (slotW * 3) + (gap * 2);
            int startX = (w - totalW) / 2;

            Color frameColor = isPlayerMode ? new Color(0, 255, 255) : new Color(255, 0, 255); // Cyan vs Magenta
            Color glowColor = isPlayerMode ? new Color(0, 200, 255, 60) : new Color(200, 0, 150, 60);

            // Vẽ 3 khung
            for (int i = 0; i < 3; i++) {
                int x = startX + i * (slotW + gap);
                drawMysticalFrame(g2, x, slotY, slotW, slotH, frameColor, glowColor);
            }
        }

        private void drawMysticalFrame(Graphics2D g2, int x, int y, int w, int h, Color border, Color glow) {
            // Đổ bóng nền khung
            g2.setColor(glow);
            g2.fillRoundRect(x, y, w, h, 20, 20);

            // Viền khung
            g2.setColor(border);
            g2.setStroke(new BasicStroke(3f));
            g2.drawRoundRect(x, y, w, h, 20, 20);

            // Trang trí góc (decor)
            int dist = 20;
            g2.setStroke(new BasicStroke(5f));
            // Góc trên trái
            g2.drawLine(x, y, x + dist, y);
            g2.drawLine(x, y, x, y + dist);
            // Góc trên phải
            g2.drawLine(x + w, y, x + w - dist, y);
            g2.drawLine(x + w, y, x + w, y + dist);
            // Góc dưới trái
            g2.drawLine(x, y + h, x + dist, y + h);
            g2.drawLine(x, y + h, x, y + h - dist);
            // Góc dưới phải
            g2.drawLine(x + w, y + h, x + w - dist, y + h);
            g2.drawLine(x + w, y + h, x + w, y + h - dist);
        }

        private void drawMagicPatterns(Graphics2D g2, int w, int h) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int cx = w / 2;
            int cy = h / 2;

            if (isPlayerMode) {

                // Tông màu: Xanh dương sáng (Cyan/Blue) trắng

                // 1. Ánh sáng nền (Background Glow)
                RadialGradientPaint bgGlow = new RadialGradientPaint(cx, cy, 350,
                        new float[] { 0f, 0.8f, 1f },
                        new Color[] { new Color(0, 100, 255, 30), new Color(0, 50, 150, 10), new Color(0, 0, 0, 0) });
                g2.setPaint(bgGlow);
                g2.fillOval(cx - 350, cy - 350, 700, 700);

                g2.setColor(new Color(135, 206, 250)); // LightSkyBlue
                g2.setStroke(new BasicStroke(2f));

                // 2. Hai vòng tròn đồng tâm (Concentric Circles)
                g2.drawOval(cx - 210, cy - 210, 420, 420); // Vòng ngoài
                g2.setStroke(new BasicStroke(1f));
                g2.drawOval(cx - 190, cy - 190, 380, 380); // Vòng trong

                // 3. Ngôi sao 6 cánh (Hexagram) - Biểu tượng ma thuật cổ điển
                Polygon star1 = new Polygon();
                Polygon star2 = new Polygon();
                int r = 190;
                for (int i = 0; i < 3; i++) {
                    // Tam giác xuôi
                    star1.addPoint((int) (cx + r * Math.cos(i * 2 * Math.PI / 3 - Math.PI / 2)),
                            (int) (cy + r * Math.sin(i * 2 * Math.PI / 3 - Math.PI / 2)));
                    // Tam giác ngược
                    star2.addPoint((int) (cx + r * Math.cos(i * 2 * Math.PI / 3 + Math.PI / 2)),
                            (int) (cy + r * Math.sin(i * 2 * Math.PI / 3 + Math.PI / 2)));
                }

                // Vẽ 2 tam giác lồng nhau
                g2.setColor(new Color(224, 255, 255, 200)); // LightCyan pha trong suốt
                g2.setStroke(new BasicStroke(2f));
                g2.drawPolygon(star1);
                g2.drawPolygon(star2);

                // 4. Các ký tự Rune trang trí giữa 2 vòng tròn
                g2.setColor(new Color(100, 200, 255, 150));
                g2.setFont(new Font("Serif", Font.PLAIN, 14));
                int runeR = 200; // Nằm giữa 190 và 210
                String runes = "A R C A N E M A G I C P O W E R S O U L";
                double step = 2 * Math.PI / runes.length();
                for (int i = 0; i < runes.length(); i++) {
                    double angle = i * step - Math.PI / 2;
                    int tx = (int) (cx + runeR * Math.cos(angle));
                    int ty = (int) (cy + runeR * Math.sin(angle));

                    // Xoay chữ theo vòng tròn
                    AffineTransform orig = g2.getTransform();
                    g2.translate(tx, ty);
                    g2.rotate(angle + Math.PI / 2);
                    g2.drawString(String.valueOf(runes.charAt(i)), -4, 4);
                    g2.setTransform(orig);
                }

                // 5. Vòng tròn nhỏ trung tâm phát sáng
                g2.setColor(new Color(255, 255, 255, 200));
                g2.fillOval(cx - 5, cy - 5, 10, 10);
                g2.drawOval(cx - 80, cy - 80, 160, 160);

            } else {
                // --- ENEMY: CURSED PENTAGRAM (Ngôi Sao 5 Cánh Nguyền Rủa) ---
                // Thay vì gai góc lộn xộn, dùng hình học chuẩn sắc sảo

                // 1. Ánh sáng tà ác (Dark Glow)
                RadialGradientPaint darkGlow = new RadialGradientPaint(cx, cy, 320,
                        new float[] { 0f, 0.9f, 1f },
                        new Color[] { new Color(150, 0, 50, 40), new Color(50, 0, 0, 20), new Color(0, 0, 0, 0) });
                g2.setPaint(darkGlow);
                g2.fillOval(cx - 320, cy - 320, 640, 640);

                g2.setColor(new Color(255, 40, 40)); // Đỏ tươi ma mị
                g2.setStroke(new BasicStroke(2f));

                // 2. Vòng tròn cổ ngữ (Rune Circle)
                g2.drawOval(cx - 200, cy - 200, 400, 400);
                g2.setStroke(new BasicStroke(1f));
                g2.drawOval(cx - 180, cy - 180, 360, 360);

                // Ký tự cổ ngữ quỷ
                g2.setFont(new Font("Serif", Font.BOLD, 16));
                g2.setColor(new Color(255, 100, 100, 180));
                String darkRunes = "D E M O N I C F I R E C H A O S D O O M";
                double step = 2 * Math.PI / darkRunes.length();
                for (int i = 0; i < darkRunes.length(); i++) {
                    double angle = i * step - Math.PI / 2;
                    int tx = (int) (cx + 190 * Math.cos(angle)); // Nằm giữa 2 vòng
                    int ty = (int) (cy + 190 * Math.sin(angle));
                    AffineTransform orig = g2.getTransform();
                    g2.translate(tx, ty);
                    g2.rotate(angle + Math.PI / 2);
                    g2.drawString(String.valueOf(darkRunes.charAt(i)), -4, 4);
                    g2.setTransform(orig);
                }

                // 3. Ngôi sao 5 cánh ngược (Inverted Pentagram)
                g2.setColor(new Color(220, 20, 60)); // Crimson
                g2.setStroke(new BasicStroke(3f));
                Polygon pentagram = new Polygon();
                int r = 180;
                for (int i = 0; i < 5; i++) {
                    // Xoay để đỉnh quay xuống dưới (Ngược)
                    double angle = i * 2 * Math.PI / 5 - Math.PI / 2;
                    pentagram.addPoint((int) (cx + r * Math.cos(angle)), (int) (cy + r * Math.sin(angle)));
                }
                // Vẽ ngôi sao bằng cách nối các điểm cách nhau
                Polygon starShape = new Polygon();
                // 0 -> 2 -> 4 -> 1 -> 3 -> 0
                int[] order = { 0, 2, 4, 1, 3 };
                for (int idx : order) {
                    starShape.addPoint(pentagram.xpoints[idx], pentagram.ypoints[idx]);
                }
                g2.drawPolygon(starShape);

                // 4. Họa tiết phụ bên trong
                g2.setColor(new Color(100, 0, 0, 100));
                g2.fillPolygon(starShape);

                // Tâm
                g2.setColor(Color.RED);
                g2.fillOval(cx - 6, cy - 6, 12, 12);
                g2.setStroke(new BasicStroke(1f));
                g2.drawOval(cx - 50, cy - 50, 100, 100);
            }
        }

        private void drawParticles(Graphics2D g2, int w, int h) {
            g2.setColor(new Color(255, 255, 255, 100));
            // Vẽ ngẫu nhiên một số chấm sáng (giả lập hardcode để ổn định)
            // Trong thực tế có thể dùng Random, ở đây fix cứng một vài điểm cho đẹp
            int[] px = { 100, 250, 500, 750, 800, 150, 400, 600, 850, 300 };
            int[] py = { 100, 200, 150, 100, 300, 500, 450, 550, 400, 50 };
            for (int i = 0; i < px.length; i++) {
                g2.fillOval(px[i], py[i], 3, 3);
            }
        }
    }

    // === Custom Label với hiệu ứng phát sáng ===
    private static class GlowLabel extends JLabel {
        private Color glowColor;

        public GlowLabel(String text, int align) {
            super(text, align);
            this.glowColor = Color.BLACK; // Default shadow/glow
        }

        public void setGlowColor(Color c) {
            this.glowColor = c;
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            String text = getText();
            if (text == null || text.isEmpty())
                return;

            if (text.startsWith("<html>")) {
                super.paintComponent(g);
                return;
            }

            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = (getHeight() + fm.getAscent()) / 2 - fm.getDescent();

            g2.setColor(glowColor);
            g2.setStroke(new BasicStroke(3f));
            for (int dx = -2; dx <= 2; dx++) {
                for (int dy = -2; dy <= 2; dy++) {
                    if (dx != 0 || dy != 0)
                        g2.drawString(text, x + dx, y + dy);
                }
            }

            g2.setColor(getForeground());
            g2.drawString(text, x, y);
        }
    }
}
