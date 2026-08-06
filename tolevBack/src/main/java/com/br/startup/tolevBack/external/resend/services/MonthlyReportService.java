package com.br.startup.tolevBack.external.resend.services;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class MonthlyReportService {

    private final TemplateEngine templateEngine;
    private final ResendEmailService resendEmailService;

    public MonthlyReportService(TemplateEngine templateEngine, ResendEmailService resendEmailService) throws IOException {
        this.templateEngine = templateEngine;
        this.resendEmailService = resendEmailService;
    }

    byte[] bytes = new ClassPathResource("templates/monthly-report.html")
            .getInputStream()
            .readAllBytes();

    String html = new String(bytes, StandardCharsets.UTF_8);

    public void enviarRelatorio() {

    resendEmailService.enviarEmail(
        html
    );

    }

    //Descomentar para forçar o envio
    
//    @PostConstruct
//    public void debugEnviarEmail() {
//        System.out.println(">>> Disparando e-mail de teste via @PostConstruct...");
//        enviarRelatorio();
//    }
}
