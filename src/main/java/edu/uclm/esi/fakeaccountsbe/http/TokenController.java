package edu.uclm.esi.fakeaccountsbe.http;

import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.fakeaccountsbe.model.CredencialesRegistro;
import edu.uclm.esi.fakeaccountsbe.model.User;
import edu.uclm.esi.fakeaccountsbe.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Es un proyecto que utiliza Spring, que tiene una serie de notaciones que nos deja decir el rol de una clase en un sistema

@RestController //Decimos que es un controlador, o sea que va a recibir peticiones http
@RequestMapping("tokens") //Nombre público (lo de /users/)
@CrossOrigin("*")
public class TokenController {
	
	@PutMapping("/validar")
	public void validar(@RequestBody String token) {
		if (new Random().nextBoolean()) {
			throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED);
		}
	}
	
}
















