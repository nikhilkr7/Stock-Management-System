import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
//import java.util.Random;

public class main {

    private static final Map<String, Double> stockPrices = new HashMap<>();
   // private static final Random random = new Random();
    private static DecimalFormat decimalFormat = new DecimalFormat("#.##");

    private static JFrame frame;
    private static JComboBox<String> stockComboBox;
    private static JTextField quantityField;
    private static JLabel priceLabel;
    private static JLabel balanceLabel;
    private static JTextArea logArea;
    private static JButton buyButton;
    private static JButton sellButton;
    private static JButton addFundsButton;
    private static JButton withdrawFundsButton;

    private static double userBalance = 10000.0;
    private static Map<String, Integer> userStocks = new HashMap<>();
    private static final Map<String, Double> stockPriceChanges = new HashMap<>();

    public static void main(String[] args) {
        initializeStockPrices();

        frame = new JFrame("Stock Trading App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(6, 2));

        stockComboBox = new JComboBox<>(stockPrices.keySet().toArray(new String[0]));
        quantityField = new JTextField();
        priceLabel = new JLabel();
        balanceLabel = new JLabel("Balance: $" + decimalFormat.format(userBalance));
        logArea = new JTextArea();
        buyButton = new JButton("Buy");
        sellButton = new JButton("Sell");
        addFundsButton = new JButton("Add Funds");
        withdrawFundsButton = new JButton("Withdraw Funds");

        buyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buyStock();
            }
        });

        sellButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sellStock();
            }
        });

        addFundsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addFunds();
            }
        });

        withdrawFundsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                withdrawFunds();
            }
        });

        stockComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStockPriceLabel();
            }
        });

        frame.add(new JLabel("Select Stock:"));
        frame.add(stockComboBox);
        frame.add(new JLabel("Enter Quantity:"));
        frame.add(quantityField);
        frame.add(new JLabel("Price:"));
        frame.add(priceLabel);
        frame.add(buyButton);
        frame.add(sellButton);
        frame.add(addFundsButton);
        frame.add(withdrawFundsButton);
        frame.add(balanceLabel);
        frame.add(new JScrollPane(logArea));

        frame.pack();
        frame.setVisible(true);
    }

    private static void initializeStockPrices() {
        stockPrices.put("AAPL", 150.0);
        stockPrices.put("GOOGL", 2800.0);
        stockPrices.put("MSFT", 290.0);
        // Add more stocks and prices as needed
    }
    private static void updateStockPriceLabel() {
        String selectedStock = (String) stockComboBox.getSelectedItem();
        if (selectedStock != null) {
            double price = stockPrices.getOrDefault(selectedStock, 0.0);
            priceLabel.setText("$" + decimalFormat.format(price));
        }
    }
    static {
        stockPriceChanges.put("AAPL", 0.02);  // 2% increase or decrease
        stockPriceChanges.put("GOOGL", 0.01); // 1% increase or decrease
        stockPriceChanges.put("MSFT", 0.03);  // 3% increase or decrease
        // Add more stocks and price changes as needed
    }
    private static void buyStock() {
        String selectedStock = (String) stockComboBox.getSelectedItem();
        if (selectedStock != null) {
            int quantity = Integer.parseInt(quantityField.getText());
    
            if (quantity <= 0) {
                logArea.append("Invalid quantity. Please enter a positive number.\n");
                return;
            }
    
            double price = stockPrices.getOrDefault(selectedStock, 0.0);
            double totalPrice = price * quantity;
    
            if (totalPrice > userBalance) {
                logArea.append("Insufficient balance to buy this stock.\n");
                return;
            }
    
            userBalance -= totalPrice;
            userStocks.put(selectedStock, userStocks.getOrDefault(selectedStock, 0) + quantity);
    
            // Simulate a price change (increase or decrease)
            double priceChange = stockPriceChanges.getOrDefault(selectedStock, 0.0);
            price += price * priceChange;
    
            stockPrices.put(selectedStock, price); // Update the stock price
    
            logArea.append("Bought " + quantity + " shares of " + selectedStock + " for $" + decimalFormat.format(totalPrice) + "\n");
            balanceLabel.setText("Balance: $" + decimalFormat.format(userBalance));
            updateStockPriceLabel(); // Update the displayed price
        }
    }
    
    private static void sellStock() {
        String selectedStock = (String) stockComboBox.getSelectedItem();
        if (selectedStock != null) {
            int quantity = Integer.parseInt(quantityField.getText());
    
            if (quantity <= 0) {
                logArea.append("Invalid quantity. Please enter a positive number.\n");
                return;
            }
    
            int ownedQuantity = userStocks.getOrDefault(selectedStock, 0);
    
            if (quantity > ownedQuantity) {
                logArea.append("You don't own enough shares of this stock to sell.\n");
                return;
            }
    
            double price = stockPrices.getOrDefault(selectedStock, 0.0);
            double totalPrice = price * quantity;
    
            userBalance += totalPrice;
            userStocks.put(selectedStock, ownedQuantity - quantity);
    
            // Simulate a price change (increase or decrease)
            double priceChange = stockPriceChanges.getOrDefault(selectedStock, 0.0);
            price += price * priceChange;
    
            stockPrices.put(selectedStock, price); // Update the stock price
    
            logArea.append("Sold " + quantity + " shares of " + selectedStock + " for $" + decimalFormat.format(totalPrice) + "\n");
            balanceLabel.setText("Balance: $" + decimalFormat.format(userBalance));
            updateStockPriceLabel(); // Update the displayed price
        }
    }
    private static void addFunds() {
        try {
            double amount = Double.parseDouble(JOptionPane.showInputDialog("Enter the amount to add:"));
            if (amount <= 0) {
                logArea.append("Invalid amount. Please enter a positive number.\n");
            } else {
                userBalance += amount;
                balanceLabel.setText("Balance: $" + decimalFormat.format(userBalance));
                logArea.append("Added $" + decimalFormat.format(amount) + " to your balance.\n");
            }
        } catch (NumberFormatException ex) {
            logArea.append("Invalid amount format. Please enter a valid number.\n");
        }
    }

    private static void withdrawFunds() {
        try {
            double amount = Double.parseDouble(JOptionPane.showInputDialog("Enter the amount to withdraw:"));
            if (amount <= 0) {
                logArea.append("Invalid amount. Please enter a positive number.\n");
            } else if (amount > userBalance) {
                logArea.append("Insufficient funds to withdraw.\n");
            } else {
                userBalance -= amount;
                balanceLabel.setText("Balance: $" + decimalFormat.format(userBalance));
                logArea.append("Withdrawn $" + decimalFormat.format(amount) + " from your balance.\n");
            }
        } catch (NumberFormatException ex) {
            logArea.append("Invalid amount format. Please enter a valid number.\n");
        }
    }
}