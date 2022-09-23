package com.ticho.boot.security.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 登录用户信息
 *
 * @author zhajianjun
 * @date 2020-07-02 20:08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "登录用户信息")
@ToString
public class SecurityUser implements UserDetails, Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "账户", position = 10)
    private String username;

    @ApiModelProperty(value = "密码", position = 20)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @ApiModelProperty(value = "用户状态。1-正常,2-已失效,3-已被锁定,4-已过期", position = 30)
    private Integer status = 2;

    @ApiModelProperty(value = "角色信息", position = 40)
    private List<SimpleGrantedAuthority> authorities;

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return status != 4;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != 3;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return status != 2;
    }

    @Override
    public boolean isEnabled() {
        return status == 1;
    }
}
