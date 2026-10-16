package com.shimi.gogoscrum.setting.repository;

import com.shimi.gogoscrum.setting.model.Setting;
import com.shimi.gsf.core.repository.GeneralRepository;

import java.util.Optional;

public interface SettingRepository extends GeneralRepository<Setting> {
    Optional<Setting> findFirstByOrderByIdAsc();
}

