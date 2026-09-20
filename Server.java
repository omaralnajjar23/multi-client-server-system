import java.net.*;
import java.io.*;
import java.util.ArrayList;


// Each object of this class serves one connected client
class multiClients extends Thread {

    Socket clientSocket;

    public multiClients(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            
            DataInputStream din = new DataInputStream(clientSocket.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

         
            while (true) {

                // First value tells the server whether the client wants signup or login
                int choice1 = din.readInt();

                switch (choice1) {

                    
                    case 1: {
                        int choice2 = din.readInt(); 

                        switch (choice2) {

                            
                            case 1: {
                                
                                int adminCode = (int) (Math.random() * 9000) + 1000;
                                System.out.println("[Server] Admin verification code: " + adminCode);
                                writer.println("Enter the given Admin code (in server):  ");

                                
                                boolean codeAccepted = false;
                                while (!codeAccepted) {
                                    int userCode = din.readInt();
                                    if (userCode == adminCode) {
                                        codeAccepted = true;
                                        
                                        while (true) {
                                            writer.println("Enter Username: ");
                                            String username = reader.readLine();
                                            boolean taken = false;
                                            synchronized (Server.users) {
                                                for (User u : Server.users) {
                                                    if (u.getUsername().equals(username)) { taken = true; break; }
                                                }
                                                if (taken) {
                                                    writer.println("This username already exists:");
                                                } else {
                                                    writer.println("Enter Password: ");
                                                    String password = reader.readLine();
                                                    Server.users.add(new Admin(username, password));
                                                    Server.saveUsers();
                                                    writer.println("Admin registered successfully");
                                                    break;
                                                }
                                            }
                                        }
                                    } else {
                                        writer.println("WRONG CODE! Please enter the correct code:");
                                    }
                                }
                                break;
                            }

                            
                            case 2: {
                                while (true) {
                                    writer.println("Enter Username: ");
                                    String username = reader.readLine();
                                    boolean taken = false;
                                    synchronized (Server.users) {
                                        for (User u : Server.users) {
                                            if (u.getUsername().equals(username)) { taken = true; break; }
                                        }
                                        if (taken) {
                                            writer.println("This username already exists:");
                                        } else {
                                            writer.println("Enter Password: ");
                                            String password = reader.readLine();
                                            Server.users.add(new Customer(username, password));
                                            Server.saveUsers();
                                            writer.println("Customer registered successfully");
                                            break;
                                        }
                                    }
                                }
                                break; 
                            }
                        }
                        break; 
                    }

                
                    
                    
                    case 2: {
                        int choice3 = din.readInt(); 

                        switch (choice3) {

                           
                            case 1: {
                                Admin loggedAdmin = null;
                               
                                while (loggedAdmin == null) {
                                    writer.println("Enter Username: ");
                                    String username = reader.readLine();
                                    writer.println("Enter Password: ");
                                    String password = reader.readLine();

                                    synchronized (Server.users) {
                                        for (User u : Server.users) {
                                            if (u.getUsername().equals(username)) {
                                                if (!u.getPassword().equals(password)) {
                                                    writer.println("Wrong Username or Password");
                                                    break;
                                                }
                                                if (u instanceof Admin) {
                                                    loggedAdmin = (Admin) u;
                                                    writer.println("Logged in Successfully!!");
                                                } else {
                                                    writer.println("This is not an Admin account!");
                                                }
                                                break;
                                            }
                                        }
                                        
                                        if (loggedAdmin == null) {
                                            
                                            boolean found = false;
                                            for (User u : Server.users) {
                                                if (u.getUsername().equals(username)) { found = true; break; }
                                            }
                                            if (!found) writer.println("Wrong Username or Password");
                                        }
                                    }
                                }

                                writer.println("Welcome Admin: " + loggedAdmin.getUsername());

                                
                                boolean adminActive = true;
                                while (adminActive) {
                                    int op = din.readInt(); 

                                    switch (op) {

                                        
                                        case 1: {
                                            while (true) {
                                                try {
                                                    String  name  = reader.readLine();
                                                    double  price = Double.parseDouble(reader.readLine());
                                                    int     id    = Integer.parseInt(reader.readLine());
                                                    if (price <= 0) {
                                                        writer.println("Invalid price or ID format.");
                                                        continue;
                                                    }
                                                    Item newItem = new Item(name, price, id);
                                                    boolean idExists = false;
                                                    synchronized (Server.items) {
                                                        for (Item item : Server.items) {
                                                            if (item.id == id) {
                                                                idExists = true;
                                                                break;
                                                            }
                                                        }
                                                        if (!idExists) {
                                                            Server.items.add(newItem);
                                                            Server.saveItems();
                                                        }
                                                    }
                                                    if (idExists) {
                                                        writer.println("ID is Already exist");
                                                        continue;
                                                    }
                                                    writer.println("Item added: " + newItem.toString());
                                                    String again = reader.readLine(); // "y" or "n"
                                                    if (again.equalsIgnoreCase("n")) break;
                                                } catch (NumberFormatException e) {
                                                    writer.println("Invalid price or ID format.");
                                                }
                                            }
                                            break; 
                                        }

                                       
                                        case 2: {
                                            while (true) {
                                                try {
                                                    int removeId = Integer.parseInt(reader.readLine());
                                                    boolean removed = false;
                                                    synchronized (Server.items) {
                                                        for (Item item : Server.items) {
                                                            if (item.id == removeId) {
                                                                Server.items.remove(item);
                                                                Server.saveItems();
                                                                removed = true;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    writer.println(removed
                                                        ? "Item removed successfully."
                                                        : "Item ID " + removeId + " not found.");
                                                    String again = reader.readLine();
                                                    if (again.equalsIgnoreCase("n")) break;
                                                } catch (NumberFormatException e) {
                                                    writer.println("Invalid ID format.");
                                                }
                                            }
                                            break; 
                                        }

                                        
                                        case 3: {
                                            while (true) {
                                                try {
                                                    int modifyId = Integer.parseInt(reader.readLine());
                                                    boolean found = false;
                                                    synchronized (Server.items) {
                                                        for (Item item : Server.items) {
                                                            if (item.id == modifyId) { found = true; break; }
                                                        }
                                                    }
                                                    if (!found) {
                                                        writer.println("NOT FOUND");
                                                        continue;
                                                    }
                                                    writer.println("FOUND");
                                                    String newName  = reader.readLine();
                                                    double newPrice = Double.parseDouble(reader.readLine());
                                                    synchronized (Server.items) {
                                                        for (Item item : Server.items) {
                                                            if (item.id == modifyId) {
                                                                item.name = newName;
                                                                item.setPriceWithoutTax(newPrice);
                                                                Server.saveItems();
                                                                writer.println("Item modified: " + item.toString());
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    String again = reader.readLine();
                                                    if (again.equalsIgnoreCase("n")) break;
                                                } catch (NumberFormatException e) {
                                                    writer.println("Invalid ID or price format.");
                                                }
                                            }
                                            break; 
                                        }

                                       
                                        case 4: {
                                            writer.println("Goodbye Admin!");
                                            adminActive = false;
                                            break;
                                        }
                                    }
                                }
                                break;
                            }

                            
                            case 2: {
                                Customer loggedCustomer = null;

                               
                                while (loggedCustomer == null) {
                                    writer.println("Enter Username: ");
                                    String username = reader.readLine();
                                    writer.println("Enter Password: ");
                                    String password = reader.readLine();

                                    synchronized (Server.users) {
                                        boolean userFound = false;
                                        for (User u : Server.users) {
                                            if (u.getUsername().equals(username)) {
                                                userFound = true;
                                                if (!u.getPassword().equals(password)) {
                                                    writer.println("Wrong Username or Password");
                                                    break;
                                                }
                                                if (u instanceof Customer) {
                                                    loggedCustomer = (Customer) u;
                                                    
                                                    writer.println("Welcome Customer: " + loggedCustomer.getUsername());
                                                } else {
                                                    writer.println("This is not a Customer account!");
                                                }
                                                break;
                                            }
                                        }
                                        if (!userFound) writer.println("Wrong Username or Password");
                                    }
                                }

                                
                                boolean customerActive = true;
                                while (customerActive) {
                                    int customerChoice = din.readInt(); 

                                    switch (customerChoice) {

                                       
                                        case 1: {
                                            
                                            StringBuilder itemList = new StringBuilder();
                                            synchronized (Server.items) {
                                                for (Item item : Server.items)
                                                    itemList.append(item.toString()).append("\n");
                                            }
                                            writer.println(itemList.toString().trim());
                                            writer.println("END");

                                            Order newOrder = new Order(Server.getOrderCounter());
                                            while (true) {
                                                int itemId = din.readInt();
                                                boolean itemFound = false;
                                                synchronized (Server.items) {
                                                    for (Item item : Server.items) {
                                                        if (item.id == itemId) {
                                                            newOrder.addItem(item);
                                                            writer.println("Added: " + item.toString());
                                                            itemFound = true;
                                                            break;
                                                        }
                                                    }
                                                }
                                                if (!itemFound) writer.println("Item not found.");
                                                int addMore = din.readInt(); 
                                                if (addMore == 2) break;
                                            }

                                            
                                            StringBuilder summary = new StringBuilder("Order #" + newOrder.code + ":\n");
                                            for (Item item : newOrder.getItems())
                                                summary.append(item.toString()).append("\n");
                                            summary.append("Total: ").append(String.format("%.2f", newOrder.orderValue()));
                                            writer.println(summary.toString().trim());
                                            writer.println("END");

                                            loggedCustomer.addOrder(newOrder);
                                            Server.saveUsers();
                                            break;
                                        }

                                        
                                        case 2: {
                                            ArrayList<Order> orders = loggedCustomer.getOrders();
                                            if (orders.isEmpty()) {
                                                writer.println("No orders yet.");
                                                writer.println("END");
                                                break;
                                            }

                                            StringBuilder ordersList = new StringBuilder();
                                            for (Order o : orders)
                                                ordersList.append("Code: ").append(o.code)
                                                          .append(" | Date: ").append(o.getDateCreated()).append("\n");
                                            writer.println(ordersList.toString().trim());
                                            writer.println("END");

                                            int listChoice = din.readInt(); 

                                            if (listChoice == 1) { 
                                                int reorderCode = din.readInt();
                                                Order previous = loggedCustomer.findOrderByCode(reorderCode);
                                                if (previous == null) {
                                                    writer.println("Order not found.");
                                                    break;
                                                }
                                                Order reOrder = new Order(Server.getOrderCounter(), previous);
                                                writer.println("Order initialized with previous items.");

                                                
                                                boolean reorderDone = false;
                                                while (!reorderDone) {
                                                    int modifyChoice = din.readInt(); 
                                                    if (modifyChoice == 1) { 
                                                        StringBuilder iList = new StringBuilder();
                                                        synchronized (Server.items) {
                                                            for (Item item : Server.items)
                                                                iList.append(item.toString()).append("\n");
                                                        }
                                                        writer.println(iList.toString().trim());
                                                        writer.println("END");
                                                        while (true) {
                                                            int addId = din.readInt();
                                                            synchronized (Server.items) {
                                                                for (Item item : Server.items) {
                                                                    if (item.id == addId) {
                                                                        reOrder.addItem(item);
                                                                        writer.println("Added: " + item.toString());
                                                                        break;
                                                                    }
                                                                }
                                                            }
                                                            int more = din.readInt();
                                                            if (more == 2) break;
                                                        }
                                                    } else if (modifyChoice == 2) { 
                                                        int removeId = din.readInt();
                                                        boolean removed = false;
                                                        for (Item item : reOrder.getItems()) {
                                                            if (item.id == removeId) {
                                                                reOrder.getItems().remove(item);
                                                                writer.println("Removed: " + item.name);
                                                                removed = true;
                                                                break;
                                                            }
                                                        }
                                                        if (!removed) writer.println("Item not found in order.");
                                                    } else if (modifyChoice == 3) {
                                                        reorderDone = true;
                                                    }
                                                }

                                                
                                                StringBuilder reSum = new StringBuilder("Reorder #" + reOrder.code + ":\n");
                                                for (Item item : reOrder.getItems())
                                                    reSum.append(item.toString()).append("\n");
                                                reSum.append("Total: ").append(String.format("%.2f", reOrder.orderValue()));
                                                writer.println(reSum.toString().trim());
                                                writer.println("END");
                                                loggedCustomer.addOrder(reOrder);
                                                Server.saveUsers();

                                            } else if (listChoice == 2) { 
                                                int showCode = din.readInt();
                                                Order shown = loggedCustomer.findOrderByCode(showCode);
                                                if (shown == null) {
                                                    writer.println("Order not found.");
                                                    writer.println("END");
                                                    break;
                                                }
                                                StringBuilder showSum = new StringBuilder("Order #" + shown.code + ":\n");
                                                for (Item item : shown.getItems())
                                                    showSum.append(item.toString()).append("\n");
                                                showSum.append("Total: ").append(String.format("%.2f", shown.orderValue()));
                                                writer.println(showSum.toString().trim());
                                                writer.println("END");

                                                
                                                while (true) {
                                                    int wantsReview = din.readInt(); 
                                                    if (wantsReview == 2) break;
                                                    int reviewId = din.readInt();
                                                    boolean reviewFound = false;
                                                    for (Item item : shown.getItems()) {
                                                        if (item.id == reviewId) {
                                                            writer.println("FOUND");
                                                            String reviewText = reader.readLine();
                                                            item.getReviews().add(reviewText);
                                                            Server.saveItems();
                                                            Server.saveUsers();
                                                            writer.println("Review added to: " + item.name);
                                                            reviewFound = true;
                                                            break;
                                                        }
                                                    }
                                                    if (!reviewFound) writer.println("NOT FOUND");
                                                }
                                            }
                                            break;
                                        }

                                        
                                        case 3: {
                                            writer.println("Goodbye " + loggedCustomer.getUsername());
                                            customerActive = false;
                                            break;
                                        }
                                    }
                                }
                                break; 
                            }
                        }
                        break; 
                    }
                }
            }

        } catch (IOException ex) {
            System.out.println("[multiClients] Connection closed: " + ex.getMessage());
        }
    }
}


// Common parent class for admins and customers
abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String username;
    protected String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}


// Admin account type
class Admin extends User implements Serializable {
    private static final long serialVersionUID = 2L;
    public Admin(String username, String password) { super(username, password); }
}


// Customer account type
class Customer extends User implements Serializable {
    private static final long serialVersionUID = 3L;
    private ArrayList<Order> orders = new ArrayList<>();

    public Customer(String username, String password) { super(username, password); }
    public ArrayList<Order> getOrders() { return orders; }
    public void addOrder(Order order) { orders.add(order); }

    // Search for an order using its code
    public Order findOrderByCode(int code) {
        for (Order o : orders) if (o.code == code) return o;
        return null;
    }
}


// Main server class
public class Server {

    
    static ArrayList<Item> items = new ArrayList<>();

    
    static ArrayList<User> users = new ArrayList<>();

    private static final String ITEMS_FILE = "items.out";
    private static final String USERS_FILE = "users.out";

    
    private static int orderCounter = 1000;

    

    public static void main(String[] args) {
        loadItems();
        loadUsers();
        calculateNextOrderCode();

        try {
            // Open the server socket on the project port
            ServerSocket serverSocket = new ServerSocket(7733);
            System.out.println("[Server] Started on port 7733");
            while (true) {
                // Wait for a new client connection
                Socket clientSocket = serverSocket.accept();
                System.out.println("[Server] New client connected: " + clientSocket.getInetAddress());
                // Handle this client in a separate thread
                new multiClients(clientSocket).start();
            }
        } catch (IOException e) {
            System.out.println("[Server] Fatal error: " + e.getMessage());
        }
    }

    
    // Save current items list to items.out
    static synchronized void saveItems() {
        try  {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ITEMS_FILE));
            oos.writeObject(items); 
            oos.close();
        } catch (IOException e) {
            System.out.println("[Server] Error saving items: " + e.getMessage());
        }
    }

    // Load items when the server starts
    static void loadItems() {
        File f = new File(ITEMS_FILE); 
        if (!f.exists()) { 
            System.out.println("[Server] No items file found — starting fresh.");
            return;
        }
        try  {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
            items = (ArrayList<Item>) ois.readObject(); 
            ois.close();
            System.out.println("[Server] Loaded " + items.size() + " item(s).");
        } catch (Exception e) {
            System.out.println("[Server] Error loading items: " + e.getMessage());
        }
    }

    // Save registered users and customer orders
    static synchronized void saveUsers() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE));
            synchronized (users) {
                oos.writeObject(users);
            }
            oos.close();
        } catch (IOException e) {
            System.out.println("[Server] Error saving users: " + e.getMessage());
        }
    }

    
    // Load users when the server starts
    static void loadUsers() {
        File f = new File(USERS_FILE);
        if (!f.exists()) {
            System.out.println("[Server] No users file found — starting fresh.");
            return;
        }
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
            users = (ArrayList<User>) ois.readObject();
            ois.close();
            System.out.println("[Server] Loaded " + users.size() + " user(s).");
        } catch (Exception e) {
            System.out.println("[Server] Error loading users: " + e.getMessage());
        }
    }

   

    // Find next available order code after loading previous orders
    private static synchronized void calculateNextOrderCode() { 
        int max = 1000;
        synchronized (users) {  
            for (User u : users) {
                if (u instanceof Customer) {
                    for (Order o : ((Customer) u).getOrders()) {
                        if (o.code > max) max = o.code;
                    }
                }
            }
        }
        orderCounter = max + 1;
    }

    // Return unique order code
    public static synchronized int getOrderCounter() {
        return orderCounter++;
    }
}