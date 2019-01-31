package cn.itcast.bosfore.utils;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

public class MailUtil {
    //发送邮件的服务器
	private static String smtp_host = "smtp.qq.com";
    //服务器的用户名和密码
	private static String username = "sh_xuxin@qq.com";
	private static String password = "Xuxin15601776609";
    //发件人地址
	private static String from = "sh_xuxin@qq.com";
    //激活地址
	public static String activeUrl = "http://localhost:9003/bos_fore/customer_activeMail";

	public static void sendMail(String subject, String content, String to) {
        //创建properties对象（键值对集合），用于配置会话session对象
		Properties props = new Properties();
        //设置发送邮件的服务器
		props.setProperty("mail.smtp.host", smtp_host);
        //设置发送的协议
		props.setProperty("mail.transport.protocol", "smtp");
        //指定验证为true
		props.setProperty("mail.smtp.auth", "true");
              
        //QQ邮箱需要创建验证器(服务器中设置的管理员的账号密码), 163邮箱就不用
        Authenticator auth = new Authenticator() {
        public PasswordAuthentication getPasswordAuthentication() {
        	//设置管理员的账号密码
        	return new PasswordAuthentication("sh_xuxin@qq.com", "hvkeyjtttaaebife");
            }
        };
        
        //创建一个程序与邮件服务器会话对象 Session,获得连接
		Session session = Session.getInstance(props, auth);
		
        //指定会话,构建邮件
		Message message = new MimeMessage(session);
		try {
            //设置发件人
			message.setFrom(new InternetAddress(from));
            // 设置收件人:TO:收件人   CC:抄送   BCC:暗送,密送.
			message.setRecipient(RecipientType.TO, new InternetAddress(to));
            //设置主题
			message.setSubject(subject);
            //设置内容
			message.setContent(content, "text/html;charset=utf-8");
            //创建传输对象
			Transport transport = session.getTransport();
            //连接发件邮箱
			transport.connect(smtp_host, username, password);
            //发送邮件
			transport.sendMessage(message, message.getAllRecipients());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("邮件发送失败...");
		}
	}

	public static void main(String[] args) {
		sendMail("测试邮件","你好,这是一封测试邮件","jlu_xuxin@163.com");
	}
}
