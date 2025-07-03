package com.shimi.gogoscrum.user.repository;

import com.shimi.gogoscrum.user.model.User;
import com.shimi.gsf.core.repository.GeneralRepository;

public interface UserRepository extends GeneralRepository<User> {
    User findByUsername(String username);
}
