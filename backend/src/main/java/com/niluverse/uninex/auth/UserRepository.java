package com.niluverse.uninex.auth;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByGoogleId(String googleId);

    Optional<User> findByEmail(String email);
}
