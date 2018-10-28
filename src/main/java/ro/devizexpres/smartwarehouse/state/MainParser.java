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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
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
        
        da = new Deposit("A", 0);
        db = new Deposit("B", 15);
        dc = new Deposit("C", 60);
        dd = new InfiniteDeposit("D", 24 *60);
        
        da.setSourceDeposit(db);
        db.setSourceDeposit(dc);
        dc.setSourceDeposit(dd);
        
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
        
        params = new HashMap<>();
        params.put("orderby", "Time asc");
        params.put("top", "1000");
        feed = app.readFeed(edm, serviceUrl, OlingoSampleApp.APPLICATION_JSON, "Delivery", params);
        
        for (ODataEntry entry : feed.getEntries()) {
            processDelivery(entry);
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
    /*
        "GenID": "32032679340775198",
        "Time": "/Date(1514756928929)/",
        "ArticleCode": "A0028",
        "Type": "OUT",
        "QuantitySingleUnits": 356
    */
    void processDelivery(ODataEntry entry) {
        Map<String, Object> a = entry.getProperties();
        int amount = (Integer) a.get("QuantitySingleUnits");
        String articleCod = (String) a.get("ArticleCode");
        Calendar time = (Calendar) a.get("Time"); 
        boolean out = "OUT".equals((String) a.get("Type"));
        
        if (!out) {
            int remaining = amount;
            for (Deposit d : depositList) {
                 remaining = d.add(articleCod, remaining);
            }
            return ;
        }
        Calendar timeToDeliverPaleti = (Calendar) time.clone(), 
                timeToDeliverUnits = (Calendar) time.clone();
        Article articleByCod = articlesList.parallelStream().filter(article -> articleCod.equals(article.getCod())).findFirst().get();
        int paleti = amount / articleByCod.getPalletQuantity();
        int availableInC = dc.readAllAvailableUnits(articleCod);
     
        boolean finishedOptimally = false;
        if (availableInC < paleti * articleByCod.getPalletQuantity()) {
            int availableInAB = da.readAllAvailableUnits(articleCod, 1);
            if (availableInAB + availableInC > amount) {
                dc.processStorageOut(articleCod, availableInC, time);
                timeToDeliverUnits = da.processStorageOut(articleCod, availableInAB, time);
                finishedOptimally = true;
            }
        }
        if (!finishedOptimally) {
            timeToDeliverPaleti = dc.processStorageOut(articleCod, paleti * articleByCod.getPalletQuantity(), time);
            timeToDeliverUnits = da.processStorageOut(articleCod, amount % articleByCod.getPalletQuantity(), time);
        }
        
        Calendar deliveryTime = timeToDeliverUnits;
        if (timeToDeliverUnits.after(timeToDeliverPaleti)) {
            deliveryTime = timeToDeliverUnits;
        }
        long wastedTime = TimeUnit.MILLISECONDS.toMinutes(deliveryTime.getTimeInMillis() - time.getTimeInMillis());
        if (wastedTime == 0) {
            return ;
        }
        
        System.out.println("Delivered : " + amount + " of " + articleCod + " in " + wastedTime);
    }
    
    public static void main(String... args) throws Exception{
        new MainParser().parse();
    }
}
