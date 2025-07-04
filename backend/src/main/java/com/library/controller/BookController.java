package com.library.controller;

import com.library.model.bo.BookBO;
import com.library.model.dto.book.BookCreateDTO;
import com.library.model.dto.book.BookUpdateDTO;
import com.library.model.dto.book.BookResponseDTO;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookController {

    @Inject
    BookBO bookBO;

    @POST
    @RolesAllowed({ "ADMIN" })
    public Response create(@Valid BookCreateDTO request) {
        BookResponseDTO response = bookBO.create(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @RolesAllowed({ "ADMIN" })
    public Response findAll() {
        List<BookResponseDTO> response = bookBO.findAll();
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @Path("{isbn}")
    @RolesAllowed({ "ADMIN", "MEMBER"})
    public Response findByIsbn(@PathParam("isbn") String isbn) {
        BookResponseDTO response = bookBO.findByIsbn(isbn);
        return Response.status(Response.Status.OK).entity(response).build();
    }

    @GET()
    @Path("available")
    @RolesAllowed({ "ADMIN", "MEMBER" })
    public Response findAvailableBooks() {
        List<BookResponseDTO> response = bookBO.findAvailableBooks();
        return Response.status(Response.Status.OK).entity(response).build();
    }

    @PUT()
    @Path("/{isbn}")
    @RolesAllowed({ "ADMIN" })
    public Response update(@PathParam("isbn") String isbn, BookUpdateDTO bookUpdateDTO) {
        BookResponseDTO response = bookBO.updateBook(isbn, bookUpdateDTO);
        return Response.status(Response.Status.OK).entity(response).build();
    }

    @DELETE
    @Path("/{isbn}")
    @RolesAllowed({ "ADMIN" })
    public Response delete(@PathParam("isbn") String isbn) {
        bookBO.delete(isbn);
        return Response.noContent().build();
    }
} 