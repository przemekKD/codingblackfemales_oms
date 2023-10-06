package com.cbf.base;

import io.aeron.driver.MediaDriver;
import io.aeron.driver.ThreadingMode;
import org.agrona.concurrent.BusySpinIdleStrategy;

import java.util.concurrent.TimeUnit;

public class Context {
    private static final Context context = new Context();
    private MediaDriver driver;

    public static Context instance() {
        return context;
    }

    private Context() {
        final MediaDriver.Context mediaDriverCtx = new MediaDriver.Context()
                .dirDeleteOnStart(true)
                .threadingMode(ThreadingMode.DEDICATED)
                .sharedIdleStrategy(new BusySpinIdleStrategy())
                .driverTimeoutMs(TimeUnit.MINUTES.toMillis(30)) // allow for debugging
                .clientLivenessTimeoutNs(TimeUnit.MINUTES.toNanos(30)) // allow for debugging
                .publicationUnblockTimeoutNs(TimeUnit.MINUTES.toNanos(31)) // allow for debugging
                .dirDeleteOnShutdown(true);
        driver = MediaDriver.launchEmbedded(mediaDriverCtx);
    }

    public MediaDriver getDriver() {
        return driver;
    }
}
