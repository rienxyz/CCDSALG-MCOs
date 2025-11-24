import java.util.*;
import java.io.*;

public class SocialGraph {

    private ArrayList<ArrayList<Integer>> adjList;
    private int numAccounts; // Variable 'n' from the spec

    // CONSTRUCTOR
    public SocialGraph() {
        this.adjList = new ArrayList<>();
        this.numAccounts = 0;
    }

    // Part 1 - loading graph
    public void loadData(String filename) {
        try {
            File file = new File(filename);
            Scanner fileScanner = new Scanner(file);

            if (fileScanner.hasNextInt()) {
                numAccounts = fileScanner.nextInt();
                int numFriendships = fileScanner.nextInt();

                // initialize the adjacency list for 'n' users
                // this prevents IndexOutOfBounds errors later
                adjList.clear(); // clear old data if reloading
                for (int i = 0; i < numAccounts; i++) {
                    adjList.add(new ArrayList<>());
                }

                // read the connections [cite: 43]
                for (int i = 0; i < numFriendships; i++) {
                    int u = fileScanner.nextInt();
                    int v = fileScanner.nextInt();

                    // add edge u -> v
                    adjList.get(u).add(v);

                    // add edge v -> u (bi-directional)
                    adjList.get(v).add(u);
                }

                System.out.println("Graph loaded successfully!");
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found. Please check the filename.");
        }
    }

    // Part 2: display friends list
    public void displayFriends(int id) {
        if (id < 0 || id >= numAccounts) {
            System.out.println("Error: Person ID " + id + " does not exist.");
            return;
        }

        ArrayList<Integer> friends = adjList.get(id);

        // Display count
        System.out.println("Person " + id + " has " + friends.size() + " friends!");

        // Display list
        System.out.print("List of friends: ");
        for (int friend : friends) {
            System.out.print(friend + " ");
        }
        System.out.println(); // New line for formatting
    }

    // ---------------------------------------------------
    // Part 3: display connection for pathfinding
    // ---------------------------------------------------
    public void checkConnection(int startId, int endId) {
        // validation
        if (startId < 0 || startId >= numAccounts || endId < 0 || endId >= numAccounts) {
            System.out.println("Error: One or both IDs do not exist.");
            return;
        }

        if (startId == endId) {
            System.out.println("They are the same person!");
            return;
        }

        // setup BFS Structures
        boolean[] visited = new boolean[numAccounts];
        int[] parent = new int[numAccounts]; // to retrace the path
        Arrays.fill(parent, -1); // initialize parents to -1

        Queue<Integer> queue = new LinkedList<>();

        // start BFS
        visited[startId] = true;
        queue.add(startId);
        boolean found = false;

        while (!queue.isEmpty()) {
            int current = queue.poll();

            // stop if we found the target
            if (current == endId) {
                found = true;
                break;
            }

            // check neighbors
            for (int neighbor : adjList.get(current)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    parent[neighbor] = current; // mark who discovered this neighbor
                    queue.add(neighbor);
                }
            }
        }

        // path reconstruction for backtracking
        if (found) {
            System.out.println("There is a connection from " + startId + " to " + endId + "!");

            ArrayList<Integer> path = new ArrayList<>();
            int crawl = endId;
            path.add(crawl);

            while (parent[crawl] != -1) {
                path.add(parent[crawl]);
                crawl = parent[crawl];
            }

            // The path is currently: End -> ... -> Start
            // We want to print: Start -> ... -> End
            Collections.reverse(path);

            // Print format per spec: "A is friends with B"
            for (int i = 0; i < path.size() - 1; i++) {
                System.out.println(path.get(i) + " is friends with " + path.get(i + 1));
            }
        } else {
            System.out.println("Cannot find a connection between " + startId + " and " + endId);
        }
    }

    // main

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        SocialGraph graph = new SocialGraph();
        boolean running = true;

        System.out.print("Input file path: "); // [cite: 122]
        String filename = input.next();
        graph.loadData(filename);

        while (running) {
            System.out.println("\nMAIN MENU"); // [cite: 124]
            System.out.println("[1] Get friend list");
            System.out.println("[2] Get connection");
            System.out.println("[3] Exit");
            System.out.print("Enter your choice: ");

            int choice = 0;
            if (input.hasNextInt())
                choice = input.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter ID of person: ");
                    int id = input.nextInt();
                    graph.displayFriends(id);
                    break;
                case 2:
                    System.out.print("Enter ID of first person: ");
                    int start = input.nextInt();
                    System.out.print("Enter ID of second person: ");
                    int end = input.nextInt();
                    graph.checkConnection(start, end);
                    break;
                case 3:
                    running = false;
                    System.out.println("Exiting program.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
        input.close();
    }
}