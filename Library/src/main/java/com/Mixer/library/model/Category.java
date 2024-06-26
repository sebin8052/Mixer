package com.Mixer.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categories", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Category
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;
    private String name;
    private String description;

    @Column(name = "is_activated")
    private boolean activated;
    @Column(name = "is_deleted")
    private boolean deleted;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "category", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH, CascadeType.REMOVE})
    private List<Product> products;

    public void disableCategoryAndProducts()
{
        setActivated(false);
        setDeleted(true);
        if (getProducts() != null) {
            for (Product product : getProducts())
            {
                product.setActivated(false);
            }
        }
    }
}



