package com.lts5.init.service;

import com.lts5.init.dto.FileLinkDto;
import com.lts5.init.entity.FileLink;
import com.lts5.init.entity.enums.OwnerType;

import com.lts5.init.payload.request.filelink.FileLinkCreateRequest;
import com.lts5.init.payload.request.filelink.FileLinkSearchRequest;
import com.lts5.init.payload.request.filelink.FileLinkUpdateInfo;
import com.lts5.init.repository.filelink.FileLinkRepository;
import com.primes.library.common.codes.ErrorCode;
import com.primes.library.common.exceptions.EntityNotFoundException;
import com.primes.library.common.mapper.GlobalMapper;
import com.primes.library.service.BaseService;
import com.primes.library.util.DynamicFieldQueryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileLinkService extends BaseService {
    private final FileLinkRepository fileLinkRepository;
    private final DynamicFieldQueryUtil dynamicFieldQueryUtil;
    private final GlobalMapper globalMapper;

    public List<?> getFieldValues(String fieldName, FileLinkSearchRequest searchRequest) {
        if (searchRequest == null) {
            return dynamicFieldQueryUtil.getFieldValues(FileLink.class, fieldName);
        }
        return dynamicFieldQueryUtil.getFieldValuesWithFilter(FileLink.class, fieldName, searchRequest);
    }

    @Transactional
    public List<FileLinkDto> createList(List<FileLinkCreateRequest> requests) {
        List<FileLinkDto> results = new ArrayList<>();

        for (FileLinkCreateRequest request : requests) {
            FileLinkDto fileLinkDto = globalMapper.map(request, FileLinkDto.class);
            FileLink entity = globalMapper.map(fileLinkDto, FileLink.class);
            FileLink savedEntity = fileLinkRepository.save(entity);

            results.add(globalMapper.map(savedEntity, FileLinkDto.class));
        }

        return results;
    }

    @Transactional
    public FileLinkDto update(Long id, FileLinkDto dto) {
        dto.setId(id);
        return updateSingle(dto);
    }

    @Transactional
    public List<FileLinkDto> updateAll(List<FileLinkDto> dtos) {
        return dtos.stream()
                .map(this::updateSingle)
                .toList();
    }

    @Transactional
    public void delete(List<Long> ids) {
        List<FileLink> fileLinks = fileLinkRepository.findAllById(ids);

        if(fileLinks.size() != ids.size()) {
            List<Long> existingIds = fileLinks.stream().map(FileLink::getId).toList();
            List<Long> notFoundIds = new ArrayList<>(ids);
            notFoundIds.removeAll(existingIds);

            throw new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR,
                    String.format("Id가 %s인 FileLink 데이터가 없습니다.", notFoundIds));
        }

        // 삭제할 FileLink들을 그룹별로 분류
        Map<String, List<FileLink>> groupedByDelete = fileLinks.stream()
            .collect(Collectors.groupingBy(fl -> 
                fl.getOwnerTable() + "|" + fl.getOwnerType() + "|" + fl.getOwnerId()));

        // 각 그룹별로 sortOrder 재정렬
        groupedByDelete.forEach((groupKey, deleteFileLinks) -> {
            if (!deleteFileLinks.isEmpty()) {
                FileLink firstFileLink = deleteFileLinks.get(0);
                reorderSortOrdersAfterDelete(
                    firstFileLink.getOwnerTable(),
                    firstFileLink.getOwnerType(),
                    firstFileLink.getOwnerId(),
                    deleteFileLinks
                );
            }
        });

        // 삭제 처리
        for (FileLink entity : fileLinks) {
            entity.delete();
        }
        
        fileLinkRepository.saveAll(fileLinks);
    }

    public Page<FileLinkDto> search(FileLinkSearchRequest searchRequest, Pageable pageable) {
        return fileLinkRepository.search(searchRequest, pageable).map(entity -> globalMapper.map(entity, FileLinkDto.class));
    }

    // ==================== 유틸리티 메서드 ====================
    private FileLinkDto updateSingle(FileLinkDto dto) {
        FileLink entity = fileLinkRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR, 
                    String.format("Id가 %d인 FileLink 데이터가 없습니다.", dto.getId())));
        
        // sortOrder 변경 처리
        if (dto.getSortOrder() != null && !dto.getSortOrder().equals(entity.getSortOrder())) {
            reorderSortOrdersForUpdate(entity, dto.getSortOrder());
        }
        
        changeIsPrimary(dto, entity);
        updateEntityFromDto(entity, dto);
        return globalMapper.map(entity, FileLinkDto.class);
    }

    /**
     * isPrimary 변경을 처리하고 필요한 경우 sortOrder를 재정렬합니다.
     */
    private void changeIsPrimary(FileLinkDto dto, FileLink fileLink) {
        if (dto.getIsPrimary() == null) return;

        if (dto.getIsPrimary()) {
            changeSortOrdersForGroup(dto, fileLink);
        }
    }

    /**
     * 그룹의 sortOrder를 재정렬합니다.
     */
    private void changeSortOrdersForGroup(FileLinkDto dto, FileLink fileLink) {
        List<FileLink> sameGroupFileLinks = fileLinkRepository.findByOwnerTableAndOwnerTypeAndOwnerId(
                fileLink.getOwnerTable(), fileLink.getOwnerType(), fileLink.getOwnerId());

        // 기존 isPrimary가 true인 것들을 false로 변경
        sameGroupFileLinks.stream()
            .filter(fl -> !fl.getId().equals(dto.getId()) && 
                         fl.getIsPrimary() != null && fl.getIsPrimary())
            .forEach(fl -> {
                FileLinkDto primaryDto = new FileLinkDto();
                primaryDto.setIsPrimary(false);
                updateEntityFromDto(fl, primaryDto);
            });

        // 현재 FileLink를 isPrimary=true로 설정하고 sortOrder=1로 설정
        FileLinkDto currentDto = new FileLinkDto();
        currentDto.setIsPrimary(true);
        currentDto.setSortOrder((short) 1);
        updateEntityFromDto(fileLink, currentDto);

        // 나머지 FileLink들의 sortOrder를 2, 3, 4... 순으로 재정렬
        List<FileLink> otherFileLinks = sameGroupFileLinks.stream()
            .filter(fl -> !fl.getId().equals(dto.getId()))
            .collect(Collectors.toList());

        for (int i = 0; i < otherFileLinks.size(); i++) {
            final int index = i;
            FileLinkDto sortDto = new FileLinkDto();
            sortDto.setSortOrder((short) (2 + index));
            updateEntityFromDto(otherFileLinks.get(index), sortDto);
        }

        // 모든 변경사항 저장
        List<FileLink> allFileLinks = new ArrayList<>(sameGroupFileLinks);
        fileLinkRepository.saveAll(allFileLinks);
        
        // DTO에 결과 반영
        dto.setSortOrder(fileLink.getSortOrder());
    }

    // ==================== Item 기반 FileLink 관리 메서드 ====================

    /**
     * Item 생성 시 FileLink를 처리합니다.
     */
    @Transactional
    public void createList(Long itemId, List<FileLinkUpdateInfo> fileLinkInfos) {
        if (fileLinkInfos == null || fileLinkInfos.isEmpty()) return;
        
        List<FileLinkDto> fileLinkDtos = fileLinkInfos.stream()
            .map(info -> {
                FileLinkDto dto = globalMapper.map(info, FileLinkDto.class);
                dto.setOwnerTable("items");
                dto.setOwnerId(itemId);
                return dto;
            })
            .collect(Collectors.toList());
        
        // 각 ownerType 그룹별로 sortOrder 설정
        fileLinkDtos.stream()
            .collect(Collectors.groupingBy(FileLinkDto::getOwnerType))
            .forEach((ownerType, dtos) -> autoSetSortOrders(dtos, "items", ownerType, itemId));
        
        List<FileLink> fileLinkEntities = fileLinkDtos.stream()
            .map(dto -> globalMapper.map(dto, FileLink.class))
            .collect(Collectors.toList());
        
        fileLinkRepository.saveAll(fileLinkEntities);
    }

    // ==================== sortOrder 자동 관리 메서드 ====================

    /**
     * 특정 그룹의 최대 sortOrder를 조회합니다.
     */
    private Short getMaxSortOrder(String ownerTable, OwnerType ownerType, Long ownerId) {
        List<FileLink> existingLinks = fileLinkRepository.findByOwnerTableAndOwnerTypeAndOwnerId(ownerTable, ownerType, ownerId);
        return (short) existingLinks.stream()
                .mapToInt(FileLink::getSortOrder)
                .max()
                .orElse(0);
    }

    /**
     * DTO 리스트에 대해 sortOrder를 자동으로 설정합니다.
     */
    private void autoSetSortOrders(List<FileLinkDto> fileLinkDtos, String ownerTable, OwnerType ownerType, Long ownerId) {
        Short maxSortOrder = getMaxSortOrder(ownerTable, ownerType, ownerId);

        // isPrimary=true인 것들을 sortOrder=1로 설정
        fileLinkDtos.stream()
            .filter(dto -> dto.getIsPrimary() != null && dto.getIsPrimary())
            .forEach(dto -> dto.setSortOrder((short) 1));

        // 나머지는 기존 최대값 이후부터 순차적으로 설정
        final int startCounter = maxSortOrder;
        final int[] counter = {startCounter};
        fileLinkDtos.stream()
            .filter(dto -> dto.getIsPrimary() == null || !dto.getIsPrimary())
            .forEach(dto -> dto.setSortOrder((short) ++counter[0]));
    }

    /**
     * 삭제 후 같은 그룹의 sortOrder를 재정렬합니다.
     */
    private void reorderSortOrdersAfterDelete(String ownerTable, OwnerType ownerType, Long ownerId, List<FileLink> deleteFileLinks) {
        // 삭제할 FileLink들의 sortOrder 값들
        List<Short> deleteSortOrders = deleteFileLinks.stream()
            .map(FileLink::getSortOrder)
            .filter(sortOrder -> sortOrder != null)
            .sorted()
            .toList();

        if (deleteSortOrders.isEmpty()) return;

        // 같은 그룹의 모든 FileLink 조회
        List<FileLink> remainingFileLinks = fileLinkRepository.findByOwnerTableAndOwnerTypeAndOwnerId(ownerTable, ownerType, ownerId);
        
        List<Long> deleteIds = deleteFileLinks.stream().map(FileLink::getId).toList();
        remainingFileLinks = remainingFileLinks.stream()
            .filter(fl -> !deleteIds.contains(fl.getId()))
            .collect(Collectors.toList());

        // 전체 재정렬: sortOrder를 1부터 순차적으로 재할당
        List<FileLink> sortedFileLinks = remainingFileLinks.stream()
            .sorted((fl1, fl2) -> {
                Short sort1 = fl1.getSortOrder() != null ? fl1.getSortOrder() : 0;
                Short sort2 = fl2.getSortOrder() != null ? fl2.getSortOrder() : 0;
                return sort1.compareTo(sort2);
            })
            .collect(Collectors.toList());

        for (int i = 0; i < sortedFileLinks.size(); i++) {
            FileLinkDto sortDto = new FileLinkDto();
            sortDto.setSortOrder((short) (i + 1));
            updateEntityFromDto(sortedFileLinks.get(i), sortDto);
        }

        // 변경사항 저장
        if (!sortedFileLinks.isEmpty()) {
            fileLinkRepository.saveAll(sortedFileLinks);
        }
    }

    /**
     * update 시 sortOrder 변경에 따른 재정렬
     */
    private void reorderSortOrdersForUpdate(FileLink fileLink, Short newSortOrder) {
        Short oldSortOrder = fileLink.getSortOrder();
        if (oldSortOrder == null || newSortOrder == null) return;
        
        // 같은 그룹의 모든 FileLink 조회
        List<FileLink> sameGroupFileLinks = fileLinkRepository.findByOwnerTableAndOwnerTypeAndOwnerId(
                fileLink.getOwnerTable(), fileLink.getOwnerType(), fileLink.getOwnerId());
        
        // 현재 FileLink를 제외한 나머지 FileLink들
        List<FileLink> otherFileLinks = sameGroupFileLinks.stream()
            .filter(fl -> !fl.getId().equals(fileLink.getId()))
            .collect(Collectors.toList());
        
        List<FileLink> affectedFileLinks = new ArrayList<>();
        
        if (oldSortOrder < newSortOrder) {
            otherFileLinks.stream()
                .filter(fl -> fl.getSortOrder() != null && 
                             fl.getSortOrder() > oldSortOrder && 
                             fl.getSortOrder() <= newSortOrder)
                .forEach(fl -> {
                    FileLinkDto sortDto = new FileLinkDto();
                    sortDto.setSortOrder((short) (fl.getSortOrder() - 1));
                    updateEntityFromDto(fl, sortDto);
                    affectedFileLinks.add(fl);
                });
        } else if (oldSortOrder > newSortOrder) {
            otherFileLinks.stream()
                .filter(fl -> fl.getSortOrder() != null && 
                             fl.getSortOrder() >= newSortOrder && 
                             fl.getSortOrder() < oldSortOrder)
                .forEach(fl -> {
                    FileLinkDto sortDto = new FileLinkDto();
                    sortDto.setSortOrder((short) (fl.getSortOrder() + 1));
                    updateEntityFromDto(fl, sortDto);
                    affectedFileLinks.add(fl);
                });
        }
        
        if (!affectedFileLinks.isEmpty()) {
            fileLinkRepository.saveAll(affectedFileLinks);
        }
    }
}