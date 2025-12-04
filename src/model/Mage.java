package model;

import java.util.ArrayList;
import java.util.List;

public abstract class Mage {
    protected String name;
    protected int hp;
    protected int mana;
    protected List<Skill> skills;
    protected boolean specialUsed;

    public Mage(String name) {
        this.name = name;
        this.hp = 100;
        this.mana = 50;
        this.skills = new ArrayList<>();
        this.specialUsed = false;
    }

    // ===== Getter =====
    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    public int getMana() {
        return mana;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    // ===== Giới hạn chỉ số =====
    public void limitStats() {
        if (hp > 100) hp = 100;
        if (mana > 50) mana = 50;
        if (hp < 0) hp = 0;
        if (mana < 0) mana = 0;
    }

    // ===== Kiểm tra chiêu có thể dùng không =====
    public boolean canUseSkill(Skill skill) {
        int index = skills.indexOf(skill);
        if (index == 4 && specialUsed) return false; // chiêu đặc biệt chỉ dùng 1 lần
        return mana >= skill.getManaCost();
    }

    // ===== Dùng skill chuẩn =====
    // Sử dụng cùng Skill.execute(user, target, playerMage, enemyMage)
    public void useSkill(Skill skill, Mage target) {
        if (!canUseSkill(skill)) {
            System.out.println("❌ " + name + " không thể dùng " + skill.getName() + "!");
            return;
        }

        // Chiêu đặc biệt đánh dấu
        int index = skills.indexOf(skill);
        if (index == 4) specialUsed = true;

        skill.execute(this, target, this, target); // dùng 4 tham số để log chính xác

        limitStats();
        target.limitStats();
    }
    public void useSkillSample(Skill skill, Mage target) {
        if (!canUseSkill(skill)) {
//            System.out.println("❌ " + name + " không thể dùng " + skill.getName() + "!");
            return;
        }

        // Chiêu đặc biệt đánh dấu
        int index = skills.indexOf(skill);
        if (index == 4) specialUsed = true;

        skill.executeSample(this, target, this, target); // dùng 4 tham số để log chính xác

        limitStats();
        target.limitStats();
    }

    // ===== Nhận sát thương =====
    public void takeDamage(int amount) {
        hp = Math.max(0, hp - amount);
    }

    // ===== Hồi HP =====
    public void heal(int amount) {
        hp = Math.min(100, hp + amount);
    }

    // ===== Dùng mana =====
    public void useMana(int amount) {
        mana = Math.max(0, mana - amount);
    }

    // ===== Hồi mana =====
    public void regainMana(int amount) {
        mana = Math.min(50, mana + amount);
    }

    // ===== Kiểm tra sống =====
    public boolean isAlive() {
        return hp > 0;
    }

    // ===== Chiêu đặc biệt =====
    public abstract void useSpecial(Mage target);

    public abstract Mage cloneMage();

    @Override
    public String toString() {
        return name + " (HP: " + hp + ", Mana: " + mana + ")";
    }
}
