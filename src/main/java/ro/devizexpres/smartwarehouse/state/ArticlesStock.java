/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.devizexpres.smartwarehouse.state;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author robert.damian
 */
public class ArticlesStock implements Comparable<ArticlesStock> {
    private String articleCode;
    private int minQuantity;
    private int maxQuantity;
    private int maxCapacity;

    private int currentQuantity;

    public ArticlesStock(String articleCode, int minQuantity, int maxQuantity, int maxCapacity) {
        this.articleCode = articleCode;
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity;
        this.maxCapacity = maxCapacity;
        this.currentQuantity = 0;
    }
    
    public boolean contains(String articleCode) {
        return this.articleCode.equals(articleCode);
    }
    
    public int add(int amount) {
        if (amount < 0) {
            Logger.getLogger(ArticlesStock.class.getName())
                    .log(Level.SEVERE, "Added negative amount");
        }
        int remaining;
        if (currentQuantity + amount > maxQuantity) {
            remaining = currentQuantity + amount - maxQuantity;
            currentQuantity = maxQuantity;
            return remaining;
        }
        
        currentQuantity += amount;
        return 0;
    }
    
    public boolean forceAdd(int amount) {
//        if (currentQuantity + amount > maxCapacity) {
//            return false;
//        }
        
        currentQuantity += amount;
        return true;
    }

    @Override
    public int compareTo(ArticlesStock o) {
        return articleCode.compareTo(o.articleCode);
    }
    
    public void print(int identation) {
        String prefix = new String(new char[identation]).replace("\0", "\t");
        System.out.println(prefix + "Stock for " + articleCode + ", currentQuantity = " + currentQuantity);
    }
    
    public int getArticles(int amount) {
        if (amount < 0) {
            Logger.getLogger(ArticlesStock.class.getName())
                    .log(Level.SEVERE, "Received order for negative amount {0}", amount);
        }
        
        int delivered;
        if (amount > currentQuantity) {
            delivered = currentQuantity;
            currentQuantity = 0;
            return delivered;
        }
        currentQuantity -= amount;
        return amount;
    }
    
    public int getCurrentQuantity() {
        return currentQuantity;
    }
}
