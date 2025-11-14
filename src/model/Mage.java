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
    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMana() { return mana; }
    public List<Skill> getSkills() { return skills; }

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
        if (index == 4 && specialUsed) return false; // chiêu 5 chỉ dùng 1 lần
        return mana >= skill.getManaCost();
    }

    // ===== Tấn công (KHÔNG TurnManager) =====
    public void attack(Mage target, Skill skill) {
        if (!canUseSkill(skill)) {
            System.out.println("❌ " + name + " không thể dùng " + skill.getName() + "!");
            return;
        }

        useMana(skill.getManaCost());
        skill.execute(this, target); // KHÔNG còn TM

        // nếu là chiêu số 5 → đánh dấu đã dùng
        int index = skills.indexOf(skill);
        if (index == 4) specialUsed = true;

        limitStats();
    }

    // ===== Nhận sát thương =====
    public void takeDamage(int amount) {
        hp = Math.max(0, hp - amount);
        System.out.println("💢 " + name + " mất " + amount + " HP (còn lại: " + hp + ")");
    }

    // ===== Hồi HP =====
    public void heal(int amount) {
        hp = Math.min(100, hp + amount);
        System.out.println("💖 " + name + " hồi " + amount + " HP (hiện tại: " + hp + ")");
    }

    // ===== Mất mana =====
    public void loseMana(int amount) {
        mana = Math.max(0, mana - amount);
        System.out.println("💧 " + name + " mất " + amount + " mana (còn lại: " + mana + ")");
    }

    // ===== Dùng mana =====
    public void useMana(int amount) {
        mana = Math.max(0, mana - amount);
    }

    // ===== Hồi mana =====
    public void regainMana(int amount) {
        mana = Math.min(50, mana + amount);
        System.out.println("🔮 " + name + " hồi " + amount + " mana (hiện tại: " + mana + ")");
    }

    // ===== Kiểm tra sống =====
    public boolean isAlive() {
        return hp > 0;
    }

    // ===== Chiêu đặc biệt =====
    public abstract void useSpecial(Mage target);

    // ===== Hỗ trợ cast skill không TM =====
    public void attackWithSkill(Mage target, Skill skill) {
        attack(target, skill);
    }

    @Override
    public String toString() {
        return name + " (HP: " + hp + ", Mana: " + mana + ")";
    }
}
