/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.devizexpres.smartwarehouse.exporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import ro.devizexpres.smartwarehouse.OlingoSampleApp;

/**
 *
 * @author robert.damian
 */
public class CSVExporter {
    private static final int PAGE_SIZE = 1000;
    private static final String SERVICE_URL = "https://thebestrunsap2018z3d3pet6df.hana.ondemand.com/ro/sap/hackathon/team12/service.xsodata";
    
    static OlingoSampleApp app;
    static Edm edm;
    
    public static void main (String... args) throws Exception {
        Authenticator authenticator = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return (new PasswordAuthentication("TEAM12_USER",
                        "12TheBestRunSap@2018!".toCharArray()));
            }
        };
        Authenticator.setDefault(authenticator);

        app = new OlingoSampleApp();
        String serviceUrl = "https://thebestrunsap2018z3d3pet6df.hana.ondemand.com/ro/sap/hackathon/team12/service.xsodata";

        edm = app.readEdm(serviceUrl);
        Map<String, String> params = new HashMap<>();
        params.put("filter", "ArticleCode eq 'A0001'");
        params.put("orderby", "Time desc");
        params.put("top", "100000");
        
        int pageCount = 0;
       
        File outputFile = new File("deliveries.csv");
        try (BufferedWriter bfw = new BufferedWriter(new FileWriter(outputFile))) {
            while (pageCount < 100) {
                processPage(bfw, pageCount++);
            }
        }
    }
    
    static void processPage(BufferedWriter bfw, int pageNumber) throws Exception{
        System.out.println("\n----- Read Feed ------------------------------");
        Map<String, String> params = new HashMap<>();
        params.put("filter", "ArticleCode eq 'A0001'");
        params.put("orderby", "Time desc");
        params.put("top", "" + PAGE_SIZE);
        params.put("skip", "" + PAGE_SIZE * pageNumber);
        ODataFeed feed = app.readFeed(edm, SERVICE_URL, OlingoSampleApp.APPLICATION_JSON, "Delivery", params);
        
        for(ODataEntry entry : feed.getEntries()) {
            StringBuilder line = new StringBuilder();
            Calendar c = (Calendar) entry.getProperties().get("Time");
            line.append(c.getTime().getTime())
                    .append(", ")
                    .append((String) entry.getProperties().get("Type"))
                    .append(", ")
                    .append((Integer) entry.getProperties().get("QuantitySingleUnits"))
                    .append("\n");
            bfw.append(line.toString());
        }
        
    }
}
