package mas.project.boezio.ay2122.ontology;

import jade.content.Concept;

public class Teaching implements Concept {

    private Lesson lesson;
    private SchoolClass schoolClass;

    public Teaching(Lesson lesson, SchoolClass schoolClass) {
        this.lesson = lesson;
        this.schoolClass = schoolClass;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public SchoolClass getSchoolClass() {
        return schoolClass;
    }

    public void setSchoolClass(SchoolClass schoolClass) {
        this.schoolClass = schoolClass;
    }
}
