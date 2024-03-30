package com.Bassbazaar.library.service;

import com.Bassbazaar.library.model.Image;


import java.util.List;

public interface ImageService
{
    List<Image> findProductImages(long id);

    List<Image> findAll();
}
