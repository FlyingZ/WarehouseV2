/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.devizexpres.smartwarehouse.state;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 *
 * @author robert.damian
 */
public class StorageUnit {
    private String code;
    private int capacity;
    private Integer maxWeight;
    private BigDecimal maxVolume;
    
    private int currentQuantity;
    private Set<ArticlesStock> articlesStockSet;

    public StorageUnit(String code, int capacity, Integer maxWeight, BigDecimal maxVolume) {
        this.code = code;
        this.capacity = capacity;
        this.maxWeight = maxWeight;
        this.maxVolume = maxVolume;
        currentQuantity = 0;
    
        articlesStockSet = new TreeSet<>();
    }

    
    
    public boolean canHold(String articleCod) {
        return getStocksFor(articleCod).size() > 0;
    }
    
    public List<ArticlesStock> getStocksFor(String articleCod) {
        return articlesStockSet.stream()
                .filter(stock -> stock.contains(articleCod))
                .collect(Collectors.toList());
    }
    
    public boolean add(String articleCod, int amount) {
        return getStocksFor(articleCod).stream().map((stock) -> stock.add(amount)).anyMatch((isOk) -> (isOk));
    }
    
    public boolean forceAdd(String articleCod, int amount) {
        return getStocksFor(articleCod).stream().map((stock) -> stock.forceAdd(amount)).anyMatch((isOk) -> (isOk));
    }
    
    public String getCod() {
        return code;
    }
    
    public void addArticlesStock(ArticlesStock stock) {
        articlesStockSet.add(stock);
    }
    
    public void print (int identation) {
        String prefix = new String(new char[identation]).replace("\0", "\t");
        
        System.out.println(prefix + "Storage unit: " + code);
        for (ArticlesStock s : articlesStockSet) {
            s.print(identation + 1);
        }
    }
}
