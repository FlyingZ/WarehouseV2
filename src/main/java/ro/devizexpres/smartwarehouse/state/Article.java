/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.devizexpres.smartwarehouse.state;

import java.math.BigDecimal;
import java.util.Map;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;

/**
 *
 * @author robert.damian
 */
public class Article {
    private String cod;
    private int palletQuantity;
    private BigDecimal singleUnitWeight;
    private BigDecimal palletWeight;
    private BigDecimal singleUnitVolume;
    private BigDecimal palletVolume;

    public Article(String cod, int palletQuantity, BigDecimal singleUnitWeight, BigDecimal palletWeight, BigDecimal singleUnitVolume, BigDecimal palletVolume) {
        this.cod = cod;
        this.palletQuantity = palletQuantity;
        this.singleUnitWeight = singleUnitWeight;
        this.palletWeight = palletWeight;
        this.singleUnitVolume = singleUnitVolume;
        this.palletVolume = palletVolume;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public double getPalletQuantity() {
        return palletQuantity;
    }

    public BigDecimal getSingleUnitWeight() {
        return singleUnitWeight;
    }

    public void setSingleUnitWeight(BigDecimal singleUnitWeight) {
        this.singleUnitWeight = singleUnitWeight;
    }

    public BigDecimal getPalletWeight() {
        return palletWeight;
    }

    public void setPalletWeight(BigDecimal palletWeight) {
        this.palletWeight = palletWeight;
    }

    public BigDecimal getSingleUnitVolume() {
        return singleUnitVolume;
    }

    public void setSingleUnitVolume(BigDecimal singleUnitVolume) {
        this.singleUnitVolume = singleUnitVolume;
    }

    public BigDecimal getPalletVolume() {
        return palletVolume;
    }

    public void setPalletVolume(BigDecimal palletVolume) {
        this.palletVolume = palletVolume;
    }
    

    /*
    "Code": "A0001",
    "PalletQuantity": 3,
    "SingleUnitWeight": "0.5",
    "PalletWeight": "2",
    "SingleUnitVolume": "0.001",
    "PalletVolume": "0.003"
    */
    public static Article createFromEntry(ODataEntry entry) {
        Map<String, Object> a = entry.getProperties();
        return new Article((String) a.get("Code"), 
                (Integer) a.get("PalletQuantity"),
                (BigDecimal) a.get("SingleUnitWeight"),
                (BigDecimal) a.get("PalletWeight"),
                (BigDecimal) a.get("SingleUnitVolume"),
                (BigDecimal) a.get("PalletVolume"));
    }
}
