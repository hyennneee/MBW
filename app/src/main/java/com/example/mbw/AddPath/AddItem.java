package com.example.mbw.AddPath;

public class AddItem {

    private String num; // 버스 번호, 지하철 몇호선
    private String numId;   // 노선 번호

    private String start; // 시작 버스 정류장, 지하철 역
    private String startId; // 시작 버스 정류장, 지하철 역 아이디


    private String busType; // subway면 null

    private String end; // 시작 버스 정류장, 지하철 역
    private String endId; // 시작 버스 정류장, 지하철 역 아이디


    // 지하철 경로 추가할 때
    public AddItem(String num, String start, String end) {
        this.num = num;
        this.start = start;
        this.end = end;
    }

    public AddItem(String busType, String num, String numId, String start, String startId, String end, String endId) {
        this.busType = busType;
        this.num = num;
        this.numId = numId;
        this.start = start;
        this.startId = startId;
        this.end = end;
        this.endId = endId;
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
}



