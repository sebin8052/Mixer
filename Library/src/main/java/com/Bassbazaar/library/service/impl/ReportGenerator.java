package com.Bassbazaar.library.service.impl;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;

@Service
public class ReportGenerator
{
    /* DashboardController*/
    public String generateProductStatsPdf(List<Object[]> productStats, String from, String to){
        String fileName = UUID.randomUUID().toString();
        String rootPath = System.getProperty("user.dir");
        String uploadDir = rootPath + "/src/main/resources/static/reports/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String filePath = uploadDir + fileName + ".pdf";
        Document document = new Document(PageSize.A1);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            Paragraph title = new Paragraph("Product Stats Report");
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);
            Paragraph period = new Paragraph("From "+from+" to "+to);
            period.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(period);
            document.add(new Paragraph("\n"));
            PdfPTable table = new PdfPTable(5);
            table.addCell("Product ID");
            table.addCell("Product Name");
            table.addCell("Category");
            table.addCell("Quantity Sold");
            table.addCell("Revenue");
            for (Object [] productStat : productStats) {
                table.addCell(productStat[0].toString());
                table.addCell(productStat[1].toString());
                table.addCell(String.valueOf(productStat[2]));
                table.addCell(String.valueOf(productStat[3]));
                table.addCell(String.valueOf(productStat[4]));
                System.out.println(productStat[0] + "" + productStat[1]);
            }
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell("Total Quantity Sold: " + String.valueOf(productStats.stream().mapToLong(stat -> (long) stat[3]).sum()));
            table.addCell("Total Revenue : " + String.valueOf(productStats.stream().mapToDouble(stat -> (double) stat[4]).sum()));
            document.add(table);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
        return filePath;
    }

    /*     DashboardController  */
    public String generateProductStatsCsv(List<Object[]> productStats) {
        String fileName = UUID.randomUUID().toString();
        String rootPath = System.getProperty("user.dir");
        String uploadDir = rootPath + "/src/main/resources/static/reports/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String filePath = uploadDir + fileName + ".csv";
        try (PrintWriter writer = new PrintWriter(filePath)) {
            writer.println("Product ID,Product Name,Category,Total Quantity Sold,Total Revenue");
            for (Object [] productStat : productStats) {
                writer.printf("%s,%s,%s,%s,%s%n",
                        productStat[0].toString(),
                        productStat[1].toString(),
                        productStat[2].toString(),
                        productStat[3].toString(),
                        productStat[4].toString());
                System.out.println(productStat[0] + "" + productStat[1]);
            }
            writer.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filePath;
    }
}

