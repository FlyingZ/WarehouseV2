/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.devizexpres.smartwarehouse.state;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import ro.devizexpres.smartwarehouse.state.modifiers.Transaction;

/**
 *
 * @author robert.damian
 */
public class Deposit {
    
    String depositName;
    Set<StorageUnit> storageUnitSet;
    Map<Transaction, Calendar> futureOrders;
    
    Deposit sourceDeposit;
    private int addedTime;
    
    public Deposit(String depositName, int addedTime) {
        this.depositName = depositName;
        storageUnitSet = new HashSet<>();
        futureOrders = new HashMap<>();
        
        this.addedTime = addedTime;
    }
    
    public int add(String articleCod, int amount) {
        List<StorageUnit> availableStorageUnits = storageUnitSet.stream().filter(unit -> unit.canHold(articleCod)).collect(Collectors.toList());
        
        int remaining = amount;
        for (StorageUnit unit : availableStorageUnits) {
             remaining = unit.add(articleCod, remaining);
        }
        
        return remaining;
    }
    
    public void forceAdd(String articleCod, int amount) {
        List<StorageUnit> availableStorageUnits = storageUnitSet.stream().filter(unit -> unit.canHold(articleCod)).collect(Collectors.toList());
        
        for (StorageUnit unit : availableStorageUnits) {
            boolean isOk = unit.forceAdd(articleCod, amount);
            
            if (isOk) {
                return ;
            }
        }
        
        Logger.getLogger(Deposit.class.getName()).log(Level.WARNING, 
                String.format("Was not able to store article %s in deposit %s", articleCod, depositName));
    }
    
    public void addStorageUnit(StorageUnit u) {
        storageUnitSet.add(u);
    }
    
    public String getDepositName() {
        return depositName;
    }
    
    public void print() {
        System.out.println("~~~~~~~~~ " + depositName + " ~~~~~~~~~~");
        for (StorageUnit u : storageUnitSet) {
            u.print(1);
        }
    }
    
    private void handleBeforeInteraction(Calendar time) {
        futureOrders.entrySet().stream().filter((order) -> (order.getValue().before(time))).map((order) -> order.getKey()).forEach((t) -> {
            if ("C".equals(depositName)) {
                Logger.getLogger(Deposit.class.getName()).log(Level.INFO, "Am procesat o tranzactie cu amount: {0}", t.getQuantity());
            }
            add(t.getArticleCod(), t.getQuantity());
        });
    }
    public Calendar processStorageOut(String articleCod, int amount, Calendar time) {
        return processStorageOut(articleCod, amount, time, false);
    }
    
    public Calendar processStorageOut(String articleCod, int amount, Calendar time, boolean forOther) {        
        handleBeforeInteraction(time);
        int retrieved = getArticles(articleCod, amount);
        Calendar result = (Calendar) time.clone();
        if (forOther) {
            result.add(Calendar.MINUTE, addedTime);
        }
        if (retrieved < amount) {
            result = sourceDeposit.processStorageOut(articleCod, amount - retrieved, result, true);
        } else {
            retrieved = amount;
        }
        
        getSupplyFromSource(articleCod, retrieved, time);
        
        return result;
    }
    
    protected void getSupplyFromSource(String articleCod, int amount, Calendar time) {
        if (amount < 0) {
            Logger.getLogger(Deposit.class.getName()).log(Level.SEVERE, "Ordered negative amount {0}", amount);
        }
        // rafac stoc
        Calendar supplyTime = sourceDeposit.processStorageOut(articleCod, amount, time, true);
        futureOrders.put(new Transaction(articleCod, amount), supplyTime);
    }
    
    protected int getArticles(String articleCod, int amount) {
        int retrievedFromDeposit = 0;
        for (StorageUnit u : storageUnitSet) {
            int retrieved = u.getArticles(articleCod, amount);
            retrievedFromDeposit += retrieved;
            
            if (retrievedFromDeposit == amount) {
                return amount;
            }
        }
        
        return retrievedFromDeposit;
    } 
    
    public void setSourceDeposit(Deposit deposit) {
        sourceDeposit = deposit;
    }
    
    public int readAllAvailableUnits(String articleCod) {
        return readAllAvailableUnits(articleCod, 0);
    }
    
    public int readAllAvailableUnits(String articleCode, int depth) {
        int availableHere = storageUnitSet.stream()
                .mapToInt(storageUnit -> storageUnit.readAvailableUnits(articleCode))
                .sum();
                
        if (depth == 0) {
            return availableHere;
        }
        
        return availableHere + sourceDeposit.readAllAvailableUnits(articleCode, depth - 1);
    }
}
