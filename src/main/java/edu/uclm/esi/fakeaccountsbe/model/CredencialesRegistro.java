package edu.uclm.esi.fakeaccountsbe.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CredencialesRegistro {
	// Con registrar1, los interceptores pueden inyectar los datos en un objeto CredencialesRegistro a través de los getters y setters.
	private String email;
	private String pwd1;
	private String pwd2;
	
	public void comprobar() {
		if (!pwd1.equals(pwd2))
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Las passwords no coinciden");
		if (pwd1.length()<4)
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "La contraseña tiene que tener 4 o más caracteres");
	}
	
	//Los getters y los setters, para que los interceptores puedan inyectar, tienen que tener el mismo nombre que los campos del JSON
	// Si hay más campos que aquí no están, los ignora
	
	public String getEmail() { 
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPwd1() {
		return pwd1;
	}
	public void setPwd1(String pwd) {
		this.pwd1 = pwd;
	}
	public String getPwd2() {
		return pwd2;
	}
	public void setPwd2(String pwd) {
		this.pwd2 = pwd;
	}
	
}
