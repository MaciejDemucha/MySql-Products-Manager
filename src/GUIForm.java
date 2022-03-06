//https://www.youtube.com/watch?v=jOXCkXc5X38&list=PLo4535whUBh4PU1DLOZKeLKtMcHWVvZTe&index=17&t=1697s

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class GUIForm extends Component {
    private JPanel Main;
    private JTextField textFieldName;
    private JTextField textFieldPrice;
    private JButton addButton;
    private JButton deleteButton;
    private JButton updateButton;
    private JTextField textFieldMin;
    private JTextField textFieldQuantity;
    private JButton searchButton;
    private JLabel Title;
    private JTable productsTable;
    private JScrollPane scrollPane;
    private JButton sortAlphabeticallyButton;
    private JButton descButton;
    private JTextField textFieldMax;
    private JButton showAllButton;
    private JButton ascButton;
    private DefaultTableModel tableModel;
    private Vector<String> headers = new Vector<>();

    Connection con;
    PreparedStatement pst;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Products Database Manager");
        frame.setContentPane(new GUIForm().Main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public GUIForm() {
        headers.add("ID");
        headers.add("Name");
        headers.add("Price");
        headers.add("Quantity");
        Connect();
        tableModel = new DefaultTableModel(returnData(), headers){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productsTable.setModel(tableModel);

        //Dodanie rekordu do bazy danych
        addButton.addActionListener(e -> {
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
                refresh();
            }
            catch (SQLException e1){
                e1.printStackTrace();
            }


        });

        //Wyszukiwanie rekordu w przedziale ceny
        searchButton.addActionListener(e -> {
            refreshPrice();
        });

        //Modyfikacja wybranego rekordu
        updateButton.addActionListener(e -> {
            String name, price, quantity, id;
            name = textFieldName.getText();
            price = textFieldPrice.getText();
            quantity = textFieldQuantity.getText();
            id = (String) productsTable.getModel().getValueAt(getSelectedIndex(), 0);

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
                refresh();
            }
            catch (SQLException e3){
                e3.printStackTrace();
            }
        });

        //Usunięcie zaznaczonego rekordu
        deleteButton.addActionListener(e -> {
            String id;
            id = (String) productsTable.getModel().getValueAt(getSelectedIndex(), 0);

            try{
                pst = con.prepareStatement("DELETE FROM products WHERE id = ?");
                pst.setString(1, id);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null,"Record deleted");
                refresh();
            }
            catch (SQLException e4){
                e4.printStackTrace();
            }
        });

        //Sortowanie alfabetyczne
        sortAlphabeticallyButton.addActionListener(e -> {
            refresh("SELECT * FROM products ORDER BY name");
        });

        //Sortowanie wg. ceny malejąco
        descButton.addActionListener(e -> {
            refresh("SELECT * FROM products ORDER BY price DESC");
        });

        //Sortowanie wg. ceny rosnąco
        ascButton.addActionListener(e -> {
            refresh("SELECT * FROM products ORDER BY price ASC");
        });
        
        //Reset filtrów wyświetlania rekordów
        showAllButton.addActionListener(e -> {
            refresh();
        });

    }

    //Połączenie aplikacji z bazą danych
    public void Connect(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            //Wpisywanie hasła
            String password = JOptionPane.showInputDialog("Password: ");

            con = DriverManager.getConnection
                    ("jdbc:mysql://localhost/products", "root", password);
            JOptionPane.showMessageDialog
                    (null,"Successfully connected to database");
        }
        catch (SQLException | ClassNotFoundException e){
                JOptionPane.showMessageDialog(null ,"Connection error");
                e.printStackTrace();
        }
    }

    //Pobranie wszystkich rekordów z bazy danych
    public Vector<Vector<String>> returnData(){
        try{
            Vector<Vector<String>> data = new Vector<>();

            pst = con.prepareStatement("SELECT * FROM products");
            ResultSet rs = pst.executeQuery();

            while(rs.next()){

                Vector<String> record = new Vector<>();
                record.add(rs.getInt(1) + "");
                record.add(rs.getString(2));
                record.add(rs.getInt(3) + "");
                record.add(rs.getInt(4) + "");
                data.add(record);

            }
            return data;
        }
        catch (SQLException e5){
            e5.printStackTrace();
        }
        return null;
    }

    //Pobranie wybranych rekordów z bazy danych
    public Vector<Vector<String>> returnData(String query){
        try{
            Vector<Vector<String>> data = new Vector<>();

            pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            while(rs.next()){

                Vector<String> record = new Vector<>();
                record.add(rs.getInt(1) + "");
                record.add(rs.getString(2));
                record.add(rs.getInt(3) + "");
                record.add(rs.getInt(4) + "");
                data.add(record);

            }
            return data;
        }
        catch (SQLException e5){
            e5.printStackTrace();
        }
        return null;
    }

    //Pobranie rekordów z bazy danych wg. przedziału cen
    public Vector<Vector<String>> returnDataPrice(){
        try{
            String min = textFieldMin.getText();
            String max = textFieldMax.getText();
            Vector<Vector<String>> data = new Vector<>();

            pst = con.prepareStatement("SELECT * FROM products WHERE price BETWEEN ? AND ?");
            pst.setString(1, min);
            pst.setString(2, max);
            ResultSet rs = pst.executeQuery();

            while(rs.next()){

                Vector<String> record = new Vector<>();
                record.add(rs.getInt(1) + "");
                record.add(rs.getString(2));
                record.add(rs.getInt(3) + "");
                record.add(rs.getInt(4) + "");
                data.add(record);

            }
            return data;
        }
        catch (SQLException e5){
            e5.printStackTrace();
        }
        return null;
    }

    //Wyczyszczenie pól tekstowych
    public void clearTextBoxes(){
        textFieldName.setText("");
        textFieldPrice.setText("");
        textFieldQuantity.setText("");
        textFieldMin.setText("");
        textFieldName.requestFocus();
    }

    //Odświeżenie tabeli z wyświetleniem wszystkich rekordów
    public void refresh(){
        tableModel.setRowCount(0);
        for(int i = 0; i < returnData().size(); i++){
            String[] row = {
                    returnData().get(i).get(0),
                    returnData().get(i).get(1),
                    returnData().get(i).get(2),
                    returnData().get(i).get(3)};

            tableModel.addRow(row);
        }
    }

    //Odświeżenie tabeli z rekordami wg. zapytania
    public void refresh(String query){
        tableModel.setRowCount(0);
        for(int i = 0; i < returnData(query).size(); i++){
            String[] row = {
                    returnData(query).get(i).get(0),
                    returnData(query).get(i).get(1),
                    returnData(query).get(i).get(2),
                    returnData(query).get(i).get(3)};

            tableModel.addRow(row);
        }
    }

    //Wyświetlenie rekordów wg. przedziału cen
    public void refreshPrice(){
        tableModel.setRowCount(0);
        for(int i = 0; i < returnDataPrice().size(); i++){
            String[] row = {
                    returnDataPrice().get(i).get(0),
                    returnDataPrice().get(i).get(1),
                    returnDataPrice().get(i).get(2),
                    returnDataPrice().get(i).get(3)};

            tableModel.addRow(row);
        }
    }


    //Pobranie indeksu zaznaczonego rekordu
    int getSelectedIndex(){
        int index = productsTable.getSelectedRow();
        if (index<0) {
            JOptionPane.showMessageDialog(this, "No record selected.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return index;
    }
}

