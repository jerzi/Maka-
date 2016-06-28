package sk.tuke.smart.maka;


public class Result {

    private String time;
    private String pace;
    private String distance;

    public Result(String time, String pace, String distance) {
        this.time = time;
        this.pace = pace;
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "Time: "+ getTime();
    }

    public String getTime() {
        return time;
    }

    public String getPace() {
        return pace;
    }

    public String getDistance() {
        return distance;
    }
}
