package com.lts5.init.repository.filelink;

import com.lts5.init.entity.FileLink;
import com.lts5.init.payload.request.filelink.FileLinkSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FileLinkRepositoryCustom {
    Page<FileLink> search(FileLinkSearchRequest searchRequest, Pageable pageable);
}
