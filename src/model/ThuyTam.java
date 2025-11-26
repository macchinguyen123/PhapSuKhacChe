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

        if (mana < 20) {
            System.out.println("Không đủ mana để dùng chiêu đặc biệt!");
            return;
        }

        specialUsed = true;

        // Trừ mana bản thân đúng chuẩn
        int manaCost = Math.min(20, mana);
        useMana(manaCost);
        System.out.println("💧 " + name + " mất " + manaCost + " mana để dùng chiêu đặc biệt.");

        System.out.println(name + " dùng chiêu đặc biệt 🌊 Tuyệt Kỹ Thủy Tâm!");

        if (target instanceof HoaLong) {
            int healAmount = Math.min(50, 20 * 2); // ví dụ hồi gấp đôi sát thương
            heal(healAmount);
            System.out.println("Khắc chế Hỏa Long! Hồi " + healAmount + " HP (tối đa 50).");
        } else if (target instanceof PhongVu) {
            target.useMana(Math.min(10, target.getMana())); // trừ mana đối thủ chuẩn
            regainMana(Math.min(10, target.getMana()));     // hồi mana cho bản thân
            target.takeDamage(10);
            System.out.println("Khắc chế Phong Vũ! Gây 10 sát thương, hút 10 mana và chuyển cho Thủy Tâm.");
        } else if (target instanceof ThuyTam) {
            regainMana(50);  // hồi mana cho bản thân
            takeDamage(10);
            System.out.println("Gặp cùng hệ Thủy Tâm! Hồi full mana nhưng mất 10 HP.");
        }
    }

}