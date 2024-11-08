package ar.edu.iw3.util;

import ar.edu.iw3.model.Product;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PdfGenerator {

    public static byte[] generateSimpleTextPdf(String content) throws DocumentException, IOException {
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, baos);
        document.open();
        document.add(new Paragraph(content));
        document.close();
        return baos.toByteArray();
    }

    public static byte[] generateFuelLoadingReconciliationReport(
            float initialWeighing,
            float finalWeighing,
            float productLoaded,
            float netWeight,
            float difference,
            float avgTemperature,
            float avgDensity,
            float avgFlow,
            Product product
    ) throws DocumentException, IOException {
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        document.open();

        Paragraph header = new Paragraph("REPORTE DE CONCILIACIÓN DE CARGA DE COMBUSTIBLE",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY));
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);

        document.add(new Paragraph("Fecha de generación: " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY)));
        document.add(new Paragraph(" "));

        Image logo = Image.getInstance("logo.png");
        logo.scaleToFit(500, 150);
        logo.setAlignment(Element.ALIGN_CENTER);
        document.add(logo);

        // Información del producto en una tabla
        PdfPTable productTable = new PdfPTable(2);
        productTable.setWidthPercentage(100);
        productTable.setSpacingBefore(10f);
        productTable.setSpacingAfter(10f);
        PdfPCell productHeader = new PdfPCell(new Phrase("Información del Producto Cargado",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
        productHeader.setColspan(2);
        productHeader.setBackgroundColor(BaseColor.DARK_GRAY);
        productHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        productTable.addCell(productHeader);
        productTable.addCell("Producto:");
        productTable.addCell(product.getProduct());
        productTable.addCell("Descripción:");
        productTable.addCell(product.getDescription());
        document.add(productTable);

        // Datos de pesaje en una tabla
        PdfPTable weighingTable = new PdfPTable(2);
        weighingTable.setWidthPercentage(100);
        weighingTable.setSpacingBefore(10f);
        weighingTable.setSpacingAfter(10f);
        PdfPCell weighingHeader = new PdfPCell(new Phrase("Datos del Pesaje",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
        weighingHeader.setColspan(2);
        weighingHeader.setBackgroundColor(BaseColor.DARK_GRAY);
        weighingHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        weighingTable.addCell(weighingHeader);
        weighingTable.addCell("Pesaje inicial:");
        weighingTable.addCell(initialWeighing + " kg");
        weighingTable.addCell("Pesaje final:");
        weighingTable.addCell(finalWeighing + " kg");
        weighingTable.addCell("Producto cargado:");
        weighingTable.addCell(productLoaded + " kg");
        weighingTable.addCell("Neto por balanza:");
        weighingTable.addCell(netWeight + " kg");
        weighingTable.addCell("Diferencia entre balanza y caudalímetro:");
        weighingTable.addCell(difference + " kg");
        document.add(weighingTable);

        // Promedios durante la carga en una tabla
        PdfPTable averagesTable = new PdfPTable(2);
        averagesTable.setWidthPercentage(100);
        averagesTable.setSpacingBefore(10f);
        averagesTable.setSpacingAfter(10f);
        PdfPCell averagesHeader = new PdfPCell(new Phrase("Promedios durante la Carga",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
        averagesHeader.setColspan(2);
        averagesHeader.setBackgroundColor(BaseColor.DARK_GRAY);
        averagesHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        averagesTable.addCell(averagesHeader);
        averagesTable.addCell("Promedio de temperatura:");
        averagesTable.addCell(avgTemperature + " °C");
        averagesTable.addCell("Promedio de densidad:");
        averagesTable.addCell(avgDensity + " kg/m³");
        averagesTable.addCell("Promedio de caudal:");
        averagesTable.addCell(avgFlow + " L/h");
        document.add(averagesTable);

        // Nota de confidencialidad y firma
        document.add(new Paragraph("Generado por: FuelOps S.A",
                FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY)));
        document.add(new Paragraph("Este reporte es confidencial y está destinado únicamente para el uso de la empresa.",
                FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY)));

        document.close();
        return baos.toByteArray();
    }

}
