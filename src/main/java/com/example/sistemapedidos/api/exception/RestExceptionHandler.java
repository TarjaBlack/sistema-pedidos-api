package com.example.sistemapedidos.api.exception;

import com.example.sistemapedidos.api.ErroResposta;
import com.example.sistemapedidos.domain.exception.EntidadeNaoEncontradaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<ErroResposta> handleNotFound(EntidadeNaoEncontradaException ex){
        ErroResposta erro = new ErroResposta(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage()
                );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }
}
