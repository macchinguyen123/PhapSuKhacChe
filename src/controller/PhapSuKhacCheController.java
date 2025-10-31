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

        // 🧙‍♂️ Người chơi tấn công trước
        String playerResult = executeSingleAction(player, enemy, action, true);
        view.updateBars(player, enemy);
        view.updateLog(playerResult);

        if (checkEnd()) return;

        // 🤖 Sau 1 giây, máy phản công
        Timer timer = new Timer(3000, e -> {
            if (gameOver || !player.isAlive() || !enemy.isAlive()) return;

            int aiAction = pickAIAction();
            String aiResult = executeSingleAction(enemy, player, aiAction, false);
            view.updateBars(player, enemy);
            view.updateLog(playerResult + "<br>" + aiResult);
            checkEnd();
        });
        timer.setRepeats(false);
        timer.start();
        if (player instanceof PhongVuModel)
            view.animateAttackForPhongVu();

        if (enemy instanceof PhongVuModel)
            view.animateAttackForPhongVuEnemy();


    }

    // === Máy chọn chiêu ===
    private int pickAIAction() {
        if (enemy.getMana() < 10) return 5;                // Hết mana thì hồi mana
        if (enemy.getHP() < 30 && enemy.getMana() >= 25) return 3; // Thấp máu thì hồi máu
        if (!enemy.isUsedSpecial() && enemy.getMana() >= 20) return 6; // Dùng chiêu đặc biệt
        return rand.nextInt(3); // Ngẫu nhiên giữa lửa, nước, gió
    }

    // === Xử lý 1 hành động (đánh 1 chiều) ===
    private String executeSingleAction(CharacterModel attacker, CharacterModel defender, int action, boolean isPlayer) {
        StringBuilder sb = new StringBuilder();

        if (isPlayer)
            sb.append("🧙‍♂️ Người chơi dùng ").append(actionName(action)).append("!<br>");
        else
            sb.append("🤖 Máy phản công bằng ").append(actionName(action)).append("!<br>");

        // Hiệu ứng phép thuật
        view.showSkillEffect(action, isPlayer);

        // Thực hiện hành động
        applyAction(attacker, defender, action);

        sb.append("➡️ ").append(attacker.getName()).append(": HP ").append(attacker.getHP())
                .append(", Mana ").append(attacker.getMana()).append("<br>");
        sb.append("➡️ ").append(defender.getName()).append(": HP ").append(defender.getHP())
                .append(", Mana ").append(defender.getMana()).append("<br>");

        return sb.toString();
    }

    // === Thực thi hành động ===
    private void applyAction(CharacterModel atk, CharacterModel def, int act) {
        switch (act) {
            case 0, 1, 2 -> { // Lửa - Nước - Gió
                if (atk.getMana() < 10) return;
                atk.changeMana(-10);
                int dmg = 15;
                def.changeHP(-dmg);
            }
            case 3 -> { // Hồi máu
                if (atk.getMana() >= 25) {
                    atk.changeMana(-25);
                    atk.changeHP(20);
                }
            }
            case 4 -> { // Phòng thủ
                if (atk.getMana() >= 15) {
                    atk.changeMana(-15);
                    def.changeHP(-5);
                }
            }
            case 5 -> { // Hồi mana
                atk.changeHP(-15);
                atk.changeMana(25);
            }
            case 6 -> atk.special(def); // Chiêu đặc biệt riêng từng nhân vật
        }
    }

    // === Tên chiêu ===
    private String actionName(int act) {
        return switch (act) {
            case 0 -> "🔥 Lửa";
            case 1 -> "💧 Nước";
            case 2 -> "🌪️ Gió";
            case 3 -> "❤️ Hồi Máu";
            case 4 -> "🛡️ Phòng Thủ";
            case 5 -> "✨ Hồi Mana";
            case 6 -> "💥 Chiêu Đặc Biệt";
            default -> "Không rõ";
        };
    }

    // === Kiểm tra kết thúc trận ===
    private boolean checkEnd() {
        if (!player.isAlive() || !enemy.isAlive()) {
            gameOver = true;

            if (player.isAlive())
                JOptionPane.showMessageDialog(view, "🎉 Bạn thắng!");
            else
                JOptionPane.showMessageDialog(view, "💀 Bạn thua!");

            // 🔁 Reset toàn bộ JFrame (giống mở lại game từ đầu)
            view.dispose();
            SwingUtilities.invokeLater(() -> new PhapSuKhacCheView().setVisible(true));

            return true;
        }
        return false;
    }
}
