package mas.project.boezio.ay2122.ontology;

import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.schema.ConceptSchema;
import jade.content.schema.ObjectSchema;
import jade.content.schema.PredicateSchema;
import jade.content.schema.PrimitiveSchema;

public class SchoolTimetableOntology extends Ontology {

    public static final String ONTOLOGY_NAME = "School-Timetable-Ontology";

    public static final String LESSON = "Lesson";
    public static final String HOUR = "hour";
    public static final String DAY = "day";

    public static final String SCHOOL_CLASS = "SchoolClass";
    public static final String YEAR = "year";
    public static final String LETTER = "letter";

    public static final String SUBSTITUTION = "Substitutes";
    public static final String PROPOSED_LESSON = "proposedLesson";
    public static final String CURRENT_LESSON = "currentLesson";

    public static final String TEACHING = "Teaching";
    public static final String TEACH_LESSON = "lesson";
    public static final String TEACH_CLASS = "schoolClass";

    public static final String TIMETABLE = "TimetableConcept";
    public static final String TEACHINGS = "teachings";

    private static SchoolTimetableOntology instance = new SchoolTimetableOntology();

    public static Ontology getInstance(){
        return instance;
    }

    private SchoolTimetableOntology(){

        super(ONTOLOGY_NAME, BasicOntology.getInstance());

        try{
            add(new ConceptSchema(LESSON), Lesson.class);
            add(new ConceptSchema(SCHOOL_CLASS), SchoolClass.class);
            add(new PredicateSchema(SUBSTITUTION), Substitution.class);
            add(new ConceptSchema(TEACHING), Teaching.class);
            add(new ConceptSchema(TIMETABLE), TimetableConcept.class);

            // Structure of the scheme for the Lesson concept
            ConceptSchema lesson = (ConceptSchema) getSchema(LESSON);
            lesson.add(HOUR, (PrimitiveSchema) getSchema(BasicOntology.INTEGER));
            lesson.add(DAY, (PrimitiveSchema) getSchema(BasicOntology.INTEGER));

            // Structure of the schema for the SchoolClass concept
            ConceptSchema schoolClass = (ConceptSchema) getSchema(SCHOOL_CLASS);
            schoolClass.add(YEAR, (PrimitiveSchema) getSchema(BasicOntology.INTEGER));
            schoolClass.add(LETTER, (PrimitiveSchema) getSchema(BasicOntology.STRING));

            // Structure of the Substitution predicate
            PredicateSchema substitution = (PredicateSchema) getSchema(SUBSTITUTION);
            substitution.add(PROPOSED_LESSON, getSchema(LESSON));
            substitution.add(CURRENT_LESSON, getSchema(LESSON));

            // Structure of the schema for Teaching concept
            ConceptSchema teaching = (ConceptSchema) getSchema(TEACHING);
            teaching.add(TEACH_LESSON, (ConceptSchema) getSchema(LESSON));
            teaching.add(TEACH_CLASS, (ConceptSchema) getSchema(SCHOOL_CLASS));

            // Structure of the schema for Teachings concept
            ConceptSchema timetable = (ConceptSchema) getSchema(TIMETABLE);
            timetable.add(TEACHINGS, (ConceptSchema) getSchema(TEACHING), 0, ObjectSchema.UNLIMITED);


        }catch (OntologyException oe){
            oe.printStackTrace();
        }
    }

}
