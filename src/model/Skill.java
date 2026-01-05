package model;

public class Skill {
    private String name;
    private int manaCost;
    private int damage;
    private int heal;
    private int manaGain;
    private String description;
    private boolean isSpecial;
    // ảnh hiệu ứng
    private String effectImg;
    private boolean targetSelf;


    public Skill(String name, int manaCost, int damage, int heal, int manaGain, boolean isSpecial, String description, boolean targetSelf) {
        this.name = name;
        this.manaCost = manaCost;
        this.damage = damage;
        this.heal = heal;
        this.manaGain = manaGain;
        this.isSpecial = isSpecial;
        this.description = description;
        this.effectImg = null; // default
        this.targetSelf = targetSelf;
    }

    public String getName() {
        return name;
    }

    public int getManaCost() {
        return manaCost;
    }

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

    // --- Phương thức use mở cho override anonymous ---
    public void use(Mage user, Mage target, Mage actualPlayer, Mage actualEnemy) {
        execute(user, target, actualPlayer, actualEnemy);
    }

    public void execute(Mage user, Mage target, Mage actualPlayer, Mage actualEnemy) {
        // Kiểm tra mana
        if (user.getMana() < manaCost) {
            System.out.println("⚠️ " + user.getName() + " không đủ mana dùng " + name + "!");
            return;
        }

        // Lưu giá trị trước khi dùng skill để log
        int userHpBefore = user.getHp();
        int targetHpBefore = target.getHp();
        int userManaBefore = user.getMana();
        int targetManaBefore = target.getMana();

        // --- Trừ mana của user ---
        user.useMana(manaCost);

        // --- DAMAGE ---
        if (damage > 0) target.takeDamage(damage);

        // --- HEAL ---
        if (heal != 0) {
            Mage healTarget = targetSelf ? user : target;
            if (heal > 0) healTarget.heal(heal);
            else healTarget.takeDamage(-heal);
        }

        // --- MANA GAIN cho user ---
        if (manaGain > 0) user.regainMana(manaGain);

        // --- Skill override anonymous (ví dụ Cơn Lốc) ---
        int targetManaLost = 0;
        if (user instanceof PhongVu && name.equals("Cơn Lốc")) {
            targetManaLost = Math.min(8, target.getMana());
            target.useMana(targetManaLost); // trực tiếp trừ mana đối thủ
        }

        // --- Các skill đặc biệt ---
        if (isSpecial) {
            if (user instanceof HoaLong) ((HoaLong) user).useSpecial(target);
            else if (user instanceof PhongVu) ((PhongVu) user).useSpecial(target);
            else if (user instanceof ThuyTam) ((ThuyTam) user).useSpecial(target);

            // 🔒 ĐÁNH DẤU ĐÃ DÙNG
            user.markSpecialUsed();
        }


        int userHpChange = user.getHp() - userHpBefore;
        int targetHpChange = targetHpBefore - target.getHp(); // Gây sát thương dương
        int userManaChange = user.getMana() - userManaBefore;
        int targetManaChange = targetManaBefore - target.getMana(); // Mana mất dương

        System.out.println("\n===== TỔNG KẾT LƯỢT =====");
        System.out.println(user.getName() + " dùng chiêu: " + name);
        System.out.println("Gây sát thương: " + targetHpChange); // luôn dương
        System.out.println("Hồi HP: " + Math.max(userHpChange,0));
        System.out.println("Tốn mana: " + (manaCost));
        System.out.println("Hồi mana: " + Math.max(userManaChange,0));
        if (targetManaChange != 0)
            System.out.println("Đối thủ mất mana: " + targetManaChange);

        // --- Trạng thái hiện tại chính xác ---
        System.out.println("\n--- Trạng thái hiện tại ---");
        System.out.println("Bạn    - HP: " + actualPlayer.getHp() + " | Mana: " + actualPlayer.getMana());
        System.out.println("Đối thủ - HP: " + actualEnemy.getHp() + " | Mana: " + actualEnemy.getMana());
        System.out.println("===========================\n");

        // --- Giới hạn max/min HP và mana ---
        user.limitStats();
        target.limitStats();
    }
    public void executeSample(Mage user, Mage target, Mage actualPlayer, Mage actualEnemy) {
        // Kiểm tra mana
        if (user.getMana() < manaCost) {
            return;
        }

        // Lưu giá trị trước khi dùng skill để log
        int userHpBefore = user.getHp();
        int targetHpBefore = target.getHp();
        int userManaBefore = user.getMana();
        int targetManaBefore = target.getMana();

        // --- Trừ mana của user ---
        user.useMana(manaCost);

        // --- DAMAGE ---
        if (damage > 0) target.takeDamage(damage);

        // --- HEAL ---
        if (heal != 0) {
            Mage healTarget = targetSelf ? user : target;
            if (heal > 0) healTarget.heal(heal);
            else healTarget.takeDamage(-heal);
        }

        // --- MANA GAIN cho user ---
        if (manaGain > 0) user.regainMana(manaGain);

        // --- Skill override anonymous (ví dụ Cơn Lốc) ---
        int targetManaLost = 0;
        if (user instanceof PhongVu && name.equals("Cơn Lốc")) {
            targetManaLost = Math.min(8, target.getMana());
            target.useMana(targetManaLost); // trực tiếp trừ mana đối thủ
        }

        // --- Các skill đặc biệt ---
        if (isSpecial) {
            if (user instanceof HoaLong) ((HoaLong) user).useSpecial(target);
            else if (user instanceof PhongVu) ((PhongVu) user).useSpecial(target);
            else if (user instanceof ThuyTam) ((ThuyTam) user).useSpecial(target);

            // 🔒 ĐÁNH DẤU ĐÃ DÙNG
            user.markSpecialUsed();
        }

        int userHpChange = user.getHp() - userHpBefore;
        int targetHpChange = targetHpBefore - target.getHp(); // Gây sát thương dương
        int userManaChange = user.getMana() - userManaBefore;
        int targetManaChange = targetManaBefore - target.getMana(); // Mana mất dương

//        System.out.println("\n===== TỔNG KẾT LƯỢT =====");
//        System.out.println(user.getName() + " dùng chiêu: " + name);
//        System.out.println("Gây sát thương: " + targetHpChange); // luôn dương
//        System.out.println("Hồi HP: " + Math.max(userHpChange,0));
//        System.out.println("Tốn mana: " + (manaCost));
//        System.out.println("Hồi mana: " + Math.max(userManaChange,0));
        if (targetManaChange != 0)
//            System.out.println("Đối thủ mất mana: " + targetManaChange);

        // --- Trạng thái hiện tại chính xác ---
//        System.out.println("\n--- Trạng thái hiện tại ---");
//        System.out.println("Bạn    - HP: " + actualPlayer.getHp() + " | Mana: " + actualPlayer.getMana());
//        System.out.println("Đối thủ - HP: " + actualEnemy.getHp() + " | Mana: " + actualEnemy.getMana());
//        System.out.println("===========================\n");

        // --- Giới hạn max/min HP và mana ---
        user.limitStats();
        target.limitStats();
    }
    public void setEffectImg(String path) {
        this.effectImg = path;
    }

    public String getEffectImg() {
        return effectImg;
    }


    @Override
    public String toString() {
        return name + " (Mana: " + manaCost + ", DMG: " + damage + ", Heal: " + heal + ")";
    }
    private SkillType type;


    public enum SkillType {
        DAMAGE, HEAL
    }
    public SkillType getType() {
        return type;
    }
    public boolean isTargetSelf() {
        return targetSelf;
    }

    public String getDescription() {
        return description;
    }
}
