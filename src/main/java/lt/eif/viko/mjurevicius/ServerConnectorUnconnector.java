package lt.eif.viko.mjurevicius;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerConnectorUnconnector {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private volatile boolean active = true;
    String TESTPassword = "1234";
    float TESTBudget = 125.25f;

    public void start(int port) {
        new Thread(() -> deactivateListener()).start();

        try {
            while (active) {
                if (serverSocket == null || serverSocket.isClosed()) {
                    serverSocket = new ServerSocket(port);
                    System.out.println("Server started on port: " + port);
                }

                try {
                    System.out.println("Waiting for client...");
                    clientSocket = serverSocket.accept();

                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                    String message = "";
                    while (!"Over.".equals(message)) {
                        message = in.readLine();

                        if (message == null) {
                            System.out.println("Client disconnected.");
                            break;
                        }

                        if (!message.isEmpty()) {
                            System.out.println("Message received: " + message);
                            if ("Connecting to payment system.".equals(message)) {
                                out.println("Connection made.");

                                Floatnbool floatnbool = handlePaymentSum(out, in);
                                if (!floatnbool.isBoolToHold()) {
                                    continue;
                                }
                                float paymentSum = floatnbool.getFloatToHold();

                                boolean passwordValid = handlePassword(TESTPassword, out, in);
                                if (!passwordValid) {
                                    continue;
                                }

                                Floatnbool budgethandler;
                                budgethandler = handleBalance(TESTBudget, paymentSum, out, in);
                                if (!budgethandler.isBoolToHold()) {
                                    continue;
                                }
                                TESTBudget = budgethandler.getFloatToHold();
                                System.out.println("New budget: " + TESTBudget);

                            } else {
                                out.println("Unrecognized greeting");
                            }
                        }
                    }
                } catch (IOException ex) {
                    System.out.println("Exception occurred: " + ex.getMessage());
                } finally {
                    stop();
                }

                System.out.println("Reminder: write stop to stop the server");
                System.out.println("For TESTING PURPOSES: budget remaining: " + TESTBudget);
            }
        } catch (IOException ex) {
            System.out.println("Exception occurred while setting up the server: " + ex.getMessage());
        }
    }

    private boolean handlePassword(String validPassword, PrintWriter out, BufferedReader in) throws IOException {
        int attempts = 3;
        String message = "";

        while (attempts > 0) {
            message = in.readLine();

            if (message == null || !message.startsWith("Password: ")){
                out.println("Unsupported format, terminating.");
                return false;
            }

            String password  = message.split(": ")[1];
            System.out.println("Received password: " + password);

            if (password.equals(validPassword)){
                out.println("Password correct.");
                return true;
            }

            else {
                attempts --;
                if (attempts > 0) {
                    out.println("Incorrect password. " + attempts + " attempts remaining.");
                } else {
                    out.println("Incorrect password. No more attempts remaining. Terminating.");
                    return false;
                }

            }
        }
        return false;
    }

    private Floatnbool handlePaymentSum(PrintWriter out, BufferedReader in) throws IOException {
        String message = in.readLine();

        if (message == null || !message.startsWith("Payment sum: ")) {
            out.println("Unsupported format, terminating.");
            return new Floatnbool(false,-1);
        }

        try {
            float sum = Float.parseFloat(message.split(": ")[1]);
            System.out.println("Parsed sum: " + sum);
            out.println("Sum received, please input password.");
            return new Floatnbool(true,sum);
        } catch (NumberFormatException e) {
            out.println("Unsupported format, terminating.");
            return new Floatnbool(false,-1);
        }
    }

    private Floatnbool handleBalance(float balance, float paymentSum, PrintWriter out, BufferedReader in) throws IOException {
        if (balance >= paymentSum){
            balance -= paymentSum;
            out.println("Payment complete. Have a lovely day.");
            return new Floatnbool(true,balance);
        } else {
            out.println("Payment failed. Insufficient funds.");
            return new Floatnbool(false,balance);
        }
    }

    private void deactivateListener()
    {
        Scanner input = new Scanner(System.in);
        String choice = "";
        while(active){
            choice = input.nextLine();
            if (choice.equalsIgnoreCase("stop")){
                System.out.println("Server stopped");
                active = false;
                stop();
            }
        }
    }

    public void stop()
    {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ex){
            System.out.println("Exception occurred: " + ex.getMessage());
        }
    }
}
