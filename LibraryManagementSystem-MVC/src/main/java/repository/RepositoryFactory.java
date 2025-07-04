package repository;

import repository.custom.impl.*;

public class RepositoryFactory {

    private static RepositoryFactory repositoryFactory;

    private RepositoryFactory() {
        // -----Private constructor to prevent instantiation
    }

    public static RepositoryFactory getInstance() {
        if (repositoryFactory == null) {
            repositoryFactory = new RepositoryFactory();
        }
        return repositoryFactory;
    }

    public enum RepositoryType {
        BOOK, AUTHOR, CATEGORY, PUBLISHER, BORROW, MEMBER, FINE
    }

    public <T extends SuperRepository> T getRepository(RepositoryType type) {
        switch (type) {
            case BOOK:
                return (T) new BookRepositoryImpl();
            case CATEGORY:
                return (T) new CategoryRepositoryImpl();
            case BORROW:
                return (T) new BorrowRepositoryImpl();
            case MEMBER:
                return (T) new MemberRepositoryImpl();
            case FINE:
                return (T) new FineRepositoryImpl();
            default:
                throw new RuntimeException("Invalid repository type: " + type);
        }
    }
}
