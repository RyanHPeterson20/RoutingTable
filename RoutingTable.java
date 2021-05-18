/*
Ryan Peterson
Hw10 - Routing Table
 */

import  java.io.*;
import  java.util.*;

/*RoutingTable builds a routing table for a network router, given a network topology that includes link cost.
this uses dijkstra's algorithm to determine the shortest path between any two nodes on the network, then
builds a routing table based on the shortest path.

Constructor that takes the cost matrix and the number of network nodes as a parameter.
Both parameters of constructor are integers.
 */
class RoutingTable {
    int [][] userMatrix;
    int numNodes;
    int [][] costMatrix;

    List<String> N_prime = new ArrayList<>();
    List<Integer> remainingNodes = new ArrayList<>();
    int removeValue;

    List<String> Y_prime = new ArrayList<>();
    int[] D_i;
    int[] D_k;
    int cost; //c(k,i)
    List<Integer> P = new ArrayList<>();
    List<String> P_output = new ArrayList<>();

    //routing table as hashtable
    HashMap<String, String> routeTable = new HashMap<>();

    //constructor with parameters of the cost matrix and the number of nodes in the network
    RoutingTable(int[][] Matrix, int nodes) {
        userMatrix = Matrix;
        numNodes = nodes;
    }

    //initializes for dijkstra's algorithm, including creating the appropriate cost matrix for the network topology
    private void initializer () {
        //clean up the matrix to only be NxN (where N = numNodes)
        costMatrix = new int[numNodes+1][numNodes+1];
        for (int i = 0; i < costMatrix.length; i++) {
            for (int j = 0; j < costMatrix.length; j++) {
                costMatrix[i][j] = userMatrix[i][j];
            }
        }

        //remaining nodes to be searched (not in N')
        N_prime.add("V0");
        removeValue = 0;

        for (int i =0; i < costMatrix.length; i++) {
            remainingNodes.add(i);
        }
        for (int i : remainingNodes) {
            if (remainingNodes.get(i) == removeValue) {
                remainingNodes.remove(i);
                break;
            }
        }

        D_i = costMatrix[0];

        //update the P vector
        for (int i =0; i < D_i.length; i++) {
            if (D_i[i] == 0 || D_i[i] == 214748364) {
                P.add(-1);
            }
            else {
                P.add(0);
            }
        }
        for (int i = 0; i < P.size(); i++) {
            if (P.get(i) == -1) {
                P_output.add("-");
            }
            else {
                P_output.add("V"+P.get(i));
            }
        }

        //assignment required these outputs
        System.out.println("Initialization");
        System.out.println("N': " + N_prime);
        System.out.println("Y': " + Y_prime);

        System.out.println("D(i): " + Arrays.toString(D_i));
        System.out.println("P: " + P_output + "\r\n");
    }

    private void dijkstrasAlgorithm() {
        initializer();

        //begin loop
        while (N_prime.size() != D_i.length) {
            //find k not in N'
            List<Integer> distanceMinimum = new ArrayList<>();
            int min_index = 0;
            int min = 2147483647;
            for (int i : remainingNodes) {
                if (D_i[i] < min) {
                    min = D_i[i];
                    min_index = i;
                }
            }

            //remove mind_index from remaining nodes list
            for (int i = 0; i < remainingNodes.size(); i++) {
                if (remainingNodes.get(i) == min_index) {
                    remainingNodes.remove(i);
                }
            }

            N_prime.add("V" + min_index);
            String edge = "(V" + P.get(min_index) + "," + "V" + min_index + ")";
            Y_prime.add(edge);

            //create a D(k)
            D_k = costMatrix[min_index];
            cost = D_i[min_index];

            //check D(k) + c(k, i) < D(i)
            for (int i = 0; i < D_i.length; i++) {
                if ((cost + D_k[i]) < D_i[i]) {
                    D_i[i] = D_k[i] + cost;
                    P.set(i, min_index);
                }
            }

            //update the P vector
            for (int i = 0; i < P.size(); i++) {
                if (P.get(i) == -1) {
                    P_output.set(i, "-");
                }
                else {
                    P_output.set(i, "V"+P.get(i));
                }
            }

            //assignment required this output
            //print out update to everything for loop
            System.out.println("After iteration of the loop.");
            System.out.println("N': " + N_prime);
            System.out.println("Y': " + Y_prime);
            System.out.println("D(i): " + Arrays.toString(D_i));
            System.out.println("P: " + P_output + "\r\n");
        }

    }

    public HashMap<String, String> routingTable () {
        dijkstrasAlgorithm();
        for (int i = 1; i < N_prime.size(); i++) {
            int j = i;
            while(P.get(j) != 0) {
                j = P.get(j);
            }
            String desto = "V" + i;
            String firstlink = "(V0 , V" + j + ")";
            routeTable.put(desto, firstlink);
        }
        return routeTable;
    }

}

public class Hw10_Routing {
    public static void main(String[] args) throws IOException{
        //begin with prompt to get number of routers
        String userInput;
        int routers = 0;
        int actualRouters = 0; //counts the actual number of routers in the cost file
        boolean router_validation = true;

        int nodeOne;
        int nodeTwo;
        int link;

        BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in));

        //this only has validation for n >= 2 (non validation on non-integer inputs)
        System.out.print("Please enter number of routers (greater than or equal to 2): ");
        while ((userInput = sysIn.readLine()) != null) {
            routers = Integer.parseInt(userInput);
            if (routers >= 2) {
                break;
            } else {
                System.out.print("Please enter number of routers (greater than or equal to 2): ");
            }
        }

        //assignment required this output
        System.out.println(routers);

        //create cost matrix
        int[][] costMatrix = new int[routers][routers];
        //add  max value "2147483647".
        for (int[] row : costMatrix)
            Arrays.fill(row, 214748364);
        //set diagonal to 0
        for (int i = 0; i < (routers - 1); i++) {
            costMatrix[i][i] = 0;
        }

        //import network topology
        String linkCost;
        BufferedReader networkTopo = new BufferedReader(new FileReader("topo.txt"));//hardcoded .txt file

        int row = 1;
        while  ((linkCost = networkTopo.readLine()) != null) {
            String[] invalidCheck = linkCost.split("\\s+");

            nodeOne = Integer.parseInt(invalidCheck[0]);
            nodeTwo = Integer.parseInt(invalidCheck[1]);
            link = Integer.parseInt(invalidCheck[2]);

            //get the actual count of routers from cost file
            if (nodeOne > actualRouters || nodeTwo > actualRouters) {
                if (nodeOne > nodeTwo) {
                    actualRouters = nodeOne;
                }
                else    {
                    actualRouters = nodeTwo;
                }
            }

            if (nodeOne > (routers-1) || nodeTwo > (routers-1) || link < 1) {
                System.out.print("Row of first invalid number: ");
                System.out.println(row);
                networkTopo.close();

                //get new topo.txt file from user
                System.out.print("Please enter correct cost input file: ");
                userInput = sysIn.readLine();
                BufferedReader newNetworkTopo = new BufferedReader(new FileReader(userInput));
                networkTopo = newNetworkTopo;

                //clear cost matrix/row and try again
                row =1;
                //add max value "214748364".
                for (int[] newrow : costMatrix)
                    Arrays.fill(newrow, 214748364);
                //set diagonal to 0
                for (int i = 0; i < (routers - 1); i++) {
                    costMatrix[i][i] = 0;
                }
            }
            else {
                costMatrix[nodeOne][nodeTwo] = link;
                costMatrix[nodeTwo][nodeOne] = link;
                row ++;
            }
        }
        networkTopo.close();

        RoutingTable shortedPath = new RoutingTable(costMatrix, actualRouters);
        HashMap<String, String> V0_routeTable = new HashMap<>();

        V0_routeTable = shortedPath.routingTable();
        System.out.println("Destination   " + "Link");
        for (String i : V0_routeTable.keySet()) {
            System.out.println(i + "          " + V0_routeTable.get(i));
        }
    }
}
