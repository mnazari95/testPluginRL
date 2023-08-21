package com.mnazari95;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

@Getter
@Setter
public class PlayerDamages {

    private int hitCounter = 0;
    private int totalDamage = 0;
    private List<Integer> recentDamageList = new Stack<>();
    private int npcFightingId = 104;
    private int gameTickCounter = 0;
    private boolean overheadPrayerState = false;
    private int overheadTickCounter = 0;

    private int overheadEventTriggeredCounter = 0;

    private int playerId = 0;

    public void resetPrayerTickCounter()
    {
        this.overheadTickCounter = 0;
    }

    public void incrementPrayerTickCounter()
    {
        this.overheadTickCounter++;
    }
    public void resetOverheadCounter()
    {
        this.overheadEventTriggeredCounter = 0;
    }

    public void incrementOverheadCounter()
    {
        this.overheadEventTriggeredCounter++;
    }
    public void addPlayerDamage(int damage) {
        totalDamage += damage;
    }

    public void incrementHitCounter() {
        hitCounter++;
    }

    public void resetGameTickCounter()
    {
        this.gameTickCounter = 0;
    }

    public void incrementGameTickCounter()
    {
        this.gameTickCounter++;
    }
}
