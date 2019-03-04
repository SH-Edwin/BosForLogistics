package cn.itcast.bos.quartz;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import cn.itcast.bos.service.take_delivery.PromotionService;
import cn.itcast.bos.service.take_delivery.WayBillService;

public class QuarzJob implements Job{

	@Autowired
	private PromotionService promotionService;
	
	@Autowired
	private WayBillService wayBillService;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		//每隔10分钟,检查活动是否过期,如果当前日期大于promotion的endDate,那么就把status修改为2
		System.out.println("活动自动过期程序执行...");
		promotionService.updateStatus(new Date());
		//每隔10分钟,更新运单数据库和索引一致
		System.out.println("自动同步运单数据库和索引库执行...");
		wayBillService.syncIndex();
		
	}
}
