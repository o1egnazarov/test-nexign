package ru.noleg.testnexign.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class Call {

    private long totalSeconds;

    public Call(long totalSeconds) {
        this.totalSeconds = totalSeconds;
    }

    public void addSeconds(long seconds) {
        this.totalSeconds += seconds;
    }

    @JsonGetter("totalTime")
    public String getFormattedTime() {
        long HH = this.totalSeconds / 3600;
        long MM = (this.totalSeconds % 3600) / 60;
        long SS = this.totalSeconds % 60;

        return String.format("%02d:%02d:%02d", HH, MM, SS);
    }

    @JsonSetter("totalTime")
    public void setFormattedTime(String time) {
        String[] parts = time.split(":");
        long hours = Long.parseLong(parts[0]);
        long minutes = Long.parseLong(parts[1]);
        long seconds = Long.parseLong(parts[2]);
        this.totalSeconds = hours * 3600 + minutes * 60 + seconds;
    }

    @Override
    public String toString() {
        long HH = this.totalSeconds / 3600;
        long MM = (this.totalSeconds % 3600) / 60;
        long SS = this.totalSeconds % 60;

        return String.format("Total time: %02d:%02d:%02d", HH, MM, SS);
    }
}
