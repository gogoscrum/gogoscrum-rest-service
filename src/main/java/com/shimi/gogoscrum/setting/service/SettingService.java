package com.shimi.gogoscrum.setting.service;

import com.shimi.gogoscrum.setting.model.Setting;
import com.shimi.gogoscrum.setting.model.SettingFilter;
import com.shimi.gsf.core.service.GeneralService;

public interface SettingService extends GeneralService<Setting, SettingFilter> {
    Setting getCurrent();

    Setting updateCurrent(Setting setting);

    boolean isSignupAllowed();

    void checkSignupAllowed();
}

