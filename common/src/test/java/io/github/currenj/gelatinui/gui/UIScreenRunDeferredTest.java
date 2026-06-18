package io.github.currenj.gelatinui.gui;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class UIScreenRunDeferredTest {

    @Test
    public void deferredTaskRunsOnNextUpdateNotImmediately() {
        UIScreen screen = new UIScreen(800, 600);
        AtomicBoolean ran = new AtomicBoolean(false);

        screen.runDeferred(() -> ran.set(true));
        assertFalse(ran.get());

        screen.update(0f);
        assertTrue(ran.get());
    }

    @Test
    public void deferredTaskQueuedDuringFlushRunsOnTheFollowingFlushNotThisOne() {
        UIScreen screen = new UIScreen(800, 600);
        AtomicInteger nestedRunCount = new AtomicInteger(0);

        screen.runDeferred(() -> screen.runDeferred(nestedRunCount::incrementAndGet));

        screen.update(0f);
        assertEquals(0, nestedRunCount.get(), "nested deferred task should not run on the same flush it was queued in");

        screen.update(0f);
        assertEquals(1, nestedRunCount.get(), "nested deferred task should run on the following flush");
    }
}
