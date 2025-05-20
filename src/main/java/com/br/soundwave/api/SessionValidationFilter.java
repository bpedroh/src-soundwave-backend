package com.br.soundwave.api;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.br.soundwave.Core.Exceptions.GenericExcpetion;
import com.br.soundwave.Core.Exceptions.NotConfirmedEmailExcpetion;
import com.br.soundwave.Core.Exceptions.NotUsingMFAExcpetion;
import com.br.soundwave.Core.Model.ClientModel;
import com.br.soundwave.Core.Model.SessionManagerModel;
import com.br.soundwave.Core.Repository.ClientRepository;
import com.br.soundwave.Core.Services.SessionManagerService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SessionValidationFilter extends OncePerRequestFilter{
	
	@Autowired
	private ClientRepository clientRepository;
	
	@Autowired
	private SessionManagerService managerService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		
		
		String sessionToken = null;

		String path = httpRequest.getRequestURI();
	
	    if (path.startsWith("/client/login") || 
	        path.startsWith("/client/register") || 
	        path.startsWith("/client/change-password/") || 
	        path.startsWith("/client/change-password-email") ||
	        path.startsWith("/token/validate-token") ||
	        path.startsWith("/mfa/setup") ||
	        path.startsWith("/mfa/validate") 
	    		) {
	        filterChain.doFilter(request, response);
	        return;
	    }
		
	    if (request.getCookies() != null) {
	        for (Cookie cookie : request.getCookies()) {
	            if ("SESSION_ID".equals(cookie.getName())) {
	                sessionToken = cookie.getValue();
	                break;
	            }
	        }
	    }
	    
	    System.out.print(sessionToken);
	    
	    try {
	        if (isSessionValid(sessionToken)) {
	            filterChain.doFilter(request, response);
	        }
	    } catch (NotConfirmedEmailExcpetion ex) {
	        response.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
	        response.setContentType("application/json");
	        response.getWriter().write("{\"error\": \"" + ex.getMessage() + "\"}");
	    } catch (NotUsingMFAExcpetion ex) {
	        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
	        response.setContentType("application/json");
	        response.getWriter().write("{\"error\": \"" + ex.getMessage() + "\"}");
	    } catch (GenericExcpetion ex) {
	        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	        response.setContentType("application/json");
	        response.getWriter().write("{\"error\": \"" + ex.getMessage() + "\"}");
	    } catch (Exception ex) {
	        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        response.setContentType("application/json");
	        response.getWriter().write("{\"error\": \"Sessão expirada, realize login novamente.\"}");
	    }
	} 
		
	private boolean isSessionValid(String token) throws Exception {
        Long id = managerService.getClientIdByToken(token);
        
        	if(id == null) {
        		throw new GenericExcpetion("Sessão invalida.");
        	}
        	
        	ClientModel client = clientRepository.findById(id).orElseThrow(() -> new GenericExcpetion("Sessao invalida"));
        	
        	 if (!client.isEmailVerified()) {
        	        throw new NotConfirmedEmailExcpetion("Você deve confirmar seu e-mail para acessar essa página.");
        	    }
        	 
        	 if (!client.isMfaEnabled()) {
        	        throw new NotUsingMFAExcpetion("Para continuar com essa requisição, é necessário utilizar MFA.");
        	    }
        	
        
		return true;
        
    }
}
	

