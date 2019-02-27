package cn.itcast.bos.quartz;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import cn.itcast.bos.service.take_delivery.PromotionService;

public class PromotionJob implements Job{

	@Autowired
	private PromotionService promotionService;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		//每隔60分钟,检查活动是否过期,如果当前日期大于promotion的endDate,那么就把status修改为2
		System.out.println("活动过期程序执行...");
		promotionService.updateStatus(new Date());
	}
}
