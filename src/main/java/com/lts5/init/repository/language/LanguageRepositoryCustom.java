package com.lts5.init.repository.language;

import com.lts5.init.entity.Language;

import java.util.Optional;

public interface LanguageRepositoryCustom {
    Optional<Language> findByIsoCode(String isoCode);
} 