package com.lts5.init.service;

import com.lts5.init.dto.ProgressVendorDto;
import com.lts5.init.entity.ProgressVendor;
import com.lts5.init.payload.request.progressvendor.ProgressVendorCreateRequest;
import com.lts5.init.payload.request.progressvendor.ProgressVendorSearchRequest;
import com.lts5.init.payload.request.progressvendor.ProgressVendorUpdateAllRequest;
import com.lts5.init.payload.request.progressvendor.ProgressVendorUpdateRequest;
import com.lts5.init.repository.progressvendor.ProgressVendorRepository;
import com.primes.library.common.codes.ErrorCode;
import com.primes.library.common.exceptions.AlreadyExistException;
import com.primes.library.common.exceptions.EntityNotFoundException;
import com.primes.library.common.mapper.GlobalMapper;
import com.primes.library.service.BaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgressVendorService extends BaseService {

    private final ProgressVendorRepository progressVendorRepository;
    private final GlobalMapper globalMapper;

    @Transactional
    public ProgressVendorDto create(ProgressVendorCreateRequest request) {
        // 중복 체크
        if (progressVendorRepository.findByProgressIdAndVendorId(request.getProgressId(), request.getVendorId()).isPresent()) {
            throw new AlreadyExistException(ErrorCode.BAD_REQUEST_ERROR, "이미 존재하는 공정-업체 관계입니다.");
        }

        ProgressVendor progressVendor = ProgressVendor.builder()
                .progressId(request.getProgressId())
                .vendorId(request.getVendorId())
                .unitCost(request.getUnitCost())
                .quantity(request.getQuantity())
                .unit(request.getUnit())
                .isDefaultVendor(request.getIsDefaultVendor() != null ? request.getIsDefaultVendor() : false)
                .createBy(request.getCreateBy())
                .build();

        ProgressVendor savedProgressVendor = progressVendorRepository.save(progressVendor);
        return globalMapper.map(savedProgressVendor, ProgressVendorDto.class);
    }

    @Transactional
    public List<ProgressVendorDto> createProgressVendors(List<ProgressVendorCreateRequest> requests) {
        List<ProgressVendor> entities = requests.stream()
                .<ProgressVendor>map(request -> {
                    // 중복 체크
                    if (progressVendorRepository.findByProgressIdAndVendorId(request.getProgressId(), request.getVendorId()).isPresent()) {
                        throw new AlreadyExistException(ErrorCode.BAD_REQUEST_ERROR, 
                            String.format("이미 존재하는 공정-업체 관계입니다. (progressId: %d, vendorId: %d)", 
                                request.getProgressId(), request.getVendorId()));
                    }

                    return ProgressVendor.builder()
                            .progressId(request.getProgressId())
                            .vendorId(request.getVendorId())
                            .unitCost(request.getUnitCost())
                            .quantity(request.getQuantity())
                            .unit(request.getUnit())
                            .isDefaultVendor(request.getIsDefaultVendor() != null ? request.getIsDefaultVendor() : false)
                            .createBy(request.getCreateBy())
                            .build();
                })
                .toList();

        List<ProgressVendor> savedEntities = progressVendorRepository.saveAll(entities);
        return savedEntities.stream()
                .map(entity -> globalMapper.map(entity, ProgressVendorDto.class))
                .toList();
    }

    public ProgressVendorDto getProgressVendor(Long progressId, Long vendorId) {
        ProgressVendor progressVendor = progressVendorRepository.findByProgressIdAndVendorId(progressId, vendorId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR, 
                    String.format("progressId: %d, vendorId: %d인 ProgressVendor 데이터가 없습니다.", progressId, vendorId)));

        return globalMapper.map(progressVendor, ProgressVendorDto.class);
    }

    public Page<ProgressVendorDto> searchProgressVendors(ProgressVendorSearchRequest request, Pageable pageable) {
        Page<ProgressVendor> progressVendors = progressVendorRepository.searchProgressVendors(request, pageable);
        return progressVendors.map(entity -> globalMapper.map(entity, ProgressVendorDto.class));
    }

    public List<ProgressVendorDto> getVendorsByItemProgressId(Long progressId) {
        List<ProgressVendor> progressVendors = progressVendorRepository.findByProgressIdWithVendor(progressId);
        return progressVendors.stream()
                .map(entity -> globalMapper.map(entity, ProgressVendorDto.class))
                .toList();
    }

    public List<ProgressVendorDto> getDefaultVendorsByProgressId(Long progressId) {
        List<ProgressVendor> progressVendors = progressVendorRepository.findByProgressIdAndIsDefaultVendorWithVendor(progressId, true);
        return progressVendors.stream()
                .map(entity -> globalMapper.map(entity, ProgressVendorDto.class))
                .toList();
    }

    @Transactional
    public ProgressVendorDto update(Long progressId, Long vendorId, ProgressVendorUpdateRequest request) {
        ProgressVendor progressVendor = progressVendorRepository.findByProgressIdAndVendorId(progressId, vendorId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR, 
                    String.format("progressId: %d, vendorId: %d인 ProgressVendor 데이터가 없습니다.", progressId, vendorId)));

        // isDefaultVendor가 true로 변경되는 경우 기본 업체 관리
        Boolean newIsDefaultVendor = request.getIsDefaultVendor() != null ? request.getIsDefaultVendor() : progressVendor.getIsDefaultVendor();
        if (Boolean.TRUE.equals(newIsDefaultVendor) && !Boolean.TRUE.equals(progressVendor.getIsDefaultVendor())) {
            updateDefaultVendor(progressId, vendorId);
        }

        ProgressVendor updatedProgressVendor = ProgressVendor.builder()
                .progressId(progressVendor.getProgressId())
                .vendorId(progressVendor.getVendorId())
                .unitCost(request.getUnitCost() != null ? request.getUnitCost() : progressVendor.getUnitCost())
                .quantity(request.getQuantity() != null ? request.getQuantity() : progressVendor.getQuantity())
                .unit(request.getUnit() != null ? request.getUnit() : progressVendor.getUnit())
                .isDefaultVendor(newIsDefaultVendor)
                .createBy(progressVendor.getCreateBy())
                .createAt(progressVendor.getCreateAt())
                .updateBy(request.getUpdateBy())
                .updateAt(LocalDateTime.now())
                .build();

        ProgressVendor savedProgressVendor = progressVendorRepository.save(updatedProgressVendor);
        return globalMapper.map(savedProgressVendor, ProgressVendorDto.class);
    }

    @Transactional
    public List<ProgressVendorDto> updateAll(List<ProgressVendorUpdateAllRequest> requests) {
        return requests.stream()
                .map(request -> updateSingle(request))
                .toList();
    }

    @Transactional
    public void delete(Long progressId, List<Long> vendorIds) {
        List<ProgressVendor> existingEntities = new ArrayList<>();
        
        for (Long vendorId : vendorIds) {
            progressVendorRepository.findByProgressIdAndVendorId(progressId, vendorId)
                    .ifPresentOrElse(
                            existingEntities::add,
                            () -> {
                                throw new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR,
                                    String.format("progressId: %d, vendorId: %d인 ProgressVendor 데이터가 없습니다.", progressId, vendorId));
                            }
                    );
        }

        progressVendorRepository.deleteAll(existingEntities);
    }

    @Transactional
    public void deleteByProgressId(Long progressId) {
        List<ProgressVendor> progressVendors = progressVendorRepository.findByProgressId(progressId);
        if (!progressVendors.isEmpty()) {
            progressVendorRepository.deleteAll(progressVendors);
        }
    }

    public List<ProgressVendorDto> getAllProgressVendors() {
        List<ProgressVendor> progressVendors = progressVendorRepository.findAll();
        return progressVendors.stream()
                .map(entity -> globalMapper.map(entity, ProgressVendorDto.class))
                .toList();
    }

    // ==================== 유틸리티 메서드 ====================

    /**
     * 특정 progressId에서 새로운 기본 업체를 설정하고, 기존 기본 업체들을 해제합니다.
     * @param progressId 공정 ID
     * @param newDefaultVendorId 새로운 기본 업체 ID
     */
    @Transactional
    public void updateDefaultVendor(Long progressId, Long newDefaultVendorId) {
        // 같은 progressId를 가진 모든 ProgressVendor 조회
        List<ProgressVendor> progressVendors = progressVendorRepository.findByProgressId(progressId);
        
        for (ProgressVendor pv : progressVendors) {
            if (pv.getVendorId().equals(newDefaultVendorId)) {
                // 새로운 기본 업체는 true로 설정
                if (!Boolean.TRUE.equals(pv.getIsDefaultVendor())) {
                    ProgressVendor updatedPv = ProgressVendor.builder()
                            .progressId(pv.getProgressId())
                            .vendorId(pv.getVendorId())
                            .unitCost(pv.getUnitCost())
                            .quantity(pv.getQuantity())
                            .unit(pv.getUnit())
                            .isDefaultVendor(true)
                            .createBy(pv.getCreateBy())
                            .createAt(pv.getCreateAt())
                            .updateBy(pv.getUpdateBy())
                            .updateAt(java.time.LocalDateTime.now())
                            .build();
                    progressVendorRepository.save(updatedPv);
                }
            } else {
                // 다른 업체들은 false로 설정
                if (Boolean.TRUE.equals(pv.getIsDefaultVendor())) {
                    ProgressVendor updatedPv = ProgressVendor.builder()
                            .progressId(pv.getProgressId())
                            .vendorId(pv.getVendorId())
                            .unitCost(pv.getUnitCost())
                            .quantity(pv.getQuantity())
                            .unit(pv.getUnit())
                            .isDefaultVendor(false)
                            .createBy(pv.getCreateBy())
                            .createAt(pv.getCreateAt())
                            .updateBy(pv.getUpdateBy())
                            .updateAt(java.time.LocalDateTime.now())
                            .build();
                    progressVendorRepository.save(updatedPv);
                }
            }
        }
    }

    private ProgressVendorDto updateSingle(ProgressVendorUpdateAllRequest request) {
        ProgressVendor progressVendor = progressVendorRepository.findByProgressIdAndVendorId(request.getProgressId(), request.getVendorId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR, 
                    String.format("progressId: %d, vendorId: %d인 ProgressVendor 데이터가 없습니다.", request.getProgressId(), request.getVendorId())));

        // isDefaultVendor가 true로 변경되는 경우 기본 업체 관리
        Boolean newIsDefaultVendor = request.getIsDefaultVendor() != null ? request.getIsDefaultVendor() : false;
        if (Boolean.TRUE.equals(newIsDefaultVendor) && !Boolean.TRUE.equals(progressVendor.getIsDefaultVendor())) {
            updateDefaultVendor(request.getProgressId(), request.getVendorId());
        }

        ProgressVendor updatedProgressVendor = ProgressVendor.builder()
                .progressId(progressVendor.getProgressId())
                .vendorId(progressVendor.getVendorId())
                .unitCost(request.getUnitCost())
                .quantity(request.getQuantity())
                .unit(request.getUnit())
                .isDefaultVendor(newIsDefaultVendor)
                .createBy(progressVendor.getCreateBy())
                .createAt(progressVendor.getCreateAt())
                .updateBy(request.getUpdateBy())
                .updateAt(LocalDateTime.now())
                .build();

        ProgressVendor savedProgressVendor = progressVendorRepository.save(updatedProgressVendor);
        return globalMapper.map(savedProgressVendor, ProgressVendorDto.class);
    }
} 