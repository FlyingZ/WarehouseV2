/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ro.devizexpres.smartwarehouse;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.HashMap;
import java.util.Map;
import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.exception.ODataException;
import static ro.devizexpres.smartwarehouse.OlingoSampleApp.prettyPrint;


/**
 *
 * @author Florin
 */
public class QRCodeGen {

    private static void generateQRCodeImage(String text, int width, int height, String filePath)
            throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }

    public static void main(String[] args) throws IOException, ODataException, Exception {
        Authenticator authenticator = new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return (new PasswordAuthentication("TEAM12_USER",
                        "12TheBestRunSap@2018!".toCharArray()));
            }
        };
        Authenticator.setDefault(authenticator);
        
        generateQRCode("A0001");
    }
    
    public static void generateQRCode(String articleName) throws IOException, ODataException{
    OlingoSampleApp article = new OlingoSampleApp();
        String serviceUrl = "https://thebestrunsap2018z3d3pet6df.hana.ondemand.com/ro/sap/hackathon/team12/service.xsodata";
        Map<String, String> params = new HashMap<>();
        String articleParam = "Code eq '" + articleName + "'";
        params.put("filter", articleParam);
        Edm edm = article.readEdm(serviceUrl);
        System.out.println("\n----- Read Feed ------------------------------");
        ODataFeed feed = article.readFeed(edm, serviceUrl, OlingoSampleApp.APPLICATION_JSON, "Article", params);
        String dataToPrint = new String();
        System.out.println("Read: " + feed.getEntries().size() + " entries: ");
        for (ODataEntry entry : feed.getEntries()) {
            System.out.println("##########");
            System.out.println("Entry:\n" + prettyPrint(entry));
            System.out.println("##########");
            dataToPrint = prettyPrint(entry);
        }
        try {
            generateQRCodeImage(dataToPrint, 350, 350, "./QRcodes/" + articleName + ".png");
        } catch (WriterException e) {
            System.out.println("Could not generate QR Code, WriterException :: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Could not generate QR Code, IOException :: " + e.getMessage());
        }
    }
}


