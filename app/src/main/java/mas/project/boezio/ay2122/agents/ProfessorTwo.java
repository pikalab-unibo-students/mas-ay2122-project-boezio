package mas.project.boezio.ay2122.agents;

import mas.project.boezio.ay2122.ontology.Lesson;

import java.util.HashSet;
import java.util.Set;

public class ProfessorTwo extends Professor{
    @Override
    protected String setName() {
        return "Bianchi";
    }

    @Override
    protected Set<Lesson> generatePreferences() {
        return new HashSet<>();
    }
}
