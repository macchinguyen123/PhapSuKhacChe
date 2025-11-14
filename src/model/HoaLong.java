package model;

public class HoaLong extends Mage {

    public HoaLong() {
        super("🔥 Hỏa Long");

        // 1) Đánh thường — 0 mana — gây 10 sát thương, hồi +5 mana.
        skills.add(new Skill("Đánh Thường", 0, 10, 0, 5, false, "Tấn công cơ bản, hồi 5 mana"));

        // 2) Lửa Thánh — 10 mana — gây 12 sát thương
        skills.add(new Skill("Lửa Thánh", 10, 12, 0, 0, false, "Sát thương ổn định"));

        // 3) Hỏa Bạo — 18 mana — gây 30 sát thương và tự mất 10HP
        skills.add(new Skill("Hỏa Bạo", 18, 30, -10, 0, false, "Gây sát thương mạnh, tự mất 10 HP"));

        // 4) Hồi HP — 15 mana — hồi 25 HP
        skills.add(new Skill("Hồi Phục", 15, 0, 25, 0, false, "Hồi 25 HP"));

        // 5) Khắc chế đặc biệt — 20 mana
        skills.add(new Skill("Long Viêm Trảm", 20, 0, 0, 0, true,
                "Chiêu đặc biệt, hiệu quả khác nhau tùy đối thủ"));
    }

    @Override
    public void useSpecial(Mage target) {
        if (specialUsed) {
            System.out.println("Chiêu đặc biệt đã dùng rồi!");
            return;
        }
        specialUsed = true;

        System.out.println(name + " dùng chiêu đặc biệt Long Viêm Trảm!");

        if (target instanceof PhongVu) {
            // Khắc chế mạnh Gió
            target.takeDamage(38);
            System.out.println("Khắc chế Gió! Gây 38 sát thương.");
        } else if (target instanceof ThuyTam) {
            // Khắc chế yếu hơn Thuỷ
            target.takeDamage(20);
            heal(15);
            regainMana(5);
            System.out.println("Khắc chế Thuỷ! Gây 20 sát thương, hồi 15 HP và 5 mana.");
        } else if (target instanceof HoaLong) {
            // Gặp cùng hệ
            target.takeDamage(30);
            regainMana(10);
            System.out.println("Gặp cùng hệ Hỏa! Gây 30 sát thương và hồi 10 mana.");
        } else {
            // Đối thủ khác (mặc định)
            target.takeDamage(30);
            System.out.println("Gây 30 sát thương thường.");
        }
    }
}
