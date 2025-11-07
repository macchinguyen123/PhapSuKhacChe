package game;

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

    public void execute(Mage user, Mage target, TurnManager turnManager) {
        if (user.getMana() < manaCost) {
            System.out.println("⚠️ Không đủ mana để dùng chiêu này!");
            return;
        }

        user.useMana(manaCost);

        if (damage > 0) target.takeDamage(damage);
        if (heal > 0) user.heal(heal);
        if (manaGain > 0) user.regainMana(manaGain);

        System.out.println(user.getName() + " dùng chiêu " + name + " lên " + target.getName());

        if (isSpecial) {
            if (user instanceof HoaLong) ((HoaLong) user).useSpecial(target);
            else if (user instanceof PhongVu) ((PhongVu) user).useSpecial(target);
            else if (user instanceof ThuyTam) ((ThuyTam) user).useSpecial(target, turnManager);
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
