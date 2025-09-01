package com.shimi.gogoscrum.doc.service;

import com.shimi.gogoscrum.doc.model.Doc;
import com.shimi.gogoscrum.doc.model.DocFilter;
import com.shimi.gsf.core.service.GeneralService;

public interface DocService extends GeneralService<Doc, DocFilter> {
    Doc updatePublicAccess(Long id, Boolean publicAccess);
}
