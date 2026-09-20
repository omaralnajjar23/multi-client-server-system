import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.JOptionPane;

public class Item implements java.io.Serializable{
    public String name;
    public int id;
    private ArrayList<String> reviews = new ArrayList();  
    private double priceWithoutTax;
    private static double salesTax = 0.16;

    public Item(){
        name = JOptionPane.showInputDialog("Please enter item name: ");
        priceWithoutTax = Double.parseDouble(JOptionPane.showInputDialog("Please enter item price: "));
        while(priceWithoutTax<=0){
            priceWithoutTax = Double.parseDouble(JOptionPane.showInputDialog("Please enter positive non-zero value for the price: "));
        }
        id = Integer.parseInt(JOptionPane.showInputDialog("Please enter item ID: "));
        
    }
    public Item(String name, double priceWithoutTax, int id){
        this.name = name;
        this.priceWithoutTax = priceWithoutTax;
        this.id = id;
    }
    public void addReview(){
        reviews.add(JOptionPane.showInputDialog("Please enter your review for "+name));
    }
    public ArrayList<String> getReviews(){
        return reviews;
    }
    public double getPriceWithoutTax(){
        return priceWithoutTax;
    }
    public void setPriceWithoutTax(double priceWithoutTax){
        if(priceWithoutTax>0) this.priceWithoutTax = priceWithoutTax;
    }
    public static double getSalesTax() {
        return salesTax;
    }
    public static void setSalesTax(double tax) {
        salesTax = tax;
    }
    public double getPriceWithTax(){
        return priceWithoutTax + priceWithoutTax * salesTax;
    }
    public void printReviews(){
        String s = "";
        for(int i=0; i<reviews.size(); i++)
            s+=reviews.get(i)+"\n";
        JOptionPane.showMessageDialog(null, s);
    }
    @Override
    public String toString(){
        return id+"-"+name+"-"+getPriceWithTax();
    }
}

