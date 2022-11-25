package mas.project.boezio.ay2122.ontology;

import jade.content.Predicate;

public class Substitution implements Predicate {

    private Lesson proposedLesson;
    private Lesson currentLesson;

    public Lesson getProposedLesson() {
        return proposedLesson;
    }

    public void setProposedLesson(Lesson proposedLesson) {
        this.proposedLesson = proposedLesson;
    }

    public Lesson getCurrentLesson() {
        return currentLesson;
    }

    public void setCurrentLesson(Lesson currentLesson) {
        this.currentLesson = currentLesson;
    }
}
