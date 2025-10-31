package demo1;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class PhapSuKhacCheGUI extends JFrame {
    private CharacterModel player, enemy;
    private JLabel playerHP, playerMana, enemyHP, enemyMana, log;
    private JProgressBar hpBarPlayer, manaBarPlayer, hpBarEnemy, manaBarEnemy;
    private JButton[] skillButtons;
    private Random rand = new Random();
    private boolean gameOver = false;
    private JLabel bgLabel, playerImgLabel, enemyImgLabel;

    public PhapSuKhacCheGUI() {
        setTitle("⚔️ Pháp Sư Khắc Chế");
        setSize(950, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Thêm background
        ImageIcon bgIcon = new ImageIcon("src/img/img.png");
        Image bgScaled = bgIcon.getImage().getScaledInstance(950, 600, Image.SCALE_SMOOTH);
        bgLabel = new JLabel(new ImageIcon(bgScaled));
        bgLabel.setBounds(0, 0, 950, 600);
        bgLabel.setLayout(null);

        setContentPane(bgLabel);

        showStartScreen(); // Trang khởi đầu
    }

    /** ==================== TRANG KHỞI ĐẦU ==================== */
    private void showStartScreen() {
        bgLabel.removeAll();
        bgLabel.repaint();

        JLabel title = new JLabel("⚔️ PHÁP SƯ KHẮC CHẾ ⚔️", SwingConstants.CENTER);
        title.setForeground(new Color(255, 215, 0));
        title.setFont(new Font("Serif", Font.BOLD, 42));
        title.setBounds(100, 150, 750, 80);
        bgLabel.add(title);

        JButton startBtn = createTransparentButton("▶ BẮT ĐẦU");
        startBtn.setBounds(350, 300, 250, 50);
        startBtn.addActionListener(e -> {
            chooseCharacter();
        });
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

    /** ==================== CHỌN NHÂN VẬT ==================== */
    private void chooseCharacter() {
        String[] options = {"🔥 Hỏa Long", "💧 Thủy Tâm", "🌪️ Phong Vũ"};
        int choice = JOptionPane.showOptionDialog(this, "Chọn nhân vật của bạn:", "Chọn nhân vật",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == JOptionPane.CLOSED_OPTION) {
            showStartScreen();
            return;
        }

        switch (choice) {
            case 0 -> player = new HoaLongModel();
            case 1 -> player = new ThuyTamModel();
            case 2 -> player = new PhongVuModel();
            default -> player = new HoaLongModel();
        }

        // Máy chọn ngẫu nhiên
        int enemyChoice = rand.nextInt(3);
        enemy = switch (enemyChoice) {
            case 0 -> new HoaLongModel();
            case 1 -> new ThuyTamModel();
            default -> new PhongVuModel();
        };

        JOptionPane.showMessageDialog(this, "👾 Máy chọn: " + enemy.getName());

        // Dọn nền và tạo giao diện trận đấu
        bgLabel.removeAll();
        bgLabel.revalidate();
        bgLabel.repaint();

        setupUI();
    }

    /** ==================== GIAO DIỆN TRẬN ĐẤU ==================== */
    private void setupUI() {
        // Ảnh nhân vật
        playerImgLabel = new JLabel();
        playerImgLabel.setBounds(100, 200, 200, 200);
        setCharacterImage(playerImgLabel, player);
        bgLabel.add(playerImgLabel);

        enemyImgLabel = new JLabel();
        enemyImgLabel.setBounds(650, 200, 200, 200);
        setCharacterImage(enemyImgLabel, enemy);
        bgLabel.add(enemyImgLabel);

        // Tên nhân vật
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

        // Thanh HP/Mana
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

        // Nút kỹ năng
        String[] names = {"🔥 Lửa", "💧 Nước", "🌪️ Gió", "❤️ Hồi Máu", "🛡️ Phòng Thủ", "✨ Hồi Mana", "💥 Chiêu Đặc Biệt"};
        skillButtons = new JButton[names.length];

        for (int i = 0; i < names.length; i++) {
            JButton b = new JButton(names[i]);
            b.setBounds(40 + (i % 4) * 220, 470 + (i / 4) * 60, 200, 40);
            b.setFont(new Font("Arial", Font.BOLD, 14));
            final int idx = i;
            b.addActionListener(e -> {
                if (!gameOver)
                    playerTurn(idx);
            });
            bgLabel.add(b);
            skillButtons[i] = b;
        }

        // Log
        log = new JLabel("Trận đấu bắt đầu!", SwingConstants.CENTER);
        log.setForeground(Color.WHITE);
        log.setBounds(100, 380, 750, 50);
        log.setFont(new Font("Consolas", Font.PLAIN, 16));
        bgLabel.add(log);

        updateBars();

        bgLabel.revalidate();
        bgLabel.repaint();
    }

    /** ==================== CÁC HÀM PHỤ ==================== */
    private void setCharacterImage(JLabel label, CharacterModel model) {
        ImageIcon icon;
        if (model instanceof HoaLongModel)
            icon = new ImageIcon("src/img/Screenshot 2025-10-30 233345.png");
        else if (model instanceof ThuyTamModel)
            icon = new ImageIcon("src/img/Screenshot 2025-10-30 235048.png");
        else
            icon = new ImageIcon("src/img/Screenshot 2025-10-30 233345.png");

        Image scaled = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        label.setIcon(new ImageIcon(scaled));
    }

    private void showSkillEffect(String imgPath, JLabel targetLabel) {
        ImageIcon icon = new ImageIcon(imgPath);
        Image scaled = icon.getImage().getScaledInstance(targetLabel.getWidth(), targetLabel.getHeight(), Image.SCALE_SMOOTH);
        JLabel effect = new JLabel(new ImageIcon(scaled));
        effect.setBounds(targetLabel.getX(), targetLabel.getY(), targetLabel.getWidth(), targetLabel.getHeight());
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

    // --- phần xử lý lượt đánh giữ nguyên ---
    private void playerTurn(int action) {
        if (!player.isAlive() || !enemy.isAlive()) return;
        int aiAction = pickAIAction();
        String result = executeTurn(player, enemy, action, aiAction);
        updateBars();
        if (checkEnd()) return;
        log.setText("<html>" + result.replace("\n", "<br>") + "</html>");
    }

    private int pickAIAction() {
        if (enemy.getMana() < 10) return 5;
        if (enemy.getHP() < 30 && enemy.getMana() >= 25) return 3;
        if (!enemy.usedSpecial && enemy.getMana() >= 20) return 6;
        return rand.nextInt(3);
    }

    private String executeTurn(CharacterModel p, CharacterModel e, int pAct, int eAct) {
        StringBuilder sb = new StringBuilder();
        sb.append("Người chơi dùng: ").append(actionName(pAct)).append(" | Máy dùng: ").append(actionName(eAct)).append("\n");
        if (pAct == 6) p.special(e);
        if (eAct == 6) e.special(p);
        applyAction(p, e, pAct, eAct);
        applyAction(e, p, eAct, pAct);
        sb.append("➡️ ").append(p.getName()).append(": HP ").append(p.getHP()).append(", Mana ").append(p.getMana()).append("\n");
        sb.append("➡️ ").append(e.getName()).append(": HP ").append(e.getHP()).append(", Mana ").append(e.getMana()).append("\n");
        return sb.toString();
    }

    private void applyAction(CharacterModel atk, CharacterModel def, int act, int oppAct) {
        switch (act) {
            case 0, 1, 2 -> {
                if (atk.getMana() < 10) return;
                atk.changeMana(-10);
                if (atk == player) {
                    if (act == 0) showSkillEffect("src/img/lua.png", enemyImgLabel);
                    else if (act == 1) showSkillEffect("src/img/nuoc.png", enemyImgLabel);
                    else if (act == 2) showSkillEffect("src/img/gio.png", enemyImgLabel);
                } else {
                    if (act == 0) showSkillEffect("src/img/lua.png", playerImgLabel);
                    else if (act == 1) showSkillEffect("src/img/nuoc.png", playerImgLabel);
                    else if (act == 2) showSkillEffect("src/img/gio.png", playerImgLabel);
                }
                int dmg = 15;
                if ((act == 0 && oppAct == 2) || (act == 1 && oppAct == 0) || (act == 2 && oppAct == 1)) dmg = 20;
                else if (act == oppAct) dmg = 10;
                def.changeHP(-dmg);
            }
            case 3 -> { if (atk.getMana() >= 25) { atk.changeMana(-25); atk.changeHP(20); } }
            case 4 -> { if (atk.getMana() >= 15) atk.changeMana(-15); def.changeHP(-5); }
            case 5 -> { atk.changeHP(-15); atk.changeMana(25); }
        }
    }

    private String actionName(int act) {
        return switch (act) {
            case 0 -> "Lửa";
            case 1 -> "Nước";
            case 2 -> "Gió";
            case 3 -> "Hồi Máu";
            case 4 -> "Phòng Thủ";
            case 5 -> "Hồi Mana";
            case 6 -> "Chiêu Đặc Biệt";
            default -> "Không rõ";
        };
    }

    private void updateBars() {
        hpBarPlayer.setValue(player.getHP());
        manaBarPlayer.setValue(player.getMana());
        hpBarEnemy.setValue(enemy.getHP());
        manaBarEnemy.setValue(enemy.getMana());
        playerHP.setText("HP: " + player.getHP());
        playerMana.setText("Mana: " + player.getMana());
        enemyHP.setText("HP: " + enemy.getHP());
        enemyMana.setText("Mana: " + enemy.getMana());
    }

    private boolean checkEnd() {
        if (!player.isAlive() || !enemy.isAlive()) {
            gameOver = true;
            if (player.isAlive())
                JOptionPane.showMessageDialog(this, "🎉 Bạn thắng!");
            else
                JOptionPane.showMessageDialog(this, "💀 Bạn thua!");
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PhapSuKhacCheGUI().setVisible(true));
    }
}


// ------------------- Character Models --------------------

abstract class CharacterModel {
    protected String name;
    protected int hp = 100, mana = 50;
    protected boolean usedSpecial = false;

    public String getName() { return name; }
    public int getHP() { return hp; }
    public int getMana() { return mana; }
    public boolean isAlive() { return hp > 0; }

    public void changeHP(int amount) {
        hp = Math.max(0, Math.min(100, hp + amount));
    }

    public void changeMana(int amount) {
        mana = Math.max(0, Math.min(50, mana + amount));
    }

    public abstract void special(CharacterModel opponent);
}

class HoaLongModel extends CharacterModel {
    public HoaLongModel() { name = "🔥 Hỏa Long"; }

    @Override
    public void special(CharacterModel opponent) {
        if (usedSpecial || mana < 30) return;
        usedSpecial = true;
        changeMana(-30);
        opponent.changeHP(-40);
        changeHP(-10);
    }
}

class ThuyTamModel extends CharacterModel {
    public ThuyTamModel() { name = "💧 Thủy Tâm"; }

    @Override
    public void special(CharacterModel opponent) {
        if (usedSpecial || mana < 25) return;
        usedSpecial = true;
        changeMana(-25);
        changeHP(10);
    }
}

class PhongVuModel extends CharacterModel {
    public PhongVuModel() { name = "🌪️ Phong Vũ"; }

    @Override
    public void special(CharacterModel opponent) {
        if (usedSpecial || mana < 20) return;
        usedSpecial = true;
        changeMana(-20);
        opponent.changeHP(-15);
        if (opponent.getMana() >= 10)
            opponent.changeMana(-10);
        else {
            opponent.changeMana(-opponent.getMana());
            opponent.changeHP(-5);
        }
    }
}
