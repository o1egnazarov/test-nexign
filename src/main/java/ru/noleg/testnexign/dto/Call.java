package ru.noleg.testnexign.dto;

public class Call {

    private String totalTime;

    public Call(long totalTime) {
        long HH = totalTime / 3600;
        long MM = (totalTime % 3600) / 60;
        long SS = totalTime % 60;

        this.totalTime = String.format("%02d:%02d:%02d", HH, MM, SS);
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void addSeconds(long seconds) {
        long HH = seconds / 3600;
        long MM = (seconds % 3600) / 60;
        long SS = seconds % 60;

        this.totalTime = String.format("%02d:%02d:%02d", HH, MM, SS);
    }

    @Override
    public String toString() {
        return "Call{" +
                "totalTime='" + totalTime + '\'' +
                '}';
    }
}
