package org.computerspareparts.csms.global.service;

import org.computerspareparts.csms.global.entity.Part;
import org.computerspareparts.csms.global.repository.PartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PartService {

    private final PartRepository partRepository;

    public PartService(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    public Part getPartById(Integer id) {
        return partRepository.findById(id).orElse(null);
    }

    public List<Part> findAll() {
        return partRepository.findAll();
    }

    public List<Part> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) return findAll();
        return partRepository.findByNameContainingIgnoreCase(name.trim());
    }

    public List<Part> findLowStockParts() {
        return partRepository.findLowStockParts();
    }

    public long count() {
        return partRepository.count();
    }

    public Optional<Part> findById(Integer id) {
        return partRepository.findById(id);
    }

    @Transactional
    public Part createPart(Part part) {
        // ensure id is null for create
        part.setPartId(null);
        return partRepository.save(part);
    }

    @Transactional
    public Part updatePart(Integer id, Part incoming) {
        // find existing: if exists, update fields and save; otherwise treat as create with specified id
        return partRepository.findById(id).map(existing -> {
            existing.setName(incoming.getName());
            existing.setBrand(incoming.getBrand());
            existing.setCategory(incoming.getCategory());
            existing.setPrice(incoming.getPrice());
            existing.setStockLevel(incoming.getStockLevel());
            existing.setReorderLevel(incoming.getReorderLevel());
            existing.setImageUrl(incoming.getImageUrl());
            existing.setDescription(incoming.getDescription());
            return partRepository.save(existing);
        }).orElseGet(() -> {
            // If the part didn't exist, create it (keeping provided id not recommended for identity columns)
            incoming.setPartId(null);
            return partRepository.save(incoming);
        });
    }

    @Transactional
    public boolean deletePart(Integer id) {
        if (!partRepository.existsById(id)) {
            return false;
        }
        partRepository.deleteById(id);
        return true;
    }
}
