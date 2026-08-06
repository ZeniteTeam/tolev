package com.br.startup.tolevBack.external.resend.services;

import com.br.startup.tolevBack.users.integration.api.UserIntegrationApi;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class ResendEmailService {

    public final UserIntegrationApi userIntegrationApi;

    public final Resend resend;

    public ResendEmailService(Resend resend, UserIntegrationApi userIntegrationApi) {
        this.userIntegrationApi = userIntegrationApi;
        this.resend = resend;
    }

    public void enviarEmail(
//            String email,
//            String nome,
            String html
    ) {

        CreateEmailOptions params =
                CreateEmailOptions.builder()
                        //email do tolev
                        .from("xxxxxx@tolev.com.br")
                        //email do remetente (substituir por varável)
                        .to("xxxxxxxxxxx@gmail.com")
                        //nome do remetente (substituir por varável)
                        .subject("nome" + ", seu relatório Tolev chegou!")
                        .html(html)
                        .build();

        try {
            resend.emails().send(params);
        } catch (ResendException e) {
            throw new RuntimeException(
                    "Erro ao enviar email",
                    e
            );
        }
    }


}
