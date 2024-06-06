package es.dsw.controllers.apis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import es.dsw.models.auth.AuthenticationRequest;
import es.dsw.models.auth.AuthenticationResponse;

import es.dsw.service.auth.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/auth")
public class AuthenticationController 
{
	@Autowired
	private AuthenticationService authenticationService;
	
	
	/*
	 * Endpoint de utileria para comprobar si el token es válido.
	 */
	
	@GetMapping(value= {"/validate-token"})
	public ResponseEntity<Boolean> validar(
			@RequestParam String jwt)
	{
		boolean isTokenValid = authenticationService.validateToken(jwt);
		return ResponseEntity.ok(isTokenValid);
	} 
	
	
	/*
	 * Endpoint de autenticación.
	 */
	
	@ResponseBody
	@PostMapping(value = {"/authenticate"}, produces = "application/json")
	public ResponseEntity<AuthenticationResponse> autenticar
			(@RequestBody AuthenticationRequest authRequest,
			 HttpServletResponse response) 
	{
		AuthenticationResponse rsp = authenticationService.login(authRequest);		
		
		// Crear la cookie
	    Cookie objCookie = new Cookie("token", rsp.getJwt());
	    // La cookie expira después de 7 días.
	    objCookie.setMaxAge(7 * 24 * 60 * 60);         
	    // La cookie solo puede ser accedida por el servidor.
	    // Si l ponemos a true, a la cookie no se podrá acceder desde el lado del cliente (Frontend, Framework como JS, JQUERY, etc..)
	    objCookie.setHttpOnly(false); 
	    // Definimos la ruta para la cual la cookie es válida.
	    // con el path base "/" la cookie se enviará para todas las solicitudes al dominio, sin importar la ruta.
	    objCookie.setPath("/");
	    // Añade la cookie al objeto response
	    response.addCookie(objCookie);
	    
		return ResponseEntity.ok(rsp);
	}

}
