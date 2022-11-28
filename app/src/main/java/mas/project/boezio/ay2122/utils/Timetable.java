package mas.project.boezio.ay2122.utils;

import mas.project.boezio.ay2122.ontology.SchoolClass;

public class Timetable {

    private final int numHours;
    private final int numDays;
    private final SchoolClass[][] classes;

    public Timetable(int numHours, int numDays){
        this.numHours = numHours;
        this.numDays = numDays;
        classes = new SchoolClass[numHours][numDays];
    }

    public int getNumHours() {
        return numHours;
    }

    public int getNumDays() {
        return numDays;
    }

    public void setEntry(int hour, int day, SchoolClass schoolClass){
        classes[hour-1][day-1] = schoolClass;
    }

    public SchoolClass getEntry(int hour, int day){
        return classes[hour-1][day-1];
    }
}
