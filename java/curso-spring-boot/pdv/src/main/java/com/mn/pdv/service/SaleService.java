package com.mn.pdv.service;

import com.mn.pdv.dto.ProductSaleDTO;
import com.mn.pdv.dto.ProductInfoDTO;
import com.mn.pdv.dto.SaleDTO;
import com.mn.pdv.dto.SaleInfoDTO;
import com.mn.pdv.entity.ItemSale;
import com.mn.pdv.entity.Product;
import com.mn.pdv.entity.Sale;
import com.mn.pdv.entity.User;
import com.mn.pdv.exceptions.InvalidOperationException;
import com.mn.pdv.exceptions.NoItemException;
import com.mn.pdv.repository.ItemSaleRepository;
import com.mn.pdv.repository.ProductRepository;
import com.mn.pdv.repository.SaleRepository;
import com.mn.pdv.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;
    private final ItemSaleRepository itemSaleRepository;

    public List<SaleInfoDTO> findAll() {
        return saleRepository.findAll().stream().map(sale -> getSaleInfo(sale)).collect(Collectors.toList());
    }

    private SaleInfoDTO getSaleInfo(Sale sale) {

        var products = getProductInfo(sale.getItems());
        BigDecimal total = getTotal(products);

        return SaleInfoDTO.builder()
                .user(sale.getUser().getName())
                .date(sale.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .products(products)
                .total(total)
                .build();
    }

    private BigDecimal getTotal(List<ProductInfoDTO> products) {
        BigDecimal total = new BigDecimal(0);
        for (int i = 0; i < products.size(); i++) {
            ProductInfoDTO currentProduct = products.get(i);
            total = total.add(currentProduct.getPrice()
                    .multiply(new BigDecimal(currentProduct.getQuantity())));
        }
        return total;
    }

    private List<ProductInfoDTO> getProductInfo(List<ItemSale> items) {
        if (CollectionUtils.isEmpty(items)) return Collections.emptyList();

        return items.stream().map(
                item -> ProductInfoDTO
                        .builder()
                        .id(item.getId())
                        .description(item.getProduct().getDescription())
                        .price(item.getProduct().getPrice())
                        .quantity(item.getQuantity()).build()
        ).collect(Collectors.toList());
    }

    @Transactional
    public long save(SaleDTO sale) {
        User user = userRepository.findById(sale.getUserid())
                .orElseThrow(() -> new NoItemException("Usuário não encontrado."));

        Sale newSale = new Sale();
        newSale.setUser(user);
        newSale.setDate(LocalDate.now());
        List<ItemSale> items = getItemSale(sale.getItems());

        newSale = saleRepository.save(newSale);

        saveItemSale(items, newSale);

        return newSale.getId();
    }

    private void saveItemSale(List<ItemSale> items, Sale newSale) {
        for (ItemSale item : items) {
            item.setSale(newSale);
            itemSaleRepository.save(item);
        }
    }

    private List<ItemSale> getItemSale(List<ProductSaleDTO> products) {

        if (products.isEmpty()) {
            throw new InvalidOperationException("Não é possível adicionar a venda sem itens!");
        }

        return products.stream().map(item -> {
            Product product = productRepository.findById(item.getProductid())
                    .orElseThrow(() -> new NoItemException("Item da venda não encontrado"));

            ItemSale itemSale = new ItemSale();
            itemSale.setProduct(product);
            itemSale.setQuantity(item.getQuantity());

            if (product.getQuantity() == 0) {
                throw new NoItemException(String.format("Produto (%s) sem estoque.", product.getDescription()));
            }

            if (product.getQuantity() < item.getQuantity()) {
                throw new InvalidOperationException(String.format(
                        "A quantidade de (%s) items da venda é maior que a quantidade de (%s) items disponivel em estoque.",
                        item.getQuantity(), product.getQuantity())
                );
            }

            int total = product.getQuantity() - item.getQuantity();
            product.setQuantity(total);
            productRepository.save(product);

            return itemSale;
        }).collect(Collectors.toList());
    }

    public Object getById(long id) {
        Sale sale = saleRepository.findById(id).orElseThrow(() -> new NoItemException("Venda não encontrada"));
        return getSaleInfo(sale);
    }
}
