package mas.project.boezio.ay2122.ontology;


import jade.content.Concept;

public class Lesson implements Concept {

    private int hour;
    private int day;

    public Lesson(int hour, int day) {
        this.hour = hour;
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "hour=" + hour +
                ", day=" + day +
                '}';
    }
}
