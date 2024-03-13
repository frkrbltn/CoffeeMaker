package edu.ncsu.csc.CoffeeMaker.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import edu.ncsu.csc.CoffeeMaker.models.enums.Role;

public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess ( final HttpServletRequest request, final HttpServletResponse response,
            final Authentication authentication ) throws IOException, ServletException {

        if ( authentication.getAuthorities().contains( new SimpleGrantedAuthority( Role.MANAGER.name() ) ) ) {
            response.sendRedirect( "/management" );
        }

        if ( authentication.getAuthorities().contains( new SimpleGrantedAuthority( Role.BARISTA.name() ) ) ) {
            response.sendRedirect( "/barista" );
        }

        if ( authentication.getAuthorities().contains( new SimpleGrantedAuthority( Role.CUSTOMER.name() ) ) ) {
            response.sendRedirect( "/customer" );
        }

    }

}
