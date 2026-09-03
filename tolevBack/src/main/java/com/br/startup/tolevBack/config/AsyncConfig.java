package com.br.startup.tolevBack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Pool onde as análises rodam.
 *
 * <p>Separado do pool do Tomcat de propósito: a análise varre até 180 dias de
 * transações e pode chamar o Gemini, então rodar na thread da requisição faria
 * o POST do usuário esperar por algo que não interessa à resposta.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "analysisExecutor")
    public Executor analysisExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("analise-");
        // Fila cheia significa que o usuário está lançando mais rápido do que a
        // análise processa. Descartar é correto: cada execução recalcula tudo do
        // zero, então a próxima já cobre o que foi descartado. Abortar com
        // exceção só encheria o log de ruído.
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(20);
        executor.initialize();
        return executor;
    }
}
