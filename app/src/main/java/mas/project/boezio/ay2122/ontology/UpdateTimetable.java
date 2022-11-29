package mas.project.boezio.ay2122.ontology;

import jade.content.Predicate;

public class UpdateTimetable implements Predicate {

    private TimetableConcept timetable;

    public TimetableConcept getTimetable() {
        return timetable;
    }

    public void setTimetable(TimetableConcept timetable) {
        this.timetable = timetable;
    }
}
