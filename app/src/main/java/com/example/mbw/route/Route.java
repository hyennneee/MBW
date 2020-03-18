package com.example.mbw.route;

import java.util.ArrayList;

public class Route {
    private int id, totalTime, walkingTime, cost, group, myPathIdx = -1, likedNum;
    private ArrayList<Item> itemList;
    boolean liked = false;

    public Route(int id, int totalTime, int walkingTime, int cost, int group, ArrayList<Item> itemList) {
        this.id = id;
        this.totalTime = totalTime;
        this.walkingTime = walkingTime;
        this.cost = cost;
        this.group = group;
        this.itemList = itemList;
    }

    public int getId() {
        return id;
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

    public void setMyPathIdx(int myPathIdx) {
        this.myPathIdx = myPathIdx;
    }

    public int getMyPathIdx() {
        return myPathIdx;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public void setLikedNum(int likedNum) {
        this.likedNum = likedNum;
    }

    public int getLikedNum() {
        return likedNum;
    }
}