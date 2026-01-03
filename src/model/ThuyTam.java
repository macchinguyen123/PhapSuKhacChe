package model;

public class ThuyTam extends Mage {

    public ThuyTam() {
        super("Thủy Tâm");

        // 1) Đánh thường — 0 mana — gây 10 sát thương, hồi +5 mana
        Skill danhThuong = new Skill("Đánh Thường", 0, 10, 0, 5, false, "Tấn công cơ bản, hồi 5 mana", false);
        skills.add(danhThuong);
        danhThuong.setEffectImg("src/img/thuyTam/nuoc.png");

        // 2) Vòi Nước — 10 mana — gây 12 sát thương, hồi 10 HP
        Skill voiNuoc =new Skill("Vòi Nước", 10, 12, 10, 0, false, "Gây sát thương ổn định, hồi 10 HP", false);
        skills.add(voiNuoc);
        voiNuoc.setEffectImg("src/img/thuyTam/VoiNuoc.png");

        // 3) Xoáy Nước — 18 mana — gây 22 sát thương, hồi 8 mana
        Skill xoayNuoc = new Skill("Xoáy Nước", 18, 22, 0, 8, false, "Tấn công mạnh, hồi 8 mana",false);
        xoayNuoc.setEffectImg("src/img/thuyTam/XoayNuoc.png");
        skills.add(xoayNuoc);

        // 4) Hồi HP — 15 mana — hồi 20 HP
        Skill hoiHP = new Skill("Hồi Thủy", 15, 0, 20, 0, false, "Hồi 20 HP",true);
        skills.add(hoiHP);
        hoiHP.setEffectImg("src/img/thuyTam/hoiMau.png");

        // 5) Chiêu đặc biệt — 20 mana
        Skill tuyetKyThuyTam =new Skill("Tuyệt Kỹ Thủy Tâm", 20, 0, 0, 0, true, "Chiêu đặc biệt, khắc chế từng hệ",false);
        skills.add(tuyetKyThuyTam);
        tuyetKyThuyTam.setEffectImg("src/img/thuyTam/TuyetKyThuyTam.png");
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
    public void useSpecialSample(Mage target) {
        if (specialUsed) {
            return;
        }

        if (mana < 20) {
            return;
        }

        specialUsed = true;

        // Trừ mana bản thân đúng chuẩn
        int manaCost = Math.min(20, mana);
        useMana(manaCost);


        if (target instanceof HoaLong) {
            int healAmount = Math.min(50, 20 * 2); // ví dụ hồi gấp đôi sát thương
            heal(healAmount);

        } else if (target instanceof PhongVu) {
            target.useMana(Math.min(10, target.getMana())); // trừ mana đối thủ chuẩn
            regainMana(Math.min(10, target.getMana()));     // hồi mana cho bản thân
            target.takeDamage(10);

        } else if (target instanceof ThuyTam) {
            regainMana(50);  // hồi mana cho bản thân
            takeDamage(10);

        }
    }

    @Override
    public Mage cloneMage() {
        ThuyTam m = new ThuyTam();

        m.hp = this.hp;
        m.mana = this.mana;
        m.specialUsed = this.specialUsed;

        // Skills dùng chung nên không cần clone sâu
        m.skills = this.skills;

        return m;
    }

    @Override
    public double heuristic(Mage enemyState, Mage playerState) {
        double score = 0;

        // 1. HP quan trọng nhất
        score += (enemyState.getHp() - playerState.getHp()) * 3.0;

        // 2. Mana để dùng skill
        score += (enemyState.getMana() - playerState.getMana()) * 1.0;

        // 3. Chiêu đặc biệt chưa dùng => tiềm năng cao
        if (!enemyState.specialUsed) score += 6;
        if (!playerState.specialUsed) score -= 4; // người chơi chưa dùng special => rủi ro

        // 4. Đánh giá tiềm năng các skill còn lại
        for (Skill skill : enemyState.getSkills()) {
            if (!enemyState.canUseSkill(skill)) continue;

            // Skill hồi máu
            if (skill.getHeal() > 0) {
                int missingHp = 100 - enemyState.getHp();
                double healValue = Math.min(skill.getHeal(), missingHp);
                score += healValue * 0.5; // càng hồi nhiều HP càng tốt
            }

            // Skill hồi mana
            if (skill.getManaGain() > 0) {
                int missingMana = 50 - enemyState.getMana();
                double manaValue = Math.min(skill.getManaGain(), missingMana);
                score += manaValue * 0.3; // hồi mana cũng có giá trị
            }

            // Skill sát thương
            score += skill.getDamage() * 0.2; // damage ít quan trọng hơn HP/Mana
        }

        return score;
    }


}