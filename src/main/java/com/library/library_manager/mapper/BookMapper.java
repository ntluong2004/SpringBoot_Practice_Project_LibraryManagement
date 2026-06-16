package com.library.library_manager.mapper;

import com.library.library_manager.dto.book.BookRequestDTO;
import com.library.library_manager.dto.book.BookResponseDTO;
import com.library.library_manager.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookMapper {

    // Nếu lúc tạo mới, DTO truyền vào có trường nào lệch thì cấu hình tương tự
    Book bookRequestDTOToBook(BookRequestDTO studentRequest);

    // QUAN TRỌNG: Chỉ định map trường bookId từ Entity sang trường id của DTO
    @Mapping(source = "bookId", target = "id")
    BookResponseDTO bookToBookResponseDTO(Book book);
}
