package com.example.sistemapedidos.domain.exception;

public class EntidadeNaoEncontradaException extends RuntimeException {
    public EntidadeNaoEncontradaException(String mensagem){
        super(mensagem);
    }
}
