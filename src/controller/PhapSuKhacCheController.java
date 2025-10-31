package controller;

import model.*;
import view.PhapSuKhacCheView;

import javax.swing.*;
import java.util.Random;

public class PhapSuKhacCheController {
    private final PhapSuKhacCheView view;
    private final Random rand = new Random();
    private CharacterModel player, enemy;
    private boolean gameOver = false;

    public PhapSuKhacCheController(PhapSuKhacCheView view) {
        this.view = view;
    }

    // === Xử lý chọn nhân vật ===
    public void chooseCharacter() {
        String[] options = {"🔥 Hỏa Long", "💧 Thủy Tâm", "🌪️ Phong Vũ"};
        int choice = JOptionPane.showOptionDialog(view, "Chọn nhân vật của bạn:", "Chọn nhân vật",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == JOptionPane.CLOSED_OPTION) {
            view.showStartScreen();
            return;
        }

        switch (choice) {
            case 0 -> player = new HoaLongModel();
            case 1 -> player = new ThuyTamModel();
            case 2 -> player = new PhongVuModel();
            default -> player = new HoaLongModel();
        }

        int enemyChoice = rand.nextInt(3);
        enemy = switch (enemyChoice) {
            case 0 -> new HoaLongModel();
            case 1 -> new ThuyTamModel();
            default -> new PhongVuModel();
        };

        JOptionPane.showMessageDialog(view, "👾 Máy chọn: " + enemy.getName());
        view.setupBattleUI(player, enemy);
    }

    // === Lượt người chơi ===
    public void playerTurn(int action) {
        if (gameOver || !player.isAlive() || !enemy.isAlive()) return;
        int aiAction = pickAIAction();
        String result = executeTurn(player, enemy, action, aiAction);
        view.updateBars(player, enemy);
        if (checkEnd()) return;
        view.updateLog(result);
    }

    private int pickAIAction() {
        if (enemy.getMana() < 10) return 5;
        if (enemy.getHP() < 30 && enemy.getMana() >= 25) return 3;
        if (!enemy.isUsedSpecial() && enemy.getMana() >= 20) return 6;
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
                view.showSkillEffect(act, atk == player);
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

    private boolean checkEnd() {
        if (!player.isAlive() || !enemy.isAlive()) {
            gameOver = true;

            if (player.isAlive())
                JOptionPane.showMessageDialog(view, "🎉 Bạn thắng!");
            else
                JOptionPane.showMessageDialog(view, "💀 Bạn thua!");

            // 🔁 Reset toàn bộ JFrame (giống mở lại game từ đầu)
            view.dispose(); // đóng cửa sổ hiện tại
            SwingUtilities.invokeLater(() -> new PhapSuKhacCheView().setVisible(true));

            return true;
        }
        return false;
    }


}
