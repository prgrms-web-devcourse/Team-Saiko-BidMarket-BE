package com.saiko.bidmarket.user.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Entity
@Table(name = "`group`")
public class Group {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Column(length = 20)
  private String name;

  @OneToMany(mappedBy = "group")
  private List<GroupPermission> permissions = new ArrayList<>();

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public List<GrantedAuthority> getAuthorities() {
    return permissions.stream()
                      .map(gp -> new SimpleGrantedAuthority(gp.getPermission().getName()))
                      .collect(Collectors.toList());
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
        .append("id", id)
        .append("name", name)
        .append("permissions", permissions)
        .toString();
  }

}
