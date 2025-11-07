package game;

import java.util.List;
import java.util.Random;

public class Enemy {
    public Mage mage;

    public Skill chooseSkill() {
        List<Skill> list = mage.getSkills();
        return list.get(new Random().nextInt(list.size()));
    }
}
