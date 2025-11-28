package model;

import java.util.List;
import java.util.Random;

public class Enemy {
    public Mage mage;
    private Random random = new Random();

    public Enemy(Mage mage) {
        this.mage = mage;
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

    public double heuristic(Mage player) {
        Mage enemy = this.mage;
        double score = 0;

        // HP
        score += (enemy.getHp() - player.getHp()) * 2.0;

        // Mana
        score += (enemy.getMana() - player.getMana()) * 0.5;

        // Special skill availability
        if (!enemy.specialUsed) score += 5;
        if (!player.specialUsed) score -= 3;

        return score;
    }

}
