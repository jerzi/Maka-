package sk.tuke.smart.maka;


public class Achievement {

    private String name;
    private String time;
    private String pace;
    private String distance;
    private String description;

    private String active;

    public Achievement(String name, String description, String time, String pace, String distance, String active) {
        this.name = name;
        this.description = description;
        this.time = time;
        this.pace = pace;
        this.distance = distance;
        setActive(active);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
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

    public String isActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return name + "\n" + description + "\n" + active;
    }
}
