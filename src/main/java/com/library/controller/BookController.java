package com.library.controller;

import com.library.bo.BookBO;
import com.library.dto.request.BookRequestDTO;
import com.library.dto.response.BookResponseDTO;
import jakarta.inject.Inject;
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
    public Response create(BookRequestDTO request) {
        BookResponseDTO response = bookBO.create(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    public List<BookResponseDTO> findAll() {
        return bookBO.findAll();
    }

    @GET
    @Path("{isbn}")
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
    public Response findAvailableBooks() {
        try {
            List<BookResponseDTO> response = bookBO.findAvailableBooks();
            return Response.status(Response.Status.OK).entity(response).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
    }

    @PUT
    @Path("/{id}")
    public BookResponseDTO update(@PathParam("id") Long id, BookRequestDTO request) {
        return bookBO.update(id, request);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        bookBO.delete(id);
        return Response.noContent().build();
    }
} 