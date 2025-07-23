package com.bookstore.booksmanagementsystem.controller;

import com.bookstore.booksmanagementsystem.dto.ListBookDTO;
import com.bookstore.booksmanagementsystem.dto.ListDTO;
import com.bookstore.booksmanagementsystem.service.ListService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lists")
public class ListController {

    private final ListService listService;

    public ListController(ListService listService) {
        this.listService = listService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ListDTO> createList(@Valid @RequestBody ListDTO listDTO) {
        ListDTO createdList = listService.createList(listDTO);
        return new ResponseEntity<>(createdList, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListDTO> getListById(@PathVariable Long id) {
        ListDTO listDTO = listService.getListById(id);
        return ResponseEntity.ok(listDTO);
    }

    @GetMapping
    public ResponseEntity<List<ListDTO>> getAllLists(
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Integer year) {
        List<ListDTO> lists;
        if (classId != null && year != null) {
            ListDTO specificList = listService.getListByClassAndYear(classId, year);
            lists = List.of(specificList);
        } else {
            lists = listService.getAllLists();
        }
        return ResponseEntity.ok(lists);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ListDTO> updateList(@PathVariable Long id, @Valid @RequestBody ListDTO listDTO) {
        ListDTO updatedList = listService.updateList(id, listDTO);
        return ResponseEntity.ok(updatedList);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteList(@PathVariable Long id) {
        ListDTO list = listService.getListById(id);
        listService.deleteList(id);
        return ResponseEntity.ok("List for class '" + list.getClassName() + "' successfully deleted");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/books")
    public ResponseEntity<ListBookDTO> addBookToList(@Valid @RequestBody ListBookDTO listBookDTO) {
        ListBookDTO addedBook = listService.addBookToList(listBookDTO);
        return new ResponseEntity<>(addedBook, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/books/{listBookId}")
    public ResponseEntity<ListBookDTO> updateBookInList(@PathVariable Long listBookId, @Valid @RequestBody ListBookDTO listBookDTO) {
        ListBookDTO updatedBook = listService.updateBookInList(listBookId, listBookDTO);
        return ResponseEntity.ok(updatedBook);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/books/{listBookId}")
    public ResponseEntity<String> removeBookFromList(@PathVariable Long listBookId) {
        ListBookDTO book = listService.getBooksForList(listBookId).stream()
                .filter(b -> b.getId().equals(listBookId))
                .findFirst()
                .orElse(null);
        listService.removeBookFromList(listBookId);
        String bookTitle = (book != null) ? book.getBookTitle() : "Book";
        return ResponseEntity.ok(bookTitle + " successfully removed from list");
    }

    @GetMapping("/{listId}/books")
    public ResponseEntity<List<ListBookDTO>> getBooksForList(@PathVariable Long listId) {
        List<ListBookDTO> booksInList = listService.getBooksForList(listId);
        return ResponseEntity.ok(booksInList);
    }
}
