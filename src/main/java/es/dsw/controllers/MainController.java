package es.dsw.controllers;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import es.dsw.models.Alumno;
import es.dsw.models.auth.AuthenticationRequest;


import jakarta.servlet.http.HttpServletResponse;

@Controller
public class MainController 
{
	@GetMapping(value = {"/login"})
	synchronized public String login() { return "login";}
	
	@GetMapping(value = {"/","/index"})
	synchronized public String index
			(@CookieValue(name="token") String jwtToken,
			 HttpServletResponse response)
	{
		// Configurar encabezados de la solicitud
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
		
		// Crear la entidad de la solicitud
	    HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

	    // Enviar la solicitud GET y obtener la respuesta
	    ResponseEntity.ok(entity);
	    
		return "index";
	}	

	@ResponseBody
	@PostMapping (value = { "/autenticacion"}, produces = "application/json")
	public String authenticate
			(@RequestParam(name="username", required=true, defaultValue="") String username,
			 @RequestParam(name="password", required=true, defaultValue="") String password,
			 @CookieValue(name="token") String jwtToken,
			 HttpServletResponse response) 
	{		
		final String USER_AUTHENTICATED = "http://localhost:8080/api/demo11/auth/authenticate";
		
		// Configurar encabezados de la solicitud
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        
		AuthenticationRequest authRequest = new AuthenticationRequest();
		authRequest.setUsername(username);
		authRequest.setPassword(password);
	
		HttpEntity<AuthenticationRequest> request = new HttpEntity<>(authRequest, headers);	

		RestTemplate restTemplate = new RestTemplate();	
		
		// Hacer la solicitud al endpoint de validación
		ResponseEntity<AuthenticationRequest> serverResponse = restTemplate.exchange(USER_AUTHENTICATED, HttpMethod.POST, request, AuthenticationRequest.class);
		
		System.out.println("\nRespuesta del servidor:" + serverResponse);
		
		return "redirect:/index?"+jwtToken;
	}

	@ResponseBody
	@GetMapping(value= {"/test1"})
	public String test1(@CookieValue(name="token") String jwtToken) 
	{
		// Configurar encabezados de la solicitud
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        
		final String GET_ALL_URL = "http://localhost:8080/api/demo11/alumnos/getAll";	
		
		HttpEntity<Alumno> requestEntity = new HttpEntity<>(headers);
		 	     
	    // Enviar la solicitud GET con las cabeceras
	    RestTemplate restTemplate = new RestTemplate();	
	    
	    ResponseEntity<Alumno[]> responseEntity = restTemplate.exchange(GET_ALL_URL, HttpMethod.GET, requestEntity, Alumno[].class);
	    Alumno[] alumnos = responseEntity.getBody();
	    
	    // Hacer algo con el array de alumnos obtenidos
	    for (Alumno alumno : alumnos) 
	    {
	    	System.out.println("\nAlumno: " + alumno.getNombre() +", "
	    	        + alumno.getApellidos() + ", dni: "+alumno.getDni() + ", edad: " + alumno.getEdad() + " años.");
	    }

		return "index";
	}

	
	@ResponseBody
	@GetMapping(value= {"/test2"},produces = "application/json")
	public String test2(@CookieValue(name="token") String jwtToken) 
	{
		// Configurar encabezados de la solicitud
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        final String GET_ONE_URL = "http://localhost:8080/api/demo11/alumnos/getOne";
        
        HttpEntity<Alumno> requestEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        String nif = "55665544A";  // NIF de ejemplo
       
        Alumno alumno = restTemplate.postForObject(GET_ONE_URL + "?nif={nif}", requestEntity, Alumno.class, nif);
        
        // Hacer algo con el alumno
        System.out.println("\nAlumno encontrado: " + alumno.getNombre() +", "
        + alumno.getApellidos() + ", dni: "+alumno.getDni() + ", edad: " + alumno.getEdad() + " años.");

		return "index";
	}
	
	@ResponseBody
	@GetMapping(value= {"/test3"},produces = "application/json")
	public String test3(@CookieValue(name="token") String jwtToken) 
	{		
		final String AGREGAR_ALUMNO_URL = "http://localhost:8080/api/demo11/alumnos/add";

	    // Crear un objeto Alumno para enviar en la solicitud
	    Alumno nuevoAlumno = new Alumno("12345678X", "Jose Miguel", "Pérez", 25, true);

	    // Configurar encabezados de la solicitud
	    HttpHeaders headers = new HttpHeaders();
	    headers.set("Authorization", "Bearer " + jwtToken);
	    headers.setContentType(MediaType.APPLICATION_JSON);	        

	    // Crear una entidad HttpEntity con el objeto Alumno y los encabezados
	    HttpEntity<Alumno> requestEntity = new HttpEntity<>(nuevoAlumno, headers);

	    // Realizar la solicitud POST y obtener la respuesta
	    RestTemplate restTemplate = new RestTemplate();
	    ResponseEntity<String> responseEntity = restTemplate.postForEntity(AGREGAR_ALUMNO_URL, requestEntity, String.class);

	    // Imprimir la respuesta del servidor
	    System.out.println("\nRespuesta del servidor: " + responseEntity.getBody() + 
	    		"\nAlumnos: " + nuevoAlumno.getNombre() +", "+ nuevoAlumno.getApellidos() + ", dni: "+nuevoAlumno.getDni() + ", edad: " + nuevoAlumno.getEdad() + " años.");
	          
		return "index";
	}
}
