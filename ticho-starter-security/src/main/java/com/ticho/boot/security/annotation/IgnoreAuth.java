/*
 * Copyright (c) 2020 pig4cloud Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ticho.boot.security.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 忽略token校验
 *
 * <p>
*     和PreAuthorize有实际意义上的冲突，不要联合使用，token都不校验了，权限就无法验证了
 *   @see org.springframework.security.access.prepost.PreAuthorize
 * </p>
 *
 * @author zhajianjun
 * @date 2022-09-26 14:11:34
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreAuth {

    /** 是否内部服务调用 true-是，false-否 */
    @AliasFor("inner")
    boolean value() default false;

    @AliasFor("value")
    boolean inner() default false;

}
