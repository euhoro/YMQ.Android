package com.ymarq.eu.common;

import android.graphics.Bitmap;

import com.ymarq.eu.entities.DataApiResult;
import com.ymarq.eu.entities.DataProduct;

/**
 * Created by eu on 1/1/2015.
 */
public interface IImageService {
    DataApiResult<Bitmap> GetImage(DataProduct dataProduct,boolean async);
}
