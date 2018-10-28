/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.devizexpres.smartwarehouse.state;

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
    
    public boolean add(int amount) {
        if (currentQuantity + amount > maxQuantity) {
            return false;
        }
        
        currentQuantity += amount;
        return true;
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
    
}
