package com.library.library_manager.service.impl;

import com.library.library_manager.dto.BorrowSessionResponseDTO;
import com.library.library_manager.dto.LoanRequestDTO;
import com.library.library_manager.dto.LoanResponseDTO;
import com.library.library_manager.dto.ReservationResponseDTO;
import com.library.library_manager.dto.staff.StaffRequestDTO;
import com.library.library_manager.dto.staff.StaffResponseDTO;
import com.library.library_manager.entity.*;
import com.library.library_manager.exception.AppException;
import com.library.library_manager.exception.ErrorCode;
import com.library.library_manager.enums.BookCopyStatus;
import com.library.library_manager.mapper.LoanMapper;
import com.library.library_manager.mapper.StaffMapper;
import com.library.library_manager.repository.*;
import com.library.library_manager.service.IStaffService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StaffService implements IStaffService {

    IStaffRepository staffRepository;
    IUserRepository userRepository;
    IPositionRepository positionRepository;

    private final ILoanRepository loanRepository;
    private final IBookCopyRepository bookCopyRepository;
    private final IStudentRepository studentRepository;
    private final IReservationRepository reservationRepository;

    private final StaffMapper staffMapper;
    private final LoanMapper loanMapper;

    @Override
    public List<StaffResponseDTO> findAll() {
        return staffMapper.toStaffResponseList(staffRepository.findAll());
    }

    @Override
    @Transactional
    public StaffResponseDTO createStaff(StaffRequestDTO request) { // <-- 1. Đổi tham số thành StaffRequestDTO

        // 2. Tìm chức vụ (Position) dựa trên positionId truyền trực tiếp từ DTO
        Position position = positionRepository.findById(request.getPositionId())
                .orElseThrow(() -> new AppException(ErrorCode.POSITION_NOT_FOUND));

        // 3. Tạo mới đối tượng User (vì Staff thường quan hệ 1-1 với User, cần có User trước)
        // Lương kiểm tra xem DTO của bạn có trường username/email không thì set bổ sung vào nhé
        User user = User.builder()
                .fullName(request.getFullName())
                .build();

        // 4. Build đối tượng Staff Entity từ các nguyên liệu trên để chuẩn bị lưu vào DB
        Staff staff = Staff.builder()
                .user(user)
                .position(position)
                .build();

        // 5. Lưu Staff Entity xuống DB (nó sẽ tự động lưu kèm User nếu bạn cấu hình CascadeType.ALL)
        Staff savedStaff = staffRepository.save(staff);

        // 6. Map sang ResponseDTO để trả về cho Frontend/Postman
        return staffMapper.staffToStaffResponseDTO(savedStaff);
    }

    @Override
    @Transactional
    public StaffResponseDTO updateStaff(Long staffId, StaffRequestDTO request) { // ĐẢM BẢO LÀ StaffRequestDTO
        Staff existingStaff = staffRepository.findById(staffId)
                .orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));

        // Cập nhật chức vụ
        if (request.getPositionId() != null) {
            Position newPos = positionRepository.findById(request.getPositionId())
                    .orElseThrow(() -> new AppException(ErrorCode.POSITION_NOT_FOUND));
            existingStaff.setPosition(newPos);
        }

        // Cập nhật họ tên
        if (request.getFullName() != null && existingStaff.getUser() != null) {
            existingStaff.getUser().setFullName(request.getFullName());
        }

        return staffMapper.staffToStaffResponseDTO(staffRepository.save(existingStaff));
    }

    @Override
    @Transactional
    public void deleteStaff(Long staffId) {
        Staff staff = staffRepository.findById(staffId).orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));
        staffRepository.delete(staff);
    }

    // Helper (delegate sang StaffMapper)
    private StaffResponseDTO mapToResponseDTO(Staff staff) {
        return staffMapper.staffToStaffResponseDTO(staff);
    }

    // 1. Lấy thông tin tổng quát khi quét thẻ
    public BorrowSessionResponseDTO getBorrowSession(String studentCode) {
        Student student = studentRepository.findByStudentCode(studentCode).orElseThrow(() -> new RuntimeException("Student not found: " + studentCode));

        String username = student.getUser().getUserName();

        List<Reservation> resList = reservationRepository.findByStudent_User_UserNameAndStatus(username, "Đang giữ");
        List<Loan> loanList = loanRepository.findByUser_UserNameAndReturnedAtIsNull(username);

        long remainingLimit = 5 - (resList.size() + loanList.size());

        return BorrowSessionResponseDTO.builder().pendingReservations(loanMapper.toReservationResponseList(resList)).currentLoans(loanMapper.toLoansResponseList(loanList)).totalDebt(student.getTotalDebt()).remainingLimit(Math.max(0, remainingLimit)).build();
    }

    // 2 & 6. Xác nhận SV đến lấy sách (Cả từ Reservation và Loan PENDING)
    @Transactional
    public void confirmPickup(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu mượn"));

        loan.setStatus("BORROWED");
        loan.setBorrowDate(LocalDateTime.now()); // Đổi từ setLoanDate thành setBorrowDate
        loanRepository.save(loan);
    }

    public String checkEligibility(String studentCode, String barcode) {
        Student student = studentRepository.findByStudentCode(studentCode).orElseThrow(() -> new RuntimeException("Student not found"));

        BookCopy copy = bookCopyRepository.findByBarcode(barcode).orElseThrow(() -> new RuntimeException("Barcode not found"));

        if (student.getTotalDebt() > 0) {
            return "FAIL: Sinh viên đang có công nợ: " + student.getTotalDebt();
        }

        if (Boolean.TRUE.equals(copy.getIsLocked()) || !"Available".equals(copy.getStatus())) {
            return "FAIL: Sách đã được giữ hoặc đang cho mượn.";
        }

        String username = student.getUser().getUserName();
        long currentTotal = loanRepository.countByUser_UserNameAndReturnedAtIsNull(username) + reservationRepository.countByStudent_User_UserNameAndStatus(username, "Đang giữ");

        if (currentTotal >= 5) {
            return "FAIL: Vượt quá hạn mức 5 cuốn.";
        }

        return "PASS";
    }

    // 5. Tạo phiếu mượn (Mượn tại quầy)
    @Transactional
    public Loan createLoan(LoanRequestDTO dto) {
        // THÊM DÒNG NÀY: Tìm đối tượng student trước khi sử dụng
        Student student = studentRepository.findByStudentCode(dto.getStudentCode()).orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên"));

        BookCopy copy = bookCopyRepository.findById(dto.getCopyId()).orElseThrow(() -> new RuntimeException("Không tìm thấy bản sao sách"));

        Loan loan = Loan.builder().user(student.getUser()) // Bây giờ 'student' đã tồn tại, lỗi sẽ biến mất
                .bookCopy(copy).borrowDate(LocalDateTime.now()).dueDate(LocalDateTime.now().plusDays(14)).status("PENDING_PICKUP").staffNote(dto.getNote()).build();

        copy.setIsLocked(true);
        copy.setStatus(BookCopyStatus.RESERVED);
        bookCopyRepository.save(copy);

        return loanRepository.save(loan);
    }

    // 1. (Nhánh Reservation) Chuyển Đặt trước thành Phiếu mượn
    @Transactional
    public void confirmPickupFromReservation(Long resId) {
        Reservation res = reservationRepository.findById(resId).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt"));

        // LẤY STUDENT TỪ RESERVATION
        Student student = res.getStudent();

        res.setStatus("COMPLETED");
        reservationRepository.save(res);

        Loan loan = Loan.builder().user(student.getUser()) // Lấy User từ Student của đơn đặt
                .bookCopy(res.getBookCopy()).borrowDate(LocalDateTime.now()).dueDate(LocalDateTime.now().plusDays(14)).status("BORROWED").staffNote("Mượn từ đơn đặt trước #" + resId).build();

        loanRepository.save(loan);
    }

    // 10. Đánh dấu mất/hư hỏng
    @Transactional
    public void markIssue(Long loanId, String issueType) {
        Loan loan = loanRepository.findById(loanId).orElseThrow();
        loan.setStatus(issueType);

        // SỬA TẠI ĐÂY: Tìm Student từ User của Loan
        Student student = studentRepository.findByUser(loan.getUser()).orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin sinh viên"));

        double fine = issueType.equals("LOST") ? 200000.0 : 50000.0;
        student.setTotalDebt(student.getTotalDebt() + fine);

        studentRepository.save(student);
        loanRepository.save(loan);
    }

    // Hủy đơn đặt tại quầy
    @Transactional
    public void cancelReservationAtCounter(Long resId) {
        Reservation res = reservationRepository.findById(resId).orElseThrow();
        res.setStatus("Bị hủy");
        if (res.getBookCopy() != null) {
            res.getBookCopy().setIsLocked(false);
            res.getBookCopy().setStatus(BookCopyStatus.AVAILABLE);
            bookCopyRepository.save(res.getBookCopy());
        }
        reservationRepository.save(res);
    }

    // Hủy phiếu mượn chưa giao
    @Transactional
    public void cancelLoanBeforePickup(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow();
        BookCopy copy = loan.getBookCopy();
        copy.setIsLocked(false);
        copy.setStatus(BookCopyStatus.AVAILABLE);
        bookCopyRepository.save(copy);
        loanRepository.delete(loan);
    }
}