package com.Mixer.library.service;

import com.Mixer.library.model.Image;


import java.util.List;

public interface ImageService
{
    List<Image> findProductImages(long id);

    List<Image> findAll();
}
