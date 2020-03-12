package com.example.mbw.route;

import com.google.android.gms.maps.model.LatLng;

public class DetailItem {

    //TODO : 남은 시간 자동으로 감소하도록!!
    /*
알아야 하는 것 : 1, 2, 3, 4, 5 의 갯수, 순서
    1. 버스 : 정류장 이름, 버스 번호, 정류장 번호, 남은 시간 1, 혼잡도 1, 남은 시간 2, 혼잡도 2, 이동시간, 이동 정류장 개수, 저상버스 여부, 버스 타입
    2. 지하철_환승 : 역 이름, __행1, 남은시간1, __행2, 남은시간2, 빠른 환승 지점, 빠른 환승 도보시간, 빠른 환승 도보거리, 이동시간, 이동 역 개수,
    3. 지하철_끝 : 역 이름, __행1, 남은시간1, __행2, 남은시간2, 빠른 하차 지점, 이동시간, 이동 역 개수
    4. 도보 : 이동시간, 이동거리
    5. 시작점 : 시작지점
    6. 도착점 : 도착지점
 */
    // 시작 좌표, 도착 좌표 : sx, sy, ex, ey (다 알아야 지도에 표시가 가능함)


    private LatLng start;
    private LatLng end;

    private int imageType; // 이미지 뭘로 할지 (1~9 : 지하철, 10 : 인천 1호선, 11 : 분당선, 12 : 경의선, 13 : 신분당선, 14 : 공항철도, 15 : 중앙선, 16 : 경춘선, 17 : 수인선)

    private String spotName; // 정류장 이름, 역 이름, 시작지점 이름, 끝지점 이름
    private String spotName2;   // 하차 지점 (버스, 지하철의 경우)

    private String wayNum; // 지하철의 경우 방면표시, 버스의 경우 정류장 아이디
    private String wayNum2;   // 도착지점 버스 정류장 아이디

    //2개씩 표시, 없는거는 null로
    private int busType1, busType2; // 버스 타입
    private String busNum1, busNum2;  // 버스 번호
    private String direction1, direction2; // ~~ 행
    private String remaining1, remaining2; // 남은시간 (상태) ex. 4분 40초 (여유)

    private int time; // 이동시간
    private int passedStop; // 이동한 정류장, 역 갯수
    private int walkDistance; // 도보이동거리

    //지하철 환승 할 때만 필요
    private int transTime; //환승도보 시간
    private int transDistance; // 환승도보거리


    //지하철 환승, 끝일 때
    private int transExit;
    private String fastPlatform; // 빠른 하차, 빠른 환승

    //지하철 엘레베이터 이동경로
    private String context1;

    public String getContext1() {
        return context1;
    }

    public void setContext1(String context1) {
        this.context1 = context1;
    }

    public String getContext2() {
        return context2;
    }

    public void setContext2(String context2) {
        this.context2 = context2;
    }

    private String context2;

    // 시작점, 종점의 경우
    public DetailItem(LatLng start, int imageType, String spotName) {
        this.start = start;
        this.imageType = imageType;
        this.spotName = spotName;
    }

    // 버스의 경우
    public DetailItem(LatLng start, LatLng end, int imageType, String spotName, String spotName2, String wayNum, String wayNum2, int busType1, int busType2,
                      String busNum1, String busNum2, String remaining1, String remaining2, int time, int passedStop) {
        this.start = start;
        this.end = end;
        this.imageType = imageType;
        this.spotName = spotName;
        this.spotName2 = spotName2;

        this.wayNum = wayNum;
        this.wayNum2 = wayNum;

        this.busType1 = busType1;
        this.busType2 = busType2;
        this.busNum1 = busNum1;
        this.busNum2 = busNum2;

        this.remaining1 = remaining1;
        this.remaining2 = remaining2;

        this.time = time;
        this.passedStop = passedStop;
    }

    // 지하철 환승
    public DetailItem(LatLng start, LatLng end, int imageType, String spotName, String spotName2, String wayNum,
                      String direction1, String direction2, String remaining1, String remaining2, int time, int passedStop,
                      String context1, String context2) {
        this.start = start;
        this.end = end;
        this.imageType = imageType;
        this.spotName = spotName;
        this.spotName2 = spotName2;
        this.wayNum = wayNum;

        //only for subway
        this.direction1 = direction1;
        this.direction2 = direction2;

        this.remaining1 = remaining1;
        this.remaining2 = remaining2;

        this.time = time;
        this.passedStop = passedStop;
/*
        // 환승도보 정보 (지하철 갈아 탈 때만 필요)
        this.transTime = transTime;
        this.transDistance = transDistance;
*/
        this.context1 = context1;
        this.context2 = context2;
    }


    // 지하철 끝
    public DetailItem(LatLng start, LatLng end, int imageType, String spotName, String spotName2, String wayNum, String direction1, String direction2,
                      String remaining1, String remaining2, int time, int passedStop, int transExit, String fastPlatform) {
        this.start = start;
        this.end = end;
        this.imageType = imageType;
        this.spotName = spotName;
        this.spotName2 = spotName2;
        this.wayNum = wayNum;

        //only for subway
        this.direction1 = direction1;
        this.direction2 = direction2;

        this.remaining1 = remaining1;
        this.remaining2 = remaining2;

        this.time = time;
        this.passedStop = passedStop;

        this.transExit = transExit;
        this.fastPlatform = fastPlatform;
    }


    // 도보의 경우
    public DetailItem(LatLng start, LatLng end, int imageType, int time, int walkDistance) {
        this.start = start;
        this.end = end;
        this.imageType = imageType;
        this.time = time;
        this.walkDistance = walkDistance;
    }


    // Getter and Setter
    public LatLng getStart() {
        return start;
    }

    public void setStart(LatLng start) {
        this.start = start;
    }

    public LatLng getEnd() {
        return end;
    }

    public void setEnd(LatLng end) {
        this.end = end;
    }


    public int getImageType() {
        return imageType;
    }

    public void setImageType(int imageType) {
        this.imageType = imageType;
    }

    public String getSpotName() {
        return spotName;
    }

    public void setSpotName(String spotName) {
        this.spotName = spotName;
    }

    public String getSpotName2() {
        return spotName2;
    }

    public void setSpotName2(String spotName2) {
        this.spotName2 = spotName2;
    }

    public String getWayNum() {
        return wayNum;
    }

    public void setWayNum(String wayNum) {
        this.wayNum = wayNum;
    }

    public String getWayNum2() {
        return wayNum2;
    }

    public void setWayNum2(String wayNum) {
        this.wayNum2 = wayNum2;
    }

    public int getBusType1() {
        return busType1;
    }

    public void setBusType1(int busType1) {
        this.busType1 = busType1;
    }

    public int getBusType2() {
        return busType2;
    }

    public void setBusType2(int busType2) {
        this.busType2 = busType2;
    }

    public String getBusNum1() {
        return busNum1;
    }

    public void setBusNum1(String busNum1) {
        this.busNum1 = busNum1;
    }

    public String getBusNum2() {
        return busNum2;
    }

    public void setBusNum2(String busNum2) {
        this.busNum2 = busNum2;
    }

    public String getDirection1() {
        return direction1;
    }

    public void setDirection1(String direction1) {
        this.direction1 = direction1;
    }

    public String getDirection2() {
        return direction2;
    }

    public void setDirection2(String direction2) {
        this.direction2 = direction2;
    }

    public String getRemaining1() {
        return remaining1;
    }

    public void setRemaining1(String remaining1) { this.remaining1 = remaining1; }

    public String getRemaining2() {
        return remaining2;
    }

    public void setRemaining2(String remaining2) {
        this.remaining2 = remaining2;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getPassedStop() {
        return passedStop;
    }

    public void setPassedStop(int passedStop) {
        this.passedStop = passedStop;
    }

    public int getWalkDistance() {
        return walkDistance;
    }

    public void setWalkDistance(int walkDistance) {
        this.walkDistance = walkDistance;
    }

    public int getTransTime() {
        return transTime;
    }

    public void setTransTime(int transTime) {
        this.transTime = transTime;
    }

    public int getTransDistance() {
        return transDistance;
    }

    public void setTransDistance(int transDistance) {
        this.transDistance = transDistance;
    }

    public String getFastPlatform() {
        return fastPlatform;
    }

    public void setFastPlatform(String fastPlatform) {
        this.fastPlatform = fastPlatform;
    }


    public int getTransExit() {
        return transExit;
    }

    public void setTransExit(int transExit) {
        this.transExit = transExit;
    }
}

