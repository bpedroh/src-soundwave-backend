package com.br.soundwave.Core.Exceptions;

public class UserNotFoundException extends RuntimeException{
	public UserNotFoundException(String mensagem) {
		super(mensagem);
	}
}
