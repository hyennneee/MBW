package com.example.mbw.problemArea;

import com.google.gson.annotations.SerializedName;

public class problemAreaData {
    @SerializedName("subwayCode")
    private int subwayCode;

    @SerializedName("stationName")
    private String stationName;

    @SerializedName("transfer")
    private int transfer;

    @SerializedName("nextStation")
    private String nextStation;

    @SerializedName("endExitNo")
    private String endExitNo;

    @SerializedName("problemNo")
    private int problemNo;

    @SerializedName("problem")
    private String problem;


    public problemAreaData(int subwayCode, String stationName, int transfer, String nextStation, String endExitNo, int problemNo, String problem) {
        this.subwayCode = subwayCode;
        this.stationName = stationName;
        this.transfer = transfer;
        this.nextStation = nextStation;
        this.endExitNo = endExitNo;
        this.problemNo = problemNo;
        this.problem = problem;
    }

    public int getSubwayCode() {
        return subwayCode;
    }

    public String getStationName() {
        return stationName;
    }

    public int getTransfer() {
        return transfer;
    }

    public String getNextStation() {
        return nextStation;
    }

    public String getEndExitNo() {
        return endExitNo;
    }

    public int getProblemNo() {
        return problemNo;
    }

    public String getProblem() {
        return problem;
    }

}
