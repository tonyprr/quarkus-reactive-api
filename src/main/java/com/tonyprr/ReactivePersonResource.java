package com.tonyprr;

import com.tonyprr.model.Person;
import com.tonyprr.model.PersonPartial;
import com.tonyprr.repository.PersonRepository;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Duration;

@Path("/person")
public class ReactivePersonResource {

    @Inject
    PersonRepository personRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Person> personAll() {
        return personRepository.streamAll();
    }

    @GET
    @Path("/stream-sse")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public Multi<Person> personAllSee() {
        return personRepository.streamAll()
                .onItem().call(() ->
                        Uni.createFrom().nullItem().onItem().delayIt().by(Duration.ofSeconds(1))
            ).invoke(person -> System.out.println(person.getId()));
    }

    @GET
    @Path("/{id:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Person> findById(Long id) {
        return personRepository.findById(id);
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<String> save(@Valid Person person) {
        return personRepository.persistAndFlush(person)
                .map(unused -> "save ok");
    }

    @PUT
    @Path("/{id:\\d+}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> update(@Valid Person person, Long id) {
        return personRepository.findById(id)
                .flatMap(personPersist -> {
                    if (personPersist == null) {
                        return Uni.createFrom().item(
                                Response.status(Response.Status.NOT_FOUND).build());
                    } else {
                        personPersist.setId(id);
                        personPersist.setName(person.getName());
                        personPersist.setEmail(person.getEmail());
                        personPersist.setMaritalStatus(person.getMaritalStatus());
                        personPersist.setBirth(person.getBirth());
                        System.out.println("before update....");
                        return personRepository.persistAndFlush(personPersist)
                                .map(unused -> Response.ok().build());
                    }
                });
    }

    @PATCH
    @Path("/{id:\\d+}")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Integer> updateEmailAndMarital(@Valid PersonPartial personPartial, Long id) {
        personPartial.setId(id);
        return personRepository.update(
                "email = :email, maritalStatus = :status WHERE id = :id",
                Parameters.with("email", personPartial.getEmail())
                    .and("status", personPartial.getMaritalStatus())
                    .and("id", personPartial.getId())
            );
    }
}