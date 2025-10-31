package model;

public class PhongVuModel extends CharacterModel {
    public PhongVuModel() { name = "🌪️ Phong Vũ"; }

    @Override
    public void special(CharacterModel opponent) {
        if (usedSpecial || mana < 20) return;
        usedSpecial = true;
        changeMana(-20);
        opponent.changeHP(-15);
        if (opponent.getMana() >= 10)
            opponent.changeMana(-10);
        else {
            opponent.changeMana(-opponent.getMana());
            opponent.changeHP(-5);
        }
    }
}
