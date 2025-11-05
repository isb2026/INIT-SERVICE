package com.lts5.init.repository.translatedword;

import com.lts5.init.entity.TranslatedWord;
import com.primes.library.repository.SimpleBaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TranslatedWordRepository extends SimpleBaseRepository<TranslatedWord, Long>, TranslatedWordRepositoryCustom {
    @Query("SELECT tw FROM TranslatedWord tw " +
           "JOIN FETCH tw.rootWord rw " +
           "WHERE tw.language.id = :languageId")
    List<TranslatedWord> findByLanguageId(@Param("languageId") Long languageId);
} 