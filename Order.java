import java.util.*;
import java.io.*;
import java.net.*;

public class Order implements java.io.Serializable{
    private static final long serialVersionUID = 4L;
    public int code;
    private ArrayList<Item> items = new ArrayList();
    private Date dateCreated = new Date();

    
    public Order(int code){
        this.code = code;
    }
    public Order(int code, Order order) {
    this.code = code;
    for (int i = 0; i < order.getItems().size(); i++) {
        Item original = order.getItems().get(i); // To add item to orders simplicity 
        items.add(new Item(original.name, original.getPriceWithoutTax(), original.id));
        }
    }
    public ArrayList<Item> getItems(){
        return items;
    }
    public Date getDateCreated(){return dateCreated;}
    public void addItem(Item item){
        items.add(item);
    }
    public double orderValue(){
       double sum=0;
       for(int i=0; i<items.size(); i++)
            sum += items.get(i).getPriceWithTax();
       return sum;
    }
    
}
