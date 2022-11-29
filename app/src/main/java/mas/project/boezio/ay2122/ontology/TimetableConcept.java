package mas.project.boezio.ay2122.ontology;

import jade.content.Concept;

import jade.util.leap.List;

public class TimetableConcept implements Concept {

    private List teachings;

    public List getTeachings() {
        return teachings;
    }

    public void setTeachings(List teachings) {
        this.teachings = teachings;
    }
}
