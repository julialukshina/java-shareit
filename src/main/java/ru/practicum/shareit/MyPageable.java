package ru.practicum.shareit;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class MyPageable implements Pageable {
    private final int offset;
    private final int limit;
    private final Sort sort;

    private static final int DEFAULT_PAGE_SIZE = 10;

    public MyPageable(int offset, int limit, Sort sort) {
        this.offset = offset;
        this.limit = limit;
        this.sort = sort;
    }

    public static Pageable of(Integer from, Integer size) {
//        if (from == null && size == null) {
//            from = 0;
//            size = DEFAULT_PAGE_SIZE;
//        }
//        validateOrThrowException(from, size);
        return new MyPageable(from, size, Sort.unsorted());
    }

    public static Pageable of(Integer from, Integer size, Sort sort) {
//        if (from == null && size == null) {
//            from = 0;
//            size = DEFAULT_PAGE_SIZE;
//        }
//        validateOrThrowException(from, size);
        return new MyPageable(from, size, sort);
    }

//    private static void validateOrThrowException(Integer from, Integer size) {
//        if (size < 1 || from < 0) {
//            throw new ValidationException("size must be positive and from must be more then 0");
//        }
//    }


    @Override
    public int getPageNumber() {
        return offset / limit;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new MyPageable(offset + limit, limit, sort);
    }

    @Override
    public Pageable previousOrFirst() {
        return new MyPageable(offset, limit, sort);
    }

    @Override
    public Pageable first() {
        return new MyPageable(offset, limit, sort);
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new MyPageable(offset + limit * pageNumber, limit, sort);
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }
}
