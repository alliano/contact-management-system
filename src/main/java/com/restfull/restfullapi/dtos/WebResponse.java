package com.restfull.restfullapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @Builder
@Setter @Getter @AllArgsConstructor
public class WebResponse<T> {
    
    private T data;

    private String errors;

    private PagingResponse paging;
}
