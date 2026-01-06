package model;

public class PhongVu extends Mage {

    public PhongVu() {
        super("Phong Vũ");

        // 1) Đánh thường — 0 mana — gây 10 sát thương, hồi +5 mana
        Skill danhThuong = new Skill("Đánh Thường", 0, 10, 0, 5, false, "Tấn công cơ bản, hồi 5 mana", false);
        danhThuong.setEffectImg("src/img/phongVu/danhThuong.png");
        skills.add(danhThuong);

        //2)
        Skill conLoc = new Skill("      Cơn Lốc     ", 10, 18, 0, 0, false,
                "Gây sát thương 14 và làm đối thủ mất 8 mana", false);
        conLoc.setEffectImg("src/img/phongVu/gio.png");
        skills.add(conLoc);

        // 3) Bảo Linh — 18 mana — gây 24 sát thương và hồi 10 HP
        Skill baoLinh = new Skill("Bảo Linh", 18, 24, 10, 0, false, "Tấn công mạnh, hồi 10 HP", false);
        baoLinh.setEffectImg("src/img/phongVu/baolinh.png");
        skills.add(baoLinh);

        // 4) Hồi HP — 15 mana — hồi 20 HP
        Skill hoiHP = new Skill("Hồi Phong", 15, 0, 20, 0, false, "Hồi 20 HP", true);
        hoiHP.setEffectImg("src/img/phongVu/HoiHP.png");
        skills.add(hoiHP);

        // 5) Chiêu đặc biệt — 20 mana
        Skill chieuDacBiet = new Skill("Phong Thần Kích", 20, 0, 0, 0, true,
                "Chiêu đặc biệt, hiệu quả khác nhau tùy đối thủ", false);
        chieuDacBiet.setEffectImg("src/img/phongVu/chieuDacBiet.png");
        skills.add(chieuDacBiet);
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
        System.out.println(name + " mất " + manaCost + " mana để dùng chiêu đặc biệt.");

        System.out.println(name + " dùng chiêu đặc biệt Phong Thần Kích!");

        if (target instanceof HoaLong) {
            target.takeDamage(25);
            heal(15);
            System.out.println("Khắc chế Hỏa Long! Phản lại 25 sát thương và hồi 10 HP.");
        } else if (target instanceof ThuyTam) {
            target.takeDamage(15);
            regainMana(10);
            heal(20);
            System.out.println("Khắc chế Thuỷ Tâm! Phản 15 sát thương, hồi 20 HP và 10 mana.");
        } else if (target instanceof PhongVu) {
            target.takeDamage(30);
            regainMana(10);
            System.out.println("Gặp cùng hệ Phong! Gây 30 sát thương và hồi 10 mana.");
        }
    }

    public void useSpecialSample(Mage target) {
        if (specialUsed) {

            return;
        }

        if (mana < 20) {

            return;
        }

        specialUsed = true;

        // Trừ mana bản thân và log
        int manaCost = Math.min(20, mana);
        useMana(manaCost);

        if (target instanceof HoaLong) {
            target.takeDamage(25);
            heal(10);

        } else if (target instanceof ThuyTam) {
            target.takeDamage(15);
            regainMana(10);
            heal(20);

        } else if (target instanceof PhongVu) {
            target.takeDamage(30);
            regainMana(10);

        }
    }

    @Override
    public Mage cloneMage() {
        PhongVu m = new PhongVu();

        m.hp = this.hp;
        m.mana = this.mana;
        m.specialUsed = this.specialUsed;

        // Skills dùng chung nên không cần clone sâu
        m.skills = this.skills;

        return m;
    }

    // ưu tiên gây khó khăn cho đối thủ (mana/chiêu)
    @Override
    public double heuristic(Mage enemyState, Mage playerState) {
        double score = 0;

        // HP
        score += (enemyState.getHp() - playerState.getHp()) * 2.0;

        // Mana
        score += (enemyState.getMana() - playerState.getMana()) * 0.6;


        // 3. BONUS KHI PLAYER HP THẤP - cơ hội giết địch
        if (playerState.getHp() < 25) {
            score += 20; // Tình huống có lợi, sắp thắng
        }

        // 4. Chiêu đặc biệt chưa dùng => tiềm năng cao
        if (!enemyState.specialUsed) score += 6;
        if (!playerState.specialUsed) score -= 4;

        // PHẠT NẶNG KHI HP THẤP
        if (enemyState.getHp() < 20) {
            score -= 25; // Nguy hiểm!
        } else if (enemyState.getHp() < 35) {
            score -= 12; // Cảnh báo
        }

        // 5. Đếm số skill có thể dùng (ưu thế linh hoạt)
        int enemySkillCount = 0;
        int playerSkillCount = 0;

        for (Skill skill : enemyState.getSkills()) {
            if (enemyState.canUseSkill(skill)) {
                enemySkillCount++;
            }
        }

        for (Skill skill : playerState.getSkills()) {
            if (playerState.canUseSkill(skill)) {
                playerSkillCount++;
            }
        }

        // Ưu thế về số lượng lựa chọn
        score += (enemySkillCount - playerSkillCount) * 2;

        return score;
    }

}