package edu.uclm.esi.fakeaccountsbe.dao;

import org.springframework.data.repository.CrudRepository; //Todos los métodos CRUD lo podemos hacer con esto
//Spring nos genera para nosotros métodos de clase select, insert aposta la clase User

import edu.uclm.esi.fakeaccountsbe.model.User;

public interface UserDao extends CrudRepository<User, String> {

	User findByCookie(String fakeUserId);

}
