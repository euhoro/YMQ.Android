package com.ymarq.eu.common;

import com.ymarq.eu.entities.DataProduct;

import java.util.List;

/**
 * Created by eu on 12/28/2014.
 */
public interface IOnProductsReceived {
    void fireOnProductsReceived(List<DataProduct> products);
    void fireOnOneProductReceived(DataProduct product);
}
