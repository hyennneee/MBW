package com.example.mbw;

import com.google.android.gms.maps.model.LatLng;

public class transitMapData {


    int lineNo; // -1 (버스), 1~9 (1호선~9호선)
    int transitMode; // 2(버스), 1(지하철)
    LatLng start;
    LatLng end;


    public transitMapData(int lineNo, int transitMode, LatLng start, LatLng end) {
        this.lineNo = lineNo;
        this.transitMode = transitMode;
        this.start = start;
        this.end = end;
    }


    public int getLineNo() {
        return lineNo;
    }

    public void setLineNo(int lineNo) {
        this.lineNo = lineNo;
    }

    public int getTransitMode() {
        return transitMode;
    }

    public void setTransitMode(int transitMode) {
        this.transitMode = transitMode;
    }

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

}
