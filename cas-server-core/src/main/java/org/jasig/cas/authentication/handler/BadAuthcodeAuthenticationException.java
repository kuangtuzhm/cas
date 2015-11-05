package org.jasig.cas.authentication.handler;

import javax.security.auth.login.LoginException;

public class BadAuthcodeAuthenticationException extends LoginException {  
    
    /** Serializable ID for unique id. */  
    private static final long serialVersionUID = 5501212207531289993L;  
  
    /** Code description. */  
    public static final String CODE = "error.authentication.authcode.bad";  
  
    /** 
     * Constructs a TicketCreationException with the default exception code. 
     */  
    public BadAuthcodeAuthenticationException() {  
        super(CODE);  
    }  
  
    /** 
     * Constructs a TicketCreationException with the default exception code and 
     * the original exception that was thrown. 
     *  
     * @param throwable the chained exception 
     */  
    public BadAuthcodeAuthenticationException(String msg) {  
        super(msg);  
    }
}  
