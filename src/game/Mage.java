package game;

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

    // ===== Hành động tấn công =====
    public void attack(Mage target, Skill skill, TurnManager tm) {
        if (mana < skill.getManaCost()) {
            System.out.println("❌ " + name + " không đủ mana để dùng " + skill.getName() + "!");
            return;
        }
        skill.execute(this, target, tm);
    }

    // ===== Nhận sát thương =====
    public void takeDamage(int amount) {
        hp = Math.max(0, hp - amount);
        System.out.println("💢 " + name + " mất " + amount + " HP (còn lại: " + hp + ")");
    }

    // ===== Hồi phục HP =====
    public void heal(int amount) {
        hp += amount;
        if (hp > 100) hp = 100;
        System.out.println("💖 " + name + " hồi " + amount + " HP (hiện tại: " + hp + ")");
    }

    // ===== Mất mana =====
    public void loseMana(int amount) {
        mana = Math.max(0, mana - amount);
        System.out.println("💧 " + name + " mất " + amount + " mana (còn lại: " + mana + ")");
    }

    // ===== Sử dụng mana =====
    public void useMana(int amount) {
        mana = Math.max(0, mana - amount);
    }

    // ===== Hồi mana =====
    public void regainMana(int amount) {
        mana += amount;
        if (mana > 50) mana = 50;
        System.out.println("🔮 " + name + " hồi " + amount + " mana (hiện tại: " + mana + ")");
    }

    // ===== Kiểm tra còn sống =====
    public boolean isAlive() {
        return hp > 0;
    }

    // ===== Chiêu đặc biệt (ghi đè ở từng subclass) =====
    public abstract void useSpecial(Mage target);

    // ===== Dành cho kỹ năng cần hiệu ứng riêng (vd: Cơn Lốc) =====
    public void attackWithSkill(Mage target, Skill skill) {
        // Mặc định: chỉ thực hiện kỹ năng bình thường
        TurnManager dummyTM = null;
        attack(target, skill, dummyTM);
    }

    @Override
    public String toString() {
        return name + " (HP: " + hp + ", Mana: " + mana + ")";
    }
    public void limitStats() {
        if (hp > 100) hp = 100;
        if (mana > 50) mana = 50;
        if (hp < 0) hp = 0;
        if (mana < 0) mana = 0;
    }

}
