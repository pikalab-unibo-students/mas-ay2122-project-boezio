package mas.project.boezio.ay2122.agents;

import mas.project.boezio.ay2122.ontology.Lesson;

import java.util.HashSet;
import java.util.Set;

public class ProfessorBianchi extends Professor{
    @Override
    protected String setName() {
        return "Bianchi";
    }

    @Override
    protected Set<Lesson> generatePreferences() {
        Set<Lesson> preferences = new HashSet<>();
        preferences.add(new Lesson(2,5));
        return preferences;
    }
}
