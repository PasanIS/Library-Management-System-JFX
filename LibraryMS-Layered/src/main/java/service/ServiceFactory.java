package service;

import service.custom.*;
import service.custom.impl.*;

public class ServiceFactory {
    private static BookService bookService;
    private static MemberService memberService;
    private static StaffService staffService;
    private static BorrowingService borrowingService;
    private static FineService fineService;

    private ServiceFactory() {
        // Private constructor to prevent instantiation
    }

    public static synchronized BookService getBookService() {
        if (bookService == null) {
            bookService = new BookServiceImpl();
        }
        return bookService;
    }

    public static synchronized MemberService getMemberService() {
        if (memberService == null) {
            memberService = new MemberServiceImpl();
        }
        return memberService;
    }

    public static synchronized StaffService getStaffService() {
        if (staffService == null) {
            staffService = new StaffServiceImpl();
        }
        return staffService;
    }

    public static synchronized BorrowingService getBorrowingService() {
        if (borrowingService == null) {
            borrowingService = new BorrowingServiceImpl();
        }
        return borrowingService;
    }

    public static synchronized FineService getFineService() {
        if (fineService == null) {
            fineService = new FineServiceImpl();
        }
        return fineService;
    }
}
