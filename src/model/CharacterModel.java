package model;

public abstract class CharacterModel {
    protected String name;
    protected int hp = 100, mana = 50;
    protected boolean usedSpecial = false;

    public String getName() {
        return name;
    }

    public int getHP() {
        return hp;
    }

    public int getMana() {
        return mana;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public void changeHP(int amount) {
        hp = Math.max(0, Math.min(100, hp + amount));
    }

    public void changeMana(int amount) {
        mana = Math.max(0, Math.min(50, mana + amount));
    }

    public abstract void special(CharacterModel opponent);

    public boolean isUsedSpecial() {
        return usedSpecial;
    }

    public void setUsedSpecial(boolean usedSpecial) {
        this.usedSpecial = usedSpecial;
    }

}
