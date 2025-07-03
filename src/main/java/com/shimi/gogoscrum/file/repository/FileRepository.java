package com.shimi.gogoscrum.file.repository;

import com.shimi.gogoscrum.file.model.File;
import com.shimi.gsf.core.repository.GeneralRepository;

public interface FileRepository extends GeneralRepository<File> {
    File findByFullPath(String path);
}
