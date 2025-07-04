package repository;

import repository.custom.*;
import repository.custom.impl.*;

public class RepositoryFactory {
    private static BookRepository bookRepository;
    private static MemberRepository memberRepository;
    private static StaffRepository staffRepository;
    private static BorrowingRepository borrowingRepository;
    private static FineRepository fineRepository;

    private RepositoryFactory() {
        // Private constructor to prevent instantiation
    }

    public static synchronized BookRepository getBookRepository() {
        if (bookRepository == null) {
            bookRepository = new BookRepositoryImpl();
        }
        return bookRepository;
    }

    public static synchronized MemberRepository getMemberRepository() {
        if (memberRepository == null) {
            memberRepository = new MemberRepositoryImpl();
        }
        return memberRepository;
    }

    public static synchronized StaffRepository getStaffRepository() {
        if (staffRepository == null) {
            staffRepository = new StaffRepositoryImpl();
        }
        return staffRepository;
    }

    public static synchronized BorrowingRepository getBorrowingRepository() {
        if (borrowingRepository == null) {
            borrowingRepository = new BorrowingRepositoryImpl();
        }
        return borrowingRepository;
    }

    public static synchronized FineRepository getFineRepository() {
        if (fineRepository == null) {
            fineRepository = new FineRepositoryImpl();
        }
        return fineRepository;
    }
}
