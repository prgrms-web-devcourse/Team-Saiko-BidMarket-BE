package com.saiko.bidmarket.user.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class GroupPermission {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "group_id")
  private Group group;

  @ManyToOne(optional = false)
  @JoinColumn(name = "permission_id")
  private Permission permission;

  public Long getId() {
    return id;
  }

  public Group getGroup() {
    return group;
  }

  public Permission getPermission() {
    return permission;
  }

}
