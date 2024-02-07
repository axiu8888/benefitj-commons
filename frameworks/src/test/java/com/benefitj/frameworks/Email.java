package com.benefitj.frameworks;

import org.apache.commons.mail.*;

import java.net.MalformedURLException;
import java.net.URL;

public class Email {

  public static void main(String[] args) throws EmailException {
    sendEmail();
  }


  /**
   * @throws EmailException
   * @describe 发送包含附件的邮件（附件为本地资源）
   */
  public static void sendEmail() throws EmailException {
    SimpleEmail email = new SimpleEmail();
    email.setHostName("smtp.163.com");
    email.setAuthenticator(new DefaultAuthenticator("dafeisuowen01@163.com", "You15239333774"));
    email.setSSLOnConnect(true);
    email.addTo("dafeisuowen01@163.com", "dingxiuan");
    email.setFrom("dingxiuan@sensecho.com", "dingxiuan");
    email.setSubject("磁盘空间不足");
    email.setMsg("磁盘空间不足，请及时处理无用数据，以便腾出空间！");
    email.send();
  }

  /**
   * @throws EmailException
   * @describe 发送包含附件的邮件（附件为本地资源）
   */
  public static void sendEmailsWithAttachments() throws EmailException {
    // 创建一个attachment（附件）对象
    EmailAttachment attachment = new EmailAttachment();
    //设置上传附件的地址
    attachment.setPath("C:\\Users\\Administrator\\Pictures\\Saved Pictures\\conti.png");
    attachment.setDisposition(EmailAttachment.ATTACHMENT);
    //这个描述可以随便写
    attachment.setDescription("Picture of conti");
    //这个名称要注意和文件格式一致,这将是接收人下载下来的文件名称
    attachment.setName("conti.png");

    //因为要上传附件，所以用MultiPartEmail()方法创建一个email对象，固定步骤都是一样的
    MultiPartEmail email = new MultiPartEmail();
    email.setHostName("smtp.163.com");
    email.setAuthenticator(new DefaultAuthenticator("myemailaddress@163.com", "myshouquanma"));
    email.setSSLOnConnect(true);
    email.addTo("receiveemail@qq.com", "Conti Zhang");
    email.setFrom("myemailaddress@163.com", "Me");
    email.setSubject("图片");
    email.setMsg("这是发送给你的图片");
    //将附件添加到邮件
    email.attach(attachment);

    email.send();
  }


  /**
   * @throws EmailException
   * @throws MalformedURLException
   * @describe 发送内容为HTML格式的邮件
   */
  public static void sendHTMLFormattedEmail() throws EmailException, MalformedURLException {
    // 这里需要使用HtmlEmail创建一个email对象
    HtmlEmail email = new HtmlEmail();
    email.setHostName("smtp.163.com");
    email.setAuthenticator(new DefaultAuthenticator("myemailaddresss@163.com", "myshouquanma"));
    email.addTo("receiveemail@qq.com", "Conti Zhang");
    email.setFrom("myemailaddress@163.com", "Me");
    email.setSubject("Test email with inline image");

    // 嵌入图像并获取内容id,虽然案例这样写，但我感觉直接在html内容里面写图片网络地址也可以
    URL url = new URL("http://www.apache.org/images/asf_logo_wide.gif");
    String cid = email.embed(url, "Apache logo");

    // 设置html内容
    email.setHtmlMsg("<html>The apache logo - <img src=\"cid:" + cid + "\"></html>");

    // 设置替代内容，如果不支持html
    email.setTextMsg("你的邮件客户端不支持html邮件");
    email.send();
  }

//  /**
//   * @describe 发送内容为HTML格式的邮件（嵌入图片更方便）
//   * @throws MalformedURLException
//   * @throws EmailException
//   */
//  public static void sendHTMLFormattedEmailWithEmbeddedImages() throws MalformedURLException, EmailException {
//    //html邮件模板
//    HtmlEmail email = new HtmlEmail();
//    String htmlEmailTemplate = "<img src=\"http://www.conti.com/images/1.jpg\">";
//    DataSourceResolver[] dataSourceResolvers =new DataSourceResolver[]{new DataSourceFileResolver(),new DataSourceUrlResolver(new URL("http://"))};
//    email.setDataSourceResolver(new DataSourceCompositeResolver(dataSourceResolvers));
//    email.setHostName("smtp.qq.com");
//    email.setAuthenticator(new DefaultAuthenticator("myemailaddress@qq.com", "myshouquanma"));
//    email.addTo("receiveemail@qq.com", "Conti Zhang");
//    email.setFrom("myemailaddress@qq.com", "Me");
//    email.setSubject("Test email with inline image");
//    email.setHtmlMsg(htmlEmailTemplate);
//    email.setTextMsg("你的邮件客户端不支持html邮件");
//
//    email.send();
//  }

}
