document.addEventListener("DOMContentLoaded", () => 
{
	$(document).ready(()=>
	{
		$('body').on('click', '#boton', () =>
 	 	{
			$("#prueba").html('<p style="color: red;text-align: left; font-family: "Lato", sans-serif; font-size: 40px;">FUNCIONA</p>');
  		});
	});

	// Obtener el token de la cookie
	let jwtToken = document.cookie.replace(/(?:(?:^|.*;\s*)token\s*\=\s*([^;]*).*$)|^.*$/, "$1");

	// Configurar jQuery para añadir el token a los encabezados de cada petición
	$.ajaxSetup(
	{
		headers: 
		{
			'Authorization': 'Bearer ' + jwtToken
		}
	});

	// Hacer la petición
	$.get("/index", function(data) 
	{
		console.log(data);
	});











});

