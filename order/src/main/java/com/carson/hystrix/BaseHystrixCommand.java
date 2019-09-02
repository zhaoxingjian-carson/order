
package com.carson.hystrix;

import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import com.carson.HystrixAno;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;

@SuppressWarnings("hiding")
public class BaseHystrixCommand<Object> extends HystrixCommand<Object> {

	private ProceedingJoinPoint joinPoint;

	/**
	 * @param group
	 */
	public BaseHystrixCommand(ProceedingJoinPoint joinPoint) {
		super(setter(joinPoint));
		this.joinPoint = joinPoint;
	}

	@SuppressWarnings("unchecked")
	protected Object run() throws Exception {
		//System.out.println("当前线程名称:" + Thread.currentThread().getName());
		try {
			Object object = (Object) joinPoint.proceed();
			return object;
		} catch (Throwable e) {
			throw new Exception(e);
		}
	}

	private static Setter setter(ProceedingJoinPoint joinPoint) {
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		HystrixAno hystrixAno = methodSignature.getMethod().getDeclaredAnnotation(HystrixAno.class);
		// 服务分组
		HystrixCommandGroupKey groupKey = HystrixCommandGroupKey.Factory.asKey(hystrixAno.groupName());
		// 服务标识
		HystrixCommandKey commandKey = HystrixCommandKey.Factory.asKey(hystrixAno.commandName());
		// 线程池名称
		HystrixThreadPoolKey threadPoolKey = HystrixThreadPoolKey.Factory.asKey(hystrixAno.threadPoolName());
		// #####################################################
		// 线程池配置 线程池大小为10,线程存活时间15秒 队列等待的阈值为100,超过100执行拒绝策略
		HystrixThreadPoolProperties.Setter threadPoolProperties = HystrixThreadPoolProperties.Setter()
				.withCoreSize(hystrixAno.threadCoreSize()).withKeepAliveTimeMinutes(hystrixAno.threadAliveTime())
				.withQueueSizeRejectionThreshold(hystrixAno.threadQueueSize());
		// ########################################################
		// 命令属性配置Hystrix 开启超时
		HystrixCommandProperties.Setter commandProperties = HystrixCommandProperties.Setter()
				// 采用线程池方式实现服务隔离
				.withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
				// 禁止
				.withExecutionTimeoutEnabled(false);
		return HystrixCommand.Setter.withGroupKey(groupKey).andCommandKey(commandKey).andThreadPoolKey(threadPoolKey)
				.andThreadPoolPropertiesDefaults(threadPoolProperties).andCommandPropertiesDefaults(commandProperties);
	}

	@Override
	protected Object getFallback() {
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		HystrixAno hystrixAno = methodSignature.getMethod().getAnnotation(HystrixAno.class);
		if (StringUtils.isBlank(hystrixAno.fallbackMethod())) {
			//System.out.println("getFallback");
			throw new RuntimeException("fallback");
		} else {
			try {
				Method method = null;
				method = joinPoint.getTarget().getClass().getMethod(hystrixAno.fallbackMethod(), null);
				Object object = (Object) method.invoke(joinPoint.getTarget(), null);
				return object;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
