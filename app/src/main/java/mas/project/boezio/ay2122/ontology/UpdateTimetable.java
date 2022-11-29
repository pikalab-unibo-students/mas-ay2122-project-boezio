package mas.project.boezio.ay2122.ontology;

import jade.content.AgentAction;

public class UpdateTimetable implements AgentAction {

    private TimetableConcept timetable;


    public TimetableConcept getTimetable() {
        return timetable;
    }

    public void setTimetable(TimetableConcept timetable) {
        this.timetable = timetable;
    }
}
