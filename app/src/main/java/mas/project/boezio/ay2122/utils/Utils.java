package mas.project.boezio.ay2122.utils;

import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import mas.project.boezio.ay2122.ontology.SchoolClass;

import java.util.*;

public class Utils {

    public static final int NUM_HOURS = 5;
    public static final int NUM_DAYS = 5;

    public static final String CP_THEORY =
                        """
                        problem(P111, P112, P113, P114, P115, P121, P122, P123, P124, P125, P131, P132, P134, P135, P141, P142, P143, P144, P145, P151, P152, P153, P154, P155, P211, P212, P213, P214, P215, P221, P222, P223, P224, P225, P231, P232, P234, P235, P241, P242, P243, P244, P245, P251, P252, P253, P254, P255, P311, P312, P313, P314, P315, P321, P322, P323, P324, P325, P331, P332, P334, P335, P341, P342, P343, P344, P345, P351, P352, P353, P354, P355, P411, P412, P413, P414, P415, P421, P422, P423, P424, P425, P431, P432, P434, P435, P441, P442, P443, P444, P445, P451, P452, P453, P454, P455):-
                            % Domain of the variables (number of classes + 0 for no class)
                            ins([P111, P112, P113, P114, P115, P121, P122, P123, P124, P125, P131, P132, P134, P135, P141, P142, P143, P144, P145, P151, P152, P153, P154, P155],'..'(0,2)),
                            [P211, P212, P213, P214, P215, P221, P222, P223, P224, P225, P231, P232, P234, P235, P241, P242, P243, P244, P245, P251, P252, P253, P254, P255], '..'(0,2)),
                            [P311, P312, P313, P314, P315, P321, P322, P323, P324, P325, P331, P332, P334, P335, P341, P342, P343, P344, P345, P351, P352, P353, P354, P355], '..'(0,2),
                            [P411, P412, P413, P414, P415, P421, P422, P423, P424, P425, P431, P432, P434, P435, P441, P442, P443, P444, P445, P451, P452, P453, P454, P455], '..'(0,2),
                            % Each professor must teach in each class for a specif number of hours
                            global_cardinality([P111, P112, P113, P114, P115, P121, P122, P123, P124, P125, P131, P132, P134, P135, P141, P142, P143, P144, P145, P151, P152, P153, P154, P155], ['-'(1,7),'-'(2,7),'-'(0,11)]),
                            global_cardinality([P211, P212, P213, P214, P215, P221, P222, P223, P224, P225, P231, P232, P234, P235, P241, P242, P243, P244, P245, P251, P252, P253, P254, P255], ['-'(1,6),'-'(2,8),'-'(0,11)]),
                            global_cardinality([P311, P312, P313, P314, P315, P321, P322, P323, P324, P325, P331, P332, P334, P335, P341, P342, P343, P344, P345, P351, P352, P353, P354, P355], ['-'(1,5),'-'(2,7),'-'(0,13)]),
                            global_cardinality([P411, P412, P413, P414, P415, P421, P422, P423, P424, P425, P431, P432, P434, P435, P441, P442, P443, P444, P445, P451, P452, P453, P454, P455], ['-'(1,7),'-'(2,3),'-'(0,15)]),
                            % No more than one professor for each class
                            all_distinct_except_0([P111,P211,P311,P411]),
                            all_distinct_except_0([P112,P212,P312,P412]),
                            all_distinct_except_0([P113,P213,P313,P413]),
                            all_distinct_except_0([P114,P214,P314,P414]),
                            all_distinct_except_0([P115,P215,P315,P415]),
                            all_distinct_except_0([P121,P221,P321,P421]),
                            all_distinct_except_0([P122,P222,P322,P422]),
                            all_distinct_except_0([P123,P223,P323,P423]),
                            all_distinct_except_0([P124,P224,P324,P424]),
                            all_distinct_except_0([P125,P225,P325,P425]),
                            all_distinct_except_0([P131,P231,P331,P431]),
                            all_distinct_except_0([P132,P232,P332,P432]),
                            all_distinct_except_0([P133,P233,P333,P433]),
                            all_distinct_except_0([P134,P234,P334,P434]),
                            all_distinct_except_0([P135,P235,P335,P435]),
                            all_distinct_except_0([P141,P241,P341,P441]),
                            all_distinct_except_0([P142,P242,P342,P442]),
                            all_distinct_except_0([P143,P243,P343,P443]),
                            all_distinct_except_0([P144,P244,P344,P444]),
                            all_distinct_except_0([P145,P245,P345,P445]),
                            all_distinct_except_0([P151,P251,P351,P451]),
                            all_distinct_except_0([P152,P252,P352,P452]),
                            all_distinct_except_0([P153,P253,P353,P453]),
                            all_distinct_except_0([P154,P254,P354,P454]),
                            all_distinct_except_0([P155,P255,P355,P455]),
                            % Each professor must have a free day
                            tuples_in([[P111, P121, P131, P141, P151]],[[0,0,0,0,0]]) #\\/ tuples_in([[P112, P122, P132, P142, P152]],[[0,0,0,0,0]]) #\\/ tuples_in([[P113, P123, P143, P153]],[[0,0,0,0,0]]) #\\/ tuples_in([[P114, P124, P134, P144, P154]],[[0,0,0,0,0]]) #\\/ tuples_in([[P115, P125, P135, P145, P155]],[[0,0,0,0,0]]),
                            tuples_in([[P211, P221, P231, P241, P251]],[[0,0,0,0,0]]) #\\/ tuples_in([[P212, P222, P232, P242, P252]],[[0,0,0,0,0]]) #\\/ tuples_in([[P213, P223, P243, P253]],[[0,0,0,0,0]]) #\\/ tuples_in([[P214, P224, P234, P244, P254]],[[0,0,0,0,0]]) #\\/ tuples_in([[P215, P225, P235, P245, P255]],[[0,0,0,0,0]]),
                            tuples_in([[P311, P321, P331, P341, P351]],[[0,0,0,0,0]]) #\\/ tuples_in([[P312, P322, P332, P342, P352]],[[0,0,0,0,0]]) #\\/ tuples_in([[P313, P323, P343, P353]],[[0,0,0,0,0]]) #\\/ tuples_in([[P314, P324, P334, P344, P354]],[[0,0,0,0,0]]) #\\/ tuples_in([[P315, P325, P335, P345, P355]],[[0,0,0,0,0]]),
                            tuples_in([[P411, P421, P431, P441, P451]],[[0,0,0,0,0]]) #\\/ tuples_in([[P412, P422, P432, P442, P452]],[[0,0,0,0,0]]) #\\/ tuples_in([[P413, P423, P443, P453]],[[0,0,0,0,0]]) #\\/ tuples_in([[P414, P424, P434, P444, P454]],[[0,0,0,0,0]]) #\\/ tuples_in([[P415, P425, P435, P445, P455]],[[0,0,0,0,0]]).
                        """;

    public static final String CP_GOAL = "problem(P111, P112, P113, P114, P115, P121, P122, P123, P124, P125, P131, P132, P134, P135, P141, P142, P143, P144, P145, P151, P152, P153, P154, P155, P211, P212, P213, P214, P215, P221, P222, P223, P224, P225, P231, P232, P234, P235, P241, P242, P243, P244, P245, P251, P252, P253, P254, P255, P311, P312, P313, P314, P315, P321, P322, P323, P324, P325, P331, P332, P334, P335, P341, P342, P343, P344, P345, P351, P352, P353, P354, P355, P411, P412, P413, P414, P415, P421, P422, P423, P424, P425, P431, P432, P434, P435, P441, P442, P443, P444, P445, P451, P452, P453, P454, P455), label([P111, P112, P113, P114, P115, P121, P122, P123, P124, P125, P131, P132, P134, P135, P141, P142, P143, P144, P145, P151, P152, P153, P154, P155, P211, P212, P213, P214, P215, P221, P222, P223, P224, P225, P231, P232, P234, P235, P241, P242, P243, P244, P245, P251, P252, P253, P254, P255, P311, P312, P313, P314, P315, P321, P322, P323, P324, P325, P331, P332, P334, P335, P341, P342, P343, P344, P345, P351, P352, P353, P354, P355, P411, P412, P413, P414, P415, P421, P422, P423, P424, P425, P431, P432, P434, P435, P441, P442, P443, P444, P445, P451, P452, P453, P454, P455])";

    public static final Map<Integer, SchoolClass> classesMap = new HashMap<>()
    {
        {
            put(0, null);
            put(1, new SchoolClass(1,"A"));
            put(2, new SchoolClass(2, "A"));
        }
    };

    public static Map<AID, Map<SchoolClass,Integer>> initializeHours(){
        final int NUM_PROFESSORS = 4;
        final int NUM_CLASSES = 2;
        // this information is provided by the school
        Map<AID, Map<SchoolClass,Integer>> hoursMap = new HashMap<>();
        // hours for each professor in each class
        List<Map<SchoolClass,Integer>> professorsMap = new ArrayList<>();
        for(int i = 1; i <= NUM_PROFESSORS; i++){
            professorsMap.add(new HashMap<>());
        }
        // hours for each professor in each class
        int[][] hours = {{7, 6, 5, 7}, {7, 8, 7, 3}};
        // School classes
        SchoolClass[] classes = new SchoolClass[2];
        classes[0] = new SchoolClass(1,"A");
        classes[1] = new SchoolClass(2, "A");
        // hours of each professor
        for(int i = 0; i < NUM_CLASSES; i++){
            for(int j = 0; j < NUM_PROFESSORS; j++){
                professorsMap.get(j).put(classes[i], hours[i][j]);
            }
        }
        for(int i = 0; i < NUM_PROFESSORS; i++){
            hoursMap.put(new AID("professor" + i, AID.ISLOCALNAME), professorsMap.get(i));
        }

        return hoursMap;
    }

    public static void registerOntology(ContentManager cm, Codec codec, Ontology ontology){
        cm.registerLanguage(codec);
        cm.registerOntology(ontology);
    }

    public static void registerService(Agent agent, String typeService) {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(agent.getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(typeService);
        sd.setName(agent.getLocalName()+"-"+typeService);
        dfd.addServices(sd);
        try {
            DFService.register(agent, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    public static List<AID> getServiceProviders(Agent agent, String type){
        List<AID> providers = new ArrayList<>();
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(type);
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(agent,template);
            for (DFAgentDescription dfAgentDescription : result) {
                providers.add(dfAgentDescription.getName());
            }
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
        return providers;
    }

    public static void printMessage(Agent agent, String message){
        String agentAID = agent.getAID().getName();
        System.out.println("["+agentAID+"]: " + message);
    }

}
