package com.pt.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO<E> {

    private Integer count;

    private Integer page;

    private Integer totalPage;

    private Long totalCount;

   private List<E> data;

}
