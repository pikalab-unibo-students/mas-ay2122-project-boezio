package mas.project.boezio.ay2122.agents;

import mas.project.boezio.ay2122.ontology.Lesson;

import java.util.Set;

public class ProfessorVerdi extends Professor{
    @Override
    protected String setName() {
        return "Verdi";
    }

    @Override
    protected Set<Lesson> generatePreferences() {
        return null;
    }
}
