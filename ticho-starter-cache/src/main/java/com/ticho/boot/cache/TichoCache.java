package com.ticho.boot.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-02-06 16:45
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TichoCache {

    /** key的名字 */
    private String name;

    /** 过期时间，单位:秒(s) */
    private int ttl;

    /** 最大存储数量 */
    private int maxSize;

}
