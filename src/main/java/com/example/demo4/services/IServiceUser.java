package com.example.demo4.services;

import java.util.List;

import com.example.demo4.entities.User;

public interface IServiceUser {
	
	void ajouter(User user);

	void supprimer(int id);

	void modifier(User user, int id);

	List<User> afficherTous();

	User rechercherUserParId(int id);

}
