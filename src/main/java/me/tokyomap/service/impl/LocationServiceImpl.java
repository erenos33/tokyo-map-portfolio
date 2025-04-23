package me.tokyomap.service.impl;

import lombok.RequiredArgsConstructor;
import me.tokyomap.domain.location.repository.LocationRepository;
import me.tokyomap.dto.location.LocationResponseDto;
import me.tokyomap.mapper.LocationMapper;
import me.tokyomap.service.LocationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;


    @Override
    public List<LocationResponseDto> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(LocationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LocationResponseDto> getLocationsByAdminLevel2(String adminLevel2) {
        return locationRepository.findByAdminLevel2ContainingIgnoreCase(adminLevel2).stream()
                .map(LocationMapper::toDto)
                .collect(Collectors.toList());
    }
}
