package edu.ncsu.csc.CoffeeMaker.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.JdbcUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import edu.ncsu.csc.CoffeeMaker.models.enums.Role;

/**
 * This class lets the system know how to find users in the database and to
 * authenticate them.
 *
 * @author Rio Mabanag rrmabana
 */
@EnableWebSecurity
public class SecurityConfigs extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;

    @Override
    protected void configure ( final AuthenticationManagerBuilder auth ) throws Exception {
        final JdbcUserDetailsManagerConfigurer<AuthenticationManagerBuilder> dbManager = auth.jdbcAuthentication();

        dbManager.dataSource( dataSource ).passwordEncoder( passwordEncoder() )
                .usersByUsernameQuery( "select username,password,enabled from user WHERE username = ?;" )
                .authoritiesByUsernameQuery( "select username, role from user where username=?" );

    }

    /*
     * gives the permissions of what users can look at depending on their role
     */
    @Override
    protected void configure ( final HttpSecurity http ) throws Exception {
        http.csrf().disable().authorizeRequests().antMatchers( "/management" ).hasAuthority( Role.MANAGER.name() )
                .antMatchers( "/barista" ).hasAnyAuthority( Role.MANAGER.name(), Role.BARISTA.name() )
                .antMatchers( "/customer" )
                .hasAnyAuthority( Role.MANAGER.name(), Role.BARISTA.name(), Role.CUSTOMER.name() ).anyRequest()
                .authenticated().and().formLogin().loginPage( "/logIn" ).permitAll().successHandler( successHandler() )
                .and().exceptionHandling().accessDeniedHandler( accessDeniedHandler() ).and().logout()
                .logoutRequestMatcher( new AntPathRequestMatcher( "/logout" ) ).logoutSuccessUrl( "/logIn?logout" )
                .permitAll();

    }

    @Override
    public void configure ( final WebSecurity web ) throws Exception {

        // allows access without auth
        web.ignoring().antMatchers( "/api/v1/users", "/signUp", "/css/*" );
    }

    @Bean
    public PasswordEncoder passwordEncoder () {
        // Use NoOpPasswordEncoder to handle plain text passwords
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler () {
        return new CustomAccessDenied();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler () {
        return new CustomSuccessHandler();
    }

}
