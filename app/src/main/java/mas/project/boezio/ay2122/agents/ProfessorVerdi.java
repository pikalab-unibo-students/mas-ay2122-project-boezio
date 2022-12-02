package mas.project.boezio.ay2122.agents;

import mas.project.boezio.ay2122.ontology.Lesson;

import java.util.HashSet;
import java.util.Set;

public class ProfessorVerdi extends Professor{
    @Override
    protected String setName() {
        return "Verdi";
    }

    @Override
    protected Set<Lesson> generatePreferences() {
        Set<Lesson> preferences = new HashSet<>();
        preferences.add(new Lesson(1,1));
        preferences.add(new Lesson(4,3));
        return preferences;
    }
}
