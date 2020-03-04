package com.example.mbw.AddPath;

import java.util.ArrayList;

public class AddItem {

    private String num; // 버스 번호, 지하철 몇호선
    private String numId;   // 노선 번호

    private String start; // 시작 버스 정류장, 지하철 역
    private String startId; // 시작 버스 정류장, 지하철 역 아이디


    private String busType; // subway면 null

    private String end; // 시작 버스 정류장, 지하철 역
    private String endId; // 시작 버스 정류장, 지하철 역 아이디


    private Double startX, startY, endX, endY;
    private int passingStation;
    private ArrayList<String> passingStationList;



    // 버스 경로 추가할 때
    public AddItem(String busType, String num, String start, String end, Double startX, Double startY, Double endX, Double endY, String startId, int passingStation, ArrayList passingStationList) {
        this.busType = busType;
        this.num = num;
        this.start = start;
        this.startId = startId;
        this.end = end;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.passingStation = passingStation;
        this.passingStationList = passingStationList;
    }


    // 지하철 경로
    public AddItem(String busType, String num, String start, String startId, String end, String endId) {
        this.busType = busType;
        this.num = num;
        //this.numId = numId;
        this.start = start;
        this.startId = startId;
        this.end = end;
        this.endId = endId;
    }

// 버스 경로 추가할 때
    public AddItem(String busType, String num, String start, String end) {
        this.busType = busType;
        this.num = num;
        //this.numId = numId;
        this.start = start;
        this.end = end;
    }


    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getNumId() {
        return numId;
    }

    public void setNumId(String numId) {
        this.numId = numId;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getStartId() {
        return startId;
    }

    public void setStartId(String startId) {
        this.startId = startId;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getEndId() {
        return endId;
    }

    public void setEndId(String endId) {
        this.endId = endId;
    }

    public String getBusType() {
        return busType;
    }

    public void setBusType(String busType) {
        this.busType = busType;
    }

    public Double getStartX() {
        return startX;
    }

    public void setStartX(Double startX) {
        this.startX = startX;
    }

    public Double getStartY() {
        return startY;
    }

    public void setStartY(Double startY) {
        this.startY = startY;
    }

    public Double getEndX() {
        return endX;
    }

    public void setEndX(Double endX) {
        this.endX = endX;
    }

    public Double getEndY() {
        return endY;
    }

    public void setEndY(Double endY) {
        this.endY = endY;
    }

    public int getPassingStation() {
        return passingStation;
    }

    public void setPassingStation(int passingStation) {
        this.passingStation = passingStation;
    }

    public ArrayList<String> getPassingStationList() {
        return passingStationList;
    }

    public void setPassingStationList(ArrayList<String> passingStationList) {
        this.passingStationList = passingStationList;
    }
}



