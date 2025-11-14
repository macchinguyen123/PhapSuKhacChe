package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Enemy {
    public Mage mage;
    private final Random rnd = new Random();

    public Skill chooseSkill() {
        List<Skill> skills = mage.getSkills();

        // Lọc chiêu có thể dùng
        List<Skill> usable = new ArrayList<>();
        for (Skill s : skills) {
            if (mage.getMana() >= s.getManaCost()) usable.add(s);
        }

        // Không có thì return null
        if (usable.isEmpty()) return null;

        // Chọn random 1 skill
        return usable.get(rnd.nextInt(usable.size()));
    }

    // Nếu có đối thủ thì cũng chỉ random
    public Skill chooseSkill(Mage opponent) {
        List<Skill> skills = mage.getSkills();

        List<Skill> usable = new ArrayList<>();
        for (Skill s : skills) {
            if (mage.getMana() >= s.getManaCost()) usable.add(s);
        }

        if (usable.isEmpty()) return null;

        return usable.get(rnd.nextInt(usable.size()));
    }
}
