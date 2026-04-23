package com.demo.support;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Profile("main-method-test")
public class MainMethodAutoCloseListener implements ApplicationListener<ApplicationReadyEvent> {
    private static final AtomicBoolean INVOKED = new AtomicBoolean(false);

    public static void reset() {
        INVOKED.set(false);
    }

    public static boolean wasInvoked() {
        return INVOKED.get();
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        INVOKED.set(true);
        event.getApplicationContext().close();
    }
}
