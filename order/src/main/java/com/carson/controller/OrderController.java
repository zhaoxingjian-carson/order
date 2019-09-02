/**
 * 功能说明:
 * 功能作者:
 * 创建日期:
 * 版权归属:每特教育|蚂蚁课堂所有 www.itmayiedu.com
 */
package com.carson.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.carson.HystrixAno;
import com.carson.hystrix.OrderHystrixCommand;
import com.carson.hystrix.OrderHystrixCommand2;
import com.carson.service.MemberService;
import com.google.common.util.concurrent.RateLimiter;

/**
 * 功能说明: <br>
 * 创建作者:每特教育-余胜军<br>
 * 创建时间:2018年6月30日 下午12:45:16<br>
 * 教育机构:每特教育|蚂蚁课堂<br>
 * 版权说明:上海每特教育科技有限公司版权所有<br>
 * 官方网站:www.itmayiedu.com|www.meitedu.com<br>
 * 联系方式:qq644064779<br>
 * 注意:本内容有每特教育学员共同研发,请尊重原创版权
 */
@RestController
@RequestMapping("/order")
public class OrderController {
	@Autowired
	private MemberService memberService;

	@RequestMapping("/orderIndex")
	@HystrixAno(fallbackMethod = "fallback", threadCoreSize = 2, threadQueueSize = 5)
	public Object orderIndex() throws InterruptedException {
		JSONObject member = memberService.getMember();
		// System.out.println("当前线程名称:" + Thread.currentThread().getName() +
		// ",订单服务调用会员服务:member:" + member);
		return member;
	}

	public JSONObject fallback() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", 500);
		jsonObject.put("msg", "系统错误！");
		return jsonObject;
	}

	// 已经理解
	@RequestMapping("/orderIndexHystrix")
	public Object orderIndexHystrix() throws InterruptedException {
		return new OrderHystrixCommand(memberService).execute();
	}

	@RequestMapping("/orderIndexHystrix2")
	public Object orderIndexHystrix2() throws InterruptedException {
		return new OrderHystrixCommand2(memberService).execute();
	}

	private RateLimiter rateLimiter=RateLimiter.create(5);
	
	@RequestMapping("/findOrderIndex")
	public Object findIndex() {
		rateLimiter.acquire();
		System.out.println("当前线程:" + Thread.currentThread().getName() + ",findOrderIndex");
		return "findOrderIndex";
	}
}
