package com.github.learndifferent.mtm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Elasticsearch 索引的数据结构（这里归为了 DTO，实际上也是 DO）
 *
 * @author zhou
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class WebForSearchDTO implements Serializable {

    private String title;
    private String url;
    private String img;
    /**
     * 网页简介
     */
    private String desc;

    private static final long serialVersionUID = 1L;
}
