package mas.project.boezio.ay2122.agents;

import clpfd.ClpFdLibrary;

import it.unibo.tuprolog.core.Struct;
import it.unibo.tuprolog.core.Var;
import it.unibo.tuprolog.solve.Solution;
import it.unibo.tuprolog.solve.Solver;
import it.unibo.tuprolog.solve.flags.TrackVariables;
import it.unibo.tuprolog.solve.library.Runtime;
import it.unibo.tuprolog.theory.Theory;

import jade.content.ContentElementList;
import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import mas.project.boezio.ay2122.ontology.*;
import mas.project.boezio.ay2122.utils.*;

import java.util.*;

public class TimeScheduler extends Agent {

    // service offered by the time scheduler
    public static final String SERVICE = "negotiationMediator";

    // timetable of each Professor agent
    private Map<AID, Timetable> timetables;
    // free day of each Professor agent
    private final Map<AID, Integer> freeDays = new HashMap<>();

    // weekly hours for each professor in each class
    private final Map<AID,Map<SchoolClass,Integer>> hoursPerProfessor = Utils.initializeHours();

    // ontology information
    private final Codec codec = new SLCodec();
    private final Ontology ontology = SchoolTimetableOntology.getInstance();

    // content manager to deal with ontology
    private final ContentManager cm = getContentManager();

    protected void setup(){

        Utils.printMessage(this, "Hi everyone, I'm the time-scheduler");

        Utils.registerOntology(cm, codec, ontology);

        // register itself as time scheduler
        Utils.registerService(this, SERVICE);

        // dummy behaviour for testing
        addBehaviour(new Dummy());

        // agent's behaviours
        //addBehaviour(new TimetableBehaviour(Utils.NUM_HOURS, Utils.NUM_DAYS, hoursPerProfessor));
        addBehaviour(new WaitProposalBehaviour());

    }

    private class Dummy extends OneShotBehaviour{

        @Override
        public void action() {


            // initialize different lessons
            Lesson[][] lessons = new Lesson[Utils.NUM_HOURS][Utils.NUM_DAYS];
            for(int i=0; i < Utils.NUM_HOURS; i++){
                for(int j=0; j < Utils.NUM_DAYS; j++){
                    lessons[i][j] = new Lesson(i+1,j+1);
                }
            }

            // school classes
            SchoolClass firstA = Utils.classesMap.get(1);
            SchoolClass secondA = Utils.classesMap.get(2);

            // timetable of professor1
            jade.util.leap.List teachingsFirst = new jade.util.leap.ArrayList();
            teachingsFirst.add(new Teaching(lessons[0][0], firstA));
            teachingsFirst.add(new Teaching(lessons[1][0], firstA));
            teachingsFirst.add(new Teaching(lessons[2][0], secondA));
            teachingsFirst.add(new Teaching(lessons[3][0], secondA));
            teachingsFirst.add(new Teaching(lessons[4][0], secondA));
            teachingsFirst.add(new Teaching(lessons[0][2], secondA));
            teachingsFirst.add(new Teaching(lessons[1][2], secondA));
            teachingsFirst.add(new Teaching(lessons[2][2], firstA));
            teachingsFirst.add(new Teaching(lessons[3][2], firstA));
            teachingsFirst.add(new Teaching(lessons[0][3], secondA));
            teachingsFirst.add(new Teaching(lessons[4][3], firstA));
            teachingsFirst.add(new Teaching(lessons[0][4], secondA));
            teachingsFirst.add(new Teaching(lessons[3][4], firstA));
            teachingsFirst.add(new Teaching(lessons[4][4], firstA));
            TimetableConcept timeConceptFirst = new TimetableConcept();
            timeConceptFirst.setTeachings(teachingsFirst);
            UpdateTimetable updateFirst = new UpdateTimetable();
            updateFirst.setTimetable(timeConceptFirst);

            // timetable of professor2
            jade.util.leap.List teachingsSecond = new jade.util.leap.ArrayList();
            teachingsSecond.add(new Teaching(lessons[0][1], firstA));
            teachingsSecond.add(new Teaching(lessons[1][1], firstA));
            teachingsSecond.add(new Teaching(lessons[1][1], firstA));
            teachingsSecond.add(new Teaching(lessons[3][1], secondA));
            teachingsSecond.add(new Teaching(lessons[4][1], secondA));
            teachingsSecond.add(new Teaching(lessons[4][2], firstA));
            teachingsSecond.add(new Teaching(lessons[3][3], secondA));
            teachingsSecond.add(new Teaching(lessons[4][3], secondA));
            teachingsSecond.add(new Teaching(lessons[0][4], firstA));
            teachingsSecond.add(new Teaching(lessons[1][4], firstA));
            teachingsSecond.add(new Teaching(lessons[2][4], secondA));
            teachingsSecond.add(new Teaching(lessons[3][4], secondA));
            teachingsSecond.add(new Teaching(lessons[4][4], secondA));
            TimetableConcept timeConceptSecond = new TimetableConcept();
            timeConceptSecond.setTeachings(teachingsSecond);
            UpdateTimetable updateSecond = new UpdateTimetable();
            updateSecond.setTimetable(timeConceptSecond);

            // timetable of professor3
            jade.util.leap.List teachingsThird = new jade.util.leap.ArrayList();
            teachingsThird.add(new Teaching(lessons[0][0], secondA));
            teachingsThird.add(new Teaching(lessons[1][0], secondA));
            teachingsThird.add(new Teaching(lessons[2][0], firstA));
            teachingsThird.add(new Teaching(lessons[0][2], firstA));
            teachingsThird.add(new Teaching(lessons[1][2], firstA));
            teachingsThird.add(new Teaching(lessons[2][2], secondA));
            teachingsThird.add(new Teaching(lessons[3][2], secondA));
            teachingsThird.add(new Teaching(lessons[1][3], secondA));
            teachingsThird.add(new Teaching(lessons[2][3], secondA));
            teachingsThird.add(new Teaching(lessons[3][3], firstA));
            teachingsThird.add(new Teaching(lessons[1][4], secondA));
            teachingsThird.add(new Teaching(lessons[2][4], firstA));
            TimetableConcept timeConceptThird = new TimetableConcept();
            timeConceptThird.setTeachings(teachingsThird);
            UpdateTimetable updateThird = new UpdateTimetable();
            updateThird.setTimetable(timeConceptThird);

            // timetable of professor4
            jade.util.leap.List teachingsFourth = new jade.util.leap.ArrayList();
            teachingsFourth.add(new Teaching(lessons[3][0], firstA));
            teachingsFourth.add(new Teaching(lessons[4][0], firstA));
            teachingsFourth.add(new Teaching(lessons[0][1], secondA));
            teachingsFourth.add(new Teaching(lessons[1][1], secondA));
            teachingsFourth.add(new Teaching(lessons[2][1], secondA));
            teachingsFourth.add(new Teaching(lessons[3][1], firstA));
            teachingsFourth.add(new Teaching(lessons[4][1], firstA));
            teachingsFourth.add(new Teaching(lessons[4][2], secondA));
            teachingsFourth.add(new Teaching(lessons[0][3], firstA));
            teachingsFourth.add(new Teaching(lessons[1][3], firstA));
            teachingsFourth.add(new Teaching(lessons[2][3], firstA));
            TimetableConcept timeConceptFourth = new TimetableConcept();
            timeConceptFourth.setTeachings(teachingsFourth);
            UpdateTimetable updateFourth = new UpdateTimetable();
            updateFourth.setTimetable(timeConceptFourth);

            // update timetables and free days

            // initialize timetables
            timetables = new HashMap<>();
            int numProfessors = hoursPerProfessor.size();
            for(int i=1; i <= numProfessors; i++){
                timetables.put(
                        new AID("professor"+i, AID.ISLOCALNAME),
                        new Timetable(Utils.NUM_HOURS, Utils.NUM_DAYS)
                );
            }

            // initialize professor AID
            AID[] profAIDs = new AID[Utils.NUM_PROFESSORS];
            for(int i=0; i < Utils.NUM_PROFESSORS; i++){
                profAIDs[i] = new AID("professor"+(i+1), AID.ISLOCALNAME);
            }

            // update timetable for each professor
            jade.util.leap.List[] allTeachings =
                    new jade.util.leap.List[] { teachingsFirst, teachingsSecond, teachingsThird, teachingsFourth };
            int size = allTeachings.length;
            // teachings of each professor
            for(int i=0; i < size; i++){
                jade.util.leap.List teachings = allTeachings[i];
                int numTeachings = teachings.size();
                Timetable timetable = timetables.get(profAIDs[i]);
                // different teachings of a professor
                for(int j=0; j < numTeachings; j++){
                    Teaching currentTeach = (Teaching) teachings.get(j);
                    Lesson currentLesson = currentTeach.getLesson();
                    timetable.setEntry(
                            currentLesson.getHour(),
                            currentLesson.getDay(),
                            currentTeach.getSchoolClass()
                    );
                }
            }

            // update free days for each professor
            freeDays.put(profAIDs[0], 2);
            freeDays.put(profAIDs[1], 1);
            freeDays.put(profAIDs[2], 2);
            freeDays.put(profAIDs[3], 5);

            // send timetables to all professors
            UpdateTimetable[] msgContent =
                    new UpdateTimetable[] {updateFirst, updateSecond, updateThird, updateFourth };

            ACLMessage[] messages = new ACLMessage[Utils.NUM_PROFESSORS];
            for(int i=0; i < Utils.NUM_PROFESSORS; i++){
                messages[i] = new ACLMessage(ACLMessage.INFORM);
                messages[i].addReceiver(profAIDs[i]);
                messages[i].setLanguage(codec.getName());
                messages[i].setOntology(ontology.getName());
                ContentElementList cel = new ContentElementList();
                cel.add(msgContent[i]);
                try {
                    cm.fillContent(messages[i], cel);
                } catch (OntologyException | Codec.CodecException e) {
                    e.printStackTrace();
                }
            }

            // send messages
            for(int i=0; i < Utils.NUM_PROFESSORS; i++){
                myAgent.send(messages[i]);
            }
        }
    }

    // Behaviour to create school timetables of each professor using Constraint Logic Programing

    private class TimetableBehaviour extends OneShotBehaviour{

        private final int numHours;
        private final int numDays;
        private final Map<AID,Map<SchoolClass,Integer>> hoursPerProfessor;

        private TimetableBehaviour(
                int numHours,
                int numDays,
                Map<AID,Map<SchoolClass,Integer>> hoursPerProfessor
        ){
            this.numHours = numHours;
            this.numDays = numDays;
            this.hoursPerProfessor = hoursPerProfessor;
        }

        @Override
        public void action() {
            // simplified version, model should be created dynamically
            // CP model of a specific instance
            Theory theory = Utils.getTheory();
            System.out.println("Theory has been read");
            // goal
            Struct goal = Utils.getGoal();
            System.out.println("Goal has been read");
            // solver
            Solver solver = Solver.prolog().newBuilder()
                    .runtime(Runtime.of(ClpFdLibrary.INSTANCE))
                    .flag(TrackVariables.INSTANCE, TrackVariables.ON)
                    .staticKb(theory)
                    .build();
            Solution solution = solver.solveOnce(goal);

            System.out.print("Solution is yes: ");
            System.out.println(solution.isYes());

            // initialize timetables
            timetables = new HashMap<>();
            int numProfessors = hoursPerProfessor.size();
            for(int i=1; i <= numProfessors; i++){
                timetables.put(new AID("professor"+i, AID.ISLOCALNAME), new Timetable(numHours, numDays));
            }
            // update timetables for each professor
            it.unibo.tuprolog.core.Substitution substitution = solution.getSubstitution();
            for(Var variable: substitution.keySet()){
                // extract from each variable relevant information
                String varName = variable.getName();
                int profID = Integer.parseInt(String.valueOf(varName.charAt(1)));
                AID profAID = new AID("professor"+profID, true);
                int hour = Integer.parseInt(String.valueOf(varName.charAt(2)));
                int day = Integer.parseInt(String.valueOf(varName.charAt(3)));
                // this map depends on the specific instance of the problem
                SchoolClass schoolClass = Utils.classesMap.get(substitution.get(variable));
                // update professor's timetable
                Timetable profTimetable = timetables.get(profAID);
                profTimetable.setEntry(hour, day, schoolClass);
            }
            Utils.printMessage(myAgent, "I've update all timetables");
            // update free day for each professor
            for(AID prof: timetables.keySet()){
                Timetable timeProf = timetables.get(prof);
                for(int j=0; j < numDays; j++){
                    // counting null entries mean understand whether the professor is free for all
                    // hours of day j
                    int countNull = 0;
                    for(int i=0; i < numHours; i++){
                        if(timeProf.getEntry(i,j) == null)
                            countNull++;
                    }
                    if(countNull == numHours) {
                        freeDays.put(prof, j);
                        break;
                    }
                }
            }
            // send timetables to each professor
            Set<AID> professorsAID = timetables.keySet();
            for(AID professor: professorsAID){
                // get timetable of the current professor
                Timetable timetable = timetables.get(professor);

                // add data to the content of the future message
                jade.util.leap.List teachings = new jade.util.leap.ArrayList();
                for(int i = 1; i <= numHours; i++){
                    for(int j = 1; j <= numDays; j++){
                        SchoolClass schoolClass = timetable.getEntry(i,j);
                        if(schoolClass != null){
                            Teaching teaching = new Teaching(new Lesson(i,j), schoolClass);
                            teachings.add(teaching);
                        }
                    }
                }
                TimetableConcept timeConcept = new TimetableConcept();
                timeConcept.setTeachings(teachings);
                UpdateTimetable action = new UpdateTimetable();
                action.setTimetable(timeConcept);
                // send message
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(professor);
                msg.setLanguage(codec.getName());
                msg.setOntology(ontology.getName());
                ContentElementList cel = new ContentElementList();
                cel.add(action);
                try {
                    cm.fillContent(msg, cel);
                } catch (Codec.CodecException | OntologyException e) {
                    e.printStackTrace();
                }
                myAgent.send(msg);

            }
        }
    }

    private class WaitProposalBehaviour extends CyclicBehaviour{

        private final MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);

        @Override
        public void action(){
            ACLMessage msg = myAgent.receive(mt);
            if(msg != null) {
                Utils.printMessage(myAgent, "I've received a cfp message");
                addBehaviour(new MediationBehaviour(msg));
            }else {
                Utils.printMessage(myAgent,"I'm waiting for a cfp message");
                block();
            }
        }
    }

    private class MediationBehaviour extends Behaviour{

        private int step = 0;

        // AID of the agent who has sent a proposal message
        private final AID sender;
        // lesson to change
        private Lesson proposedLesson;
        // current change proposal
        private Lesson proposedChange;
        private final Integer senderFreeDay;
        // current professor who could accept the lesson change
        private AID substitute;
        // hour and day of the lesson
        private final int hour;
        private final int day;
        // ID of the conversation
        private final String conversationID;
        // timetable and school class at proposedLesson to change
        private final Timetable timeSender;
        private final SchoolClass schoolClass;

        // list of all agents who teach in the same class the agent sender has at lesson
        private List<AID> professors = new ArrayList<>();
        // list of professors and their possible change
        private final Set<Tuple<AID,Lesson>> lessons = new LinkedHashSet<>();
        private Iterator<Tuple<AID,Lesson>> iterator;
        // template to match to detect a reply
        private final MessageTemplate mt;

        public MediationBehaviour(ACLMessage message){
            this.sender = message.getSender();
            this.senderFreeDay = freeDays.get(sender);
            this.conversationID = message.getConversationId();
            // get lesson in the content of the message
            try {
                Change change = (Change) cm.extractContent(message);
                proposedLesson = change.getLessonChange();
            } catch (Codec.CodecException | OntologyException e) {
                e.printStackTrace();
            }
            // obtain lesson information
            hour = proposedLesson.getHour();
            day = proposedLesson.getDay();
            // obtain timetable and school class in the specified lesson
            timeSender = timetables.get(sender);
            schoolClass = timeSender.getEntry(hour,day);
            // template to match for detecting a reply after a proposal
            mt = MessageTemplate.MatchConversationId(conversationID);
        }

        @Override
        public void action(){
            switch (step){
                case 0:
                    // obtain list of professors who teach in that class
                    professors = Utils.getServiceProviders(myAgent, schoolClass.toString());
                    // filter professors who have free hour at proposed lesson
                    professors.removeIf(professor -> timetables.get(professor).getEntry(hour, day) != null);
                    // filter professors whose free day is not the day of the proposed lesson
                    professors.removeIf(professor -> freeDays.get(professor) == day);
                    // create list of lessons where sender has free hour
                    for(AID professor: professors){
                        Timetable timeProf = timetables.get(professor);
                        int numHours = timeProf.getNumHours();
                        int numDays = timeProf.getNumDays();
                        for(int i=1; i <= numHours; i++){
                            for(int j=1; j <= numDays; j++){
                                // professor has the same school class, in this lesson sender is free and
                                // the day is not sender's free day
                                if(j != senderFreeDay && timeProf.getEntry(i,j) == schoolClass &&
                                        timeSender.getEntry(i,j) == null){
                                    lessons.add(new Tuple<>(professor, new Lesson(i,j)));
                                }
                            }
                        }
                    }
                    iterator = lessons.iterator();
                case 1:
                    // look for a professor and a lesson to swap until the list is empty
                    if(iterator.hasNext()){
                        // get feasible proposal
                        Tuple<AID,Lesson> element = iterator.next();
                        substitute = element.getFirstElement();
                        proposedChange = element.getSecondElement();
                        // preparation of the message
                        ACLMessage proposalMsg = new ACLMessage(ACLMessage.REQUEST);
                        // set content language and ontology
                        proposalMsg.setLanguage(codec.getName());
                        proposalMsg.setOntology(ontology.getName());
                        // add receiver
                        proposalMsg.addReceiver(substitute);
                        // ontology management
                        ContentElementList cel = new ContentElementList();
                        Substitution substitution = new Substitution();
                        substitution.setProposedLesson(proposedLesson);
                        substitution.setCurrentLesson(proposedChange);
                        cel.add(substitution);
                        try {
                            cm.fillContent(proposalMsg, cel);
                        } catch (Codec.CodecException | OntologyException e) {
                            e.printStackTrace();
                        }
                        // conversation ID to be able to detect reply message
                        proposalMsg.setConversationId(conversationID);
                        myAgent.send(proposalMsg);
                        step = 2;
                    }
                    else{
                        // there are no professors to propose a change, proposal fails
                        ACLMessage reply = new ACLMessage(ACLMessage.REFUSE);
                        reply.addReceiver(sender);
                        reply.setConversationId(conversationID);
                        myAgent.send(reply);
                        step = 4;
                    }
                    break;
                case 2:
                    // wait for the reply of the other professor
                    ACLMessage msg = myAgent.receive(mt);
                    if(msg != null){
                        if(msg.getPerformative() == ACLMessage.AGREE) {
                            ACLMessage reply = new ACLMessage(ACLMessage.PROPOSE);
                            // set content language and ontology
                            reply.setLanguage(codec.getName());
                            reply.setOntology(ontology.getName());
                            // feedback of sender is needed because proposedChange could be one of its preferences
                            ContentElementList cel = new ContentElementList();
                            Change change = new Change();
                            change.setLessonChange(proposedChange);
                            cel.add(change);
                            try {
                                cm.fillContent(reply, cel);
                            } catch (Codec.CodecException | OntologyException e) {
                                e.printStackTrace();
                            }
                            reply.addReceiver(sender);
                            reply.setConversationId(conversationID);
                            myAgent.send(reply);
                            step = 3;
                        }else if(msg.getPerformative() == ACLMessage.REFUSE){
                            step = 1;
                        }
                    }
                    else
                        block();
                    break;
                case 3:
                    // the sender could have accepted or not
                    ACLMessage informMessage = myAgent.receive(mt);
                    if(informMessage != null){
                        ACLMessage informReply;
                        if(informMessage.getPerformative() == ACLMessage.ACCEPT_PROPOSAL){
                            informReply = new ACLMessage(ACLMessage.CONFIRM);
                            // update timetables of the two professors
                            // update timetable sender
                            timeSender.setEntry(
                                    proposedChange.getHour(),
                                    proposedChange.getDay(),
                                    schoolClass
                            );
                            // update timetable other professor
                            timetables.get(substitute).setEntry(
                                    proposedLesson.getHour(),
                                    proposedLesson.getDay(),
                                    schoolClass
                            );
                            step = 4;
                        }else {
                            informReply = new ACLMessage(ACLMessage.DISCONFIRM);
                            step = 1;
                        }
                        informReply.addReceiver(substitute);
                        informReply.setConversationId(conversationID);
                        myAgent.send(informReply);
                    }else
                        block();
            }
        }

        @Override
        public boolean done(){
            return step == 4;
        }
    }

}
