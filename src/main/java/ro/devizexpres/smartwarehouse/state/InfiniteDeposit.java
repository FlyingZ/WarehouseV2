/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.devizexpres.smartwarehouse.state;

import java.util.Calendar;
import ro.devizexpres.smartwarehouse.state.modifiers.Transaction;

/**
 *
 * @author robert.damian
 */
public class InfiniteDeposit extends Deposit{
    
    public InfiniteDeposit(String depositName, int addedTime) {
        super(depositName, addedTime);
    }
    
    
    @Override
    protected int getArticles(String articleCod, int amount) {
        return amount;
    }
    
    @Override
    protected void getSupplyFromSource(String articleCod, int amount, Calendar time) {
        // No supply. We already have everything
    }
}
