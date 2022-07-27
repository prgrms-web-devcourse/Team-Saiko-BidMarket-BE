package com.saiko.bidmarket.user.service;

import java.util.Optional;

import com.saiko.bidmarket.user.entity.Group;

public interface GroupService {

  Group findByName(String name);

}
