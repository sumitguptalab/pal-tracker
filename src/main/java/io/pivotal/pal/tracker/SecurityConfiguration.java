package io.pivotal.pal.tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String forceHttps = System.getenv("SECURITY_FORCE_HTTPS");
        if (forceHttps != null && forceHttps.equals("true")) {
            http.requiresChannel().anyRequest().requiresSecure();
        }
        http.authorizeRequests().antMatchers("/**").hasRole("USER")
                .and()
                .httpBasic()
                .and()
                .csrf().disable();
    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication().withUser("user").password("password").roles("USER");
//    }

    // if you are wondering why we are using the @Autowired annotated method for basic auth in the security lab,
    // it is because spring boot 2 will use spring security 5:
    // https://spring.io/blog/2017/11/01/spring-security-5-0-0-rc1-released#password-storage-updated
    // for Ldap security see https://spring.io/guides/gs/authenticating-ldap/ link
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
            .withUser("user").password("password").roles("USER");
    }
}
