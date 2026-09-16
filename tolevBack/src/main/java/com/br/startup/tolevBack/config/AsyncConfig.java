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

    /**
     * Pool onde os extratos são lidos.
     *
     * <p>Separado do de análise porque as duas cargas são opostas. A análise é
     * curta e refaz tudo do zero, então descartar uma execução não custa nada.
     * Um extrato é o contrário: leva dezenas de segundos, o usuário está
     * olhando a tela esperando, e descartar significaria deixar a importação
     * presa em PROCESSANDO para sempre. Compartilhar o pool faria uma leitura de
     * PDF segurar a fila de análises atrás dela.
     *
     * <p>Daí também a fila curta e o AbortPolicy: recusar na hora dá ao
     * {@code ImportExtratoService} a chance de marcar a importação como falha e
     * dizer ao usuário para tentar de novo. Silenciosamente aceitar e nunca
     * processar seria a única saída pior.
     */
    @Bean(name = "extratoExecutor")
    public Executor extratoExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(20);
        executor.setThreadNamePrefix("extrato-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        // Uma leitura em curso já foi paga à API do Gemini; vale esperar o dobro
        // do timeout da chamada para não jogar fora no meio de um deploy.
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(120);
        executor.initialize();
        return executor;
    }
}
