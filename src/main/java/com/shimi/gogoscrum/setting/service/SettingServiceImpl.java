package com.shimi.gogoscrum.setting.service;

import com.shimi.gogoscrum.common.exception.ErrorCode;
import com.shimi.gogoscrum.common.service.BaseServiceImpl;
import com.shimi.gogoscrum.setting.model.Setting;
import com.shimi.gogoscrum.setting.model.SettingFilter;
import com.shimi.gogoscrum.setting.repository.SettingRepository;
import com.shimi.gsf.core.exception.BaseServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class SettingServiceImpl extends BaseServiceImpl<Setting, SettingFilter> implements SettingService {
    @Autowired
    private SettingRepository repository;

    @Override
    protected SettingRepository getRepository() {
        return repository;
    }

    @Override
    public Setting getCurrent() {
        return repository.findFirstByOrderByIdAsc().orElseGet(this::createDefaultSetting);
    }

    @Override
    public Setting updateCurrent(Setting setting) {
        Setting existingSetting = getCurrent();
        existingSetting.setAllowSignup(setting.getAllowSignup());
        repository.save(existingSetting);
        log.info("Updated system setting: {}", existingSetting);
        return existingSetting;
    }

    @Override
    public boolean isSignupAllowed() {
        return Boolean.TRUE.equals(getCurrent().getAllowSignup());
    }

    @Override
    public void checkSignupAllowed() {
        if (!this.isSignupAllowed()) {
            throw new BaseServiceException(ErrorCode.SIGNUP_DISABLED, "Self-service signup is disabled", HttpStatus.PRECONDITION_FAILED);
        }
    }

    @Override
    protected void beforeCreate(Setting setting) {
        if (repository.count() > 0) {
            throw new BaseServiceException("settingAlreadyExists", "System setting already exists", HttpStatus.CONFLICT);
        }
    }

    @Override
    protected void beforeDelete(Setting setting) {
        throw new BaseServiceException(ErrorCode.CANNOT_BE_DELETED, "System setting cannot be deleted", HttpStatus.PRECONDITION_FAILED);
    }

    @Override
    protected Specification<Setting> toSpec(SettingFilter filter) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private synchronized Setting createDefaultSetting() {
        return this.create(new Setting());
    }
}

