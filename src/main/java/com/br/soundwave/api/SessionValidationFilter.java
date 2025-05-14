package com.br.soundwave.api;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.br.soundwave.Core.Exceptions.GenericExcpetion;
import com.br.soundwave.Core.Exceptions.NotUsingMFAExcpetion;
import com.br.soundwave.Core.Model.ClientModel;
import com.br.soundwave.Core.Model.SessionManagerModel;
import com.br.soundwave.Core.Repository.ClientRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SessionValidationFilter extends OncePerRequestFilter{
	
	@Autowired
	private ClientRepository clientRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		String sessionToken = null;

		String path = httpRequest.getRequestURI();

	    if (path.startsWith("/login") || 
	        path.startsWith("/register") || 
	        path.startsWith("/validate-email") || 
	        path.startsWith("/forgot-password")) {
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

	    try {
			if (sessionToken == null || !isSessionValid(sessionToken)) {
			    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			    response.getWriter().write("Sessao invalida ou expirada.");
			    return;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    filterChain.doFilter(request, response);
	} 
		
	
	private boolean isSessionValid(String token) throws Exception {
        SessionManagerModel session = new SessionManagerModel();
        try {
        	Long id = session.getUserIdFromToken(token);
        	if(id != null) {
        		Optional<ClientModel> client = clientRepository.findById(id);
        		if(client.isPresent()) {
        			if(client.get().isEmailVerified()) {
        				if(client.get().isMfaEnabled()) {
        					return true;
        				}else {
        					throw new NotUsingMFAExcpetion("Para continuar com essa requisição, é necessário utiilizar MFA");
        				}
        			}else {
        				throw new GenericExcpetion("Email não confirmado");
        			}
        		}else {
        			throw new GenericExcpetion("Sessão invalida.");
        		}
        	}
        }catch (Exception e) {
			throw new Exception("Err:" + e);
		}
		return false;
        
    }
}
	

