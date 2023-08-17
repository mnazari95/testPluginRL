package com.mnazari95;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PlayerDamages {

    private int hitCounter = 0;
    private int totalDamage = 0;
    private List<Integer> recentDamageList = new ArrayList<Integer>();
    private int npcFightingId = 104;

    public void addPlayerDamage(int damage) {
        totalDamage += damage;
    }

    public void incrementHitCounter() {
        hitCounter++;
    }
}
