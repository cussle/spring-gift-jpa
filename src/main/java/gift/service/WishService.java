package gift.service;

import static gift.util.Constants.WISH_ALREADY_EXISTS;
import static gift.util.Constants.WISH_NOT_FOUND;

import gift.dto.wish.WishRequest;
import gift.dto.wish.WishResponse;
import gift.exception.wish.DuplicateWishException;
import gift.exception.wish.WishNotFoundException;
import gift.model.Wish;
import gift.repository.WishRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class WishService {

    private final WishRepository wishRepository;
    private final ProductService productService;

    public WishService(WishRepository wishRepository, ProductService productService) {
        this.wishRepository = wishRepository;
        this.productService = productService;
    }

    public List<WishResponse> getWishlistByMemberId(Long memberId) {
        return wishRepository.findAllByMemberId(memberId).stream()
            .map(WishService::convertToDTO)
            .collect(Collectors.toList());
    }

    public WishResponse addWish(WishRequest wishRequest) {
        productService.getProductById(wishRequest.product().getId());

        if (wishRepository.existsByMemberIdAndProductId(wishRequest.member().getId(),
            wishRequest.product().getId())) {
            throw new DuplicateWishException(WISH_ALREADY_EXISTS);
        }

        Wish wish = convertToEntity(wishRequest);
        Wish savedWish = wishRepository.save(wish);
        return convertToDTO(savedWish);
    }

    public void deleteWish(Long id) {
        if (wishRepository.findById(id).isEmpty()) {
            throw new WishNotFoundException(WISH_NOT_FOUND + id);
        }
        wishRepository.deleteById(id);
    }

    // Mapper methods
    private static Wish convertToEntity(WishRequest wishRequest) {
        return new Wish(null, wishRequest.member(), wishRequest.product());
    }

    private static WishResponse convertToDTO(Wish wish) {
        return new WishResponse(wish.getId(), wish.getMember(), wish.getProduct());
    }
}
