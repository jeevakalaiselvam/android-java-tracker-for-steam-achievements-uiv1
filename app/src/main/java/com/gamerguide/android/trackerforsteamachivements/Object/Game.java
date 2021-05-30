package com.gamerguide.android.trackerforsteamachivements.Object;

import java.io.Serializable;
import java.util.ArrayList;

public class Game implements Serializable {
    Game() {

    }

    private String id;
    private String name;
    private String playtime;

    public long getSearchTime() {

            return searchTime;
    }

    public void setSearchTime(long searchTime) {
        this.searchTime = searchTime;
    }

    private long searchTime = 0;

    public float getCompletion() {
        return completion;
    }

    public void setCompletion(float completion) {
        this.completion = completion;
    }

    private float completion;

    public ArrayList<Achievement> getAchievements() {
        return achievements;
    }

    public void addAchievement(Achievement achievement) {
        achievements.add(achievement);
    }

    public void setAchievements(ArrayList<Achievement> achievements) {
        this.achievements = achievements;
    }

    private ArrayList<Achievement> achievements = new ArrayList<>();

    public int getTotalAchievements() {
        return totalAchievements;
    }

    public void setTotalAchievements(int totalAchievements) {
        this.totalAchievements = totalAchievements;
    }

    private int totalAchievements;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaytime() {
        return playtime;
    }

    public void setPlaytime(String playtime) {
        this.playtime = playtime;
    }


    public Game(String id, String playtime, String name, long searchTime) {
        this.id = id;
        this.name = name;
        this.playtime = String.valueOf(Integer.parseInt(playtime) / 60);
        this.searchTime = searchTime;
    }


}
