package model;

public class HoaLong extends Mage {

    public HoaLong() {
        super("Hỏa Long");

        // 1) Đánh thường — 0 mana — gây 10 sát thương, hồi +5 mana.
        Skill danhThuong = new Skill("Đánh Thường", 0, 10, 0, 5, false, "Tấn công cơ bản, hồi 5 mana", false);
        skills.add(danhThuong);
        danhThuong.setEffectImg("src/img/hoaLong/DanhThuong.png");

        // 2) Lửa Thánh — 10 mana — gây 12 sát thương
        Skill luaThanh = new Skill("Lửa Thánh", 10, 12, 0, 0, false, "Sát thương ổn định", false);
        luaThanh.setEffectImg("src/img/hoaLong/luaThanh.png");
        skills.add(luaThanh);

        // 3) Hỏa Bạo — 18 mana — gây 30 sát thương và tự mất 10HP
        Skill hoaBao = new Skill("Hỏa Bạo", 18, 30, -10, 0, false, "Gây sát thương mạnh, tự mất 10 HP", false);
        hoaBao.setEffectImg("src/img/hoaLong/hoaBao.png");
        skills.add(hoaBao);

        // 4) Hồi HP — 15 mana — hồi 25 HP
        Skill hoiPhuc = new Skill("Hồi Phục", 15, 0, 25, 0, false, "Hồi 25 HP", true);
        skills.add(hoiPhuc);
        hoiPhuc.setEffectImg("src/img/hoaLong/HoiPhuc.png");

        // 5) Khắc chế đặc biệt — 20 mana
        Skill longViemTram = new Skill("Long Viêm Trảm", 20, 0, 0, 0, true,
                "Chiêu đặc biệt, hiệu quả khác nhau tùy đối thủ", false);
        skills.add(longViemTram);
        longViemTram.setEffectImg("src/img/hoaLong/LongViemTram.png");
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
        System.out.println("💧 " + name + " mất " + manaCost + " mana để dùng chiêu đặc biệt.");

        System.out.println(name + " dùng chiêu đặc biệt Long Viêm Trảm!");

        if (target instanceof PhongVu) {
            target.takeDamage(38);
            System.out.println("Khắc chế Gió! Gây 38 sát thương.");
        } else if (target instanceof ThuyTam) {
            target.takeDamage(20);
            heal(15);
            regainMana(5);
            System.out.println("Khắc chế Thuỷ! Gây 20 sát thương, hồi 15 HP và 5 mana.");
        } else if (target instanceof HoaLong) {
            target.takeDamage(30);
            regainMana(10);
            System.out.println("Gặp cùng hệ Hỏa! Gây 30 sát thương và hồi 10 mana.");
        }
    }

    @Override
    public Mage cloneMage() {
        HoaLong m = new HoaLong();

        m.hp = this.hp;
        m.mana = this.mana;
        m.specialUsed = this.specialUsed;
        // Skills dùng chung nên không cần clone sâu
        m.skills = this.skills;

        return m;
    }

    // ưu tiên sát thương
    @Override
    public double heuristic(Mage enemyState, Mage playerState) {
        double score = 0;

        // HP
        score += (enemyState.getHp() - playerState.getHp()) * 2.0;

        // Mana
        score += (enemyState.getMana() - playerState.getMana()) * 0.5;

        // Chiêu đặc biệt
        if (!enemyState.specialUsed)
            score += 6;
        if (!playerState.specialUsed)
            score -= 3;

        // PHẠT NẶNG KHI HP THẤP
        if (enemyState.getHp() < 20) {
            score -= 25; // Nguy hiểm!
        } else if (enemyState.getHp() < 35) {
            score -= 12; // Cảnh báo
        }

        // BONUS KHI PLAYER HP THẤP
        if (playerState.getHp() < 25) {
            score += 20; // Cơ hội giết
        }

        // Số skill có thể dùng (linh hoạt hơn)
        int enemySkillCount = 0;
        int playerSkillCount = 0;

        for (Skill skill : enemyState.getSkills()) {
            if (enemyState.canUseSkill(skill))
                enemySkillCount++;
        }

        for (Skill skill : playerState.getSkills()) {
            if (playerState.canUseSkill(skill))
                playerSkillCount++;
        }

        score += (enemySkillCount - playerSkillCount) * 2; // Ưu thế lựa chọn

        return score;
    }
}