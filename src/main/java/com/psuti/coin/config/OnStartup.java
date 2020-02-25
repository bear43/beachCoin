package com.psuti.coin.config;

import com.psuti.coin.service.Runner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class OnStartup implements ApplicationListener<ContextRefreshedEvent> {

    private boolean init;

    @Autowired
    private Runner runner;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if(!init) {
            runner.run();
            init = true;
        }
    }
}
