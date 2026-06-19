package com.comdao.api.cart;

import com.comdao.api.cart.dto.CartCreationRequestDto;
import com.comdao.api.cart.dto.CartItemResponseDto;
import com.comdao.api.cart.dto.CartItemUpdateRequestDto;
import com.comdao.api.cart.entities.CartItem;
import com.comdao.api.cart.exceptions.CartItemNotFoundException;
import com.comdao.api.cart.exceptions.CartItemNotOwnedException;
import com.comdao.api.product.ProductRepository;
import com.comdao.api.product.entities.Product;
import com.comdao.api.product.exceptions.ProductNotExistException;
import com.comdao.api.user.UserChecker;
import com.comdao.api.user.entities.User;
import com.comdao.api.user.exceptions.UserDisabledException;
import com.comdao.api.user.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;
    private final UserChecker userChecker;
    private final ProductRepository productRepository;


    @Transactional(readOnly = true)
    public Page<CartItemResponseDto> getCart(Long userId, Integer page, Integer size)
            throws UserNotFoundException, UserDisabledException {
        User user = userChecker.checkExistAndActiveById(userId);

        return cartItemRepository.findByUser_Id(userId, PageRequest.of(page, size))
                .map(cartItemMapper::toResponse);
    }

    @Transactional
    public void addToCart(Long userId, CartCreationRequestDto newCartItem)
            throws UserNotFoundException, UserDisabledException, ProductNotExistException {
        User user = userChecker.checkExistAndActiveById(userId);
        Product product;

        CartItem cartItem = cartItemRepository.findByProductIdAndUser_Id(newCartItem.getProductId(), userId).orElse(null);
        if (cartItem != null)
            cartItem.setQuantity(newCartItem.getQuantity());
        else {
            cartItem = new CartItem();
            product = productRepository.findById(newCartItem.getProductId()).orElseThrow(
                    () -> new ProductNotExistException("This product does not exist")
            );
            cartItem.setProduct(product);
            cartItem.setQuantity(newCartItem.getQuantity());
            cartItem.setUser(user);
        }

        cartItemRepository.save(cartItem);
    }

    @Transactional
    public CartItemResponseDto updateCartItem(Long userId, CartItemUpdateRequestDto updatedCartItem)
            throws CartItemNotFoundException, UserNotFoundException,
            UserDisabledException, CartItemNotOwnedException {
        User user = userChecker.checkExistAndActiveById(userId);

        CartItem cartItem = cartItemRepository.findById(updatedCartItem.getId()).orElseThrow(
                () -> new CartItemNotFoundException("Cart item does not exist")
        );

        if (!cartItem.getUser().equals(user))
            throw new CartItemNotOwnedException("This user does not own this cart item");

        cartItem.setQuantity(updatedCartItem.getQuantity());
        cartItemRepository.save(cartItem);
        return cartItemMapper.toResponse(cartItem);
    }

    @Transactional
    public void removeFromCart(Long userId, Long cartId)
            throws UserNotFoundException, UserDisabledException,
            CartItemNotFoundException, CartItemNotOwnedException {
        User user = userChecker.checkExistAndActiveById(userId);

        CartItem cartItem = cartItemRepository.findById(cartId).orElseThrow(
                () -> new CartItemNotFoundException("Cart item does not exist")
        );

        if (!cartItem.getUser().equals(user))
            throw new CartItemNotOwnedException("This user does not own this cart item");

        cartItemRepository.delete(cartItem);
    }

//    @Transactional
//    public void removeFromCart(Long userId, Long cartId)
//            throws UserNotFoundException, UserDisabledException,
//            CartItemNotFoundException, CartItemNotOwnedException {
//        User user = userChecker.checkExistAndActiveById(userId);
//
//        CartItem cartItem = cartItemRepository.findById(cartId).orElseThrow(
//                () -> new CartItemNotFoundException("Cart item does not exist")
//        );
//
//        if (!cartItem.getUser().equals(user))
//            throw new CartItemNotOwnedException("This user does not own this cart item");
//
//        cartItemRepository.delete(cartItem);
//    }
}
