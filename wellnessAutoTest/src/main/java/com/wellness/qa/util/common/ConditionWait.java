package com.wellness.qa.util.common;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class ConditionWait<T> {
    private Long timeout;
    private TimeUnit timeUnit;
    private final T input;

    public ConditionWait(Long timeout, TimeUnit timeUnit, T input) {
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.input = input;
    }

    public <R> R until(Function<? super T, ? extends R> isTrue) throws InterruptedException {
        long startTime = System.currentTimeMillis();

        for(long currentTime = System.currentTimeMillis(); currentTime <= startTime + this.timeUnit.toMillis(this.timeout); currentTime = System.currentTimeMillis()) {
            R value = isTrue.apply(this.input);
            if (value != null && (Boolean.class != value.getClass() || Boolean.TRUE.equals(value))) {
                return value;
            }

            Thread.sleep(500L);
        }

        return null;
    }
}
