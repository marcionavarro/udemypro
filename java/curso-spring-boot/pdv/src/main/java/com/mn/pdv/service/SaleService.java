package com.mn.pdv.service;

import com.mn.pdv.dto.ProductDTO;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        SaleInfoDTO saleInfoDTO = new SaleInfoDTO();
        saleInfoDTO.setUser(sale.getUser().getName());
        saleInfoDTO.setDate(sale.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        saleInfoDTO.setProducts(getProductInfo(sale.getItems()));

        return saleInfoDTO;
    }

    private List<ProductInfoDTO> getProductInfo(List<ItemSale> items) {
        return items.stream().map(item -> {
            ProductInfoDTO productInfoDTO = new ProductInfoDTO();
            productInfoDTO.setId(item.getId());
            productInfoDTO.setDescription(item.getProduct().getDescription());
            productInfoDTO.setQuantity(item.getQuantity());

            return productInfoDTO;
        }).collect(Collectors.toList());
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

    private List<ItemSale> getItemSale(List<ProductDTO> products) {

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
