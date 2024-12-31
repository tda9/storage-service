package org.example.model.dto.response;

import jakarta.validation.constraints.Min;
import lombok.*;
import org.example.model.dto.request.PagingRequest;

import java.util.List;

@Getter
@Setter
//public class PageResponse<T> extends Response<List<T>> {
//
//    @Builder.Default
//    protected int currentPage = 1;
//    @Min(value = 1)
//    @Builder.Default
//    protected int totalPage = 1;
//    @Builder.Default
//    @Min(value = 0)
//    protected int pageSize = 0;
//    @Min(value = 0)
//    @Builder.Default
//    protected long totalSize = 0;
//    protected String sortBy;
//    protected String sort;
//    public PageResponse(int currentPage, int totalPage,
//                        int pageSize, long totalSize,
//                        String sortBy,
//                        String sort,
//                        List<T> data){
//        super.setData(data);
//        this.currentPage = currentPage;
//        this.totalPage = totalPage;
//        this.pageSize = pageSize;
//        this.totalSize = totalSize;
//        this.sortBy = sortBy;
//        this.sort = sort;
//    }
//    public PageResponse(PagingRequest request, List<T> data, long totalSize){
//        super.setData(data);
//        this.currentPage = request.getCurrentPage();
//        this.totalPage = calculateTotalPage(totalSize, request.getPageSize());
//        this.pageSize = request.getPageSize();
//        this.totalSize = totalSize;
//        this.sortBy = request.getSortBy();
//        this.sort = request.getSort();
//    }
//
//    public static <T> PageResponse<T> of(PagingRequest request,
//                                         List<T> data,
//                                         long totalSize){
//        PageResponse<T> pageResponse = new PageResponse<>(request, data,totalSize);
//        pageResponse.setSort(request.getSort());
//        pageResponse.setSortBy(request.getSortBy());
//        return pageResponse;
//    }
//
//    private int calculateTotalPage(long totalSize, int pageSize){
//        return (int) Math.ceil((double) totalSize / pageSize);
//    }
//}
public class PageResponse<T> extends Response<List<T>> {

    protected int currentPage;
    @Min(value = 1)
    protected int totalPage;
    @Min(value = 0)
    protected int pageSize;
    @Min(value = 0)
    protected long totalSize;
    protected String sortBy;
    protected String sort;
//
//    // Constructor with all parameters
//    public PageResponse(int currentPage, int totalPage,
//                        int pageSize, long totalSize,
//                        String sortBy, String sort,
//                        List<T> data) {
//        super();
//        this.currentPage = currentPage;
//        this.totalPage = totalPage;
//        this.pageSize = pageSize;
//        this.totalSize = totalSize;
//        this.sortBy = sortBy;
//        this.sort = sort;
//    }
//
//    // Constructor using PagingRequest
//    public PageResponse(PagingRequest request, List<T> data, long totalSize) {
//        super();
//        this.currentPage = request.getCurrentPage();
//        this.totalPage = calculateTotalPage(totalSize, request.getPageSize());
//        this.pageSize = request.getPageSize();
//        this.totalSize = totalSize;
//        this.sortBy = request.getSortBy();
//        this.sort = request.getSort();
//    }

    public PageResponse(PagingRequest request, List<T> data, long totalSize) {
        super(data); // Initialize Response with data
        this.currentPage = request.getCurrentPage();
        this.pageSize = request.getPageSize();
        this.sortBy = request.getSortBy();
        this.sort = request.getSort();
        this.totalSize = totalSize;
        this.totalPage = calculateTotalPages(totalSize, request.getPageSize());
    }
    public static <T> PageResponse<T> of(PagingRequest request, List<T> data, long totalSize) {
        int totalPage = calculateTotalPages(totalSize, request.getPageSize());
        return new PageResponse<>(request, data, totalSize);
    }

    private static int calculateTotalPages(long totalSize, int pageSize) {
        return (int) Math.ceil((double) totalSize / pageSize);
    }
}
