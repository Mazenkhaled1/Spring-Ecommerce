package com.codewithmosh.store.controllers;


import com.codewithmosh.store.dtos.AddProductRequest;
import com.codewithmosh.store.dtos.ProductDto;
import com.codewithmosh.store.entities.Product;
import com.codewithmosh.store.repositories.CategoryRepository;
import com.codewithmosh.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {
    private final CategoryRepository categoryRepository;
    private ProductRepository productRepository;


    @GetMapping("")
    public List<ProductDto> getProducts(
            @RequestParam(required = false , name = "categoryId") Byte categoryId
    ) {
        List<Product> products ;
        if(categoryId != null){
            products = productRepository.findByCategoryId(categoryId);
        }else {
            products = productRepository.findAllWithCategory();
        }
        return products.stream().map(
                product -> new ProductDto(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getCategory().getId()
                )

        ).toList() ;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        var product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(
                new ProductDto(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getCategory().getId()
                )
        );
    }


    // post
    @PostMapping
    public ResponseEntity<ProductDto> addProduct(
            @RequestBody AddProductRequest request,
              UriComponentsBuilder uriBuilder
    )
    {
        var category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(category);
        productRepository.save(product);
        var productDto = new ProductDto(product.getId(), product.getName(), product.getDescription(), product.getPrice(), category.getId());
        var uri = uriBuilder.path("/products/{id}").buildAndExpand(product.getId()).toUri();
        return  ResponseEntity.created(uri).body(productDto);

    }


    // put
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable(name = "id") Long id ,
            @RequestBody AddProductRequest request
    )
    {
        var category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        var product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(category);
        productRepository.save(product);
        var productDto = new ProductDto(product.getId(), product.getName(), product.getDescription(), product.getPrice(), category.getId());
        return ResponseEntity.ok(productDto) ;
    }


    @DeleteMapping("/{id}/delete-product")
    public ResponseEntity<Void> deleteProduct(@PathVariable(name = "id") Long id)
    {
        var product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        productRepository.delete(product);
        return ResponseEntity.noContent().build();
    }
}
