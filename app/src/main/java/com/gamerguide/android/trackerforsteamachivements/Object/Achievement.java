package com.gamerguide.android.trackerforsteamachivements.Object;

import java.io.Serializable;

public class Achievement implements Serializable {
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

    public String getDesc() {

        if(desc == null){
            return "Hidden Achievement";
        }

        return desc;
    }

    public void setDesc(String desc) {

        this.desc = desc;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIconLocked() {
        return iconLocked;
    }

    public void setIconLocked(String iconLocked) {
        this.iconLocked = iconLocked;
    }

    public String getHidden() {
        return hidden;
    }

    public void setHidden(String hidden) {
        this.hidden = hidden;
    }

    private String id;
    private String name;
    private String desc;
    private String icon;

    public String getTag() {
        if(tag == null){
            return "story";
        }
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    private String tag;

    public int getFav() {
        return fav;
    }

    public void setFav(int fav) {
        this.fav = fav;
    }

    private int fav = 0;

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    private String percentage;
    private String iconLocked;
    private String hidden;
    private String unlockTime;

    public String getUnlockTimeFormat() {
        return unlockTimeFormat;
    }

    public void setUnlockTimeFormat(String unlockTimeFormat) {
        this.unlockTimeFormat = unlockTimeFormat;
    }

    private String unlockTimeFormat;
    private String unlocked;

    public String getUnlockTime() {
        return unlockTime;
    }

    public void setUnlockTime(String unlockTime) {
        this.unlockTime = unlockTime;
    }

    public String getUnlocked() {

        if(unlocked == null){
            return "0";
        }
        return unlocked;
    }

    public void setUnlocked(String unlocked) {
        this.unlocked = unlocked;
    }

}
