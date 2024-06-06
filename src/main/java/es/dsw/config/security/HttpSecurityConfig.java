package es.dsw.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import es.dsw.config.security.filter.JwtAuthenticationFilter;
import es.dsw.models.util.Role;


@Configuration
@EnableWebSecurity
public class HttpSecurityConfig 
{	
	/*
	 * Configuración de nuestro Http, donde pasamos nuestros filtros
	 * y nuestra lógica a la hora de hacer nuestras peticiones.
	 * Inyectamos nuestras dependencias y una de ellas en especial 
	 * nuestro filtro JWT llamado JwtAuthenticationFilter.
	 */
	
	@Autowired
	@Lazy
	private AuthenticationProvider authenticationProvider;
	
	@Autowired
	@Lazy
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	
	@Bean
	@Lazy
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
	{
		return http
				/*
				 * Desactivamos la protección CSRF (Cross-Site Request Forgery) 
				 * debido a que nosotros manejaremos nuestro propio token JWT.
				 */
				.csrf((csrfConfig) -> csrfConfig.disable())
				/*
				 * Establecemos la política de gestión de sesiones a STATELESS,
				 * lo que significa que el servidor no creará ni gestionará sesiones.
				 */
				.sessionManagement((sessionMagConfig) -> sessionMagConfig
				   									     .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				 )
				/*
				 * Establecemos el proveedor de autenticación.
				 */
				.authenticationProvider(authenticationProvider)
				/*
				 * Añadimos los filtros en indicamos nuestro filtro personalizado JWT 
				 * antes de uno propio que proporciona Spring Security.
				 */
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				/*
				 * Configuramos la autorización para las solicitudes HTTP de los endpoints.
				 */
				.authorizeHttpRequests((authorize) -> 
				{
					/*
					 * Refactorizamos y ponemos la request en un método privado.
					 */
					buildRequestMatchers(authorize);
				})				
				/*
				 *  Configuramos el mapeo de nuestro propio formulario para login.
				 */
				.formLogin(form -> 
				{
					form
				        .loginPage("/login")
				        .loginProcessingUrl("/autenticacion")
				        .permitAll();				
				})				
				/*
				 *  Al final construimos y devolvemos el SecurityFilterChain.
				 */
				.build();
	}


	private void buildRequestMatchers(
			AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize) 
	{
		authorize		
		/*
		* Autorizaciones de endpoints teniendo en cuenta los roles.
		*/
			.requestMatchers(HttpMethod.GET, "/index").hasAnyRole(Role.ADMIN.name(),Role.USER.name())
			.requestMatchers(HttpMethod.GET, "/test1").hasAnyRole(Role.ADMIN.name(),Role.USER.name())	
			.requestMatchers(HttpMethod.GET, "/test2").hasAnyRole(Role.ADMIN.name(),Role.USER.name())	
			.requestMatchers(HttpMethod.GET, "/test3").hasAnyRole(Role.ADMIN.name(),Role.USER.name())	
			.requestMatchers(HttpMethod.GET, "/alumnos/getAll").hasAnyRole(Role.ADMIN.name(),Role.USER.name())	
			.requestMatchers(HttpMethod.POST, "/alumnos/getOne").hasAnyRole(Role.ADMIN.name(),Role.USER.name())	
			.requestMatchers(HttpMethod.POST, "/alumnos/add").hasAnyRole(Role.ADMIN.name(),Role.USER.name())	
		
		
		/*
		 * Autorizacion de los endpoints púnlicos.
		 */
		
			.requestMatchers(HttpMethod.GET, "/login").permitAll()
			.requestMatchers(HttpMethod.POST, "/autenticacion").permitAll()
			.requestMatchers(HttpMethod.POST,"/auth/authenticate").permitAll()
			.requestMatchers(HttpMethod.GET,"/auth/validate-token").permitAll()
			.requestMatchers("/css/**").permitAll()
			.requestMatchers("/js/**").permitAll()
			.anyRequest().authenticated();
	}

}
