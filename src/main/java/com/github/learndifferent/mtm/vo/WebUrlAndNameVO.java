package com.github.learndifferent.mtm.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * WebUrlAndName 的 VO
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebUrlAndNameVO implements Serializable {

    String userName;
    String url;

    private static final long serialVersionUID = 1L;
}