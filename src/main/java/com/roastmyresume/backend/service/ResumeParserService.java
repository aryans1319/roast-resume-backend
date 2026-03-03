package com.roastmyresume.backend.service;

import com.roastmyresume.backend.exception.RoastException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Slf4j
@Service
public class ResumeParserService {

    private static final String PDF_CONTENT_TYPE = "application/pdf";
    private static final int MIN_TEXT_LENGTH = 100;

    public String extractTextFromPdf(MultipartFile file) {
        validateFile(file);
        try {
            byte[] fileBytes = file.getBytes();
            try (PDDocument document = Loader.loadPDF(fileBytes)) {
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document);
                if (text == null || text.trim().length() < MIN_TEXT_LENGTH) {
                    throw new RoastException("Could not extract enough text. Make sure it's not a scanned image PDF.");
                }
                log.info("Successfully extracted {} characters from resume", text.length());
                return text.trim();
            }
        } catch (RoastException e) {
            throw e;
        } catch (IOException e) {
            log.error("Failed to parse PDF", e);
            throw new RoastException("Failed to read your PDF. Please make sure it's a valid file.");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RoastException("Please upload a resume file.");
        }
        if (!PDF_CONTENT_TYPE.equals(file.getContentType())) {
            throw new RoastException("Only PDF files are supported.");
        }
    }
}