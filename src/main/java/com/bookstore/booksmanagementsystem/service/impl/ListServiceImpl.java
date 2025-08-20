package com.bookstore.booksmanagementsystem.service.impl;

import com.bookstore.booksmanagementsystem.dto.ListBookDTO;
import com.bookstore.booksmanagementsystem.dto.ListDTO;
import com.bookstore.booksmanagementsystem.entity.Book;
import com.bookstore.booksmanagementsystem.entity.ClassEntity;
import com.bookstore.booksmanagementsystem.entity.ListBook;
import com.bookstore.booksmanagementsystem.entity.ListEntity;
import com.bookstore.booksmanagementsystem.exception.DuplicateResourceException;
import com.bookstore.booksmanagementsystem.exception.ResourceNotFoundException;
import com.bookstore.booksmanagementsystem.repository.BookRepository;
import com.bookstore.booksmanagementsystem.repository.ClassEntityRepository;
import com.bookstore.booksmanagementsystem.repository.ListBookRepository;
import com.bookstore.booksmanagementsystem.repository.ListEntityRepository;
import com.bookstore.booksmanagementsystem.service.ListService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListServiceImpl implements ListService {

	private final ListEntityRepository listEntityRepository;
	private final ClassEntityRepository classEntityRepository;
	private final BookRepository bookRepository;
	private final ListBookRepository listBookRepository;
	private final ModelMapper modelMapper;

	public ListServiceImpl(ListEntityRepository listEntityRepository, ClassEntityRepository classEntityRepository,
			BookRepository bookRepository, ListBookRepository listBookRepository, ModelMapper modelMapper) {
		this.listEntityRepository = listEntityRepository;
		this.classEntityRepository = classEntityRepository;
		this.bookRepository = bookRepository;
		this.listBookRepository = listBookRepository;
		this.modelMapper = modelMapper;
	}

	@Override
	@Transactional
	public ListDTO createList(ListDTO listDTO) {
		ClassEntity classEntity = classEntityRepository.findById(listDTO.getClassId())
				.orElseThrow(() -> new ResourceNotFoundException("Class", "id", listDTO.getClassId()));

		listEntityRepository.findByClassEntityIdAndYear(listDTO.getClassId(), listDTO.getYear()).ifPresent(l -> {
			throw new DuplicateResourceException("Official List", "class and year",
					classEntity.getName() + ", " + listDTO.getYear());
		});

		ListEntity listEntity = modelMapper.map(listDTO, ListEntity.class);
		listEntity.setClassEntity(classEntity);

		ListEntity savedList = listEntityRepository.save(listEntity);
		ListDTO responseDTO = modelMapper.map(savedList, ListDTO.class);
		responseDTO.setClassName(savedList.getClassEntity().getName());
		responseDTO.setSchoolName(savedList.getClassEntity().getSchool().getName());
		return responseDTO;
	}

	@Override
	public ListDTO getListById(Long id) {
		ListEntity listEntity = listEntityRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Official List", "id", id));
		ListDTO dto = modelMapper.map(listEntity, ListDTO.class);
		dto.setClassName(listEntity.getClassEntity().getName());
		dto.setSchoolName(listEntity.getClassEntity().getSchool().getName());
		if (listEntity.getListBooks() != null) {
			dto.setListBooks(
					listEntity.getListBooks().stream().map(this::mapToListBookDTO).collect(Collectors.toSet()));
		}
		return dto;
	}

	@Override
	public List<ListDTO> getAllLists() {
		List<ListEntity> lists = listEntityRepository.findAll();
		return lists.stream().map(listEntity -> {
			ListDTO dto = modelMapper.map(listEntity, ListDTO.class);
			dto.setClassName(listEntity.getClassEntity().getName());
			dto.setSchoolName(listEntity.getClassEntity().getSchool().getName());
			if (listEntity.getListBooks() != null) {
				dto.setListBooks(
						listEntity.getListBooks().stream().map(this::mapToListBookDTO).collect(Collectors.toSet()));
			}
			return dto;
		}).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public ListDTO updateList(Long id, ListDTO listDTO) {
		ListEntity existingList = listEntityRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Official List", "id", id));

		ClassEntity classEntity = classEntityRepository.findById(listDTO.getClassId())
				.orElseThrow(() -> new ResourceNotFoundException("Class", "id", listDTO.getClassId()));

		listEntityRepository.findByClassEntityIdAndYear(listDTO.getClassId(), listDTO.getYear()).ifPresent(l -> {
			if (!l.getId().equals(id)) {
				throw new DuplicateResourceException("Official List", "class and year",
						classEntity.getName() + ", " + listDTO.getYear());
			}
		});

		existingList.setClassEntity(classEntity);
		existingList.setYear(listDTO.getYear());

		ListEntity updatedList = listEntityRepository.save(existingList);
		ListDTO responseDTO = modelMapper.map(updatedList, ListDTO.class);
		responseDTO.setClassName(updatedList.getClassEntity().getName());
		responseDTO.setSchoolName(updatedList.getClassEntity().getSchool().getName());
		if (updatedList.getListBooks() != null) {
			responseDTO.setListBooks(
					updatedList.getListBooks().stream().map(this::mapToListBookDTO).collect(Collectors.toSet()));
		}
		return responseDTO;
	}

	@Override
	public void deleteList(Long id) {
		ListEntity listEntity = listEntityRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Official List", "id", id));
		listEntityRepository.delete(listEntity);
	}

	@Override
	@Transactional
	public ListBookDTO addBookToList(ListBookDTO listBookDTO) {
		ListEntity listEntity = listEntityRepository.findById(listBookDTO.getListId())
				.orElseThrow(() -> new ResourceNotFoundException("Official List", "id", listBookDTO.getListId()));

		Book book = bookRepository.findById(listBookDTO.getBookId())
				.orElseThrow(() -> new ResourceNotFoundException("Book", "id", listBookDTO.getBookId()));

		listBookRepository.findByListEntityIdAndBookId(listBookDTO.getListId(), listBookDTO.getBookId())
				.ifPresent(lb -> {
					throw new DuplicateResourceException("Book in List", "listId and bookId",
							listBookDTO.getListId() + ", " + listBookDTO.getBookId());
				});

		ListBook listBook = new ListBook();
		listBook.setListEntity(listEntity);
		listBook.setBook(book);
		// Removed quantity from list creation

		ListBook savedListBook = listBookRepository.save(listBook);
		return mapToListBookDTO(savedListBook);
	}

	@Override
	@Transactional
	public ListBookDTO updateBookInList(Long listBookId, ListBookDTO listBookDTO) {
		ListBook existingListBook = listBookRepository.findById(listBookId)
				.orElseThrow(() -> new ResourceNotFoundException("List Book Item", "id", listBookId));

		if (!existingListBook.getListEntity().getId().equals(listBookDTO.getListId())
				|| !existingListBook.getBook().getId().equals(listBookDTO.getBookId())) {

			listBookRepository.findByListEntityIdAndBookId(listBookDTO.getListId(), listBookDTO.getBookId())
					.ifPresent(lb -> {
						if (!lb.getId().equals(listBookId)) {
							throw new DuplicateResourceException("Book in List", "listId and bookId",
									listBookDTO.getListId() + ", " + listBookDTO.getBookId());
						}
					});

			ListEntity newListEntity = listEntityRepository.findById(listBookDTO.getListId())
					.orElseThrow(() -> new ResourceNotFoundException("Official List", "id", listBookDTO.getListId()));
			Book newBook = bookRepository.findById(listBookDTO.getBookId())
					.orElseThrow(() -> new ResourceNotFoundException("Book", "id", listBookDTO.getBookId()));

			existingListBook.setListEntity(newListEntity);
			existingListBook.setBook(newBook);
		}

		// Removed quantity from list update

		ListBook updatedListBook = listBookRepository.save(existingListBook);
		return mapToListBookDTO(updatedListBook);
	}

	@Override
	public void removeBookFromList(Long listBookId) {
		ListBook listBook = listBookRepository.findById(listBookId)
				.orElseThrow(() -> new ResourceNotFoundException("List Book Item", "id", listBookId));
		listBookRepository.delete(listBook);
	}

	@Override
	public List<ListBookDTO> getBooksForList(Long listId) {
		if (!listEntityRepository.existsById(listId)) {
			throw new ResourceNotFoundException("Official List", "id", listId);
		}
		List<ListBook> listBooks = listBookRepository.findByListEntityId(listId);
		if (listBooks.isEmpty()) {
			return List.of();
		}
		return listBooks.stream().map(this::mapToListBookDTO).collect(Collectors.toList());
	}

	@Override
	public ListBookDTO getBookById(Long listBookId) {
		ListBook listBook = listBookRepository.findById(listBookId)
				.orElseThrow(() -> new ResourceNotFoundException("List Book Item", "id", listBookId));
		return mapToListBookDTO(listBook);
	}

	@Override
	public ListDTO getListByClassAndYear(Long classId, Integer year) {
		ListEntity listEntity = listEntityRepository.findByClassEntityIdAndYear(classId, year).orElseThrow(
				() -> new ResourceNotFoundException("Official List", "Class ID and Year", classId + " and " + year));
		ListDTO dto = modelMapper.map(listEntity, ListDTO.class);
		dto.setClassName(listEntity.getClassEntity().getName());
		dto.setSchoolName(listEntity.getClassEntity().getSchool().getName());
		if (listEntity.getListBooks() != null) {
			dto.setListBooks(
					listEntity.getListBooks().stream().map(this::mapToListBookDTO).collect(Collectors.toSet()));
		}
		return dto;
	}

	private ListBookDTO mapToListBookDTO(ListBook listBook) {
		ListBookDTO dto = modelMapper.map(listBook, ListBookDTO.class);
		dto.setBookTitle(listBook.getBook().getTitle());
		dto.setBookAuthor(listBook.getBook().getAuthor());
		dto.setBookPrice(listBook.getBook().getPrice());
		dto.setImageUrl(listBook.getBook().getImageUrl());
		dto.setListYear(String.valueOf(listBook.getListEntity().getYear()));
		dto.setListClassName(listBook.getListEntity().getClassEntity().getName());
		return dto;
	}
}
