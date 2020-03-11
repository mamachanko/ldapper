package io.github.mamachanko.ldapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@SpringBootApplication
public class LdapperApplication {

	public static void main(String[] args) {
		SpringApplication.run(LdapperApplication.class, args);
	}

}

@RestController
class Controller {

	@GetMapping("/")
	public String getIndex() {
		return "Hello! Here's a UUID: " + UUID.randomUUID().toString();
	}
}

@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Value("${ldap.urls}")
	private String ldapUrls;

	@Value("${ldap.base.dn}")
	private String ldapBaseDn;

	@Value("${ldap.username}")
	private String ldapSecurityPrincipal;

	@Value("${ldap.password}")
	private String ldapPrincipalPassword;

	@Value("${ldap.user.dn.pattern}")
	private String ldapUserDnPattern;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.authorizeRequests().anyRequest().fullyAuthenticated()
				.and()
				.formLogin()
				.and()
				.logout().logoutUrl("/logout")
				.invalidateHttpSession(true)
				.deleteCookies("JSESSIONID");
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
				.ldapAuthentication()
				.contextSource()
				.url(ldapUrls + ldapBaseDn)
				.managerDn(ldapSecurityPrincipal)
				.managerPassword(ldapPrincipalPassword)
				.and()
				.userDnPatterns(ldapUserDnPattern);
	}
}
