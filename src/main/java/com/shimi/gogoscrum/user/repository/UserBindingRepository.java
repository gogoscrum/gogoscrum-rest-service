package com.shimi.gogoscrum.user.repository;

import com.shimi.gogoscrum.user.model.UserBinding;
import com.shimi.gsf.core.repository.GeneralRepository;

public interface UserBindingRepository extends GeneralRepository<UserBinding> {
    UserBinding getByProviderAndExtUserId(String provider, String extUserId);

    UserBinding getByProviderAndUserId(String provider, Long userId);

    UserBinding getTopByUnionId(String unionId);
}
