package service;

import service.custom.impl.BookServiceImpl;
import service.custom.impl.BorrowServiceImpl;
import service.custom.impl.MemberServiceImpl;

public class ServiceFactory {

    private static ServiceFactory serviceFactory;

    private ServiceFactory() {}

    public static ServiceFactory getInstance() {
        if (serviceFactory == null) {
            serviceFactory = new ServiceFactory();
        }
        return serviceFactory;
    }

    public enum ServiceType {
        BOOK,
        BORROW,
        MEMBER
    }

    @SuppressWarnings("unchecked")
    public <T extends SuperService> T getService(ServiceType type) {
        switch (type) {
            case BOOK:
                return (T) new BookServiceImpl();
            case BORROW:
                return (T) new BorrowServiceImpl();
            case MEMBER:
                return (T) new MemberServiceImpl();
            default:
                throw new RuntimeException("Unknown service type: " + type);
        }
    }
}
