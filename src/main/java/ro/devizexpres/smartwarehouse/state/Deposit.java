/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.devizexpres.smartwarehouse.state;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author robert.damian
 */
public class Deposit {
    
    String depositName;
    Set<StorageUnit> storageUnitSet;
    
    public Deposit(String depositName) {
        this.depositName = depositName;
        storageUnitSet = new HashSet<>();
    }
    
    public void add(String articleCod, int amount) {
        List<StorageUnit> availableStorageUnits = storageUnitSet.stream().filter(unit -> unit.canHold(articleCod)).collect(Collectors.toList());
        
        for (StorageUnit unit : availableStorageUnits) {
            boolean isOk = unit.add(articleCod, amount);
            
            if (isOk) {
                return ;
            }
        }
        
        Logger.getLogger(Deposit.class.getName()).log(Level.WARNING, 
                String.format("Was not able to store article %s in deposit %s", articleCod, depositName));
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
}
