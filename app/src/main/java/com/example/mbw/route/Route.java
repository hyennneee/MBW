package com.example.mbw.route;

import java.util.ArrayList;

public class Route {
    //승차 정류장, 하차 정류장 정보는 여기서 처리해야될듯
    private int totalTime, walkingTime, cost, group;
    private ArrayList<Item> itemList;

    public Route(int totalTime, int walkingTime, int cost, int group, ArrayList<Item> itemList) {
        this.totalTime = totalTime;
        this.walkingTime = walkingTime;
        this.cost = cost;
        this.group = group;
        this.itemList = itemList;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public int getWalkingTime() {
        return walkingTime;
    }

    public void setWalkingTime(int walkingTime) {
        this.walkingTime = walkingTime;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public ArrayList<Item> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<Item> itemList) {
        this.itemList = itemList;
    }
}