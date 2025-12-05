package model;

import java.util.List;
import java.util.Random;

public class Enemy {
    public Mage mage;
    private Random random = new Random();
    private Skill bestSkill;//chieu tot nhat ma minimax tim duoc

    public Skill getBestSkill() {
        return bestSkill;
    }

    public Enemy(Mage mage) {
        this.mage = mage;
    }


    public Skill chooseSkillMinimax(Mage player) {
        bestSkill = null;
        minimax(true, mage.cloneMage(), player.cloneMage(), 3); // độ sâu 3

        if (bestSkill == null) {
            //nếu ko chọn đc chiêu nào thì random
            return chooseSkill(player);
        }

        return bestSkill;
    }

    public Skill chooseSkill(Mage target) {
        List<Skill> list = mage.getSkills();
        Skill skill = list.get(random.nextInt(list.size()));
        if (mage.getMana() < skill.getManaCost()) return null;
        return skill;
    }


    public void useSkill(Skill skill, Player player) {
        if (skill != null) {
            skill.execute(mage, player.mage, player.mage, mage);
        } else {
            mage.regainMana(5);
            System.out.println("🤖 " + mage.getName() + " không đủ mana, hồi 5 mana.");
        }
    }

    public double heuristic(Mage enemyState, Mage playerState) {
        double score = 0;

        // HP
        score += (enemyState.getHp() - playerState.getHp()) * 2.0;

        // Mana
        score += (enemyState.getMana() - playerState.getMana()) * 0.5;

        // chiêu đặc biệt
        if (!enemyState.specialUsed) score += 5;
        if (!playerState.specialUsed) score -= 3;

        return score;
    }

    /**
     * maximizing = true -> lượt Enemy (AI)
     * maximizing = false -> lượt Player (bạn)
     * enemyState, playerState -> bản clone (mô phỏng)
     * depth -> độ sâu còn lại
     * @param maximizing
     * @param enemyState
     * @param playerState
     * @param depth
     * @return
     */
    public double minimax(boolean maximizing, Mage enemyState, Mage playerState, int depth) {

        // điều kiện dừng: hết độ sâu và 1 bên chết
        if (depth == 0 || enemyState.getHp() <= 0 || playerState.getHp() <= 0) {
            return heuristic(enemyState, playerState);
        }

        // MAX – Enemy, chọn chiêu tốt nhất:
        if (maximizing) {
            double best = -999999999;//Enemy muốn điểm lớn nhất

            for (Skill skill : enemyState.getSkills()) {
                if (!enemyState.canUseSkill(skill)) continue;

                // Tạo state mới, mô phỏng
                Mage e = enemyState.cloneMage();
                Mage p = playerState.cloneMage();
                e.useSkillSample(skill, p);

                //Đệ quy xuống tầng dưới (lượt Player)
                double eval = minimax(false, e, p, depth - 1);

                //Máy chọn giá trị lớn nhất, đánh giá càng lớn càng có lợi
                if (eval > best) {
                    best = eval;

                    // chỉ lưu chiêu tốt nhất ở tầng 3, bước đầu tiên
                    if (depth == 3) {
                        bestSkill = skill;
                    }
                }
            }

            return best;
        }

        // MIN – Player
        else {
            double best = 999999999;

            for (Skill skill : playerState.getSkills()) {

                if (!playerState.canUseSkill(skill)) continue;

                //mô phỏng
                Mage e = enemyState.cloneMage();
                Mage p = playerState.cloneMage();

                p.useSkillSample(skill, e);

                double eval = minimax(true, e, p, depth - 1);

                if (eval < best) {//điểm nhó nhất thì enemy bị thiệt hại nhất
                    best = eval;
                }
            }

            return best;
        }
    }


}
