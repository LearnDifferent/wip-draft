package com.github.learndifferent.mtm.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Set;

/**
 * Find页面初始化。
 * <p>进入 Find 页面的时候，需要展示的数据</p>
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class FindPageInitVO implements Serializable {

    /**
     * 热搜数据
     */
    private Set<String> trendingList;
    /**
     * 是否存在可供搜索的数据
     */
    private Boolean dataStatus;

    private static final long serialVersionUID = 1L;
}