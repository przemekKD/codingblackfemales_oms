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
                .aeronDirectoryName("D:/tmp/aeron/cbf")
                .threadingMode(ThreadingMode.DEDICATED)
                .sharedIdleStrategy(new BusySpinIdleStrategy())
                .clientLivenessTimeoutNs(TimeUnit.MINUTES.toNanos(30))
                .publicationUnblockTimeoutNs(TimeUnit.MINUTES.toNanos(31))
                .dirDeleteOnShutdown(true);
        driver = MediaDriver.launchEmbedded(mediaDriverCtx);
    }

    public MediaDriver getDriver() {
        return driver;
    }
}
