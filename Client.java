import java.io.*;
import java.net.*;
import java.util.Scanner;


// Console client used to send requests to the server
public class Client {

    public static void main(String[] args) {

        try  {
            // Start the TCP connection with the server
            Socket socket = new Socket(InetAddress.getLoopbackAddress(), 7733);

            DataOutputStream dout   = new DataOutputStream(socket.getOutputStream());
            PrintWriter      writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader   bReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner          input  = new Scanner(System.in);

            System.out.println("Connection established with the server.");

            
            // Keep showing the first menu until the program exits
            while (true) { 

                System.out.println("\n---------- Main Menu ----------");
                System.out.println("1) Create new account");
                System.out.println("2) Login to existing account");
                System.out.println("--------------------------------");

                int loginChoice;
                try {
                    loginChoice = Integer.parseInt(input.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid choice. Please select 1 or 2.");
                    continue;
                }

                if (loginChoice != 1 && loginChoice != 2) {
                    System.out.println("Invalid choice. Please select 1 or 2.");
                    continue;
                }

                dout.writeInt(loginChoice);
                dout.flush();

                System.out.println("Select account type:");
                System.out.println("1) Admin");
                System.out.println("2) Customer");

                int roleChoice;
                try {
                    roleChoice = Integer.parseInt(input.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid account type.");
                    continue;
                }

                dout.writeInt(roleChoice);
                dout.flush();

               
                // Sign-up section
                if (loginChoice == 1) {

                    if (roleChoice == 1) {
                        
                        String prompt = bReader.readLine(); 
                        System.out.println(prompt);

                        while (true) {
                            int code;
                            try {
                                code = Integer.parseInt(input.nextLine().trim());
                            } catch (NumberFormatException e) {
                                System.out.println("Code must be a 4-digit number.");
                                continue;
                            }
                            dout.writeInt(code);
                            dout.flush();

                            String codeResponse = bReader.readLine();
                            System.out.println(codeResponse);

                            if (codeResponse.startsWith("WRONG CODE")) {
                                continue; 
                            }

                            
                            while (true) {
                                String username = input.nextLine().trim();
                                writer.println(username);

                                String usernameResponse = bReader.readLine();
                                System.out.println(usernameResponse);

                                if (usernameResponse.equals("This username already exists:")) {
                                    String retryPrompt = bReader.readLine(); // "Enter Username: "
                                    System.out.println(retryPrompt);
                                } else {
                                    // "Enter Password: "
                                    String password = input.nextLine().trim();
                                    writer.println(password);
                                    String finalMsg = bReader.readLine(); 
                                    System.out.println(finalMsg);
                                    break;
                                }
                            }
                            break; 
                        }

                    } else if (roleChoice == 2) {
                       
                        while (true) {
                            String usernamePrompt = bReader.readLine(); // "Enter Username: "
                            System.out.println(usernamePrompt);
                            String username = input.nextLine().trim();
                            writer.println(username);

                            String response = bReader.readLine(); 
                            System.out.println(response);

                            if (response.equals("This username already exists:")) {
                               
                            } else {
                                
                                String password = input.nextLine().trim();
                                writer.println(password);
                                String finalMsg = bReader.readLine(); 
                                System.out.println(finalMsg);
                                break;
                            }
                        }
                    }

                
                } else { 

                    // Login section
                    if (roleChoice == 1) {
                       
                        boolean loggedIn = false;
                        while (!loggedIn) {
                            System.out.println(bReader.readLine()); // "Enter Username: "
                            String username = input.nextLine().trim();
                            writer.println(username);

                            System.out.println(bReader.readLine()); // "Enter Password: "
                            String password = input.nextLine().trim();
                            writer.println(password);

                            String loginResponse = bReader.readLine();
                            System.out.println(loginResponse);

                            if (!loginResponse.equals("Logged in Successfully!!")) {
                                // wrong account type ... retry
                                continue;
                            }

                            loggedIn = true;
                            String welcomeMsg = bReader.readLine(); 
                            System.out.println(welcomeMsg);

                           
                            boolean adminActive = true;
                            while (adminActive) {
                                System.out.println("\n------ Admin Operations ------");
                                System.out.println("1) Add item");
                                System.out.println("2) Remove item");
                                System.out.println("3) Modify item");
                                System.out.println("4) Logout");

                                int op;
                                try {
                                    op = Integer.parseInt(input.nextLine().trim());
                                } catch (NumberFormatException e) {
                                    System.out.println("Please choose a valid option from 1 to 4.");
                                    continue;
                                }
                                dout.writeInt(op);
                                dout.flush();

                                switch (op) {

                                    case 1: // Add item
                                        while (true) {
                                            System.out.print("Item name: ");
                                            String name = input.nextLine().trim();
                                            writer.println(name);

                                            System.out.print("Price (without tax): ");
                                            String price = input.nextLine().trim();
                                            writer.println(price);

                                            System.out.print("Item ID: ");
                                            String id = input.nextLine().trim();
                                            writer.println(id);
                                            
                                            

                                            String response = bReader.readLine();
                                            System.out.println(response);

                                            if (response.equals("Invalid price or ID format.") || response.equals("ID is Already exist")) {
                                                System.out.println("Please try again.");
                                                continue;
                                            }

                                            System.out.print("Add another item? (Y/N): ");
                                            String again = input.nextLine().trim();
                                            writer.println(again);
                                            if (again.equalsIgnoreCase("n")) break;
                                        }
                                        break;

                                    case 2: // Remove item
                                        while (true) {
                                            System.out.print("Enter item ID to remove: ");
                                            String id = input.nextLine().trim();
                                            writer.println(id);

                                            String response = bReader.readLine();
                                            System.out.println(response);

                                            if (response.equals("Invalid ID format.")) {
                                                System.out.println("Please enter a valid integer ID.");
                                                continue;
                                            }

                                            System.out.print("Remove another? (Y/N): ");
                                            String again = input.nextLine().trim();
                                            writer.println(again);
                                            if (again.equalsIgnoreCase("n")) break;
                                        }
                                        break;

                                    case 3: // Modify item
                                        while (true) {
                                            System.out.print("Enter item ID to modify: ");
                                            String id = input.nextLine().trim();
                                            writer.println(id);

                                            String check = bReader.readLine(); // "FOUND", "NOT FOUND", or "Invalid ID or price format."
                                            if (check.equals("Invalid ID or price format.")) {
                                                System.out.println("Invalid ID, please try again.");
                                                continue;
                                            }
                                            if (check.equals("NOT FOUND")) {
                                                System.out.println("Item not found, please try again.");
                                                continue;
                                            }

                                            System.out.print("New name: ");
                                            String newName = input.nextLine().trim();
                                            writer.println(newName);

                                            System.out.print("New price (without tax): ");
                                            String newPrice = input.nextLine().trim();
                                            writer.println(newPrice);

                                            String modifyMsg = bReader.readLine();
                                            System.out.println(modifyMsg);

                                            if (modifyMsg.equals("Invalid ID or price format.")) {
                                                System.out.println("Invalid price, please try again.");
                                                continue;
                                            }

                                            System.out.print("Modify another item? (Y/N): ");
                                            String again = input.nextLine().trim();
                                            writer.println(again);
                                            if (again.equalsIgnoreCase("n")) break;
                                        }
                                        break;

                                    case 4: // Exit
                                        String goodbye = bReader.readLine(); 
                                        System.out.println(goodbye);
                                        adminActive = false;
                                        break;

                                    default:
                                        System.out.println("Please enter 1-4.");
                                }
                            }
                        }

                    } else if (roleChoice == 2) {
                       
                        boolean loggedIn = false;
                        while (!loggedIn) {
                            System.out.println(bReader.readLine()); // "Enter Username: "
                            String username = input.nextLine().trim();
                            writer.println(username);

                            System.out.println(bReader.readLine()); // "Enter Password: "
                            String password = input.nextLine().trim();
                            writer.println(password);

                            String loginResponse = bReader.readLine();
                            System.out.println(loginResponse);

                            if (!loginResponse.startsWith("Welcome Customer:")) {
                               
                                continue;
                            }

                            loggedIn = true;

                            
                            boolean customerActive = true;
                            while (customerActive) {
                                System.out.println("\n------ Customer Operations ------");
                                System.out.println("1) Make new order");
                                System.out.println("2) View previous orders");
                                System.out.println("3) Exit");

                                int choice;
                                try {
                                    choice = Integer.parseInt(input.nextLine().trim());
                                } catch (NumberFormatException e) {
                                    System.out.println("Please choose 1, 2, or 3.");
                                    continue;
                                }
                                dout.writeInt(choice);
                                dout.flush();

                                switch (choice) {

                                    case 1: { // Create a new order
                                        System.out.println("\nAvailable items:");
                                        String itemLine;
                                        while (!(itemLine = bReader.readLine()).equals("END")) {
                                            System.out.println(itemLine);
                                        }
                                        System.out.println("----------------");

                                        while (true) {
                                            System.out.print("Enter item ID to add: ");
                                            int itemId;
                                            try {
                                                itemId = Integer.parseInt(input.nextLine().trim());
                                            } catch (NumberFormatException e) {
                                                System.out.println("Please enter a valid item ID.");
                                                continue;
                                            }
                                            dout.writeInt(itemId);
                                            dout.flush();

                                            System.out.println(bReader.readLine()); // "Added: ..." or "Item not found."

                                            System.out.println("1- Add more items\n2- Finish order");
                                            int more;
                                            try {
                                                more = Integer.parseInt(input.nextLine().trim());
                                            } catch (NumberFormatException e) {
                                                more = 2;
                                            }
                                            dout.writeInt(more);
                                            dout.flush();
                                            if (more == 2) break;
                                        }

                                        System.out.println("\nOrder summary:");
                                        String sumLine;
                                        while (!(sumLine = bReader.readLine()).equals("END")) {
                                            System.out.println(sumLine);
                                        }
                                        System.out.println("----------------");
                                        break;
                                    }

                                    case 2: { // Display stored orders
                                        String firstLine = bReader.readLine();

                                        if (firstLine.equals("No orders yet.")) {
                                            System.out.println(firstLine);
                                            bReader.readLine(); // consume "END"
                                            break;
                                        }

                                        System.out.println("\nPrevious orders:");
                                        System.out.println(firstLine);
                                        String orderLine;
                                        while (!(orderLine = bReader.readLine()).equals("END")) {
                                            System.out.println(orderLine);
                                        }
                                        System.out.println("----------------");

                                        System.out.println("1) Reorder\n2) Show order details");
                                        int listChoice;
                                        try {
                                            listChoice = Integer.parseInt(input.nextLine().trim());
                                        } catch (NumberFormatException e) {
                                            listChoice = 2;
                                        }
                                        dout.writeInt(listChoice);
                                        dout.flush();

                                        if (listChoice == 1) { // Reorder
                                            System.out.print("Enter order code to reorder: ");
                                            int reCode;
                                            try {
                                                reCode = Integer.parseInt(input.nextLine().trim());
                                            } catch (NumberFormatException e) {
                                                reCode = -1;
                                            }
                                            dout.writeInt(reCode);
                                            dout.flush();

                                            String reResponse = bReader.readLine();
                                            System.out.println(reResponse);
                                            if (reResponse.equals("Order not found.")) break;

                                            
                                            reorderLoop:
                                            while (true) {
                                                System.out.println("1) Add items\n2) Remove item\n3) Finish");
                                                int mod;
                                                try {
                                                    mod = Integer.parseInt(input.nextLine().trim());
                                                } catch (NumberFormatException e) {
                                                    mod = 3;
                                                }
                                                dout.writeInt(mod);
                                                dout.flush();

                                                if (mod == 1) {
                                                    System.out.println("\nAvailable items:");
                                                    String il;
                                                    while (!(il = bReader.readLine()).equals("END")) System.out.println(il);
                                                    System.out.println("----------------");

                                                    while (true) {
                                                        System.out.print("Enter item ID to add: ");
                                                        int addId;
                                                        try {
                                                            addId = Integer.parseInt(input.nextLine().trim());
                                                        } catch (NumberFormatException e) { addId = -1; }
                                                        dout.writeInt(addId);
                                                        dout.flush();
                                                        System.out.println(bReader.readLine());
                                                        System.out.println("1) Add more\n2) Done");
                                                        int more;
                                                        try { more = Integer.parseInt(input.nextLine().trim()); }
                                                        catch (NumberFormatException e) { more = 2; }
                                                        dout.writeInt(more);
                                                        dout.flush();
                                                        if (more == 2) break;
                                                    }

                                                } else if (mod == 2) {
                                                    System.out.print("Enter item ID to remove: ");
                                                    int remId;
                                                    try { remId = Integer.parseInt(input.nextLine().trim()); }
                                                    catch (NumberFormatException e) { remId = -1; }
                                                    dout.writeInt(remId);
                                                    dout.flush();
                                                    System.out.println(bReader.readLine());

                                                } else { 
                                                    break reorderLoop;
                                                }
                                            }

                                            
                                            System.out.println("\nReorder summary:");
                                            String rl;
                                            while (!(rl = bReader.readLine()).equals("END")) System.out.println(rl);
                                            System.out.println("----------------");

                                        } else { // Show order
                                            System.out.print("Enter order code to show: ");
                                            int showCode;
                                            try { showCode = Integer.parseInt(input.nextLine().trim()); }
                                            catch (NumberFormatException e) { showCode = -1; }
                                            dout.writeInt(showCode);
                                            dout.flush();

                                            String showFirst = bReader.readLine();
                                            System.out.println(showFirst);
                                            if (showFirst.equals("Order not found.")) {
                                                bReader.readLine(); 
                                                break;
                                            }

                                            String sl;
                                            while (!(sl = bReader.readLine()).equals("END")) System.out.println(sl);

                                            
                                            while (true) {
                                                System.out.println("1) Add review\n2) Done");
                                                int wantsReview;
                                                try { wantsReview = Integer.parseInt(input.nextLine().trim()); }
                                                catch (NumberFormatException e) { wantsReview = 2; }
                                                dout.writeInt(wantsReview);
                                                dout.flush();
                                                if (wantsReview == 2) break;

                                                System.out.print("Enter item ID to review: ");
                                                int reviewId;
                                                try { reviewId = Integer.parseInt(input.nextLine().trim()); }
                                                catch (NumberFormatException e) { reviewId = -1; }
                                                dout.writeInt(reviewId);
                                                dout.flush();

                                                String reviewCheck = bReader.readLine(); // "FOUND" or "NOT FOUND"
                                                if (reviewCheck.equals("NOT FOUND")) {
                                                    System.out.println("Item not found in this order.");
                                                    continue;
                                                }
                                                System.out.print("Enter your review: ");
                                                String reviewText = input.nextLine().trim();
                                                writer.println(reviewText);
                                                System.out.println(bReader.readLine()); // "Review added to: <name>"
                                            }
                                        }
                                        break;
                                    }

                                    case 3: { // Close connection and end client
                                        String goodbye = bReader.readLine();
                                        System.out.println(goodbye);
                                        socket.close();
                                        input.close();
                                        return;
                                    }

                                    default:
                                        System.out.println("Please choose 1, 2, or 3.");
                                }
                            }
                        }
                    }
                }
            } // end outer while

        } catch (SocketException e) {
            System.out.println("No connection to server. Is it running?");
        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }
}