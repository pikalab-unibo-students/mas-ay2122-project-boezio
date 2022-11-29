package mas.project.boezio.ay2122.ontology;

import jade.content.Predicate;

public class Change implements Predicate {

    private Lesson lessonChange;


    public Lesson getLessonChange() {
        return lessonChange;
    }

    public void setLessonChange(Lesson lessonChange) {
        this.lessonChange = lessonChange;
    }
}
