package com.carson;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.carson.hystrix.BaseHystrixCommand;

@Aspect
@Component
public class AopHystrix {

	private static final Logger logger = LoggerFactory.getLogger(AopHystrix.class);

	
	/**
	 * 定义一个切点
	 */
	@Pointcut(value = "@annotation(com.carson.HystrixAno)")
	public void cutOffPoint() {
	}

	ThreadLocal<Long> startTime = new ThreadLocal<>();

	@Around("cutOffPoint()")
	public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
		startTime.set(System.currentTimeMillis());
		Object obj;
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		HystrixAno hystrixAno = methodSignature.getMethod().getAnnotation(HystrixAno.class);
		if (hystrixAno != null) {
			obj = new BaseHystrixCommand<Object>(joinPoint).execute();
		} else {
			obj = joinPoint.proceed();
		}
		return obj;
	}
}