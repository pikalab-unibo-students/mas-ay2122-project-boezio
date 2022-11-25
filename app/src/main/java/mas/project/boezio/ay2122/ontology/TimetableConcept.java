package mas.project.boezio.ay2122.ontology;

import jade.content.Concept;

import java.util.List;

public class TimetableConcept implements Concept {

    private List<Teaching> teachings;

    public List<Teaching> getTeachings() {
        return teachings;
    }

    public void setTeachings(List<Teaching> teachings) {
        this.teachings = teachings;
    }
}
