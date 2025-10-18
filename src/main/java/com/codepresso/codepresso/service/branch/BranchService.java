package com.codepresso.codepresso.service.branch;

import com.codepresso.codepresso.entity.branch.Branch;
import com.codepresso.codepresso.repository.branch.BranchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BranchService {

    private final BranchRepository branchRepository;

    public BranchService(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    public List<Branch> getAllBranches() {
        return branchRepository.findAll(Sort.by(Sort.Direction.ASC, "branchName"));
    }

    public Page<Branch> getBranchPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "branchName"));
        return branchRepository.findAll(pageable);
    }

    public Page<Branch> searchByName(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "branchName"));
        return branchRepository.findByBranchNameContainingIgnoreCase(keyword, pageable);
    }

    public Page<Branch> getNearby(double lat, double lng, double radiusKm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return branchRepository.findNearby(lat, lng, radiusKm, pageable);
    }

    public Branch getBranch(Long branchId) {
        return branchRepository.findById(branchId)
                .orElseThrow(() -> new IllegalArgumentException("해당 매장을 찾을 수 없습니다."));
    }
}
