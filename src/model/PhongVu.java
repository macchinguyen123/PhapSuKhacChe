package model;

public class PhongVu extends Mage {

    public PhongVu() {
        super("💨 Phong Vũ");

        // 1) Đánh thường — 0 mana — gây 10 sát thương, hồi +5 mana
        skills.add(new Skill("Đánh Thường", 0, 10, 0, 5, false, "Tấn công cơ bản, hồi 5 mana"));

        // 2) Cơn Lốc — 10 mana — gây 14 sát thương, làm đối thủ mất 8 mana
        skills.add(new Skill("Cơn Lốc", 10, 14, 0, 0, false, "Gây sát thương 14 và làm đối thủ mất 8 mana") {
            public void use(Mage user, Mage target) {
                // DAMAGE
                target.takeDamage(14);

                // Trừ mana đối thủ + log
                int lost = Math.min(8, target.getMana());
                target.useMana(lost);
                System.out.println("💧 " + target.getName() + " mất " + lost + " mana!");

                // Trừ mana bản thân + log
                int manaCost = Math.min(10, user.getMana());
                user.useMana(manaCost);
                System.out.println("💧 " + user.getName() + " mất " + manaCost + " mana để dùng Cơn Lốc!");

                System.out.println(user.getName() + " dùng Cơn Lốc! Gây 14 sát thương và làm " + target.getName() + " mất 8 mana.");
            }
        });



        // 3) Bảo Linh — 18 mana — gây 24 sát thương và hồi 10 HP
        skills.add(new Skill("Bảo Linh", 18, 24, 10, 0, false, "Tấn công mạnh, hồi 10 HP"));

        // 4) Hồi HP — 15 mana — hồi 20 HP
        skills.add(new Skill("Hồi Phong", 15, 0, 20, 0, false, "Hồi 20 HP"));

        // 5) Chiêu đặc biệt — 20 mana
        skills.add(new Skill("Phong Thần Kích", 20, 0, 0, 0, true, "Chiêu đặc biệt, hiệu quả khác nhau tùy đối thủ"));

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

        // Trừ mana bản thân và log
        int manaCost = Math.min(20, mana);
        useMana(manaCost);
        System.out.println("💧 " + name + " mất " + manaCost + " mana để dùng chiêu đặc biệt.");

        System.out.println(name + " dùng chiêu đặc biệt Phong Thần Kích!");

        if (target instanceof HoaLong) {
            target.takeDamage(25);
            heal(10);
            System.out.println("Khắc chế Hỏa Long! Phản lại 25 sát thương và hồi 10 HP.");
        }
        else if (target instanceof ThuyTam) {
            target.takeDamage(15);
            regainMana(10);
            heal(20);
            System.out.println("Khắc chế Thuỷ Tâm! Phản 15 sát thương, hồi 20 HP và 10 mana.");
        }
        else if (target instanceof PhongVu) {
            target.takeDamage(30);
            regainMana(10);
            System.out.println("Gặp cùng hệ Phong! Gây 30 sát thương và hồi 10 mana.");
        }
    }


}