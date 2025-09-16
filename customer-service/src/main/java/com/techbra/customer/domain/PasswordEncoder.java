package com.techbra.customer.domain;

/**
 * Interface para codificação de senhas (Porta de saída)
 * Abstrai a implementação de criptografia de senhas
 */
public interface PasswordEncoder {
    
    /**
     * Codifica uma senha em texto plano
     * @param rawPassword senha em texto plano
     * @return senha codificada
     */
    String encode(String rawPassword);
    
    /**
     * Verifica se uma senha em texto plano corresponde à senha codificada
     * @param rawPassword senha em texto plano
     * @param encodedPassword senha codificada
     * @return true se as senhas correspondem
     */
    boolean matches(String rawPassword, String encodedPassword);
}