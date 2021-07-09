package com.tonyprr.repository;

import com.tonyprr.model.Person;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class PersonRepository implements PanacheRepository<Person> {

    // put your custom logic here as instance methods

    public Uni<Person> findByName(String name){
        return find("name", name).firstResult();
    }

    public void deleteStefs() {
        delete("name", "Stef");
    }
}