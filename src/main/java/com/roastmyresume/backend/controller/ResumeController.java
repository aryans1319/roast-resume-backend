package com.roastmyresume.backend.controller;

import com.roastmyresume.backend.dto.RoastResponse;
import com.roastmyresume.backend.service.RoastService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/v1/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final RoastService roastService;

    @PostMapping(value = "/roast", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RoastResponse> roastResume(
            @RequestPart("resume") MultipartFile resume,
            @RequestPart("jobDescription") String jobDescription) {

        log.info("Received roast request for file: {}", resume.getOriginalFilename());
        RoastResponse response = roastService.roastResume(resume, jobDescription);
        return ResponseEntity.ok(response);
    }
}