package com.ai.agent.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DocumentResourceReader {

    public List<Document> getDocsFromTikaDocumentReader(String resourceUrl) {
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resourceUrl);
        return tikaDocumentReader.get();
    }

    public List<Document> getDocsFromPagePdfDocumentReader(String resourceUrl) {
        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(resourceUrl,
                PdfDocumentReaderConfig.builder()
                        .withPageTopMargin(0)
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                .withNumberOfTopTextLinesToDelete(0)
                                .build())
                        .withPagesPerDocument(1)
                        .build());
        return pdfReader.get();
    }

    public List<Document> getDocsFromParagraphPdfDocumentReader(String resourceUrl) {
        ParagraphPdfDocumentReader pdfReader = new ParagraphPdfDocumentReader(resourceUrl,
                PdfDocumentReaderConfig.builder()
                        .withPageTopMargin(0)
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                .withNumberOfTopTextLinesToDelete(0)
                                .build())
                        .withPagesPerDocument(1)
                        .build());
        return pdfReader.get();
    }

}
