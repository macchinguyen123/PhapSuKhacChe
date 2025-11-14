package model;

public class ThuyTam extends Mage {

    public ThuyTam() {
        super("💧 Thủy Tâm");

        // 1) Đánh thường — 0 mana — gây 10 sát thương, hồi +5 mana
        skills.add(new Skill("Đánh Thường", 0, 10, 0, 5, false, "Tấn công cơ bản, hồi 5 mana"));

        // 2) Vòi Nước — 10 mana — gây 12 sát thương, hồi 10 HP
        skills.add(new Skill("Vòi Nước", 10, 12, 10, 0, false, "Gây sát thương ổn định, hồi 10 HP"));

        // 3) Xoáy Nước — 18 mana — gây 22 sát thương, hồi 8 mana
        skills.add(new Skill("Xoáy Nước", 18, 22, 0, 8, false, "Tấn công mạnh, hồi 8 mana"));

        // 4) Hồi HP — 15 mana — hồi 20 HP
        skills.add(new Skill("Hồi Thủy", 15, 0, 20, 0, false, "Hồi 20 HP"));

        // 5) Chiêu đặc biệt — 20 mana
        skills.add(new Skill("Tuyệt Kỹ Thủy Tâm", 20, 0, 0, 0, true, "Chiêu đặc biệt, khắc chế từng hệ"));
    }

    @Override
    public void useSpecial(Mage target) {
        if (specialUsed) {
            System.out.println("Chiêu đặc biệt đã dùng rồi!");
            return;
        }
        specialUsed = true;

        System.out.println(name + " dùng chiêu đặc biệt 🌊 Tuyệt Kỹ Thủy Tâm!");

        if (target instanceof HoaLong) {
            // Khắc chế Hỏa → hồi gấp đôi sát thương lẽ ra nhận, tối đa 50 HP
            int healAmount = Math.min(50, 40);
            heal(healAmount);
            System.out.println("Khắc chế Hỏa Long! Hồi " + healAmount + " HP (tối đa 50).");
        } else if (target instanceof PhongVu) {
            // Gây 10 dmg
            target.takeDamage(10);

            // Hút tối đa 10 mana
            int manaSteal = Math.min(10, target.getMana());

            target.loseMana(manaSteal);
            this.regainMana(manaSteal);

            System.out.println(
                    "Khắc chế Phong Vũ! Gây 10 sát thương, hút "
                            + manaSteal + " mana và chuyển cho Thủy Tâm."
            );
        } else if (target instanceof ThuyTam) {
            // Gặp cùng hệ → hồi full mana, nhưng mất 10 HP
            regainMana(100);
            takeDamage(10);
            System.out.println("Gặp cùng hệ Thủy Tâm! Hồi full mana nhưng mất 10 HP.");
        }
    }
}
