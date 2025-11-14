package model;

import java.util.List;
import java.util.Scanner;

public class Player {
    public Mage mage;

    public Skill chooseSkill() {
        List<Skill> list = mage.getSkills();
        System.out.println("\n🪄 Chọn chiêu:");
        for (int i = 0; i < list.size(); i++)
            System.out.println((i + 1) + ". " + list.get(i));
        Scanner sc = new Scanner(System.in);
        int choice = sc.nextInt();
        return list.get(choice - 1);
    }
}
