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

    private final Timetable timetable = new Timetable(Utils.NUM_HOURS, Utils.NUM_DAYS);
    // classes where the agent teaches
    private final Set<SchoolClass> classes = new HashSet<>();
    protected Set<Lesson> preferences;
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

    protected void setup(){

        Utils.registerOntology(cm, codec, ontology);

        // agent's behaviour
        addBehaviour(new TimetableBehaviour());
        addBehaviour(new CandidateBehaviour());

    }

    private class TimetableBehaviour extends OneShotBehaviour{

        private final MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);

        @Override
        public void action() {
            // detect message about own timetable
            ACLMessage msg = receive(mt);
            if(msg != null){
                // extract timetable and save it
                try {
                    TimetableConcept timeConcept = (TimetableConcept) cm.extractContent(msg);
                    List<Teaching> teachings = timeConcept.getTeachings();
                    for(Teaching teaching: teachings){
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
                    addBehaviour(new PreferenceBehaviour(myAgent, 1500));
                } catch (Codec.CodecException | OntologyException e) {
                    e.printStackTrace();
                }
            }else
                block();
        }
    }

    private class PreferenceBehaviour extends TickerBehaviour{

        public PreferenceBehaviour(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            if(numTrials > 0){
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

            switch(step){
                case 0:
                    // obtain the AID of the timeScheduler
                    timeScheduler = Utils.getServiceProviders(myAgent, TimeScheduler.SERVICE).get(0);
                    // send a message with a proposal
                    ACLMessage proposalMsg = new ACLMessage(ACLMessage.CFP);
                    proposalMsg.addReceiver(timeScheduler);
                    // set conversation ID
                    proposalMsg.setConversationId(conversationID);
                    // set content
                    ContentElementList cel = new ContentElementList();
                    cel.add((ContentElement) pref);
                    try {
                        cm.fillContent(proposalMsg, cel);
                    } catch (Codec.CodecException | OntologyException e) {
                        e.printStackTrace();
                    }
                    myAgent.send(proposalMsg);
                    // lock the preference for CandidateBehaviour
                    lockedPreferences.add(pref);
                    step = 1;
                    break;
                case 1:
                    // if another professor proposes a change, the agent must check whether the change
                    // does not damage its own preferences
                    ACLMessage msg = myAgent.receive(mt);
                    if(msg != null){
                        if(msg.getPerformative() == ACLMessage.PROPOSE){
                            // extract the proposed lesson by the other professor
                            try {
                                Lesson proposedLesson = (Lesson) cm.extractContent(msg);
                                ACLMessage reply;
                                // the change does not damage own preferences
                                if(!preferences.contains(proposedLesson)){
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
                                    step = 2;
                                }else{
                                    reply = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
                                }
                                reply.addReceiver(timeScheduler);
                                myAgent.send(reply);
                            } catch (Codec.CodecException | OntologyException e) {
                                e.printStackTrace();
                            }

                        }else if(msg.getPerformative() == ACLMessage.REFUSE){
                            // lesson is again available
                            lockedPreferences.remove(pref);
                            step = 2;
                        }
                    }else
                        block();
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

            switch(step){
                case 0:
                    // receive a message where there is a change proposal
                    mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
                    msg = myAgent.receive(mt);
                    if(msg != null){
                        conversationID = msg.getConversationId();
                        // extract lessons
                        try {
                            Substitution substitution = (Substitution) cm.extractContent(msg);
                            proposedLesson = substitution.getProposedLesson();
                            currentLesson = substitution.getCurrentLesson();
                            // to accept the proposal the proposed change must not be in the preferences and
                            // the lesson to change must not be involved in another negotiation
                            msg = msg.createReply();
                            msg.setConversationId(conversationID);
                            if(!lockedPreferences.contains(currentLesson)
                                    && !preferences.contains(proposedLesson)){
                                msg.setPerformative(ACLMessage.AGREE);
                            }else{
                                msg.setPerformative(ACLMessage.REFUSE);
                            }
                        } catch (Codec.CodecException | OntologyException e) {
                            e.printStackTrace();
                        }
                        myAgent.send(msg);
                        step = 1;
                    }else
                        block();
                    break;
                case 1:
                    // confirm or not about the change
                    mt = MessageTemplate.MatchConversationId(conversationID);
                    msg = receive(mt);
                    if(msg != null){
                        int performative = msg.getPerformative();
                        if(performative == ACLMessage.CONFIRM){
                            // the change succeeded
                            // update own timetable
                            SchoolClass schoolClass = timetable.getEntry(
                                    currentLesson.getHour(), currentLesson.getDay()
                            );
                            timetable.setEntry(currentLesson.getHour(), currentLesson.getDay(), null);
                            timetable.setEntry(proposedLesson.getHour(), proposedLesson.getDay(), schoolClass);
                        }
                    }else
                        block();
                    step = 0;
            }
        }
    }


}
