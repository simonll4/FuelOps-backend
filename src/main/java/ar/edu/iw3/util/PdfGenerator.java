package ar.edu.iw3.util;

import ar.edu.iw3.model.Product;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
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

        // Encabezado del reporte
        document.add(new Paragraph("REPORTE DE CONCILIACIÓN DE CARGA DE COMBUSTIBLE", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
        document.add(new Paragraph("Fecha de generación: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));
        document.add(new Paragraph(" "));

        // Información del producto
        document.add(new Paragraph("Información del Producto Cargado:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
        document.add(new Paragraph("Producto: " + product.getProduct()));
        document.add(new Paragraph("Descripción: " + product.getDescription()));
        document.add(new Paragraph("Temperatura del Producto: " + product.getThresholdTemperature() + " °C"));
        document.add(new Paragraph(" "));

        // Datos de pesaje
        document.add(new Paragraph("Datos del Pesaje:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
        document.add(new Paragraph("Pesaje inicial: " + initialWeighing + " kg"));
        document.add(new Paragraph("Pesaje final: " + finalWeighing + " kg"));
        document.add(new Paragraph("Producto cargado: " + productLoaded + " kg"));
        document.add(new Paragraph("Neto por balanza (Pesaje final - Pesaje inicial): " + netWeight + " kg"));
        document.add(new Paragraph("Diferencia entre balanza y caudalímetro (Neto por balanza - Producto cargado): " + difference + " kg"));
        document.add(new Paragraph(" "));

        // Promedios de condiciones de carga
        document.add(new Paragraph("Promedios durante la Carga:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
        document.add(new Paragraph("Promedio de temperatura: " + avgTemperature + " °C"));
        document.add(new Paragraph("Promedio de densidad: " + avgDensity + " kg/m³"));
        document.add(new Paragraph("Promedio de caudal: " + avgFlow + " L/h"));
        document.add(new Paragraph(" "));

        // Firma y detalles adicionales
        document.add(new Paragraph("Generado por: FuelOps S.A ", FontFactory.getFont(FontFactory.HELVETICA, 10)));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Este reporte es confidencial y está destinado únicamente para el uso de la empresa. Cualquier divulgación o distribución no autorizada está estrictamente prohibida."));

        document.close();
        return baos.toByteArray();
    }

}
