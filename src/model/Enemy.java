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


        // ====== START MEASURE ======
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();

        minimax(true, mage.cloneMage(), player.cloneMage(), 6); // độ sâu 6
//        minimaxAlphaBeta(true, mage.cloneMage(), player.cloneMage(), 5, -Double.MAX_VALUE, Double.MAX_VALUE);


        // ====== END MEASURE ======

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
        return mage.heuristic(enemyState, playerState);
    }

    /**
     * Bọn e dựa theo cấu trúc của cô và chỉnh lại cho phù hợp với game của bạn em
     * maximizing = true -> lượt Enemy (AI)
     * maximizing = false -> lượt Player (bạn)
     * enemyState, playerState -> bản clone (mô phỏng)
     * depth -> độ sâu còn lại
     *Duyệt qua tất cả skill có thể dùng
     *
     * Với mỗi skill:
     *
     * Clone trạng thái
     *
     * Dùng skill
     *
     * Gọi minimax xuống tầng dưới
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

                //Đệ quy gọi minimax xuống tầng dưới (lượt Player), điểm đánh giá trạng thái
                double eval = minimax(false, e, p, depth - 1);

                //Máy chọn giá trị lớn nhất, đánh giá càng lớn càng có lợi
                if (eval > best) {                    best = eval;

                    // lưu chiêu tốt nhất ở tầng 4, bước đầu tiên
                    if (depth == 6) {
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

    public double minimaxAlphaBeta(boolean maximizing, Mage enemyState, Mage playerState, int depth, double alpha, double beta) {

        if (depth == 0 || enemyState.getHp() <= 0 || playerState.getHp() <= 0) {
            return heuristic(enemyState, playerState);
        }

        if (maximizing) {
            double best = -Double.MAX_VALUE;

            for (Skill skill : enemyState.getSkills()) {
                if (!enemyState.canUseSkill(skill)) continue;

                Mage e = enemyState.cloneMage();
                Mage p = playerState.cloneMage();
                e.useSkillSample(skill, p);

                double eval = minimaxAlphaBeta(false, e, p, depth - 1, alpha, beta);

                if (eval > best) {
                    best = eval;
                    if (depth == 3) bestSkill = skill;
                }

                alpha = Math.max(alpha, best);
                if (beta <= alpha) break; // cắt tỉa nhánh
            }

            return best;

        } else {
            double best = Double.MAX_VALUE;

            for (Skill skill : playerState.getSkills()) {
                if (!playerState.canUseSkill(skill)) continue;

                Mage e = enemyState.cloneMage();
                Mage p = playerState.cloneMage();
                p.useSkillSample(skill, e);

                double eval = minimaxAlphaBeta(true, e, p, depth - 1, alpha, beta);

                best = Math.min(best, eval);

                beta = Math.min(beta, best);
                if (beta <= alpha) break; // cắt tỉa nhánh
            }

            return best;
        }
    }


}
