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
}
