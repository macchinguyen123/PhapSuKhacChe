package model;

public class ThuyTamModel extends CharacterModel {
    public ThuyTamModel() { name = "💧 Thủy Tâm"; }

    @Override
    public void special(CharacterModel opponent) {
        if (usedSpecial || mana < 25) return;
        usedSpecial = true;
        changeMana(-25);
        changeHP(10);
    }
}
