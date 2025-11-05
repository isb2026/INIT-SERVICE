package com.lts5.init.repository.rootword;

import com.lts5.init.entity.RootWord;
import com.primes.library.repository.SimpleBaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RootWordRepository extends SimpleBaseRepository<RootWord, Long>, RootWordRepositoryCustom {} 