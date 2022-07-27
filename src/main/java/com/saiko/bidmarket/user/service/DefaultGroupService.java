package com.saiko.bidmarket.user.service;

import static org.apache.commons.lang3.StringUtils.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.user.entity.Group;
import com.saiko.bidmarket.user.repository.GroupRepository;

@Service
@Transactional
public class DefaultGroupService implements GroupService {

  private final GroupRepository groupRepository;

  public DefaultGroupService(GroupRepository groupRepository) {
    this.groupRepository = groupRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public Group findByName(String name) {
    Assert.isTrue(isNotBlank(name), "Name must be provided");

    return groupRepository.findByName(name)
                          .orElseThrow(() -> new NotFoundException("Group does not exist"));

  }

}
