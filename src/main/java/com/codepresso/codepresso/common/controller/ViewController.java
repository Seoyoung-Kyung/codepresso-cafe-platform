package com.codepresso.codepresso.common.controller;

import com.codepresso.codepresso.cart.dto.CartResponse;
import com.codepresso.codepresso.cart.service.CartServiceImproveGetCartByMemberId;
import com.codepresso.codepresso.member.dto.FavoriteListResponse;
import com.codepresso.codepresso.branch.entity.Branch;
import com.codepresso.codepresso.common.security.LoginUser;
import com.codepresso.codepresso.cart.service.CartService;
import com.codepresso.codepresso.branch.service.BranchService;
import com.codepresso.codepresso.member.service.FavoriteService;
import com.codepresso.codepresso.member.service.MemberProfileService;
import com.codepresso.codepresso.product.service.ProductService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * JSP 뷰를 반환하는 컨트롤러
 */
@Controller
@RequiredArgsConstructor
public class ViewController {

    @Getter
    private final MemberProfileService memberProfileService;
    private final FavoriteService favoriteService;
    private final CartService cartService;
    private final CartServiceImproveGetCartByMemberId cartServiceImproveGetCartByMemberId;
    private final BranchService branchService;

    @ModelAttribute
    public void populateBranchModalDefaults(Model model) {
        if (model.containsAttribute("selectBranches")) {
            return;
        }

        Page<Branch> branchPage = branchService.getBranchPage(0, 6);
        model.addAttribute("selectBranches", branchPage.getContent());
        model.addAttribute("branchModalHasNext", branchPage.hasNext());
        model.addAttribute("branchModalNextPage", branchPage.hasNext() ? 1 : null);
        model.addAttribute("branchModalPageSize", branchPage.getSize());
    }

    @GetMapping("/")
    public String index() { return "index"; }

    @GetMapping("/auth/signup") // /WEB-INF/views/auth/signup.jsp
    public String signupPage() {
        return "auth/signup";
    }

    @GetMapping("/auth/login") // 로그인 화면 (이미 로그인 시 매장 선택으로 이동)
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/auth/password-find") // 비밀번호 찾기 화면
    public String passwordFindPage() {
        return "auth/password-find";
    }

    @GetMapping("/auth/id-find") // 아이디 찾기 화면
    public String idFindPage() {
        return "auth/id-find";
    }

    // 매장 목록은 BranchController에서 처리

    @GetMapping("/member/mypage") // GET /member/mypage → 마이페이지 (보안설정에서 보호)
    public String mypage() {
        return "member/mypage";
    }

    /**
     * 즐겨찾기 목록
     */
    @GetMapping("/favorites")
    public String favoriteList(Authentication authentication, Model model) {
        Long memberId = null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof LoginUser lu) {
            memberId = lu.getMemberId();
        }
        try {
            // 즐겨찾기 목록 조회 후 JSP에 전달
            FavoriteListResponse favoriteList = favoriteService.getFavoriteList(memberId);
            model.addAttribute("favoriteList", favoriteList);
        } catch (Exception e) {
            // 에러 발생 시 에러 메시지를 JSP에 전달
            model.addAttribute("error", e.getMessage());
        }
        return "member/favorite-list";
    }

    @GetMapping("/cart")
    public String viewCart(@AuthenticationPrincipal LoginUser loginUser, Model model) {
        CartResponse cart = null;
        if (loginUser != null) {
            try {
                cart = cartServiceImproveGetCartByMemberId.getCartByMemberId(loginUser.getMemberId());
            } catch (IllegalArgumentException ignored) {
                // 장바구니가 없으면 빈 화면을 보여준다.
            }
        }
        model.addAttribute("cart", cart);
        return "cart/cart";
    }


    /**
     * 게시판 목록 페이지
     */
    @GetMapping("/boards/list")
    public String boardList() {
        return "board/board-list";
    }

    /**
     * 게시판 상세 페이지
     */
    @GetMapping("/boards/detail/{boardId}")
    public String boardDetail(@PathVariable Long boardId, Model model) {
        model.addAttribute("boardId", boardId);
        return "board/board-detail";
    }

    /**
     * 게시판 글쓰기 페이지
     */
    @GetMapping("/boards/write")
    public String boardWrite() {
        return "board/board-write";
    }

    /**
     * 게시판 수정 페이지
     */
    @GetMapping("/boards/edit/{boardId}")
    public String boardEdit(@PathVariable Long boardId, Model model) {
        model.addAttribute("boardId", boardId);
        return "board/board-write";
    }

}
