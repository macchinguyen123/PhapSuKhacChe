package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Enemy {
    public Mage mage;
    private final Random rnd = new Random();

    // Depth của minimax (càng cao máy sẽ 'suy nghĩ' mạnh hơn nhưng chậm hơn)
    private final int SEARCH_DEPTH = 3;

    public Skill chooseSkill() {
        List<Skill> skills = mage.getSkills();

        // Lọc chiêu có thể dùng (mana đủ). Nếu không có chiêu dùng, trả null.
        List<Skill> usable = new ArrayList<>();
        for (Skill s : skills) {
            if (mage.getMana() >= s.getManaCost()) usable.add(s);
        }
        if (usable.isEmpty()) return null;

        // Nếu chỉ 1 chiêu dùng được -> return luôn
        if (usable.size() == 1) return usable.get(0);

        // Tạo state ban đầu
        GameState enemyState = GameState.fromMage(mage);
        // NOTE: opponent state will be set by caller; here we don't have direct reference to player.
        // We'll let caller pass player state by calling chooseSkillWithOpponent(...) if needed.
        // For backward compatibility, we will randomly choose from usable when player state unknown.
        // But in typical usage GameController passes actual opponent? (we don't have that)
        // So we will try to use reflection: but simpler: pick a random usable if no context.
        // To allow better behavior, prefer a probabilistic pick if no extra context:
        return minimaxChoose(usable, enemyState);
    }

    // Alternate chooser you can call if you pass opponent mage (recommended by GameController)
    public Skill chooseSkill(Mage opponent) {
        List<Skill> skills = mage.getSkills();
        List<Skill> usable = new ArrayList<>();
        for (Skill s : skills) {
            if (mage.getMana() >= s.getManaCost()) usable.add(s);
        }
        if (usable.isEmpty()) return null;
        if (usable.size() == 1) return usable.get(0);

        GameState enemyState = GameState.fromMage(mage);
        GameState playerState = GameState.fromMage(opponent);

        return minimaxChoose(usable, enemyState, playerState);
    }

    // Fallback: minimax without known opponent – choose randomly (or simple heuristic)
    private Skill minimaxChoose(List<Skill> usable, GameState enemyState) {
        // fallback heuristic: prefer highest damage or special randomly
        Skill best = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        for (Skill s : usable) {
            double score = simpleHeuristicForSkill(enemyState, s);
            if (score > bestScore) {
                bestScore = score;
                best = s;
            }
        }
        return best != null ? best : usable.get(rnd.nextInt(usable.size()));
    }

    // Real minimax with opponent state
    private Skill minimaxChoose(List<Skill> usable, GameState enemyState, GameState playerState) {
        double bestVal = Double.NEGATIVE_INFINITY;
        Skill bestSkill = null;
        for (Skill s : usable) {
            // simulate applying s by enemy -> player
            GameState eCopy = enemyState.copy();
            GameState pCopy = playerState.copy();

            applySkillSim(eCopy, pCopy, s, true); // enemy uses skill on player

            double val = minimax(eCopy, pCopy, SEARCH_DEPTH - 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false);
            if (val > bestVal) {
                bestVal = val;
                bestSkill = s;
            } else if (val == bestVal && rnd.nextBoolean()) {
                // tie-break random
                bestSkill = s;
            }
        }
        return bestSkill != null ? bestSkill : usable.get(rnd.nextInt(usable.size()));
    }

    // Minimax (maximizing for enemy)
    private double minimax(GameState enemyState, GameState playerState, int depth, double alpha, double beta, boolean maximizingPlayer) {
        // Terminal or depth 0 => evaluate heuristic
        if (depth == 0 || enemyState.isDead() || playerState.isDead()) {
            return evaluate(enemyState, playerState);
        }

        if (maximizingPlayer) {
            double maxEval = Double.NEGATIVE_INFINITY;
            // enemy's turn: consider enemy's usable skills
            for (Skill s : enemyState.getSkills()) {
                if (enemyState.mana < s.getManaCost()) continue;
                GameState e2 = enemyState.copy();
                GameState p2 = playerState.copy();
                applySkillSim(e2, p2, s, true);
                double eval = minimax(e2, p2, depth - 1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break; // prune
            }
            // If no usable skills => enemy regenerates small mana (simulate skip)
            if (maxEval == Double.NEGATIVE_INFINITY) {
                GameState e2 = enemyState.copy();
                e2.regainManaSim(5);
                maxEval = minimax(e2, playerState.copy(), depth -1, alpha, beta, false);
            }
            return maxEval;
        } else {
            double minEval = Double.POSITIVE_INFINITY;
            // player's turn (assume player chooses best for themselves, i.e., minimize enemy score)
            for (Skill s : playerState.getSkills()) {
                if (playerState.mana < s.getManaCost()) continue;
                GameState e2 = enemyState.copy();
                GameState p2 = playerState.copy();
                applySkillSim(p2, e2, s, true); // player uses on enemy (note param order)
                double eval = minimax(e2, p2, depth -1, alpha, beta, true);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break;
            }
            // If player has no usable skill, simulate mana regen
            if (minEval == Double.POSITIVE_INFINITY) {
                GameState p2 = playerState.copy();
                p2.regainManaSim(5);
                minEval = minimax(enemyState.copy(), p2, depth -1, alpha, beta, true);
            }
            return minEval;
        }
    }

    // Heuristic evaluation: higher = better for enemy
    private double evaluate(GameState enemyState, GameState playerState) {
        if (playerState.isDead()) return 10000;
        if (enemyState.isDead()) return -10000;

        double score = 0;
        // weight hp more
        score += (enemyState.hp - playerState.hp) * 2.0;
        // some weight for mana
        score += (enemyState.mana - playerState.mana) * 0.6;
        // penalty if special available or used? prefer using special early
        if (!enemyState.specialUsed) score += 5;
        if (!playerState.specialUsed) score -= 3;

        // small bonus for being closer to kill
        double hpRatioDiff = (enemyState.hp/100.0) - (playerState.hp/100.0);
        score += hpRatioDiff * 20;

        return score;
    }

    // Apply a skill in simulation: user uses skill on target.
    // userParam is caller (attacker), targetParam is defender.
    private void applySkillSim(GameState user, GameState target, Skill s, boolean isUserAttacking) {
        // subtract mana cost
        user.mana = Math.max(0, user.mana - s.getManaCost());

        // direct damage / heal / manaGain
        if (s.getDamage() > 0) target.hp = Math.max(0, target.hp - s.getDamage());
        if (s.getHeal() > 0) user.hp = Math.min(100, user.hp + s.getHeal());
        if (s.getManaGain() > 0) user.mana = Math.min(50, user.mana + s.getManaGain());

        // special handling if skill is special
        if (s.isSpecial()) {
            // Determine user type & target type by stored type string
            String uType = user.type;
            String tType = target.type;

            if (uType.equals("HoaLong")) {
                // HoaLong special
                if (tType.equals("PhongVu")) {
                    target.hp = Math.max(0, target.hp - 38);
                } else if (tType.equals("ThuyTam")) {
                    target.hp = Math.max(0, target.hp - 20);
                    user.hp = Math.min(100, user.hp + 15);
                    user.mana = Math.min(50, user.mana + 5);
                } else if (tType.equals("HoaLong")) {
                    target.hp = Math.max(0, target.hp - 30);
                    user.mana = Math.min(50, user.mana + 10);
                } else {
                    target.hp = Math.max(0, target.hp - 30);
                }
                user.specialUsed = true;
            } else if (uType.equals("PhongVu")) {
                if (tType.equals("HoaLong")) {
                    target.hp = Math.max(0, target.hp - 25);
                    user.hp = Math.min(100, user.hp + 10);
                } else if (tType.equals("ThuyTam")) {
                    target.hp = Math.max(0, target.hp - 15);
                    user.mana = Math.min(50, user.mana + 10);
                } else if (tType.equals("PhongVu")) {
                    target.hp = Math.max(0, target.hp - 30);
                    user.mana = Math.min(50, user.mana + 10);
                } else {
                    target.hp = Math.max(0, target.hp - 30);
                }
                user.specialUsed = true;
            } else if (uType.equals("ThuyTam")) {
                // ThuyTam special
                if (tType.equals("HoaLong")) {
                    // heal big (we simulate as heal 40, capped at 50 earlier)
                    user.hp = Math.min(100, user.hp + 40);
                } else if (tType.equals("PhongVu")) {
                    target.hp = Math.max(0, target.hp - 5);
                    // simulate skip: give user a small advantage by also regaining a bit more mana
                    user.mana = Math.min(50, user.mana + 5);
                    // NOTE: full skip-turn logic is complicated in this simulation; minimax treats it as advantage
                } else if (tType.equals("ThuyTam")) {
                    user.mana = 50;
                    user.hp = Math.max(0, user.hp - 10);
                } else {
                    target.hp = Math.max(0, target.hp - 25);
                }
                user.specialUsed = true;
            } else {
                // default special: damage 30
                target.hp = Math.max(0, target.hp - 30);
                user.specialUsed = true;
            }
        }

        // ensure bounds
        user.hp = Math.min(100, Math.max(0, user.hp));
        user.mana = Math.min(50, Math.max(0, user.mana));
        target.hp = Math.min(100, Math.max(0, target.hp));
        target.mana = Math.min(50, Math.max(0, target.mana));
    }

    // Simple heuristic for fallback choose
    private double simpleHeuristicForSkill(GameState enemyState, Skill s) {
        double score = 0;
        score += (s.getDamage() * 2);
        score += (s.getHeal() * 1.5);
        if (s.isSpecial()) score += 10;
        // prefer low mana cost to preserve mana
        score -= s.getManaCost() * 0.3;
        return score;
    }

    /** Lightweight simulated game state for minimax */
    private static class GameState {
        int hp;
        int mana;
        boolean specialUsed;
        String type; // "HoaLong", "PhongVu", "ThuyTam"
        List<Skill> skills;

        GameState(int hp, int mana, boolean specialUsed, String type, List<Skill> skills) {
            this.hp = hp;
            this.mana = mana;
            this.specialUsed = specialUsed;
            this.type = type;
            // store reference to original skills list for reading mana/damage/etc.
            this.skills = skills;
        }

        static GameState fromMage(Mage m) {
            String t = "Unknown";
            if (m instanceof HoaLong) t = "HoaLong";
            else if (m instanceof PhongVu) t = "PhongVu";
            else if (m instanceof ThuyTam) t = "ThuyTam";
            // copy basic stats
            GameState gs = new GameState(m.getHp(), m.getMana(), m.specialUsed, t, m.getSkills());
            return gs;
        }

        GameState copy() {
            // shallow copy of skills reference is enough (skills immutable during sim)
            return new GameState(hp, mana, specialUsed, type, skills);
        }

        boolean isDead() {
            return hp <= 0;
        }

        List<Skill> getSkills() {
            return skills;
        }

        void regainManaSim(int amount) {
            mana = Math.min(50, mana + amount);
        }
    }
}
