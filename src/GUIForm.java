//https://www.youtube.com/watch?v=jOXCkXc5X38&list=PLo4535whUBh4PU1DLOZKeLKtMcHWVvZTe&index=17&t=1697s

import javax.swing.*;
import java.sql.*;

public class GUIForm {
    private JPanel Main;
    private JTextField textFieldName;
    private JTextField textFieldPrice;
    private JButton saveButton;
    private JButton deleteButton;
    private JButton updateButton;
    private JTextField textFieldID;
    private JTextField textFieldQuantity;
    private JButton searchButton;
    private JLabel Title;

    Connection con;
    PreparedStatement pst;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Products Database Manager");
        frame.setContentPane(new GUIForm().Main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public GUIForm() {
        Connect();
        saveButton.addActionListener(e -> {
            String name, price, quantity;

            name = textFieldName.getText();
            price = textFieldPrice.getText();
            quantity = textFieldQuantity.getText();

            try{
                pst = con.prepareStatement
                        ("INSERT INTO products(name, price, quantity) VALUES(?,?,?)");
                pst.setString(1, name);
                pst.setString(2, price);
                pst.setString(3, quantity);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Record Added");
                textFieldName.setText("");
                textFieldPrice.setText("");
                textFieldQuantity.setText("");
                textFieldName.requestFocus();
            }
            catch (SQLException e1){
                e1.printStackTrace();
            }


        });

        searchButton.addActionListener(e -> {
            String id = textFieldID.getText();
            try {
                pst = con.prepareStatement
                        ("SELECT name, price, quantity FROM products WHERE id = ?");
                pst.setString(1, id);
                ResultSet rs = pst.executeQuery();

                if(rs.next()){
                    String name = rs.getString(1);
                    String price = rs.getString(2);
                    String quantity = rs.getString(3);

                    textFieldName.setText(name);
                    textFieldPrice.setText(price);
                    textFieldQuantity.setText(quantity);
                }
                else {
                    textFieldName.setText("");
                    textFieldPrice.setText("");
                    textFieldQuantity.setText("");
                    JOptionPane.showMessageDialog(null,"Invalid ID");
                }
            }
            catch (SQLException e2){
                e2.printStackTrace();
            }
        });

        updateButton.addActionListener(e -> {
            String name, price, quantity, id;
            name = textFieldName.getText();
            price = textFieldPrice.getText();
            quantity = textFieldQuantity.getText();
            id = textFieldID.getText();

            try{
                pst = con.prepareStatement
                        ("UPDATE products SET name = ?, price = ?, quantity = ? WHERE id = ?");
                pst.setString(1, name);
                pst.setString(2, price);
                pst.setString(3, quantity);
                pst.setString(4, id);
                pst.executeUpdate();
                JOptionPane.showMessageDialog
                        (null, "Record Updated");
                clearTextBoxes();
            }
            catch (SQLException e3){
                e3.printStackTrace();
            }
        });

        deleteButton.addActionListener(e -> {
            String id;
            id = textFieldID.getText();

            try{
                pst = con.prepareStatement("DELETE FROM products WHERE id = ?");
                pst.setString(1, id);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null,"Record deleted");
                clearTextBoxes();
            }
            catch (SQLException e4){
                e4.printStackTrace();
            }
        });
    }


    public void Connect(){

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection
                    ("jdbc:mysql://localhost/products", "root", "d31f2547");
            System.out.println("Successfully connected to database");
        }
        catch (SQLException | ClassNotFoundException e){
                e.printStackTrace();
        }
    }

    public void clearTextBoxes(){
        textFieldName.setText("");
        textFieldPrice.setText("");
        textFieldQuantity.setText("");
        textFieldID.setText("");
        textFieldName.requestFocus();
    }
}
