package com.bookstore.booksmanagementsystem.service;

import com.bookstore.booksmanagementsystem.dto.ListBookDTO;
import com.bookstore.booksmanagementsystem.dto.ListDTO;

import java.util.List;

public interface ListService {
	ListDTO createList(ListDTO listDTO);

	ListDTO getListById(Long id);

	List<ListDTO> getAllLists();

	ListDTO updateList(Long id, ListDTO listDTO);

	void deleteList(Long id);

	ListBookDTO addBookToList(ListBookDTO listBookDTO);

	ListBookDTO updateBookInList(Long listBookId, ListBookDTO listBookDTO);

	void removeBookFromList(Long listBookId);

	List<ListBookDTO> getBooksForList(Long listId);

	ListDTO getListByClassAndYear(Long classId, Integer year);

	ListBookDTO getBookById(Long listBookId);
}
