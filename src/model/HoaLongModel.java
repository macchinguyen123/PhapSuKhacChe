package model;

public class HoaLongModel extends CharacterModel {
    public HoaLongModel() { name = "🔥 Hỏa Long"; }

    @Override
    public void special(CharacterModel opponent) {
        if (usedSpecial || mana < 30) return;
        usedSpecial = true;
        changeMana(-30);
        opponent.changeHP(-40);
        changeHP(-10);
    }
}
