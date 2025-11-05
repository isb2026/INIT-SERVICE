package com.lts5.init.service;

import com.primes.library.common.codes.ErrorCode;
import com.primes.library.common.exceptions.EntityNotFoundException;
import com.primes.library.common.mapper.GlobalMapper;
import com.lts5.init.dto.ProgressRouteDto;
import com.lts5.init.entity.ProgressRoute;
import com.lts5.init.payload.request.progressroute.ProgressRouteSearchRequest;
import com.lts5.init.repository.progressroute.ProgressRouteRepository;
import com.primes.library.util.DynamicFieldQueryUtil;
import com.primes.library.service.BaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgressRouteService extends BaseService {
    private final ProgressRouteRepository progressRouteRepository;
    private final DynamicFieldQueryUtil dynamicFieldQueryUtil;
    private final GlobalMapper globalMapper;

    @Transactional
    public ProgressRouteDto create(ProgressRouteDto dto) {
        ProgressRoute entity = globalMapper.map(dto, ProgressRoute.class);
        ProgressRoute savedEntity = progressRouteRepository.save(entity);
        return globalMapper.map(savedEntity, ProgressRouteDto.class);
    }
    
    @Transactional
    public List<ProgressRouteDto> createList(List<ProgressRouteDto> dtos) {
        List<ProgressRoute> entities = dtos.stream()
                .map(dto -> globalMapper.map(dto, ProgressRoute.class))
                .toList();
        
        List<ProgressRoute> savedEntities = progressRouteRepository.saveAll(entities);
        return savedEntities.stream()
                .map(entity -> globalMapper.map(entity, ProgressRouteDto.class))
                .toList();
    }
    
    @Transactional
    public ProgressRouteDto update(Long id, ProgressRouteDto dto) {
        dto.setId(id);  // DTO에 ID 설정
        return updateSingle(dto);
    }
    
    @Transactional
    public List<ProgressRouteDto> updateAll(List<ProgressRouteDto> dtos) {
        return dtos.stream()
                .map(this::updateSingle)
                .toList();
    }

    @Transactional
    public void delete(List<Long> ids) {
        List<ProgressRoute> existingEntities = progressRouteRepository.findAllById(ids);
        
        if (existingEntities.size() != ids.size()) {
            List<Long> existingIds = existingEntities.stream().map(ProgressRoute::getId).toList();
            List<Long> notFoundIds = new ArrayList<>(ids);
            notFoundIds.removeAll(existingIds);

            throw new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR,
                String.format("Id가 %s인 ProgressRoute 데이터가 없습니다.", notFoundIds));
        }

        for (ProgressRoute entity : existingEntities) {
            entity.delete();
        }
        progressRouteRepository.saveAll(existingEntities);
    }

    public List<?> getFieldValues(String fieldName, ProgressRouteSearchRequest searchRequest) {
        if (searchRequest == null) {
            return dynamicFieldQueryUtil.getFieldValues(ProgressRoute.class, fieldName);
        }else{
            return dynamicFieldQueryUtil.getFieldValuesWithFilter(ProgressRoute.class,  fieldName, searchRequest);
        }
    }

    public Page<ProgressRouteDto> search(ProgressRouteSearchRequest searchRequest, Pageable pageable) {
        return progressRouteRepository.search(searchRequest, pageable)
                .map(entity -> globalMapper.map(entity, ProgressRouteDto.class));
    }

    // ==================== 유틸리티 메서드 ====================

    private ProgressRouteDto updateSingle(ProgressRouteDto dto) {
        ProgressRoute entity = progressRouteRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR, String.format("Id 가 %d인 ProgressRoute 데이터가 없습니다.", dto.getId())));
        updateEntityFromDto(entity, dto);
        return globalMapper.map(entity, ProgressRouteDto.class);
    }
}