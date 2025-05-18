package com.br.soundwave.Core.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.br.soundwave.Config.EmailProperties;
import com.br.soundwave.Core.Exceptions.GenericExcpetion;

import jakarta.mail.internet.MimeMessage;

import freemarker.template.Template;
import freemarker.template.Configuration;


@Component
public class SmtpSendEmailService implements SendEmailService{

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private EmailProperties emailProperties;
	
	@Autowired
	private Configuration freemarkerConfig;
	
	@Override
	public boolean enviar(Mensagem mensagem) {
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
			
			String corpo = processarTemplate(mensagem);
			String from = emailProperties.getRemetente();
			
			
			helper.setFrom(from);
			helper.setTo(mensagem.getDestinatario());
			helper.setSubject(mensagem.getAssunto());
			helper.setText(corpo,true);
			
			mailSender.send(mimeMessage);
			return true;
			
		}catch (Exception e) {
			throw new GenericExcpetion("Não foi possivel enviar o e-mail" + e.getMessage());
		}
		
	}
	
	private String processarTemplate(Mensagem mensagem) {
		try {
			Template template = freemarkerConfig.getTemplate(mensagem.getCorpo());
			return FreeMarkerTemplateUtils.processTemplateIntoString(template, mensagem.getVar());
		}catch(Exception e) {
			throw new GenericExcpetion("Não foi possivel carregar o template: " + e.getMessage());
		}
	}
	
	
	
}
