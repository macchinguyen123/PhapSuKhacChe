package controller;

import model.*;
import view.GameFrame;
import javax.swing.*;
import java.awt.*;

public class GameController {
    private Player player;
    private Enemy enemy;
    private boolean isGameOver = false;
    private final GameFrame frame;
    private Mage selectedPlayerMage, selectedEnemyMage;

    public GameController(GameFrame frame) {
        this.frame = frame;
    }

    public void setPlayerMage(Mage m) {
        selectedPlayerMage = m;
    }

    public void setEnemyMage(Mage m) {
        selectedEnemyMage = m;
    }

    public void finishCharacterSelect() {
        player = new Player(selectedPlayerMage);
        enemy = new Enemy(selectedEnemyMage);

        frame.setupBattle(player.mage, enemy.mage);
        frame.updateLog("🔰 Trận đấu giữa "
                + player.mage.getName() + " và " + enemy.mage.getName() + " bắt đầu!");
    }

    public void startGame() {
        frame.showCharacterSelect(true);
    }

    /** Khi người chơi chọn skill */
    public void playerUseSkill(Skill skill) {
        if (isGameOver) return;

        // Kiểm tra mana
        if (player.mage.getMana() < skill.getManaCost()) {
            frame.updateLog("⚠️ Không đủ mana để dùng " + skill.getName() + "!");
            return;
        }

        // Player dùng skill
        player.useSkill(skill, enemy);
        frame.showSkillEffect(getSkillType(player.mage), true);

        // Enemy phản công ngay lượt đó
        Skill enemySkill = enemy.chooseSkill(player.mage);
        if (enemySkill != null) {
            enemy.useSkill(enemySkill, player);
            frame.showSkillEffect(getSkillType(enemy.mage), false);
        }

        // Cập nhật thanh HP/Mana
        player.mage.limitStats();
        enemy.mage.limitStats();
        frame.updateBars(player.mage, enemy.mage);

        // Kiểm tra thắng thua
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

    /** Xác định hiệu ứng skill */
    private int getSkillType(Mage mage) {
        if (mage instanceof HoaLong) return 0; // lửa
        if (mage instanceof ThuyTam) return 1; // nước
        return 2; // gió
    }
}
