package com.ymarq.eu.common;

import com.ymarq.eu.entities.DataApiResult;
import com.ymarq.eu.entities.DataProduct;
import com.ymarq.eu.entities.DataSubscription;

import java.util.List;

/**
 * Created by eu on 12/13/2014.
 */
public interface IProductService {
    /// <summary>
    /// Publishers Product by
    /// </summary>
    DataApiResult<DataProduct> PublishProduct(DataProduct product,boolean async);//, Stream image);
    /// <summary>
    /// Get Product by publisherId
    /// </summary>
    DataApiResult<List<DataProduct>> GetProducts(String publisherId,boolean async);

    /// <summary>
    /// Get Product by publisherId
    /// </summary>
    DataApiResult<List<DataProduct>> GetTopProducts(String publisherId,boolean async);

    /// <summary>
    /// Delete published Product
    /// </summary>
    /// <param name="productId"></param>
    DataApiResult<Boolean> DeleteProduct(DataProduct product,boolean async);
    /// <summary>
    /// Leave subscribed Product
    /// </summary>
    /// <param name="productId"></param>
    DataApiResult<Boolean> LeaveProduct(DataProduct product,String userId,boolean async);
    /// <summary>
    /// Add new subscription
    /// </summary>
    /// <param name="subscription"></param>
    DataApiResult<DataSubscription> Subscribe(DataSubscription subscription,boolean async);
    /// <summary>
    /// Returns all subscriptions of given User
    /// </summary>
    /// <param name="userId"></param>
    /// <returns></returns>
    DataApiResult<List<DataSubscription>> GetSubscriptions(String userId,boolean async);

    /// <summary>
    /// Get Product by subscription
    /// </summary>
    DataApiResult<List<DataProduct>> GetProductsBySubscription(String userId,String subscriptionId,boolean async);

    /// <summary>
    /// Delete subscription
    /// </summary>
    /// <param name="productId"></param>
    DataApiResult<Boolean> DeleteSubscription(DataSubscription dataSubscription,boolean async);

}
