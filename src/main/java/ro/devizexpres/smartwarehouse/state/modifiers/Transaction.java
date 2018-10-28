/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.devizexpres.smartwarehouse.state.modifiers;

/**
 *
 * @author robert.damian
 */
public class Transaction {
    private String articleCod;
    private int quantity;

    public Transaction(String articleCod, int quantity) {
        this.articleCod = articleCod;
        this.quantity = quantity;
    }

    public String getArticleCod() {
        return articleCod;
    }

    public int getQuantity() {
        return quantity;
    }
}
