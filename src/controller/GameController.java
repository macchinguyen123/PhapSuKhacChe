package controller;

import model.*;
import view.GameFrame;
import javax.swing.*;

public class GameController {
    private Player player;
    private Enemy enemy;
    private boolean isGameOver = false;
    private final GameFrame frame;
    private Mage selectedPlayerMage, selectedEnemyMage;

    public void setPlayerMage(Mage m) {
        selectedPlayerMage = m;
    }

    public void setEnemyMage(Mage m) {
        selectedEnemyMage = m;
    }

    public void finishCharacterSelect() {
        player = new Player();
        player.mage = selectedPlayerMage;

        enemy = new Enemy();
        enemy.mage = selectedEnemyMage;


        frame.setupBattle(player.mage, enemy.mage);
        frame.updateLog("🔰 Trận đấu giữa "
                + player.mage.getName() + " và " + enemy.mage.getName() + " bắt đầu!");
    }


    public GameController(GameFrame frame) {
        this.frame = frame;
    }


    /** Khởi tạo game */
    public void startGame() {
        frame.showCharacterSelect(true);  // bắt đầu chọn nhân vật
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
        player.mage.attack(enemy.mage, skill);
        frame.updateLog("👤 " + player.mage.getName() + " dùng " + skill.getName() + "!");
        frame.showSkillEffect(getSkillType(player.mage), true);
        frame.updateBars(player.mage, enemy.mage);

        // Giới hạn HP & Mana
        player.mage.limitStats();
        enemy.mage.limitStats();

        checkWinLose();
        if (isGameOver) return;

        // Máy phản công sau 1 giây
        Timer t = new Timer(3000, e -> {
            enemyTurn();
            frame.updateBars(player.mage, enemy.mage);
        });
        t.setRepeats(false);
        t.start();
    }

    /** Lượt của máy */
    private void enemyTurn() {
        if (isGameOver) return;

        // Dùng Minimax thực với trạng thái Player
        Skill skill = enemy.chooseSkill(player.mage);

        // Nếu không có chiêu nào đủ mana
        if (skill == null || enemy.mage.getMana() < skill.getManaCost()) {
            frame.updateLog("🤖 " + enemy.mage.getName() + " không đủ mana, nghỉ lượt và hồi lại 5 mana.");
            enemy.mage.regainMana(5);
            enemy.mage.limitStats();
            return;
        }

        // Tấn công
        enemy.mage.attack(player.mage, skill);
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
