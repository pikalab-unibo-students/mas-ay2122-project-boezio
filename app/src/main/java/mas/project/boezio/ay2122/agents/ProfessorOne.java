package mas.project.boezio.ay2122.agents;

import mas.project.boezio.ay2122.ontology.Lesson;

import java.util.HashSet;
import java.util.Set;

public class ProfessorOne extends Professor{

    protected Set<Lesson> preferences = generatePreferences();

    @Override
    protected Set<Lesson> generatePreferences(){
        Set<Lesson> preferences = new HashSet<>();
        preferences.add(new Lesson(1,2));
        return preferences;
    }
}
