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
    private boolean isPlayerTurn = true;//kiểm soát lượt


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

    public void exit(){
        System.exit(0);
    }

    /** Khi người chơi chọn skill */
    public void playerUseSkill(Skill skill) {
        if (isGameOver) return;

        if (!isPlayerTurn) {
            frame.updateLog("⏳ Chưa đến lượt bạn!");
            return;
        }

        if (player.mage.getMana() < skill.getManaCost()) {
            frame.updateLog("⚠️ Không đủ mana để dùng " + skill.getName() + "!");
            return;
        }

        // Khóa nút skill
        frame.enableSkillButtons(false);
        isPlayerTurn = false;   // KHÓA LƯỢT NGƯỜI CHƠI

        // Player dùng skill
        player.useSkill(skill, enemy);
        boolean targetIsEnemy = !skill.isTargetSelf();
        frame.showSkillEffect(skill, targetIsEnemy);
        frame.updateBars(player.mage, enemy.mage);

        frame.updateLog("✨ Bạn dùng " + skill.getName() + "! Chờ máy tấn công...");

        if (!enemy.mage.isAlive()) {
            checkWinLose();
            return;
        }

        // 5s sau máy đánh
        javax.swing.Timer delay = new javax.swing.Timer(2000, null);
        delay.addActionListener(e -> {
            enemyTurn();
            delay.stop();  // dừng ngay sau khi chạy
            if (!isGameOver) frame.enableSkillButtons(true);
        });
        delay.start();
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
    private void enemyTurn() {
        if (isGameOver) return;

        Skill enemySkill = enemy.chooseSkillMinimax(player.mage);

        if (enemySkill != null) {
            enemy.useSkill(enemySkill, player);
            // TRUE = hiệu ứng lên enemyMage
            // FALSE = hiệu ứng lên playerMage
            boolean targetIsEnemy = enemySkill.isTargetSelf();

            frame.showSkillEffect(enemySkill, targetIsEnemy);
            frame.showSkillEffect(enemySkill, targetIsEnemy);
            frame.updateLog("⚔️ Máy dùng " + enemySkill.getName() + "!");
        } else {
            frame.updateLog("❗ Máy không đủ mana để dùng skill!");
        }

        frame.updateBars(player.mage, enemy.mage);
        checkWinLose();

        if (!isGameOver) {
            isPlayerTurn = true;              // Trả lượt lại cho người chơi
            frame.enableSkillButtons(true);   // Mở nút chiêu lại
        }
    }



}
