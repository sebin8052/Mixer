package com.Bassbazaar.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products",uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Product
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;
    private String name;
    private String brand;
    private String shortDescription;
    @Column(columnDefinition = "TEXT")
    private String longDescription;
    private int currentQuantity;
    private double costPrice;
    private double salePrice;

    /* product <->  Image[one to many ]*/

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "product", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
    private List<Image> image;

    /* Category <-> Product [Many to one ]*/
    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    private Category category;

    private boolean is_activated;

    /*  used in category */
    public void setActivated(boolean categoryActivated) {
        this.is_activated = categoryActivated;
    }
}
