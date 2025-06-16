package com.library.controller;

import com.library.bo.BookBO;
import com.library.dto.request.book.BookCreateDTO;
import com.library.dto.request.book.BookUpdateDTO;
import com.library.dto.response.BookResponseDTO;
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
        try {
            BookResponseDTO response = bookBO.create(request);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
    }

    @GET
    @RolesAllowed({ "ADMIN" })
    public Response findAll() {
        try {
            List<BookResponseDTO> response = bookBO.findAll();
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
    }

    @GET
    @Path("{isbn}")
    @RolesAllowed({ "ADMIN", "MEMBER"})
    public Response findByIsbn(@PathParam("isbn") String isbn) {
        try {
            BookResponseDTO response = bookBO.findByIsbn(isbn);
            return Response.status(Response.Status.OK).entity(response).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
    }

    @GET()
    @Path("available")
    @RolesAllowed({ "ADMIN", "MEMBER" })
    public Response findAvailableBooks() {
        try {
            List<BookResponseDTO> response = bookBO.findAvailableBooks();
            return Response.status(Response.Status.OK).entity(response).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
    }

    @PUT()
    @Path("/{isbn}")
    @RolesAllowed({ "ADMIN" })
    public Response update(@PathParam("isbn") String isbn, BookUpdateDTO bookUpdateDTO) {
        try {
            BookResponseDTO response = bookBO.updateBook(isbn, bookUpdateDTO);
            return Response.status(Response.Status.OK).entity(response).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
    }

    @DELETE
    @Path("/{isbn}")
    @RolesAllowed({ "ADMIN" })
    public Response delete(@PathParam("isbn") String isbn) {
        try {
            bookBO.delete(isbn);
            return Response.noContent().build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
    }
} 