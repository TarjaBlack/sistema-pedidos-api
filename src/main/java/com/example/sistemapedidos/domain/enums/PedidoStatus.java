package com.example.sistemapedidos.domain.enums;

import java.util.Arrays;

public enum PedidoStatus {
    AGUARDANDO_PAGAMENTO(1),
    PAGO(2),
    ENVIADO(3),
    ENTREGUE(4),
    CANCELADO(5);

    private int code;

    PedidoStatus(int code){
        this.code = code;
    }

    public int getCode(){
        return code;
    }

    public static PedidoStatus valueOf(int code) {
        return Arrays.stream(PedidoStatus.values())
                .filter(v -> v.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Código inválido"));
    }
}
