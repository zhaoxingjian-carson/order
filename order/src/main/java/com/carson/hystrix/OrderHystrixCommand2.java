
package com.carson.hystrix;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.carson.service.MemberService;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;

/**
 * 功能说明: <br>
 * 创建作者:每特教育-余胜军<br>
 * 创建时间:2018年6月30日 下午1:56:20<br>
 * 教育机构:每特教育|蚂蚁课堂<br>
 * 版权说明:上海每特教育科技有限公司版权所有<br>
 * 官方网站:www.itmayiedu.com|www.meitedu.com<br>
 * 联系方式:qq644064779<br>
 * 注意:本内容有每特教育学员共同研发,请尊重原创版权
 */
@SuppressWarnings("rawtypes")
public class OrderHystrixCommand2 extends HystrixCommand<JSONObject> {
	@Autowired
	private MemberService memberService;

	/**
	 * @param group
	 */
	public OrderHystrixCommand2(MemberService memberService) {
		super(setter());
		this.memberService = memberService;
	}

	protected JSONObject run() throws Exception {

		// Thread.sleep(500);
		// System.out.println("orderIndex线程名称" +
		// Thread.currentThread().getName());
		// System.out.println("success");
		JSONObject member = memberService.getMember();
		System.out.println("当前线程名称:" + Thread.currentThread().getName() + ",订单服务调用会员服务:member:" + member);
		return member;
	}

	private static Setter setter() {
		// 服务分组
		HystrixCommandGroupKey groupKey = HystrixCommandGroupKey.Factory.asKey("members");
		// 命令属性配置 采用信号量模式
		HystrixCommandProperties.Setter commandProperties = HystrixCommandProperties.Setter()
				.withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
				// 使用一个原子计数器（或信号量）来记录当前有多少个线程在运行，当请求进来时先判断计数
				// 器的数值，若超过设置的最大线程个数则拒绝该请求，若不超过则通行，这时候计数器+1，请求返 回成功后计数器-1。
				.withExecutionIsolationSemaphoreMaxConcurrentRequests(50);
		return HystrixCommand.Setter.withGroupKey(groupKey).andCommandPropertiesDefaults(commandProperties);
	}

	@Override
	protected JSONObject getFallback() {
		// 如果Hystrix发生熔断，当前服务不可用,直接执行Fallback方法
		System.out.println("系统错误！");
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", 500);
		jsonObject.put("msg", "系统错误！");
		return jsonObject;
	}
}
