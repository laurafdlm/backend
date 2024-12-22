package edu.uclm.esi.fakeaccountsbe.http;

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

import edu.uclm.esi.fakeaccountsbe.dao.UserDao;
import edu.uclm.esi.fakeaccountsbe.model.CredencialesRegistro;
import edu.uclm.esi.fakeaccountsbe.model.User;
import edu.uclm.esi.fakeaccountsbe.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.*;

// Es un priyecto que utiliza Spring, que tiene una serie de notaciones que nos deja decir el rol de una clase en un sistema
@RestController //Decimos que es un controlador, o sea que va a recibir peticiones http
@RequestMapping("users") //Nombre público (lo de /users/)
@CrossOrigin("*") //Admite peticiones de cualquier sitio, vengan de donde vengan, sin restricciones
public class UserController {
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserDao userDao;
	
	@PostMapping("/registrar1") //Tipo POST
	public void registrar1(HttpServletRequest req, @RequestBody CredencialesRegistro cr) { //Quiere decir que en el body no  pasaran Credenciales de Registro
		//Los INTERCEPTORES se encargarán de inyectar el cuerpo del JSON (ven si pueden hacerlo) como un objeto de CredencialesRegistro (nos vamos ahí)
		cr.comprobar();
		User user = new User();
		user.setEmail(cr.getEmail());
		user.setPwd(cr.getPwd1());
		
		//Si tuviese BD, tendría que guardarlo también en la BD
		
		this.userService.registrar(req.getRemoteAddr(), user); // con getRemoteAddr() le paso la IP del cliente.
		// req lleva la información de la petición (eso también lo hacen arriba del método los interceptores)
	}
	
	@GetMapping("/registrar2")
	public void registrar2(HttpServletRequest req, @RequestParam String email, @RequestParam String pwd1, @RequestParam String pwd2) {
		// Para decir que los datos en la petición van en forma de parámetros lo hacemos con @RequestParam
		CredencialesRegistro cr = new CredencialesRegistro();
		//Ahora no los inyecta nadie, así que lo tengo que hacer yo
		cr.setEmail(email);
		cr.setPwd1(pwd1);
		cr.setPwd2(pwd2);
		cr.comprobar();
		User user = new User();
		user.setEmail(cr.getEmail());
		user.setPwd(cr.getPwd1());
		
		this.userService.registrar(req.getRemoteAddr(), user);
	}
	
	@GetMapping("/registrarMuchos")
	public void registrarMuchos(HttpServletRequest req, @RequestParam String name, @RequestParam Integer n) {
		for (int i=0; i<n; i++)
			this.registrar2(req, name + i + "@pepe.com", "Pepe1234", "Pepe1234");
	}
	
	@PutMapping("/login1")
	public String login1(HttpServletResponse response, HttpServletRequest request, @RequestBody User user) {
		String fakeUserId = this.findCookie(request, "fakeUserId");
		if(fakeUserId==null) {
			user = this.userService.find(user.getEmail(), user.getPwd());
			fakeUserId = UUID.randomUUID().toString();
			response.addCookie(new Cookie("fakeUserId", fakeUserId));
			user.setCookie(fakeUserId);
			this.userDao.save(user);
			user.setToken(UUID.randomUUID().toString());
		}else {
			user = this.userDao.findByCookie(fakeUserId);
			if(user!=null) {
				user.setToken(UUID.randomUUID().toString());
			}else {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cookie Expirada");
			}
		}
		return user.getToken();
	}
	
	private String findCookie(HttpServletRequest request, String cookieName) {
		Cookie [] cookies = request.getCookies();
		if (cookies == null) {
			return null;
		}else {
			for(int i = 0; i<cookies.length; i++) {
				if(cookies[i].getName().equals(cookieName)) {
					return cookies[i].getValue();
				}
			}
		}
		return null;
	}
	
	@GetMapping("/login2")
	public User login2(HttpServletResponse response, @RequestParam String email, @RequestParam String pwd) {
		User user = this.userService.find(email, pwd);
		user.setToken(UUID.randomUUID().toString());
		response.setHeader("token", user.getToken());
		return user;
	}
	
	@GetMapping("/login3/{email}")
	public User login3(HttpServletResponse response, @PathVariable String email, @RequestParam String pwd) {
		return this.login2(response, email, pwd);
	}
	
	@GetMapping("/getAllUsers")
	public Iterable<User> getAllUsers() {
		return this.userService.getAllUsers();
	}
	
	@DeleteMapping("/delete")
	public void delete(HttpServletRequest request, @RequestParam String email, @RequestParam String pwd) {
		//Busco al usuario
		User user = this.userService.find(email, pwd);
		
		//El delete necesita la comprobación del token. De las cabeceras, cojo el token y compruebo si es el mismo del usuario logueado
		String token = request.getHeader("token");
		if (!token.equals(user.getToken()))
			//Excepciones específicas de HTTP: ResponseStatusException
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Token " + token + " inválido");
		
		//Si todo va bien, se borra
		this.userService.delete(email);
	}
	
	@DeleteMapping("/clearAll")
	public void clearAll(HttpServletRequest request) {
		String sToken = request.getHeader("prime");
		Integer token = Integer.parseInt(sToken); //Aquí puede dar el error 500 si lo que nos pasan no es un número
		if (!isPrime(token.intValue()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Debes pasar un número primo en la cabecera");
		if (sToken.length()!=3)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El nº primo debe tener tres cifras");
		
		// Si todo va bien, ejecuto el clearAll
		this.userService.clearAll();
	}
	
	private boolean isPrime(int n) {
	    if (n <= 1) return false;
	    for (int i = 2; i <= Math.sqrt(n); i++) {
	        if (n % i == 0) return false;
	    }
	    return true;
	}
}
















