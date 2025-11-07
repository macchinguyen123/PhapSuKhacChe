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

        showStartScreen();
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
        startBtn.addActionListener(e -> chooseCharacter());
        bgLabel.add(startBtn);

        JButton exitBtn = createTransparentButton("❌ RỜI KHỎI");
        exitBtn.setBounds(350, 380, 250, 50);
        exitBtn.addActionListener(e -> System.exit(0));
        bgLabel.add(exitBtn);

        bgLabel.revalidate();
        bgLabel.repaint();
    }

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
        }

        // Máy chọn ngẫu nhiên
        int enemyChoice = rand.nextInt(3);
        enemy = switch (enemyChoice) {
            case 0 -> new HoaLongModel();
            case 1 -> new ThuyTamModel();
            default -> new PhongVuModel();
        };

        JOptionPane.showMessageDialog(this, "👾 Máy chọn: " + enemy.getName());

        bgLabel.removeAll();
        bgLabel.revalidate();
        bgLabel.repaint();

        setupUI();
    }

    /** ==================== GIAO DIỆN TRẬN ĐẤU ==================== */
    private void setupUI() {
        playerImgLabel = new JLabel();
        playerImgLabel.setBounds(100, 200, 200, 200);
        setCharacterImage(playerImgLabel, player);
        bgLabel.add(playerImgLabel);

        enemyImgLabel = new JLabel();
        enemyImgLabel.setBounds(650, 200, 200, 200);
        setCharacterImage(enemyImgLabel, enemy);
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

        // 5 kỹ năng theo mô tả mới
        String[] names = {"⚔️ Đánh thường", "🔥 Chiêu 1", "💥 Chiêu 2", "✨ Khắc chế đặc biệt", "❤️ Hồi máu"};
        skillButtons = new JButton[names.length];
        for (int i = 0; i < names.length; i++) {
            JButton b = new JButton(names[i]);
            b.setBounds(60 + i * 170, 500, 160, 40);
            b.setFont(new Font("Arial", Font.BOLD, 14));
            final int idx = i;
            b.addActionListener(e -> {
                if (!gameOver)
                    playerTurn(idx);
            });
            bgLabel.add(b);
            skillButtons[i] = b;
        }

        log = new JLabel("Trận đấu bắt đầu!", SwingConstants.CENTER);
        log.setForeground(Color.WHITE);
        log.setBounds(100, 400, 750, 50);
        log.setFont(new Font("Consolas", Font.PLAIN, 16));
        bgLabel.add(log);

        updateBars();
    }

    private void setCharacterImage(JLabel label, CharacterModel model) {
        ImageIcon icon;
        if (model instanceof HoaLongModel)
            icon = new ImageIcon("src/img/lua.png");
        else if (model instanceof ThuyTamModel)
            icon = new ImageIcon("src/img/nuoc.png");
        else
            icon = new ImageIcon("src/img/gio.png");

        Image scaled = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        label.setIcon(new ImageIcon(scaled));
    }

    private void playerTurn(int action) {
        if (!player.isAlive() || !enemy.isAlive()) return;
        int aiAction = rand.nextInt(5);
        String result = executeTurn(player, enemy, action, aiAction);
        updateBars();
        if (checkEnd()) return;
        log.setText("<html>" + result.replace("\n", "<br>") + "</html>");
    }

    private String executeTurn(CharacterModel p, CharacterModel e, int pAct, int eAct) {
        StringBuilder sb = new StringBuilder();
        sb.append("Người chơi dùng: ").append(p.actionName(pAct)).append(" | Máy dùng: ").append(e.actionName(eAct)).append("\n");
        p.useSkill(pAct, e);
        e.useSkill(eAct, p);
        sb.append("➡️ ").append(p.getName()).append(": HP ").append(p.getHP()).append(", Mana ").append(p.getMana()).append("\n");
        sb.append("➡️ ").append(e.getName()).append(": HP ").append(e.getHP()).append(", Mana ").append(e.getMana()).append("\n");
        return sb.toString();
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

/* ==================== CHARACTER MODELS ==================== */

abstract class CharacterModel {
    protected String name;
    protected int hp = 100, mana = 50;
    protected boolean usedSpecial = false;

    public String getName() { return name; }
    public int getHP() { return hp; }
    public int getMana() { return mana; }
    public boolean isAlive() { return hp > 0; }

    public void changeHP(int amount) { hp = Math.max(0, Math.min(100, hp + amount)); }
    public void changeMana(int amount) { mana = Math.max(0, Math.min(50, mana + amount)); }

    public abstract void useSkill(int idx, CharacterModel opponent);

    public String actionName(int i) {
        return switch (i) {
            case 0 -> "Đánh thường";
            case 1 -> "Chiêu 1";
            case 2 -> "Chiêu 2";
            case 3 -> "Khắc chế đặc biệt";
            case 4 -> "Hồi máu";
            default -> "???";
        };
    }
}

/* ==================== HOẢ LONG ==================== */
class HoaLongModel extends CharacterModel {
    public HoaLongModel() { name = "🔥 Hỏa Long"; }

    @Override
    public void useSkill(int idx, CharacterModel o) {
        switch (idx) {
            case 0 -> { changeMana(+5); o.changeHP(-10); } // Đánh thường
            case 1 -> { if (mana >= 10) { changeMana(-10); o.changeHP(-12); } }
            case 2 -> { if (mana >= 18) { changeMana(-18); o.changeHP(-30); changeHP(-10); } }
            case 3 -> { if (!usedSpecial && mana >= 20) { usedSpecial = true; changeMana(-20);
                if (o instanceof PhongVuModel) o.changeHP(-38);
                else if (o instanceof ThuyTamModel) { o.changeHP(-20); changeHP(+15); changeMana(+5); }
                else if (o instanceof HoaLongModel) { o.changeHP(-30); changeMana(+10); }
            }}
            case 4 -> { if (mana >= 15) { changeMana(-15); changeHP(+25); } }
        }
    }
}

/* ==================== PHONG VŨ ==================== */
class PhongVuModel extends CharacterModel {
    public PhongVuModel() { name = "🌪️ Phong Vũ"; }

    @Override
    public void useSkill(int idx, CharacterModel o) {
        switch (idx) {
            case 0 -> { changeMana(+5); o.changeHP(-10); }
            case 1 -> { if (mana >= 10) { changeMana(-10); o.changeHP(-14); o.changeMana(-8); } }
            case 2 -> { if (mana >= 18) { changeMana(-18); o.changeHP(-24); changeHP(+10); } }
            case 3 -> { if (!usedSpecial && mana >= 20) { usedSpecial = true; changeMana(-20);
                if (o instanceof HoaLongModel) { o.changeHP(-25); changeHP(+10); }
                else if (o instanceof ThuyTamModel) { o.changeHP(-15); changeHP(+10); changeMana(+10); }
                else if (o instanceof PhongVuModel) { o.changeHP(-30); changeMana(+10); }
            }}
            case 4 -> { if (mana >= 15) { changeMana(-15); changeHP(+20); } }
        }
    }
}

/* ==================== THỦY TÂM ==================== */
class ThuyTamModel extends CharacterModel {
    public ThuyTamModel() { name = "💧 Thủy Tâm"; }

    @Override
    public void useSkill(int idx, CharacterModel o) {
        switch (idx) {
            case 0 -> { changeMana(+5); o.changeHP(-10); }
            case 1 -> { if (mana >= 10) { changeMana(-10); o.changeHP(-12); changeHP(+10); } }
            case 2 -> { if (mana >= 18) { changeMana(-18); o.changeHP(-22); changeMana(+8); } }
            case 3 -> { if (!usedSpecial && mana >= 20) { usedSpecial = true; changeMana(-20);
                if (o instanceof HoaLongModel) changeHP(Math.min(50, +50));
                else if (o instanceof PhongVuModel) o.changeHP(-5);
                else if (o instanceof ThuyTamModel) { changeMana(50 - mana); changeHP(-10); }
            }}
            case 4 -> { if (mana >= 15) { changeMana(-15); changeHP(+20); } }
        }
    }
}
