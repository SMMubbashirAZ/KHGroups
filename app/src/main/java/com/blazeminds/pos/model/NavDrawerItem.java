package com.blazeminds.pos.model;

public class NavDrawerItem {
    
    private String title;
    private int icon;
    private String count = "0";
    // boolean to set visiblity of the counter
    private boolean isCounterVisible = false;
    private int thingToDo;
    
    public NavDrawerItem() {
    }
    
    public NavDrawerItem(String title, int icon, int thingToDo) {
        this.title = title;
        this.icon = icon;
        this.thingToDo = thingToDo;
    }
    
    public NavDrawerItem(String title, int icon, boolean isCounterVisible, String count) {
        this.title = title;
        this.icon = icon;
        this.isCounterVisible = isCounterVisible;
        this.count = count;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public int getIcon() {
        return this.icon;
    }
    
    public void setIcon(int icon) {
        this.icon = icon;
    }
    
    public String getCount() {
        return this.count;
    }
    
    public void setCount(String count) {
        this.count = count;
    }
    
    public boolean getCounterVisibility() {
        return this.isCounterVisible;
    }
    
    public void setCounterVisibility(boolean isCounterVisible) {
        this.isCounterVisible = isCounterVisible;
    }
    
    public int getThingToDo() {
        return thingToDo;
    }
    
    public void setThingToDo(int thingToDo) {
        this.thingToDo = thingToDo;
    }
}
