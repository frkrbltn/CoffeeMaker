package edu.ncsu.csc.CoffeeMaker.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import edu.ncsu.csc.CoffeeMaker.models.enums.Role;

public class CustomAccessDenied implements AccessDeniedHandler {

    @Override
    public void handle ( final HttpServletRequest request, final HttpServletResponse response,
            final AccessDeniedException accessDeniedException ) throws IOException, ServletException {

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println( auth );
        if ( auth.getAuthorities().contains( new SimpleGrantedAuthority( Role.BARISTA.name() ) ) ) {
            response.sendRedirect( "/barista" );
        }

        if ( auth.getAuthorities().contains( new SimpleGrantedAuthority( Role.CUSTOMER.name() ) ) ) {
            response.sendRedirect( "/customer" );
        }
    }

}
