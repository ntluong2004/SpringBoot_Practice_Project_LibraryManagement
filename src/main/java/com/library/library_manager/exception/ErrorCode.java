package com.library.library_manager.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    // 404 Not Found
    STAFF_NOT_FOUND(40403, "Staff member not found!", HttpStatus.NOT_FOUND),
    POSITION_NOT_FOUND(40404, "Position not found!", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND(40405, "User not found!", HttpStatus.NOT_FOUND),

    // 400 Bad Request
    USER_ALREADY_EXISTS(40001, "Username or email already exists!", HttpStatus.BAD_REQUEST),
    INVALID_INPUT(40000, "Invalid input data", HttpStatus.BAD_REQUEST),

    // 401 Unauthorized
    UNAUTHORIZED(40101, "Username or password is not correct", HttpStatus.UNAUTHORIZED),

    // Lỗi về Sách (404)
    BOOK_NOT_FOUND(40406, "Book not found!", HttpStatus.NOT_FOUND),
    BOOK_COPY_NOT_FOUND(40407, "Book copy not found!", HttpStatus.NOT_FOUND),

    // Lỗi về Logic (400)
    BOOK_ALREADY_EXISTS(40002, "ISBN already exists in system!", HttpStatus.BAD_REQUEST),
    CANNOT_DELETE_BOOK(40003, "Cannot delete book because some copies are being borrowed!", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_STOCK(40004, "Not enough available copies to remove!", HttpStatus.BAD_REQUEST),
    INVALID_STOCK_ADJUSTMENT(40005, "Adjustment value is invalid!", HttpStatus.BAD_REQUEST),

    // Lỗi liên quan đến Bản in (BookCopy)
    COPY_NOT_FOUND(40407, "Book copy not found!", HttpStatus.NOT_FOUND),
    CANNOT_DELETE_BORROWED_COPY(40010, "Cannot delete a copy that is currently borrowed!", HttpStatus.BAD_REQUEST),
    INVALID_COPY_STATUS(40011, "Invalid circulation status provided!", HttpStatus.BAD_REQUEST),
    BARCODE_ALREADY_EXISTS(40903, "Barcode already exists in the system!", HttpStatus.CONFLICT),

    // Student Management Errors
    STUDENT_NOT_FOUND(40408, "Student profile not found in the system.", HttpStatus.NOT_FOUND),
    STUDENT_ALREADY_EXISTS(40904, "Student code is already registered.", HttpStatus.CONFLICT),
    STUDENT_LOCKED(40302, "Student account is locked due to violations or outstanding debt.", HttpStatus.FORBIDDEN),
    INVALID_STUDENT_STATUS(40012, "The provided account status is invalid.", HttpStatus.BAD_REQUEST),
    MAX_BORROW_LIMIT_REACHED(40013, "Student has reached the maximum number of borrowed books.", HttpStatus.BAD_REQUEST),
    STUDENT_HAS_DEBT(40014, "Action denied: Student has outstanding fines/debt.", HttpStatus.BAD_REQUEST),

    LOAN_NOT_FOUND(40409, "Loan not found!", HttpStatus.NOT_FOUND),
    RESERVATION_NOT_FOUND(40410, "Reservation not found!", HttpStatus.NOT_FOUND),
    COPY_ALREADY_BORROWED(40015, "Book copy is not available for borrowing.", HttpStatus.BAD_REQUEST),
    RESERVATION_NOT_PENDING(40016, "Reservation is not in PENDING state.", HttpStatus.BAD_REQUEST),
    LOAN_ALREADY_CONFIRMED(40017, "Loan has already been confirmed or delivered.", HttpStatus.BAD_REQUEST),

    // 500 Internal Server Error
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR)
    ;
    int code;
    String message;
    HttpStatus status;
}