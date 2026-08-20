package es.ujaen.ahg00048.microservice_user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import es.ujaen.ahg00048.microservice_user.entity.User;


public interface UserRepository extends MongoRepository<User, String> {

}
