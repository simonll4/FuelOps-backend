package ar.edu.iw3.integration;

import ar.edu.iw3.util.PdfGenerator;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class PdfService {

    @Autowired
    private PdfGenerator pdfGenerator;

    public byte[] generateReport(String content) throws DocumentException, IOException {
        return pdfGenerator.generateSimpleTextPdf(content);
    }
}
