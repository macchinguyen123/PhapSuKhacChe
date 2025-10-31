package view;

import controller.PhapSuKhacCheController;
import model.CharacterModel;
import model.HoaLongModel;
import model.ThuyTamModel;

import javax.swing.*;
import java.awt.*;

public class PhapSuKhacCheView extends JFrame {
    private JLabel bgLabel, playerImgLabel, enemyImgLabel;
    private JLabel playerHP, playerMana, enemyHP, enemyMana, log;
    private JProgressBar hpBarPlayer, manaBarPlayer, hpBarEnemy, manaBarEnemy;
    private JButton[] skillButtons;
    private final PhapSuKhacCheController controller;

    public PhapSuKhacCheView() {
        setTitle("⚔️ Pháp Sư Khắc Chế");
        setSize(950, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        ImageIcon bgIcon = new ImageIcon("src/img/img.png");
        Image bgScaled = bgIcon.getImage().getScaledInstance(950, 600, Image.SCALE_SMOOTH);
        bgLabel = new JLabel(new ImageIcon(bgScaled));
        bgLabel.setBounds(0, 0, 950, 600);
        bgLabel.setLayout(null);
        setContentPane(bgLabel);

        controller = new PhapSuKhacCheController(this);
        showStartScreen();
    }

    /** ==================== TRANG KHỞI ĐẦU ==================== */
    public void showStartScreen() {
        bgLabel.removeAll();
        bgLabel.repaint();

        JLabel title = new JLabel("⚔️ PHÁP SƯ KHẮC CHẾ ⚔️", SwingConstants.CENTER);
        title.setForeground(new Color(255, 215, 0));
        title.setFont(new Font("Serif", Font.BOLD, 42));
        title.setBounds(100, 150, 750, 80);
        bgLabel.add(title);

        JButton startBtn = createTransparentButton("▶ BẮT ĐẦU");
        startBtn.setBounds(350, 300, 250, 50);
        startBtn.addActionListener(e -> controller.chooseCharacter());
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

    /** ==================== TRẬN ĐẤU ==================== */
    public void setupBattleUI(CharacterModel player, CharacterModel enemy) {
        bgLabel.removeAll();

        playerImgLabel = new JLabel();
        playerImgLabel.setBounds(100, 200, 200, 200);
        setCharacterImage(playerImgLabel, player, true);  // người chơi
        bgLabel.add(playerImgLabel);

        enemyImgLabel = new JLabel();
        enemyImgLabel.setBounds(650, 200, 200, 200);
        setCharacterImage(enemyImgLabel, enemy, false);   // máy
        bgLabel.add(enemyImgLabel);

        JLabel playerLabel = new JLabel(player.getName(), SwingConstants.CENTER);
        playerLabel.setForeground(Color.ORANGE);
        playerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        playerLabel.setBounds(50, 30, 300, 30);
        bgLabel.add(playerLabel);

        JLabel enemyLabel = new JLabel(enemy.getName(), SwingConstants.CENTER);
        enemyLabel.setForeground(Color.CYAN);
        enemyLabel.setFont(new Font("Arial", Font.BOLD, 20));
        enemyLabel.setBounds(600, 30, 300, 30);
        bgLabel.add(enemyLabel);

        hpBarPlayer = new JProgressBar(0, 100);
        hpBarPlayer.setForeground(Color.RED);
        hpBarPlayer.setBounds(50, 70, 300, 20);
        bgLabel.add(hpBarPlayer);

        manaBarPlayer = new JProgressBar(0, 50);
        manaBarPlayer.setForeground(Color.BLUE);
        manaBarPlayer.setBounds(50, 100, 300, 20);
        bgLabel.add(manaBarPlayer);

        hpBarEnemy = new JProgressBar(0, 100);
        hpBarEnemy.setForeground(Color.RED);
        hpBarEnemy.setBounds(600, 70, 300, 20);
        bgLabel.add(hpBarEnemy);

        manaBarEnemy = new JProgressBar(0, 50);
        manaBarEnemy.setForeground(Color.BLUE);
        manaBarEnemy.setBounds(600, 100, 300, 20);
        bgLabel.add(manaBarEnemy);

        playerHP = new JLabel("HP: 100");
        playerHP.setForeground(Color.WHITE);
        playerHP.setBounds(50, 125, 100, 20);
        bgLabel.add(playerHP);

        playerMana = new JLabel("Mana: 50");
        playerMana.setForeground(Color.WHITE);
        playerMana.setBounds(150, 125, 100, 20);
        bgLabel.add(playerMana);

        enemyHP = new JLabel("HP: 100");
        enemyHP.setForeground(Color.WHITE);
        enemyHP.setBounds(600, 125, 100, 20);
        bgLabel.add(enemyHP);

        enemyMana = new JLabel("Mana: 50");
        enemyMana.setForeground(Color.WHITE);
        enemyMana.setBounds(700, 125, 100, 20);
        bgLabel.add(enemyMana);

        String[] names = {"🔥 Lửa", "💧 Nước", "🌪️ Gió", "❤️ Hồi Máu", "🛡️ Phòng Thủ", "✨ Hồi Mana", "💥 Chiêu Đặc Biệt"};
        skillButtons = new JButton[names.length];

        for (int i = 0; i < names.length; i++) {
            JButton b = new JButton(names[i]);
            b.setBounds(40 + (i % 4) * 220, 470 + (i / 4) * 60, 200, 40);
            b.setFont(new Font("Arial", Font.BOLD, 14));
            final int idx = i;
            b.addActionListener(e -> controller.playerTurn(idx));
            bgLabel.add(b);
            skillButtons[i] = b;
        }

        log = new JLabel("Trận đấu bắt đầu!", SwingConstants.CENTER);
        log.setForeground(Color.WHITE);
        log.setBounds(100, 380, 750, 50);
        log.setFont(new Font("Consolas", Font.PLAIN, 16));
        bgLabel.add(log);

        updateBars(player, enemy);
        bgLabel.revalidate();
        bgLabel.repaint();
    }

    /** ==================== CẬP NHẬT ==================== */
    public void updateBars(CharacterModel player, CharacterModel enemy) {
        hpBarPlayer.setValue(player.getHP());
        manaBarPlayer.setValue(player.getMana());
        hpBarEnemy.setValue(enemy.getHP());
        manaBarEnemy.setValue(enemy.getMana());
        playerHP.setText("HP: " + player.getHP());
        playerMana.setText("Mana: " + player.getMana());
        enemyHP.setText("HP: " + enemy.getHP());
        enemyMana.setText("Mana: " + enemy.getMana());
    }

    public void updateLog(String text) {
        log.setText("<html>" + text.replace("\n", "<br>") + "</html>");
    }

    public void showSkillEffect(int act, boolean isPlayer) {
        String imgPath = switch (act) {
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
        Timer timer = new Timer(800, e -> {
            bgLabel.remove(effect);
            bgLabel.revalidate();
            bgLabel.repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void setCharacterImage(JLabel label, CharacterModel model, boolean isPlayer) {
        ImageIcon icon;

        if (model instanceof HoaLongModel) {
            icon = new ImageIcon("src/img/Screenshot 2025-10-30 233345.png");

        } else if (model instanceof ThuyTamModel) {
            icon = new ImageIcon("src/img/Screenshot 2025-10-30 235048.png");

        } else {
            // model là Phong Vũ → phân biệt người chơi hay máy
            if (isPlayer)
                icon = new ImageIcon("src/img/nguoiChoi/PhongVuUser.png");
            else
                icon = new ImageIcon("src/img/may/PhongVuMay.png");
        }

        Image scaled = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        label.setIcon(new ImageIcon(scaled));
    }
    public void animateAttackForPhongVu() {
        // Kiểm tra có hình nhân vật người chơi không
        if (playerImgLabel == null || playerImgLabel.getIcon() == null) return;

        // Ảnh tấn công tạm thời (tư thế tung chiêu)
        ImageIcon attackIcon = new ImageIcon("src/img/nguoiChoi/PhongVuUserDC.png");
        Image scaledAttack = attackIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        ImageIcon attackScaledIcon = new ImageIcon(scaledAttack);

        // Lưu lại hình gốc của Phong Vũ
        ImageIcon oldIcon = new ImageIcon("src/img/nguoiChoi/PhongVuUser.png");
        Image scaledOld = oldIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        ImageIcon oldScaledIcon = new ImageIcon(scaledOld);

        // Đổi hình sang imggg.png
        playerImgLabel.setIcon(attackScaledIcon);

        // Sau 2 giây đổi lại hình cũ
        Timer backTimer = new Timer(1000, e -> {
            playerImgLabel.setIcon(oldScaledIcon);
        });
        backTimer.setRepeats(false);
        backTimer.start();
    }
    public void animateAttackForPhongVuEnemy() {
        // Kiểm tra có hình nhân vật máy không
        if (enemyImgLabel == null || enemyImgLabel.getIcon() == null) return;

        // Ảnh tấn công tạm thời của máy (Phong Vũ tung chiêu)
        ImageIcon attackIcon = new ImageIcon("src/img/may/PhongVuMayDC.png");
        Image scaledAttack = attackIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        ImageIcon attackScaledIcon = new ImageIcon(scaledAttack);

        // Lưu lại hình gốc của máy (Phong Vũ bình thường)
        ImageIcon oldIcon = new ImageIcon("src/img/may/PhongVuMay.png");
        Image scaledOld = oldIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        ImageIcon oldScaledIcon = new ImageIcon(scaledOld);

        // Đổi hình sang tư thế đánh
        enemyImgLabel.setIcon(attackScaledIcon);

        // Sau 1 giây đổi lại hình cũ
        Timer backTimer = new Timer(1000, e -> {
            enemyImgLabel.setIcon(oldScaledIcon);
        });
        backTimer.setRepeats(false);
        backTimer.start();
    }



}
