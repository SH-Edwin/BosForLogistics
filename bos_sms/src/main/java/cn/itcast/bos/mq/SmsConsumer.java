package cn.itcast.bos.mq;

import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.springframework.stereotype.Service;

import cn.itcast.bos.utils.SmsUtils;

@Service("smsConsumer")
public class SmsConsumer implements MessageListener{

	@Override
	public void onMessage(Message message) {
		//message为map格式
		MapMessage mapMessage=(MapMessage) message;
		
		try {
			// 发送短信
			//String sendSmsByHTTPReturn = SmsUtils.sendSmsByHTTP(mapMessage.getString("telephone"),mapMessage.getString("content"));
			  String sendSmsByHTTPReturn="000/xxxxx";
			  if (sendSmsByHTTPReturn.startsWith("000")) { 
				  //发送成功 return NONE;
				  System.out.println("发送成功,发送号码为:"+mapMessage.getString("telephone")+",信息为:"+mapMessage.getString("content"));
			  }else {
				  //发送失败 
				  throw new RuntimeException("发送短信失败,信息码为:"+sendSmsByHTTPReturn);
			  }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
