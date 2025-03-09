package com.bolane.backend.Services;

import com.bolane.backend.DTOs.ServiceDTO;
import com.bolane.backend.Entities.ServiceEntity;
import com.bolane.backend.Exceptions.ResourceNotFoundException;
import com.bolane.backend.Repositories.ServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServiceEntityService {
    private final ServiceRepository serviceRepository;

    public ServiceEntityService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

   public List<ServiceEntity> getAllServices() {
        return serviceRepository.findAll();
   }

   public List<ServiceEntity> getActiveServices() {
        return serviceRepository.findByActiveTrue();
   }

   public ServiceEntity getServiceById(int id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " +id));
   }

   @Transactional
   public ServiceEntity createService(ServiceDTO serviceDTO) {
        ServiceEntity serviceEntity = new ServiceEntity();

        serviceEntity.setName(serviceDTO.getName());
        serviceEntity.setDescription(serviceDTO.getDescription());
        serviceEntity.setPrice(serviceDTO.getPrice());
        serviceEntity.setActive(serviceDTO.isActive());

        return serviceRepository.save(serviceEntity);
   }

   @Transactional
    public ServiceEntity updateService(int id, ServiceDTO serviceDTO) {
        ServiceEntity serviceEntity = getServiceById(id);

        serviceEntity.setName(serviceDTO.getName());
        serviceEntity.setDescription(serviceDTO.getDescription());
        serviceEntity.setPrice(serviceDTO.getPrice());
        serviceEntity.setActive(serviceDTO.isActive());

        return serviceRepository.save(serviceEntity);
   }

   @Transactional
    public void deleteService(int id) {
        ServiceEntity serviceEntity = getServiceById(id);
        serviceRepository.delete(serviceEntity);
   }
}
