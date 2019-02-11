package cs455.overlay.node;
import java.util.*;


public class HWONE{
    private static int numVertices = 6;
    static int total = 0;
    int cnt = 0;
    int cnt2 = 0;

    public static void main(String[] args) {
        if (args.length == 1)
            numVertices = Integer.parseInt(args[0]);
        else {
            numVertices = 6;
        }
        HWONE game = new HWONE();
        game.run();
        //creates a root node with one move made

    }

    private void run(){
        line[] data = new line[15];
        data[0] = new line((byte)0,(byte)1, (byte)1);
        Node root = new Node(data, (byte) 2);

        int desiredPlayer = 0;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Would you like to see if play A or B can win? or both(C)");
        String answer = String.valueOf(scanner.next().charAt(0));
        if(answer.toUpperCase().equals("A")){
            desiredPlayer =1;
        }
        if(answer.toUpperCase().equals("B")){
            desiredPlayer =2;
            System.out.println("Youve selected B, this one might take a while but if your memory permits\n I should beable to find you an answer");
        }
        if(answer.toUpperCase().equals("C")){
            System.out.println("Youve selected C, this one might take a while but if your memory permits\n I should beable to find you an answer");
        }
        System.out.println();
        System.out.println("Searching for " + answer.toUpperCase() +"'s winning strategy");

        if(answer.toUpperCase() == "C"){
            BreadthFirstSearch bfs = new BreadthFirstSearch(root, 1);
            bfs.compute();
            bfs = new BreadthFirstSearch(root, 2);
            bfs.compute();
        }
        BreadthFirstSearch bfs = new BreadthFirstSearch(root, desiredPlayer);
        bfs.compute();


    }

    //BFS MECHANISM, CALLS CHILD NODE GENERATOR IN NODE CLASS
    public class BreadthFirstSearch {
        Node startNode;
        Node goalNode;
        byte desiredPlayer;
        byte notWanted;

        public BreadthFirstSearch(Node start, int player){
            this.startNode = start;
            this.goalNode = null;
            this.desiredPlayer = (byte)player;
            if(player == 1)
                notWanted = (byte)2;
            else notWanted = (byte)1;
        }

        public boolean compute(){

            Queue<Node> queue = new LinkedList<>();
            ArrayList<Node> explored = new ArrayList<>();
            queue.add(this.startNode);
            explored.add(startNode);

            while(!queue.isEmpty()){

                if(total % 10000 == 0) {
                    System.out.println("    I've looked at " + total + " options so far");
                }
                Node current = queue.remove();
                if(notOver(current) == desiredPlayer) {
                    char res = ' ';
                    if(desiredPlayer == 1)
                        res = 'A';
                    if(desiredPlayer == 2)
                        res = 'B';
                    System.out.println();
                    System.out.println(current.ts());
                    System.out.println("This is the winning strategy for player " + res);
                    //System.out.println(explored);
                    System.exit(1);
                }
                else{
                    int skip = 0;
                    ArrayList<Node> move = current.getChildren(desiredPlayer);
                    for(int i = 0; i < move.size(); i++){
                        if(notOver(move.get(i)) == notWanted)
                            skip = 1;
                    }
                    if(skip == 0)
                        queue.addAll(move);
                }{}
                explored.add(current);
            }

            return false;

        }

    }

    //GAMEBOARD (AND CHILDREN)
    private class Node{
        line[] moves = new line[15];
        Node[] children = new Node[15];
        byte player;

        //CONSTRUCTOR
        private Node(line[] moves, byte player){
            this.moves = moves.clone();
            this.player = player;
        }

        //TO STRING METHOD
        private String ts(){
            String string = "";
            for (int x = 0; x < 15; x++){
                if(this.moves[x] != null) {
                    string += this.moves[x].ts();
                }
            }
            return string;
        }

        //CREATES ALL THE NODE KIDDIES! :D
        public ArrayList<Node> getChildren(byte desPlayer){
            ArrayList<Node> childNodes = new ArrayList<>();
            line[] tested = this.moves.clone();
            line[] nn = this.moves.clone();
            byte desiredPlayer = desPlayer;
            byte notWanted;
            if(desiredPlayer == 1)
                notWanted = (byte)2;
            else notWanted = (byte)1;
            int BStoppedA = 0;

            //B will obstruct A
            if(tested[getNumberOfMOves(this)-1].getColor() == desiredPlayer) {
                for (byte v1 = 0; v1 < numVertices; v1++) {
                    for (byte v2 = 0; v2 < numVertices; v2++) {
                        if ((v1 != v2) && (!contains(tested, (new line(v1, v2, desiredPlayer))))) {
                            nn[getNumberOfMOves(this)] = new line(v1, v2, desiredPlayer);
                            Node temp = new Node(nn, desiredPlayer);
                            if (notOver(temp) == desiredPlayer) {
                                nn[getNumberOfMOves(this)] = new line(v1, v2, notWanted);
                                temp = new Node(nn, notWanted);
                                childNodes.add(new Node(temp.moves, notWanted));
                                BStoppedA = 1;
                                total++;
                            }
                        }
                    }
                }
            }
            if(BStoppedA == 0){
                //cycles through all possible lines
                for (byte v1 = 0; v1 < numVertices; v1++) {
                    for (byte v2 = 0; v2 < numVertices; v2++) {

                        //If the move is available
                        if ((v1 != v2) && (!contains(tested, (new line(v1, v2, (byte)1))))) {

                            //adds move to history
                            for (int x = 0; x < 15; x++) {
                                if (tested[x] == null) {
                                    tested[x] = new line(v1, v2, player);
                                    x = 16;
                                }
                            }
                            byte newTurn;
                            if (this.player == 1) {
                                newTurn = 2;
                            } else {
                                newTurn = 1;
                            }
                            //adds the child
                            nn = this.moves.clone();
                            nn[getNumberOfMOves(this)] = new line(v1, v2, player);
                            childNodes.add(new Node(nn, newTurn));
                            total++;
                        }
                    }
                }
            }
            return childNodes;
        }
    }

    //SINGLE MOVE
    private class line{
        private byte v1; //first vertex
        private byte v2; //second vertex
        private byte color = (byte)0; //0(null), 1(blue), or 2(red)

        private line(byte v1, byte v2, byte color) {
            this.v1 = v1;
            this.v2 = v2;
            this.color = color;
        }

        //GETTERS
        private byte getV1() { return v1; }
        private byte getV2() { return v2; }
        private byte getColor() { return color; }

        private boolean connects(line line){
            if ((this.v1 == line.v2) || (this.v1 == line.v1)
                || (this.v2 == line.v1) || (this.v2 == line.v2)
                && (this.getColor() == line.getColor())) {
                return true;
            }
            return false;
        }

        //TOSTRING
        private  String ts( ){
            String ret = "(" + this.getV1() + ", " + this.getV2() + ", " + this.getColor() + ")";
            return ret;
        }

        public void isNow(line o){
            this.v1 = o.v1;
            this.v2 = o.v2;
            this.color = o.v2;
        }
        //COMPARE
        public boolean equal(line o){
            if (o.getColor() == this.getColor()
                && (o.getV1() == this.getV1())
                && (o.getV2() == this.getV2()))
                return true;
            return false;
        }
    }

    //CHECK FOR ENDGAME
    private byte notOver(Node node){
        for (int vect1 = 0; vect1 < getNumberOfMOves(node); vect1++) {
            for (int vect2 = 0; vect2 < getNumberOfMOves(node); vect2++) {
                if((node.moves[vect1].getColor() == node.moves[vect2].getColor())){
                    //if two connecting lineS are found it looks for a third
                    for (int vect3 = 0; vect3 < getNumberOfMOves(node); vect3++) {
                        //if not overlapping with other loops
                        if(vect1 != vect2 && vect2 != vect3 && vect3 != vect1) {
                            if ((node.moves[vect1].getColor() == node.moves[vect3].getColor())) {

                                //If this string is comprised of 3 pairs of three unique digits
                                //than damn done just ropped ourselves a triangle yeeha
                                String connects = Byte.toString(node.moves[vect1].v1) + Byte.toString(node.moves[vect1].v2)
                                    + Byte.toString(node.moves[vect2].v1) + Byte.toString(node.moves[vect2].v2)
                                    + Byte.toString(node.moves[vect3].v1) + Byte.toString(node.moves[vect3].v2);

                                if (connects.chars().distinct().count() == 3) {
                                    //System.out.println(connects);
                                    if (node.moves[vect1].getColor() == 1) {
                                        cnt++;
                                        //System.out.println("1 " + node.ts());
                                        return 1;
                                    }
                                    if (node.moves[vect1].getColor() == 2) {
                                        cnt2++;
                                        //System.out.println("2 " + node.ts());
                                        return 2;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return 0;
    }

    //GETS # OF CURRENT MOVES IN A NODES BOARD
    private int getNumberOfMOves(Node node){
        int count = 0;
        for (int x = 0; x < 15; x++) {
            if (node.moves[x] != null)
                count++;
        }
        return count;
    }

    //checks to see if a line is present within a line[]
    private boolean contains(line[] data, line line){
        for (int x = 0; x < 15; x++) {
            if (data[x] != null) {
                if (   ((data[x].getV1() == line.getV1())
                    || (data[x].getV1() == line.getV2()))
                    && ((data[x].getV2() == line.getV1())
                    || (data[x].getV2() == line.getV2()))) {
                    return true;
                }
            }
        }
        return false;
    }
}