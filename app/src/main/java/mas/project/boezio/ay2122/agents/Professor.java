package mas.project.boezio.ay2122.agents;

import jade.content.ContentElement;
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
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import mas.project.boezio.ay2122.ontology.*;
import mas.project.boezio.ay2122.utils.Timetable;
import mas.project.boezio.ay2122.utils.Utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Professor extends Agent {

    // name of the professor
    protected String name = setName();
    private final Timetable timetable = new Timetable(Utils.NUM_HOURS, Utils.NUM_DAYS);
    // classes where the agent teaches
    private final Set<SchoolClass> classes = new HashSet<>();
    protected Set<Lesson> preferences = generatePreferences();
    private final Set<Lesson> notSatisfiedPref = new HashSet<>();
    private final Set<Lesson> lockedPreferences = new HashSet<>();
    // ontology information
    private final Codec codec = new SLCodec();
    private final Ontology ontology = SchoolTimetableOntology.getInstance();
    // content manager to deal with ontology
    private final ContentManager cm = getContentManager();

    // num trials to launch a negotiation
    private int numTrials = 3;
    // autoincrement idNumber for conversations
    private static int idNumber = 1;
    // set name of a specific professor
    abstract protected String setName();
    // method to initialize preferences for each professor
    abstract protected Set<Lesson> generatePreferences();

    protected void setup(){

        Utils.printMessage(this, "Hi everyone, I'm Professor "+name);

        Utils.registerOntology(cm, codec, ontology);

        // agent's behaviour
        addBehaviour(new TimetableBehaviour());
        addBehaviour(new CandidateBehaviour());

    }

    private class TimetableBehaviour extends Behaviour{

        private final MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        private boolean received = false;

        @Override
        public void action() {
            // detect message about own timetable
            ACLMessage msg = receive(mt);
            if(msg != null){
                Utils.printMessage(myAgent, "I've received my timetable");
                received = true;
                // extract timetable and save it
                try {
                    UpdateTimetable update = (UpdateTimetable) cm.extractContent(msg);
                    TimetableConcept timeConcept = update.getTimetable();
                    jade.util.leap.List teachings = timeConcept.getTeachings();
                    int numTeachings = teachings.size();
                    for(int i=0; i < numTeachings; i++){
                        Teaching teaching = (Teaching) teachings.get(i);
                        Lesson lesson = teaching.getLesson();
                        SchoolClass schoolClass = teaching.getSchoolClass();
                        // update list of own classes
                        classes.add(schoolClass);
                        timetable.setEntry(lesson.getHour(), lesson.getDay(), schoolClass);
                    }
                    // register own classes as a service
                    for(SchoolClass schoolClass: classes){
                        Utils.registerService(myAgent, schoolClass.toString());
                    }
                    // check satisfied preferences and execute PreferenceBehaviour for unsatisfied ones
                    for(Lesson pref: preferences){
                        if(timetable.getEntry(pref.getHour(), pref.getDay()) != null)
                            notSatisfiedPref.add(pref);
                    }
                    addBehaviour(new PreferenceBehaviour(myAgent));
                } catch (Codec.CodecException | OntologyException e) {
                    e.printStackTrace();
                }
            }else {
                Utils.printMessage(myAgent, "I'm waiting for my timetable");
                block();
            }
        }

        @Override
        public boolean done() {
            return received;
        }
    }

    private class PreferenceBehaviour extends TickerBehaviour{

        private PreferenceBehaviour(Agent a) {
            super(a, 15000);
        }

        @Override
        protected void onTick() {
            if(numTrials > 0){
                Utils.printMessage(
                        myAgent,
                        "I'm trying to satisfy my preferences"
                );
                for(Lesson pref: notSatisfiedPref){
                    addBehaviour(new NegotiationBehaviour(pref));
                }
                numTrials--;
            }else
                myAgent.removeBehaviour(this);
        }
    }

    private class NegotiationBehaviour extends Behaviour{

        private int step = 0;

        // preference to satisfy
        private final Lesson pref;
        // timeScheduler address
        private AID timeScheduler;
        private final String conversationID = String.valueOf(idNumber++);
        private final MessageTemplate mt = MessageTemplate.MatchConversationId(conversationID);

        public NegotiationBehaviour(Lesson pref){
            this.pref = pref;
        }

        @Override
        public void action() {

            switch (step) {
                case 0 -> {
                    // obtain the AID of the timeScheduler
                    timeScheduler = Utils.getServiceProviders(myAgent, TimeScheduler.SERVICE).get(0);
                    // send a message with a proposal
                    ACLMessage proposalMsg = new ACLMessage(ACLMessage.CFP);
                    proposalMsg.addReceiver(timeScheduler);
                    // set conversation ID
                    proposalMsg.setConversationId(conversationID);
                    // set language and ontology
                    proposalMsg.setLanguage(codec.getName());
                    proposalMsg.setOntology(ontology.getName());
                    // set content
                    ContentElementList cel = new ContentElementList();
                    Change change = new Change();
                    change.setLessonChange(pref);
                    cel.add(change);
                    try {
                        cm.fillContent(proposalMsg, cel);
                    } catch (Codec.CodecException | OntologyException e) {
                        e.printStackTrace();
                    }
                    Utils.printMessage(myAgent, "I've started a negotiation for preference "+pref);
                    myAgent.send(proposalMsg);
                    // lock the preference for CandidateBehaviour
                    lockedPreferences.add(pref);
                    step = 1;
                }
                case 1 -> {
                    // if another professor proposes a change, the agent must check whether the change
                    // does not damage its own preferences
                    ACLMessage msg = myAgent.receive(mt);
                    if (msg != null) {
                        if (msg.getPerformative() == ACLMessage.PROPOSE) {
                            // extract the proposed lesson by the other professor
                            try {
                                Change proposal = (Change) cm.extractContent(msg);
                                Lesson proposedLesson = proposal.getLessonChange();
                                Utils.printMessage(
                                        myAgent,
                                        "Someone's proposed me to change my "+pref+" with "+proposedLesson
                                );
                                ACLMessage reply;
                                // outcome message
                                String outcome;
                                // the change does not damage own preferences
                                if (!preferences.contains(proposedLesson)) {
                                    // change own timetable
                                    SchoolClass schoolClass = timetable.getEntry(pref.getHour(), pref.getDay());
                                    timetable.setEntry(pref.getHour(), pref.getDay(), null);
                                    timetable.setEntry(
                                            proposedLesson.getHour(),
                                            proposedLesson.getDay(),
                                            schoolClass
                                    );
                                    // remove preference from notSatisfiedPref
                                    notSatisfiedPref.remove(pref);
                                    // remove preference from locked preferences
                                    lockedPreferences.remove(pref);
                                    // send accept message
                                    reply = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                                    outcome = "accepted";
                                    step = 2;
                                } else {
                                    reply = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
                                    outcome = "refused";
                                }
                                reply.addReceiver(timeScheduler);
                                reply.setConversationId(conversationID);
                                Utils.printMessage(
                                        myAgent,
                                        "I've "+outcome+" to change my "+pref+" for "+proposedLesson
                                );
                                myAgent.send(reply);
                            } catch (Codec.CodecException | OntologyException e) {
                                e.printStackTrace();
                            }

                        } else if (msg.getPerformative() == ACLMessage.REFUSE) {
                            Utils.printMessage(myAgent, "My preference "+pref+" was not satisfied");
                            // lesson is again available
                            lockedPreferences.remove(pref);
                            step = 2;
                        }
                    } else
                        block();
                }
            }
        }

        @Override
        public boolean done() {
            return step == 2;
        }
    }

    private class CandidateBehaviour extends CyclicBehaviour{

        private int step = 0;
        private ACLMessage msg;
        private MessageTemplate mt;
        private String conversationID;
        private Lesson proposedLesson;
        private Lesson currentLesson;

        @Override
        public void action() {

            switch (step) {
                case 0 -> {
                    // receive a message which contains a change proposal
                    mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
                    msg = myAgent.receive(mt);
                    if (msg != null) {
                        conversationID = msg.getConversationId();
                        ACLMessage reply = msg.createReply();
                        reply.setConversationId(conversationID);
                        // extract lessons
                        try {
                            Substitution substitution = (Substitution) cm.extractContent(msg);
                            proposedLesson = substitution.getProposedLesson();
                            currentLesson = substitution.getCurrentLesson();
                            Utils.printMessage(
                                    myAgent,
                                    "Someone wants to change "+proposedLesson+" with my "+currentLesson
                            );
                            // to accept the proposal the proposed change must not be in the preferences and
                            // the lesson to change must not be involved in another negotiation
                            // Outcome message
                            String outcome;
                            if (!lockedPreferences.contains(currentLesson)
                                    && !preferences.contains(proposedLesson)) {
                                reply.setPerformative(ACLMessage.AGREE);
                                step = 1;
                                outcome = "accepted";
                            } else {
                                reply.setPerformative(ACLMessage.REFUSE);
                                outcome = "refused";
                            }
                            Utils.printMessage(
                                    myAgent,
                                    "I've "+outcome+" to change "+currentLesson+" for "+proposedLesson
                            );
                        } catch (Codec.CodecException | OntologyException e) {
                            e.printStackTrace();
                        }
                        myAgent.send(reply);
                    } else {
                        Utils.printMessage(myAgent, "I'm waiting for a change proposal");
                        block();
                    }
                }
                case 1 -> {
                    // confirm or not about the change
                    mt = MessageTemplate.MatchConversationId(conversationID);
                    msg = myAgent.receive(mt);
                    if (msg != null) {
                        int performative = msg.getPerformative();
                        // outcome message for output
                        String outcome;
                        if (performative == ACLMessage.CONFIRM) {
                            // the change succeeded
                            // update own timetable
                            SchoolClass schoolClass = timetable.getEntry(
                                    currentLesson.getHour(), currentLesson.getDay()
                            );
                            timetable.setEntry(currentLesson.getHour(), currentLesson.getDay(), null);
                            timetable.setEntry(proposedLesson.getHour(), proposedLesson.getDay(), schoolClass);
                            outcome = "confirmed";
                        }else {
                            outcome = "disconfirmed";
                        }
                        Utils.printMessage(
                                myAgent,
                                "Someone's "+outcome+" the change of my "+currentLesson+" for "+proposedLesson
                        );
                        step = 0;
                    } else {
                        Utils.printMessage(myAgent, "I'm waiting for a confirm or disconfirm message");
                        block();
                    }
                }
            }
        }
    }


}
