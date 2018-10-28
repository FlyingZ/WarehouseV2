/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.devizexpres.smartwarehouse.state;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.exception.ODataException;
import ro.devizexpres.smartwarehouse.OlingoSampleApp;

/**
 *
 * @author robert.damian
 */
public class MainParser {
    private List<Article> articlesList;
    
    private Deposit da, db, dc, dd;
    private List<Deposit> depositList;
    private List<StorageUnit> storageUnitList;
    
    public MainParser() {
        articlesList = new ArrayList<>();
        storageUnitList = new ArrayList<>();
        
        da = new Deposit("A");
        db = new Deposit("B");
        dc = new Deposit("C");
        dd = new Deposit("D");
        depositList = new ArrayList<>();
        depositList.add(da);
        depositList.add(db);
        depositList.add(dc);
        depositList.add(dd);
    }
    
    public void parse() throws IOException, ODataException {
        Authenticator authenticator = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return (new PasswordAuthentication("TEAM12_USER",
                        "12TheBestRunSap@2018!".toCharArray()));
            }
        };
        Authenticator.setDefault(authenticator);

        OlingoSampleApp app = new OlingoSampleApp();
        String serviceUrl = "https://thebestrunsap2018z3d3pet6df.hana.ondemand.com/ro/sap/hackathon/team12/service.xsodata";

        Edm edm = app.readEdm(serviceUrl);
        Map<String, String> params = new HashMap<>();
//        params.put("filter", "Code eq 'A0001'");
        System.out.println("\n----- Read Feed ------------------------------");
        ODataFeed feed = app.readFeed(edm, serviceUrl, OlingoSampleApp.APPLICATION_JSON, "Article", params);

        System.out.println("Read: " + feed.getEntries().size() + " entries: ");
        for (ODataEntry entry : feed.getEntries()) {
            articlesList.add(Article.createFromEntry(entry));
        }
        
        feed = app.readFeed(edm, serviceUrl, OlingoSampleApp.APPLICATION_JSON, "StorageArea");
        
        for (ODataEntry entry : feed.getEntries()) {
            processStorageUnit(entry);
        }
        
        feed = app.readFeed(edm, serviceUrl, OlingoSampleApp.APPLICATION_JSON, "StockRule");
        
        for (ODataEntry entry : feed.getEntries()) {
            processArticlesStock(entry);
        }
        
        feed = app.readFeed(edm, serviceUrl, OlingoSampleApp.APPLICATION_JSON, "Stock");
        
        for (ODataEntry entry : feed.getEntries()) {
            processStorageStocks(entry);
        }
        
        depositList.stream().forEach((d) -> {
            d.print();
        });
    }
    
    /*
        "Code": "A0001",
        "Area": "A",
        "Capacity": 200,
        "MaxWeight": 5,
        "MaxVolume": "0.001"
    */
    void processStorageUnit(ODataEntry entry) {
        Map<String, Object> a = entry.getProperties();
        StorageUnit u = new StorageUnit((String) a.get("Code"), 
                (Integer) a.get("Capacity"), 
                (Integer) a.get("MaxWeight"), 
                (BigDecimal) a.get("MaxVolume"));
        
        String depositCode = (String) a.get("Area");
        for (Deposit deposit : depositList) {
            if (depositCode.equals(deposit.getDepositName())) {
                deposit.addStorageUnit(u);
            }
        }
        storageUnitList.add(u);
    }
    
    /*
        "ArticleCode": "A0001",
        "StorageAreaCode": "A0001",
        "MinQuantity": 1,
        "MaxQuantity": 3,
        "MaxCapacity": 3
    */
    void processArticlesStock(ODataEntry entry) {
        Map<String, Object> a = entry.getProperties();
        ArticlesStock s = new ArticlesStock((String) a.get("ArticleCode"), 
                (Integer) a.get("MinQuantity"), 
                (Integer) a.get("MaxQuantity"), 
                (Integer) a.get("MaxCapacity"));
        
        StorageUnit unit = storageUnitList.stream().filter(u -> u.getCod().equals((String) a.get("StorageAreaCode"))).findFirst().get();
        unit.addArticlesStock(s);
    }
    
    /*
        "ArticleCode": "A0001",
        "StockAreaA": 3,
        "StockAreaB": 8,
        "StockAreaC": 8
    */
    void processStorageStocks(ODataEntry entry) {
        Map<String, Object> a = entry.getProperties();
        String cod = (String) a.get("ArticleCode");
        
        da.forceAdd(cod, (Integer) a.get("StockAreaA"));
        db.forceAdd(cod, (Integer) a.get("StockAreaB"));
        dc.forceAdd(cod, (Integer) a.get("StockAreaC"));
    }
    
    public static void main(String... args) throws Exception{
        new MainParser().parse();
    }
}
