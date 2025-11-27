import java.util.*;
import java.io.*;

public class SocialGraph {
    private static final int MIN_ACCOUNTS = 1;
    private static final int MAX_ACCOUNTS = 1000;
    
    private ArrayList<ArrayList<Integer>> adjacencyList;
    private int numAccounts;

    public SocialGraph() {
        this.adjacencyList = new ArrayList<>();
        this.numAccounts = 0;
    }

    /**
     * Loads social network data from file and builds the graph
     * @param filename The path to the data file
     * @return true if loading was successful, false otherwise
     */
    public boolean loadData(String filename) {
        try (Scanner fileScanner = new Scanner(new File(filename))) {
            if (!fileScanner.hasNextInt()) {
                System.out.println("Error: Invalid file format.");
                return false;
            }

            numAccounts = fileScanner.nextInt();
            if (numAccounts < MIN_ACCOUNTS || numAccounts > MAX_ACCOUNTS) {
                System.out.println("Error: Number of accounts must be between " + 
                                 MIN_ACCOUNTS + " and " + MAX_ACCOUNTS);
                return false;
            }

            int numFriendships = fileScanner.nextInt();
            if (numFriendships < 0) {
                System.out.println("Error: Number of friendships cannot be negative.");
                return false;
            }

            // Initialize adjacency list
            adjacencyList.clear();
            for (int i = 0; i < numAccounts; i++) {
                adjacencyList.add(new ArrayList<>());
            }

            // Read and validate friendships
            for (int i = 0; i < numFriendships; i++) {
                if (!fileScanner.hasNextInt()) {
                    System.out.println("Error: Insufficient friendship data.");
                    return false;
                }
                
                int u = fileScanner.nextInt();
                int v = fileScanner.nextInt();
                
                if (!isValidAccountId(u) || !isValidAccountId(v)) {
                    System.out.println("Error: Invalid account ID in friendship data: " + u + " " + v);
                    return false;
                }
                
                if (u == v) {
                    System.out.println("Warning: Self-friendship ignored for account " + u);
                    continue;
                }

                // Add bidirectional friendship
                addFriendship(u, v);
            }

            System.out.println("Graph loaded successfully! " + 
                             numAccounts + " accounts, " + numFriendships + " friendships.");
            return true;

        } catch (FileNotFoundException e) {
            System.out.println("Error: File '" + filename + "' not found.");
            return false;
        } catch (Exception e) {
            System.out.println("Error: Unexpected problem reading file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Adds a bidirectional friendship between two accounts
     */
    private void addFriendship(int account1, int account2) {
        if (!adjacencyList.get(account1).contains(account2)) {
            adjacencyList.get(account1).add(account2);
        }
        if (!adjacencyList.get(account2).contains(account1)) {
            adjacencyList.get(account2).add(account1);
        }
    }

    /**
     * Displays all friends of a given account
     */
    public void displayFriends(int accountId) {
        if (!isValidAccountId(accountId)) {
            System.out.println("Error: Account ID " + accountId + " is invalid. Must be between 0 and " + (numAccounts - 1));
            return;
        }

        List<Integer> friends = adjacencyList.get(accountId);
        System.out.println("Account " + accountId + " has " + friends.size() + " friends:");
        
        if (friends.isEmpty()) {
            System.out.println("  No friends found.");
        } else {
            System.out.print("  Friends: ");
            for (int i = 0; i < friends.size(); i++) {
                System.out.print(friends.get(i));
                if (i < friends.size() - 1) System.out.print(", ");
            }
            System.out.println();
        }
    }

    /**
     * Checks and displays connection path between two accounts using BFS
     */
    public void checkConnection(int startId, int endId) {
        if (!isValidAccountId(startId) || !isValidAccountId(endId)) {
            System.out.println("Error: One or both account IDs are invalid.");
            return;
        }

        if (startId == endId) {
            System.out.println("These are the same account (" + startId + ").");
            return;
        }

        List<Integer> path = findShortestPath(startId, endId);
        
        if (path.isEmpty()) {
            System.out.println("No connection found between account " + startId + " and account " + endId + ".");
        } else {
            System.out.println("Connection found between account " + startId + " and account " + endId + ":");
            displayPath(path);
        }
    }

    /**
     * Finds shortest path between two accounts using BFS
     */
    private List<Integer> findShortestPath(int startId, int endId) {
        boolean[] visited = new boolean[numAccounts];
        int[] parent = new int[numAccounts];
        Arrays.fill(parent, -1);

        Queue<Integer> queue = new LinkedList<>();
        visited[startId] = true;
        queue.offer(startId);

        while (!queue.isEmpty()) {
            int current = queue.poll();

            if (current == endId) {
                return reconstructPath(parent, startId, endId);
            }

            for (int neighbor : adjacencyList.get(current)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    parent[neighbor] = current;
                    queue.offer(neighbor);
                }
            }
        }

        return Collections.emptyList();
    }

    /**
     * Reconstructs path from parent array
     */
    private List<Integer> reconstructPath(int[] parent, int startId, int endId) {
        List<Integer> path = new ArrayList<>();
        int current = endId;

        while (current != -1) {
            path.add(current);
            current = parent[current];
        }

        Collections.reverse(path);
        return path;
    }

    /**
     * Displays the connection path in a user-friendly format
     */
    private void displayPath(List<Integer> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            System.out.println("  " + path.get(i) + " is friends with " + path.get(i + 1));
        }
        System.out.println("Path length: " + (path.size() - 1) + " connections");
    }

    /**
     * Validates if an account ID is within valid range
     */
    private boolean isValidAccountId(int accountId) {
        return accountId >= 0 && accountId < numAccounts;
    }

    /**
     * Main method with improved user interface
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SocialGraph graph = new SocialGraph();
        
        System.out.println("=== Social Network Analysis ===");
        
        // File loading with retry logic
        boolean dataLoaded = false;
        while (!dataLoaded) {
            System.out.print("Enter data file path: ");
            String filename = scanner.nextLine().trim();
            
            if (filename.equalsIgnoreCase("exit")) {
                System.out.println("Goodbye!");
                return;
            }
            
            dataLoaded = graph.loadData(filename);
            if (!dataLoaded) {
                System.out.println("Please try again or type 'exit' to quit.");
            }
        }

        // Main menu loop
        boolean running = true;
        while (running) {
            displayMainMenu();
            
            if (!scanner.hasNextInt()) {
                System.out.println("Error: Please enter a valid number.");
                scanner.next(); // Clear invalid input
                continue;
            }
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    handleFriendListQuery(graph, scanner);
                    break;
                case 2:
                    handleConnectionQuery(graph, scanner);
                    break;
                case 3:
                    running = false;
                    System.out.println("Thank you for using Social Network Analysis. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please select 1, 2, or 3.");
            }
        }
        
        scanner.close();
    }

    private static void displayMainMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Get friend list");
        System.out.println("2. Check connection between two accounts");
        System.out.println("3. Exit");
        System.out.print("Enter your choice (1-3): ");
    }

    private static void handleFriendListQuery(SocialGraph graph, Scanner scanner) {
        System.out.print("Enter account ID: ");
        
        if (!scanner.hasNextInt()) {
            System.out.println("Error: Please enter a valid number.");
            scanner.next(); // Clear invalid input
            return;
        }
        
        int accountId = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        graph.displayFriends(accountId);
    }

    private static void handleConnectionQuery(SocialGraph graph, Scanner scanner) {
        System.out.print("Enter first account ID: ");
        if (!scanner.hasNextInt()) {
            System.out.println("Error: Please enter a valid number.");
            scanner.next();
            return;
        }
        int startId = scanner.nextInt();
        
        System.out.print("Enter second account ID: ");
        if (!scanner.hasNextInt()) {
            System.out.println("Error: Please enter a valid number.");
            scanner.next();
            return;
        }
        int endId = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        graph.checkConnection(startId, endId);
    }
}
