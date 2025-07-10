package br.com.devchampions.documentosextratortexto.util.converter;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DocumentoConverter {


    public static InputStream convertDocToPdf(InputStream docInputStream) throws IOException {
        try {
            // Usando Apache POI para ler DOC/DOCX
            XWPFDocument document = new XWPFDocument(docInputStream);

            // Criando PDF usando iText
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(pdfOutputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document doc = new Document(pdfDoc);

            // Extraindo texto do documento Word
            XWPFWordExtractor extractor = new XWPFWordExtractor(document);
            String text = extractor.getText();

            // Adicionando texto ao PDF
            doc.add(new Paragraph(text));

            // Fechando recursos
            doc.close();
            document.close();
            extractor.close();

            return new ByteArrayInputStream(pdfOutputStream.toByteArray());

        } catch (Exception e) {
            throw new IOException("Erro ao converter DOC para PDF: " + e.getMessage(), e);
        }
    }




}
