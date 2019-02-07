package cs455.overlay.wireformats;

import java.util.HashMap;

public class MessagingNodesList {
    //Hash map that houses all the node information
    //Each entry will include SERVER_ADDRESS and PORT
    //Key is the node's SERVER_ADDRESS
    private HashMap NODE_REGISTRY_HASH;

    public MessagingNodesList(){
        NODE_REGISTRY_HASH = new HashMap();
    }

    public String ADD_NODE(String ADDRESS, int PORT){
        String HASHKEY = ADDRESS + PORT;
        if(!NODE_REGISTRY_HASH.containsKey(HASHKEY)){
            NODE_REGISTRY_HASH.put(HASHKEY, new pair(PORT, ADDRESS));
            return "1NEW NODE ENTERED INTO MESSENGER NODE LIST";
        }
        return "0NODE ALREADY REGISTERED NO ACTION TAKEN";
    }
}

class pair {
    int PORT;
    String ADDRESS;
    pair(int v, String w) { this.PORT = v; this.ADDRESS = w;}
}
