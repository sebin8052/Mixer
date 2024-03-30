package com.Bassbazaar.library.dto;

import com.Bassbazaar.library.model.Category;
import com.Bassbazaar.library.model.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto
{
    private Long id;
    private String name;
    private String brand;
    private String longDescription;
    private String shortDescription;
    private int currentQuantity;
    private double costPrice;
    private double salePrice;


    private List<Image> image;
    private Category category;
    private boolean activated;
}
