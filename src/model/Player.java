package model;

public class Player {
    public Mage mage;

    public Player(Mage mage) {
        this.mage = mage;
    }

    public void useSkill(Skill skill, Enemy enemy) {
        // Truyền playerMage = this.mage, enemyMage = enemy.mage
        skill.execute(mage, enemy.mage, mage, enemy.mage);
    }
}
