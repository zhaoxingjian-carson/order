package com.carson;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HystrixAno {
	String groupName() default "baseGroupName";

	String commandName() default "baseCommandName";

	String threadPoolName() default "baseThreadPoolName";

	int threadCoreSize() default 10;

	int threadQueueSize() default 100;

	int threadAliveTime() default 15;

	String fallbackMethod() default "";
}
