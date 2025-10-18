package com.codepresso.codepresso.controller.branch;

import com.codepresso.codepresso.entity.branch.Branch;
import com.codepresso.codepresso.service.branch.BranchService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/branch")
public class BranchController {

    private final BranchService branchService;

    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @GetMapping("/list")
    public String list(Model model,
                       @org.springframework.web.bind.annotation.RequestParam(value = "q", required = false) String q,
                       @org.springframework.web.bind.annotation.RequestParam(value = "lat", required = false) Double lat,
                       @org.springframework.web.bind.annotation.RequestParam(value = "lng", required = false) Double lng,
                       @org.springframework.web.bind.annotation.RequestParam(value = "radius", required = false) Double radiusKm) {
        int page = 0;
        int size = 6;

        Page<Branch> branchPage;
        if (q != null && !q.isBlank()) {
            // 검색어가 있으면 위치 무시하고 이름 검색
            branchPage = branchService.searchByName(q.trim(), page, size);
            model.addAttribute("q", q.trim());
        } else if (lat != null && lng != null) {
            double radius = 2.0; // 반경 2km 고정
            branchPage = branchService.getNearby(lat, lng, radius, page, size);
            model.addAttribute("lat", lat);
            model.addAttribute("lng", lng);
            model.addAttribute("radius", radius);
        } else {
            branchPage = branchService.getBranchPage(page, size);
        }

        model.addAttribute("branches", branchPage.getContent());
        model.addAttribute("nextPage", branchPage.hasNext() ? page + 1 : null);
        model.addAttribute("pageSize", size);
        model.addAttribute("hasNext", branchPage.hasNext());
        return "branch/branch-list";
    }

    @GetMapping("/page")
    public String page(@org.springframework.web.bind.annotation.RequestParam int page,
                       @org.springframework.web.bind.annotation.RequestParam(required = false) Integer size,
                       @org.springframework.web.bind.annotation.RequestParam(value = "q", required = false) String q,
                       @org.springframework.web.bind.annotation.RequestParam(value = "lat", required = false) Double lat,
                       @org.springframework.web.bind.annotation.RequestParam(value = "lng", required = false) Double lng,
                       @org.springframework.web.bind.annotation.RequestParam(value = "radius", required = false) Double radiusKm,
                       Model model, HttpServletResponse response) {
        int pageSize = (size == null || size <= 0) ? 6 : size;
        Page<Branch> branchPage;
        if (q != null && !q.isBlank()) {
            branchPage = branchService.searchByName(q.trim(), page, pageSize);
        } else if (lat != null && lng != null) {
            double radius = 2.0; // 반경 2km 고정
            branchPage = branchService.getNearby(lat, lng, radius, page, pageSize);
        } else {
            branchPage = branchService.getBranchPage(page, pageSize);
        }
        model.addAttribute("branches", branchPage.getContent());
        response.setHeader("X-Has-Next", String.valueOf(branchPage.hasNext()));
        return "branch/branch-cards";
    }

    @GetMapping(value = "/info/{branchId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> branchInfo(@PathVariable Long branchId) {
        Branch branch = branchService.getBranch(branchId);
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", branch.getId());
        payload.put("name", branch.getBranchName());
        payload.put("address", branch.getAddress());
        payload.put("openingTime", branch.getOpeningTime());
        payload.put("closingTime", branch.getClosingTime());
        return ResponseEntity.ok(payload);
    }
}
