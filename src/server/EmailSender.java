package server;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;

public class EmailSender {

    public static boolean send(String address, String password){
            final Properties properties = new Properties();
            properties.put("mail.transport.protocol" ,"smtps");
            properties.put("mail.smtps.auth" ,"true");
            properties.put("mail.smtps.host" ,"smtp.gmail.com");
            properties.put("mail.smtps.user" ,"nddaniel00@gmail.com");
            try {

                //properties.load(EmailSender.class.getClassLoader().getResourceAsStream("mail.properties"));
                Session mailSession = Session.getDefaultInstance(properties);
                MimeMessage message = new MimeMessage(mailSession);
                message.setFrom(new InternetAddress("nddaniel00"));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(address));
                message.setSubject("Регистрация для работы с коллекцией");
                message.setText("Вы были успешно зарегистрированы. Ваш пароль: " + password);

                Transport tr = mailSession.getTransport();
                tr.connect(null, "dasha11121999");
                tr.sendMessage(message, message.getAllRecipients());
                tr.close();
                return true;
            }
            catch (Exception e){
                //e.printStackTrace();
                return false;
                //System.out.println("НЕУДАЧА");
            }
        }

}
