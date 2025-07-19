package com.library.controller.api;

import com.library.model.bo.BookBO;
import com.library.model.dto.PagedResponseDTO;
import com.library.model.dto.book.BookCreateDTO;
import com.library.model.dto.book.BookUpdateDTO;
import com.library.model.dto.book.BookResponseDTO;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Path("book")
public class BookController {

    private static final int DEFAULT_PAGE_SIZE = 10;

    @Inject
    BookBO bookBO;

    @POST
    @RolesAllowed({ "ADMIN" })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@Valid BookCreateDTO request) {
        BookResponseDTO response = bookBO.create(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @RolesAllowed({ "ADMIN" })
    @Path("get-all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll(@QueryParam("page") @DefaultValue("0") int page) {
        PagedResponseDTO<BookResponseDTO> response = bookBO.findAll(page, DEFAULT_PAGE_SIZE);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @Path("get/{isbn}")
    @RolesAllowed({ "ADMIN", "MEMBER"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response findByIsbn(@PathParam("isbn") String isbn) {
        BookResponseDTO response = bookBO.findByIsbn(isbn);
        return Response.status(Response.Status.OK).entity(response).build();
    }

    @GET()
    @Path("available-books")
    @RolesAllowed({ "ADMIN", "MEMBER" })
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAvailableBooks(@QueryParam("page") @DefaultValue("0") int page) {
        PagedResponseDTO<BookResponseDTO> response = bookBO.findAvailableBooks(page, DEFAULT_PAGE_SIZE);
        return Response.status(Response.Status.OK).entity(response).build();
    }

    @PUT()
    @Path("update/{isbn}")
    @RolesAllowed({ "ADMIN" })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("isbn") String isbn, BookUpdateDTO bookUpdateDTO) {
        BookResponseDTO response = bookBO.updateBook(isbn, bookUpdateDTO);
        return Response.status(Response.Status.OK).entity(response).build();
    }

    @DELETE
    @Path("delete/{isbn}")
    @RolesAllowed({ "ADMIN" })
    public Response delete(@PathParam("isbn") String isbn) {
        bookBO.delete(isbn);
        return Response.noContent().build();
    }

    @GET
    @Path("count-available")
    @RolesAllowed({ "ADMIN", "MEMBER" })
    @Produces(MediaType.APPLICATION_JSON)
    public Response countAvailableBooks() {
        return Response.ok(Map.of("availableBooksCount", bookBO.countAvailableBooks())).build();
    }

    @GET
    @Path("count-all")
    @RolesAllowed({ "ADMIN", "MEMBER" })
    @Produces(MediaType.APPLICATION_JSON)
    public Response countAllBooks() {
        return Response.ok(Map.of("allBooksCount", bookBO.countAllBooks())).build();
    }
} 