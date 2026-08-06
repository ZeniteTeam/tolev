package com.br.startup.tolevBack.external.resend;

import com.br.startup.tolevBack.users.integration.api.UserIntegrationApi;
import com.resend.Resend;
import org.springframework.stereotype.Service;

@Service
public class ResendEmailService {

    public final UserIntegrationApi userIntegrationApi;

    public final Resend resend;

    public ResendEmailService(Resend resend, UserIntegrationApi userIntegrationApi) {
        this.userIntegrationApi = userIntegrationApi;
        this.resend = resend;
    }


}
