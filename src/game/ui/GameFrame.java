package game.ui;

import game.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class GameFrame extends JFrame {
    private final GameController controller;
    private JLabel bgLabel, playerImgLabel, enemyImgLabel;
    private JProgressBar hpBarPlayer, manaBarPlayer, hpBarEnemy, manaBarEnemy;
    private JLabel playerHP, enemyHP, playerMana, enemyMana, log;
    private JPanel skillPanel;

    public GameFrame() {
        setTitle("⚔️ Pháp Sư Nguyên Tố");
        setSize(950, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // nền
        ImageIcon bgIcon = new ImageIcon("src/img/img.png");
        Image bgScaled = bgIcon.getImage().getScaledInstance(950, 600, Image.SCALE_SMOOTH);
        bgLabel = new JLabel(new ImageIcon(bgScaled));
        bgLabel.setBounds(0, 0, 950, 600);
        bgLabel.setLayout(null);
        setContentPane(bgLabel);

        controller = new GameController(this);
        showStartScreen();
    }

    /** ==================== TRANG KHỞI ĐẦU ==================== */
    public void showStartScreen() {
        bgLabel.removeAll();
        bgLabel.repaint();

        JLabel title = new JLabel("⚔️ PHÁP SƯ Nguyên Tố ⚔️", SwingConstants.CENTER);
        title.setForeground(new Color(255, 215, 0));
        title.setFont(new Font("Serif", Font.BOLD, 42));
        title.setBounds(100, 150, 750, 80);
        bgLabel.add(title);

        JButton startBtn = createTransparentButton("▶ BẮT ĐẦU");
        startBtn.setBounds(350, 300, 250, 50);
        startBtn.addActionListener(e -> controller.startGame());
        bgLabel.add(startBtn);

        JButton exitBtn = createTransparentButton("❌ RỜI KHỎI");
        exitBtn.setBounds(350, 380, 250, 50);
        exitBtn.addActionListener(e -> System.exit(0));
        bgLabel.add(exitBtn);

        bgLabel.revalidate();
        bgLabel.repaint();
    }

    /** Nút trong suốt, viền trắng, hover vàng */
    private JButton createTransparentButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 20));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        btn.setOpaque(false);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setForeground(new Color(255, 215, 0));
                btn.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 2));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setForeground(Color.WHITE);
                btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            }
        });

        return btn;
    }

    /** Khởi tạo giao diện trận đấu */
    public void setupBattle(Mage player, Mage enemy) {
        setBackground("src/img/img.png");   // Nền chiến đấu
        bgLabel.removeAll();

        // ảnh nhân vật
        playerImgLabel = new JLabel();
        enemyImgLabel = new JLabel();
        playerImgLabel.setBounds(100, 220, 200, 200);
        enemyImgLabel.setBounds(650, 220, 200, 200);
        playerImgLabel.setIcon(getMageImage(player, true));
        enemyImgLabel.setIcon(getMageImage(enemy, false));
        bgLabel.add(playerImgLabel);
        bgLabel.add(enemyImgLabel);

        // === Thanh máu - mana ===
        hpBarPlayer = createBar(Color.RED, 50, 70, 100);
        manaBarPlayer = createBar(new Color(30, 144, 255), 50, 100, 50);
        hpBarEnemy = createBar(Color.RED, 600, 70, 100);
        manaBarEnemy = createBar(new Color(0, 191, 255), 600, 100, 50);
        bgLabel.add(hpBarPlayer);
        bgLabel.add(manaBarPlayer);
        bgLabel.add(hpBarEnemy);
        bgLabel.add(manaBarEnemy);

        // label chỉ số
        playerHP = createLabel("HP: 100", 50, 125);
        playerMana = createLabel("Mana: 50", 150, 125);
        enemyHP = createLabel("HP: 100", 600, 125);
        enemyMana = createLabel("Mana: 50", 700, 125);
        bgLabel.add(playerHP);
        bgLabel.add(playerMana);
        bgLabel.add(enemyHP);
        bgLabel.add(enemyMana);

        // log
        log = new JLabel("Trận đấu bắt đầu!", SwingConstants.CENTER);
        log.setForeground(Color.WHITE);
        log.setBounds(100, 380, 750, 50);
        log.setFont(new Font("Consolas", Font.PLAIN, 16));
        bgLabel.add(log);

        // skill panel
        skillPanel = new JPanel(new GridLayout(1, 5, 10, 10));
        skillPanel.setOpaque(false);
        skillPanel.setBounds(80, 500, 800, 50);
        bgLabel.add(skillPanel);

        showSkills(player);

        updateBars(player, enemy);
        bgLabel.revalidate();
        bgLabel.repaint();
    }

    /** Tạo progress bar máu/mana */
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

    /** Hiển thị danh sách kỹ năng */
    public void showSkills(Mage mage) {
        skillPanel.removeAll();
        for (Skill s : mage.getSkills()) {
            JButton btn = new JButton("<html><center>" + s.getName() + "<br/>Mana: " + s.getManaCost() + "</center></html>");
            btn.setFont(new Font("Arial", Font.BOLD, 13));
            btn.setFocusPainted(false);
            btn.addActionListener((ActionEvent e) -> controller.playerUseSkill(s));
            skillPanel.add(btn);
        }
        skillPanel.revalidate();
        skillPanel.repaint();
    }

    /** Cập nhật chỉ số HP/Mana */
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

    /** Cập nhật log diễn biến */
    public void updateLog(String text) {
        log.setText("<html>" + text.replace("\n", "<br>") + "</html>");
    }

    /** Hiển thị hiệu ứng skill đơn giản */
    public void showSkillEffect(int type, boolean isPlayer) {
        String imgPath = switch (type) {
            case 0 -> "src/img/lua.png";
            case 1 -> "src/img/nuoc.png";
            case 2 -> "src/img/gio.png";
            default -> null;
        };
        if (imgPath == null) return;

        JLabel target = isPlayer ? enemyImgLabel : playerImgLabel;
        ImageIcon icon = new ImageIcon(imgPath);
        Image scaled = icon.getImage().getScaledInstance(target.getWidth(), target.getHeight(), Image.SCALE_SMOOTH);
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

    /** Kết thúc game */
    public void showEnd(String result) {
        JOptionPane.showMessageDialog(this, result);
        System.exit(0);
    }

    /** Lấy ảnh nhân vật */
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
        Image scaled = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    /** Màn hình chọn nhân vật bằng hình ảnh */
    public void showCharacterSelect(boolean selectingPlayer) {
        setBackground("src/img/bg1.png");   // Nền chọn nhân vật
        bgLabel.removeAll();
        bgLabel.repaint();

        JLabel title = new JLabel(
                selectingPlayer ? "✨ Chọn pháp sư của bạn" : "⚔️ Chọn pháp sư cho đối thủ",
                SwingConstants.CENTER
        );
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Serif", Font.BOLD, 32));
        title.setBounds(100, 40, 750, 60);
        bgLabel.add(title);

        // Panel chứa các lựa chọn nhân vật
        JPanel panel = new JPanel(new GridLayout(1, 3, 40, 10));
        panel.setOpaque(false);
        panel.setBounds(100, 150, 750, 300);
        bgLabel.add(panel);

        addCharacterOption(panel, "Hoả Long", "src/img/HoaLong.png", selectingPlayer);
        addCharacterOption(panel, "Phong Vũ", "src/img/nguoiChoi/PhongVuUser.png", selectingPlayer);
        addCharacterOption(panel, "Thuỷ Tâm", "src/img/nguoiChoi/ThuyTamUser.png", selectingPlayer);

        bgLabel.revalidate();
        bgLabel.repaint();
    }

    /** Thêm 1 lựa chọn nhân vật vào màn hình select */
    private void addCharacterOption(JPanel panel, String name, String imgPath, boolean selectingPlayer) {
        ImageIcon icon = new ImageIcon(imgPath);
        Image scaled = icon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
        icon = new ImageIcon(scaled);

        JButton btn = new JButton(name, icon);
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        // Khi nhấn chọn
        btn.addActionListener(e -> {
            Mage chosen;
            switch (name) {
                case "Hoả Long" -> chosen = new HoaLong();
                case "Phong Vũ" -> chosen = new PhongVu();
                default -> chosen = new ThuyTam();
            }

            if (selectingPlayer) {
                controller.setPlayerMage(chosen);
                showCharacterSelect(false); // Chuyển sang chọn kẻ địch
            } else {
                controller.setEnemyMage(chosen);
                controller.finishCharacterSelect(); // Bắt đầu game
            }
        });

        panel.add(btn);
    }

    private void setBackground(String path) {
        ImageIcon bgIcon = new ImageIcon(path);
        Image bgScaled = bgIcon.getImage().getScaledInstance(950, 600, Image.SCALE_SMOOTH);
        bgLabel.setIcon(new ImageIcon(bgScaled));
    }

}
