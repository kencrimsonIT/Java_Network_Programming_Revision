package Test_1.Task2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ServerProcess extends Thread {
    private Socket socket;
    private BufferedReader userIn;
    private PrintWriter userOut;
    private Dao dao;

    public ServerProcess(Socket socket) throws IOException {
        this.socket = socket;
        this.dao = new Dao();
        this.userIn = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.userOut = new PrintWriter(this.socket.getOutputStream(), true);
    }

    public void run() {
        String command, syntax, param;
        double price1 = 0; double price2 = 0;
        StringTokenizer st;
        Product product = new Product();
        List<Product> products = new ArrayList<>();
        sendResponse(userOut, "Welcome to Product Management System ...");

        while (true) {
            try {
                this.userOut.println(">");
                this.userOut.flush();
                command = this.userIn.readLine(); //Each line is a command
                if (command.isBlank()) {
                    sendResponse(userOut, "Please enter your command");
                    continue;
                }
                st = new StringTokenizer(command);
                syntax = st.nextToken(); //Each token is a syntax
                param = command.substring(syntax.length()).trim();

                //Command cases
                switch (syntax.toUpperCase()) {
                    case "QUIT":
                        sendResponse(userOut, "Goodbye!");
                        return;

                    case "ADD":
                        this.dao.addProduct(product);
                        break;

                    case "BUY":
                        if (param.isEmpty()) {
                            sendResponse(userOut, "Please enter the product ID");
                            return;
                        }
                        try {
                            List<Integer> productIDs = new ArrayList<>();
                            for (String str : param.split("\\s+")) {
                                productIDs.add(Integer.parseInt(str));
                            }
                            sendResponse(userOut, this.dao.buyProduct(productIDs));
                        } catch (NumberFormatException e) {
                            sendResponse(userOut, "Please enter the product ID");
                            return;
                        }
                        break;

                    case "PRICE":
                        displayFilteredProductsByPrice(this.dao.findByPrice(price1, price2));
                        break;

                    case "NAME":
                        displayFilteredProductsByName(this.dao.findByName(param));
                        break;

                    default:
                        sendResponse(userOut, "Invalid Command");
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void displayFilteredProductsByName(List<Product> products) {
        if (products == null || products.isEmpty()) {
            sendResponse(userOut, "There are no products in the system");
            return;
        }

        for (Product product : products) {
            sendResponse(userOut, product.toString());
        }
    }

    private void displayFilteredProductsByPrice(List<Product> products) {
        if (products == null || products.isEmpty()) {
            sendResponse(userOut, "There are no products in the system");
            return;
        }

        for (Product product : products) {
            sendResponse(userOut, product.toString());
        }
    }

    private void sendResponse(PrintWriter out, String mess) {
        out.println(mess);
        out.flush();
    }
}
