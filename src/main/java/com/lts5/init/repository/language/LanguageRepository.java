package com.lts5.init.repository.language;

import com.lts5.init.entity.Language;
import com.primes.library.repository.SimpleBaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LanguageRepository extends SimpleBaseRepository<Language, Long>, LanguageRepositoryCustom {
    
    @Query("SELECT l FROM Language l WHERE l.isoCode = :isoCode")
    Optional<Language> findByIsoCode(@Param("isoCode") String isoCode);
} 