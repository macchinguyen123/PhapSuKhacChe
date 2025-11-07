package game;

import game.ui.GameFrame;
import javax.swing.*;
import java.util.Random;

public class GameController {
    private Player player;
    private Enemy enemy;
    private TurnManager turnManager;
    private boolean isGameOver = false;
    private final GameFrame frame;

    public GameController(GameFrame frame) {
        this.frame = frame;
    }

    /** Khởi tạo game */
    public void startGame() {
        player = new Player();
        enemy = new Enemy();
        turnManager = new TurnManager();

        // Người chơi chọn pháp sư
        Object[] options = {"🔥 Hoả Long", "💨 Phong Vũ", "💧 Thuỷ Tâm"};
        int choice = JOptionPane.showOptionDialog(null,
                "Chọn pháp sư của bạn", "Chọn nhân vật",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        switch (choice) {
            case 0 -> player.mage = new HoaLong();
            case 1 -> player.mage = new PhongVu();
            case 2 -> player.mage = new ThuyTam();
            default -> player.mage = new HoaLong();
        }

        // Máy chọn ngẫu nhiên
        int rand = new Random().nextInt(3);
        enemy.mage = switch (rand) {
            case 0 -> new HoaLong();
            case 1 -> new PhongVu();
            default -> new ThuyTam();
        };

        // Hiển thị giao diện trận đấu
        frame.setupBattle(player.mage, enemy.mage);
        frame.updateLog("🔰 Trận đấu giữa " + player.mage.getName() + " và " + enemy.mage.getName() + " bắt đầu!");
    }

    /** Khi người chơi chọn skill */
    public void playerUseSkill(Skill skill) {
        if (isGameOver) return;

        // Kiểm tra mana
        if (player.mage.getMana() < skill.getManaCost()) {
            frame.updateLog("⚠️ Không đủ mana để dùng " + skill.getName() + "!");
            return;
        }

        // Tấn công
        player.mage.attack(enemy.mage, skill, turnManager);
        frame.updateLog("👤 " + player.mage.getName() + " dùng " + skill.getName() + "!");
        frame.showSkillEffect(getSkillType(player.mage), true);
        frame.updateBars(player.mage, enemy.mage);

        // Giới hạn HP & Mana
        player.mage.limitStats();
        enemy.mage.limitStats();

        checkWinLose();
        if (isGameOver) return;

        // Máy phản công sau 1 giây
        Timer t = new Timer(1000, e -> {
            enemyTurn();
            frame.updateBars(player.mage, enemy.mage);
        });
        t.setRepeats(false);
        t.start();
    }

    /** Lượt của máy */
    private void enemyTurn() {
        if (isGameOver) return;

        Skill skill = enemy.chooseSkill();

        // Nếu không có chiêu nào đủ mana
        if (skill == null || enemy.mage.getMana() < skill.getManaCost()) {
            frame.updateLog("🤖 " + enemy.mage.getName() + " không đủ mana, nghỉ lượt và hồi lại 5 mana.");
            enemy.mage.regainMana(5);
            enemy.mage.limitStats();
            return;
        }

        // Tấn công
        enemy.mage.attack(player.mage, skill, turnManager);
        frame.updateLog("🤖 " + enemy.mage.getName() + " dùng " + skill.getName() + "!");
        frame.showSkillEffect(getSkillType(enemy.mage), false);

        // Giới hạn HP & Mana
        player.mage.limitStats();
        enemy.mage.limitStats();

        checkWinLose();
    }

    /** Kiểm tra thắng/thua */
    private void checkWinLose() {
        if (!player.mage.isAlive()) {
            isGameOver = true;
            frame.showEnd("💀 Bạn đã thua!");
        } else if (!enemy.mage.isAlive()) {
            isGameOver = true;
            frame.showEnd("🏆 Chiến thắng thuộc về bạn!");
        }
    }

    /** Xác định hiệu ứng (ảnh kỹ năng) */
    private int getSkillType(Mage mage) {
        if (mage instanceof HoaLong) return 0; // lửa
        if (mage instanceof ThuyTam) return 1; // nước
        return 2; // gió
    }
}
