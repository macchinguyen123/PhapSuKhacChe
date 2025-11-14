package model;

public class Skill {
    private String name;
    private int manaCost;
    private int damage;
    private int heal;
    private int manaGain;
    private String description;
    private boolean isSpecial;

    public Skill(String name, int manaCost, int damage, int heal, int manaGain, boolean isSpecial, String description) {
        this.name = name;
        this.manaCost = manaCost;
        this.damage = damage;
        this.heal = heal;
        this.manaGain = manaGain;
        this.isSpecial = isSpecial;
        this.description = description;
    }

    public String getName() { return name; }

    public void execute(Mage user, Mage target) {
        if (user.getMana() < manaCost) {
            System.out.println("⚠️ Không đủ mana để dùng chiêu này!");
            return;
        }
        if (damage > 0) target.takeDamage(damage);

        // ⚡️ Cho phép heal âm để trừ HP người dùng
        if (heal != 0) {
            if (heal > 0) user.heal(heal);
            else user.takeDamage(-heal); // nếu heal âm → trừ HP
        }

        if (manaGain > 0) user.regainMana(manaGain);


        System.out.println(user.getName() + " dùng chiêu " + name + " lên " + target.getName());

        if (isSpecial) {
            if (user instanceof HoaLong) ((HoaLong) user).useSpecial(target);
            else if (user instanceof PhongVu) ((PhongVu) user).useSpecial(target);
            else if (user instanceof ThuyTam) ((ThuyTam) user).useSpecial(target);
        }
    }

    @Override
    public String toString() {
        return name + " (Mana: " + manaCost + ", DMG: " + damage + ", Heal: " + heal + ")";
    }

    public int getManaCost() {
        return manaCost;
    }
    // Trong class Skill (thêm các getter)
    public int getDamage() {
        return damage;
    }

    public int getHeal() {
        return heal;
    }

    public int getManaGain() {
        return manaGain;
    }

    public boolean isSpecial() {
        return isSpecial;
    }

}
