package com.ymarq.eu.ymarqtests;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Base64;

import com.ymarq.eu.business.CloudEngine;
import com.ymarq.eu.common.IAuthenticationService;
import com.ymarq.eu.common.IImageService;
import com.ymarq.eu.common.IMessagingService;
import com.ymarq.eu.common.IProductService;
import com.ymarq.eu.entities.DataApiResult;
import com.ymarq.eu.entities.DataFriendContact;
import com.ymarq.eu.entities.DataGroupFriends;
import com.ymarq.eu.entities.DataMessage;
import com.ymarq.eu.entities.DataNotificationsModel;
import com.ymarq.eu.entities.DataProduct;
import com.ymarq.eu.entities.DataSubscription;
import com.ymarq.eu.entities.DataUser;
import com.ymarq.eu.ymarq.R;

import junit.framework.Assert;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by eu on 12/28/2014.
 */
//public class CloudEngineTest extends TestCase implements IAuthenticationService, IProductService, IMessagingService, IImageService {
public class CloudEngineTest extends AndroidTestCase implements IAuthenticationService, IProductService, IMessagingService, IImageService {

private CloudEngine mCloudEngine ;

    //final private String mUserId1 = "3DB348A6-ACD6-4403-AEB8-A412907469C8";//76971ea670ff89be";
    //final private String mUserId2 = "725CE932-CED6-4A61-B2D2-29B2A68B0664";

    //AC6DADC3-8F18-489A-AB8D-39EE8118629D
    final private String mUserId1 = "ADD31FC4-CE6B-4E90-9BA8-EF988696A32B_1";//AC6DADC3-8F18-489A-AB8D-39EE8118629D_I";//76971ea670ff89be";
    final private String mUserId2 = "ADD31FC4-CE6B-4E90-9BA8-EF988696A32B_2";
    final private String mUserReal ="76971ea670ff89be";

    private static String mSubscriptionSearch1 = "trocariciZZZZ";
    private static String mSubscriptionSearch2 = "matragunaZZZZ";

    final private String mSubscriptionPhone1 = "+972545989827";
    final private String mSubscriptionPhone2 = "+972544330106";

    final private String mNick1 = "nick1";
    final private String mNick2 = "nick2";

    private String mMessage1 = "mesage1";
    private String mMessage2 = "message2";

    final private String mEmail1 = "nick1@nick1.com";
    final private String mEmail2 = "nick2@nick2.com";

    private UUID mSubscriptionId1 = null;
    private UUID mSubscriptionId2 = null;

    private String mProductId1 = "a";
    private String mProductId2 = "b";

    private UUID mSubscriptionId = UUID.randomUUID();
    final private String mProductId = "7ce32384-703c-4ef0-8b90-cd6a44195230";
    final private String mDescription = "Description4 Description3 ";

    protected void setUp() throws Exception {
        super.setUp();
        mCloudEngine = CloudEngine.getInstance();
        mCloudEngine.setApplicationContext(this.getContext().getApplicationContext());
    }

    @SmallTest
    public void testScenario()
    {
        //this tests test the basics
        //login logon / publish / subscribe / get friends
        //the users has to be new
        checkLoginLogon();

        ///////products//////////////

        checkPublishProduct();

        checkNumberProducts(1);

        checkGetProductsByUser();

        ////////subscriptions///////////

        checkSubscribe();

        checkNumberSubscription(1);

        checkGetSubscription();

        //////test search /////////

        checkNumberProducts(1);

        checkGetProductsBySubscription();

        ////////friends/////////////////

        checkUpdateFriends();

        checkGetFriendsStatus();

        ////cleanup//remove added product /subscription

        checkDeleteSubscription();

        checkDeleteProduct();

        //////test cleanup

        checkNumberSubscription(0);

        checkNumberProducts(0);
    }

    @SmallTest
    public void testNotifications() {
        checkGetNotifications();
    }

    @SmallTest
    public void testCommunication()
    {
        checkLoginLogon();

        //checkSubscribe();

        checkPublishProduct();

        //testGetNotifications();//check for newProducts

        checkSendMessages();

        checkNumberMessages(1);

        checkGetMessages();

        //testGetNotifications();//check for buyer notifications

        //checkUpdateFriends();

        //checkGetFriendsStatus();

        //checkPublishProduct();

        //testGetNotifications();//check for new products

        //////get image /////
        //checkGetImage();

        //remove everything
        //checkDeleteSubscription();

        checkDeleteProduct();

        checkNumberProducts(0);
    }

    private void checkLoginLogon()
    {
        //check regular login

        DataUser dataUser1 = new DataUser(mUserId1,mEmail1,mNick1,mSubscriptionPhone1);
        dataUser1.RegistrationId = "1234";

        DataUser receivedUser = this.LoginLogon(dataUser1, false).Result;
        assertNotNull(receivedUser);

        //check with other mail
        receivedUser.Email = mEmail2;
        receivedUser = this.LoginLogon(dataUser1, false).Result;
        assertFalse(receivedUser.Email.equals(mEmail2));


        //check regular login2
        DataUser dataUser2 = new DataUser(mUserId2,mEmail2,mNick2,mSubscriptionPhone2);
        dataUser2.RegistrationId = "5678";
        receivedUser = this.LoginLogon(dataUser2, false).Result;
        assertNotNull(receivedUser);

        //check regular logog after chaging the key
        dataUser2.RegistrationId ="9999";
        receivedUser = this.LoginLogon(dataUser2, false).Result;
        assertTrue(receivedUser.RegistrationId.equals("9999"));

        //todo
        //user without registrationid == null should fail
    }

    private void checkSubscribe()
    {
        DataSubscription ds1 = new DataSubscription(mSubscriptionSearch1,mUserId1);
        DataSubscription receivedSubscription = this.Subscribe(ds1, false).Result;
        mSubscriptionId1 = receivedSubscription.Id;
        assertNotNull(receivedSubscription);

        DataSubscription ds2 = new DataSubscription(mSubscriptionSearch2,mUserId2);
        receivedSubscription = this.Subscribe(ds2, false).Result;
        mSubscriptionId2 = receivedSubscription.Id;
        assertNotNull(receivedSubscription);
    }

    private void checkGetSubscription()
    {
        boolean found = false;
        List<DataSubscription> subscriptions1 = this.GetSubscriptions(mUserId1, false).Result;
        for (DataSubscription subscription : subscriptions1) {
            found = mSubscriptionSearch1.equals(subscription.SearchText);
            if(found)
                break;
        }
        assertTrue(found);
        found = false;

        List<DataSubscription> subscriptions2 = this.GetSubscriptions(mUserId2, false).Result;
        for (DataSubscription subscription : subscriptions2) {
            found = mSubscriptionSearch2.equals(subscription.SearchText);
            if(found)
                break;
        }
        assertTrue(found);
    }

    private void checkNumberSubscription(int number)
    {
        boolean found = false;
        List<DataSubscription> subscriptions1 = this.GetSubscriptions(mUserId1, false).Result;

        assertTrue(subscriptions1.size() == number);

        List<DataSubscription> subscriptions2 = this.GetSubscriptions(mUserId2, false).Result;
        assertTrue(subscriptions2.size() == number);
    }

    private void checkPublishProduct()
    {
        //check publish product

        Bitmap bmp = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_launcher);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        String testImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        DataProduct dataProduct = new DataProduct();
        dataProduct.Id = UUID.randomUUID().toString();
        dataProduct.UserId = mUserId1;
        mSubscriptionSearch2 = mSubscriptionSearch2 + " " + UUID.randomUUID().toString();
        dataProduct.Description = mSubscriptionSearch2;
        dataProduct.ImageContent = testImage;
        dataProduct.NotifyFriends = true;

        DataApiResult<DataProduct> prod = this.PublishProduct(dataProduct,false);
        mProductId1 = prod.Result.Id;
        assertNotNull(prod.Result);
        assertTrue(prod.Result.NotifyFriends == dataProduct.NotifyFriends);

        dataProduct = new DataProduct();
        //dataProduct.Id = UUID.randomUUID().toString();
        dataProduct.UserId = mUserId2;
        mSubscriptionSearch1 = mSubscriptionSearch1 + " " + UUID.randomUUID().toString();
        dataProduct.Description = mSubscriptionSearch1;
        dataProduct.ImageContent = testImage;
        dataProduct.NotifyFriends = true;

        prod = this.PublishProduct(dataProduct,false);
        mProductId2 = prod.Result.Id;
        assertNotNull(prod);

        //todo check with bigger image
        //check if all properties are ok
    }

    private void checkGetProductsByUser()
    {
        List<DataProduct> products = this.GetProducts(mUserId1,false).Result;
        boolean found= false;
        for(DataProduct dp : products)
            if (dp.Description.equals(mSubscriptionSearch2))
                found = true;

        assertTrue(found);

        products = this.GetProducts(mUserId2,false).Result;
        found= false;
        for(DataProduct dp : products)
            if (dp.Description.equals(mSubscriptionSearch1))
                found = true;

        assertTrue(found);
    }

    private void checkNumberProducts(int number)
    {
        List<DataProduct> products = this.GetProducts(mUserId1,false).Result;
        assertNotNull(products);
        assertTrue(products.size() == number);

        products = this.GetProducts(mUserId2,false).Result;
        assertNotNull(products);
        assertTrue(products.size() == number);
        //todo : check if the published products are already here
    }

    private void checkGetProductsBySubscription()
    {
        List<DataProduct> products = this.GetProductsBySubscription(mUserId1, mSubscriptionSearch1, false).Result;
        assertNotNull(products);
        assertTrue(products.get(0).Description.equals(mSubscriptionSearch1));

        products = this.GetProductsBySubscription(mUserId2,mSubscriptionSearch2,false).Result;
        assertNotNull(products);
        assertTrue(products.get(0).Description.equals(mSubscriptionSearch2));

        //i did not find products that are not in the scope
        products = this.GetProductsBySubscription(mUserId1, mSubscriptionSearch2, false).Result;
        assertNotNull(products);
        int numberOfProducts = products.size();
        assertTrue(numberOfProducts==0);
    }

    private void checkSendMessages()
    {
        mMessage2 = mMessage2 + UUID.randomUUID().toString();
        DataMessage dm = new DataMessage(mMessage2,mUserId2,mProductId1.toString());
        DataApiResult<Boolean> res = this.SendMessage(dm,false);
        Assert.assertTrue(res.Result);


        mMessage1 = mMessage1 + UUID.randomUUID().toString();
        dm = new DataMessage(mMessage1,mUserId1,mProductId2.toString());
        dm.ToUserId = mUserId2;//private message

        //this.SendMessage(dm,false);
        res = this.SendMessage(dm,false);
        Assert.assertTrue(res.Result);
    }

    private void checkNumberMessages(int number)
    {
        List<DataMessage> messages = this.GetMessages(UUID.fromString(mProductId1), false).Result;
        assertNotNull(messages);

        messages = this.GetMessages(UUID.fromString(mProductId2), false).Result;
        assertNotNull(messages);
        assertTrue(messages.size() == number);
        //todo - check the messages sent are here ( message1 and message2)
    }

    private void checkGetMessages()
    {
        List<DataMessage> messages = this.GetMessages(UUID.fromString(mProductId1), false).Result;
        boolean found = messages.get(0).Content.equals(mMessage2);

        assertNotNull(messages);
        assertTrue(found);
        assertTrue(messages.get(0).ToUserId.equals(""));//public

        messages = this.GetMessages(UUID.fromString(mProductId2), false).Result;

        found = messages.get(0).Content.equals(mMessage1);

        assertNotNull(messages);
        assertTrue(found);
        assertTrue(messages.get(0).ToUserId.equals(mUserId2));//private

        //todo - check the messages sent are here ( message1 and message2)
    }

    //@SmallTest
    //todo impmenet this for later
    //public void testGetMessagesByDate()
    //{
    //    // Create a calendar object with today date. Calendar is in java.util pakage.
    //    Calendar calendar = Calendar.getInstance();
    //    // Move calendar to yesterday
    //    calendar.add(Calendar.DATE, -1);
    //    // Get current date of calendar which point to the yesterday now
    //    Date yesterday = calendar.getTime();
//
    //    List<DataMessage> messages = this.GetMessagesByDate(UUID.fromString(mProductId), yesterday, false).Result;
    //    assertFalse(messages.size() > 0);
    //}

    private void checkGetNotifications()
    {
        // Create a calendar object with today date. Calendar is in java.util pakage.
        Calendar calendar = Calendar.getInstance();
        // Move calendar to yesterday x3
        calendar.add(Calendar.DATE, -1);
        // Get current date of calendar which point to the yesterday now
        Date yesterday = calendar.getTime();

        DataNotificationsModel notifications = this.GetNotificationsByUserDate(mUserReal, yesterday,false).Result;
        assertTrue(notifications.NewProducts.size() + notifications.BuyerNotifications.size() > 0);

        notifications = this.GetNotificationsByUserDate(mUserReal, yesterday,false).Result;
        assertTrue(notifications.NewProducts.size() + notifications.BuyerNotifications.size() > 0);
    }

    private void checkNumberSearchProduct(int number)
    {
        List<DataProduct> products = this.GetProductsBySubscription(mUserId1, mSubscriptionSearch1, false).Result;
        assertTrue(products.size() == number);

        products = this.GetProductsBySubscription(mUserId2, mSubscriptionSearch2, false).Result;
        assertTrue(products.size() == number);

        assertTrue(products.get(0).UserName!=null);

        String mysubstription = mSubscriptionSearch1.split(" ")[0];
        products = this.GetProductsBySubscription(mUserId2, mysubstription, false).Result;
        assertTrue(products.size()== 0);//should not return the products I have
    }

    private void checkDeleteSubscription()
    {
        DataSubscription dataSubscription = new DataSubscription();
        dataSubscription.Id = mSubscriptionId1;
        dataSubscription.UserId = mUserId1;
        DataApiResult<Boolean> res = this.DeleteSubscription(dataSubscription, false);
        assertTrue(res.Result);

        dataSubscription = new DataSubscription();
        dataSubscription.Id = mSubscriptionId2;
        dataSubscription.UserId = mUserId2;
        res = this.DeleteSubscription(dataSubscription, false);
        assertTrue(res.Result);
    }

    private void checkDeleteProduct()
    {
        DataProduct dataProduct = new DataProduct();
        dataProduct.Id = mProductId1;
        DataApiResult<Boolean> res = this.DeleteProduct(dataProduct, false);
        assertTrue(res.Result);

        dataProduct = new DataProduct();
        dataProduct.Id = mProductId2;
        res = this.DeleteProduct(dataProduct, false);
        assertTrue(res.Result);
    }

    private void checkUpdateFriends()
    {
        //DataUser dataUser1 = new DataUser(mUserId1,"e989828@gmail.com","nick1","+972545989828");
        DataGroupFriends df = new DataGroupFriends();
        df.Members = new ArrayList<>();
        df.UserId = mUserId1;

        DataFriendContact dataFriendContact1 = new DataFriendContact(mSubscriptionPhone2);
        df.Members.add(dataFriendContact1);
        DataApiResult<Boolean> res = this.UpdateFriends(df, false);

        assertTrue(res.Result);

        df = new DataGroupFriends();
        df.Members = new ArrayList<>();
        df.UserId = mUserId2;

        DataFriendContact dataFriendContact2 =  new DataFriendContact(mSubscriptionPhone1);
        df.Members.add(dataFriendContact2);
        res = this.UpdateFriends(df, false);

        assertTrue(res.Result);
    }

    private void checkGetFriendsStatus()
    {
        //DataUser dataUser1 = new DataUser(mUserId1,"e989828@gmail.com","nick1","+972545989828");
        DataGroupFriends df = new DataGroupFriends();
        df.Members = new ArrayList<>();
        df.UserId = mUserId1;

        DataFriendContact dataFriendContact1 = new DataFriendContact(mSubscriptionPhone2);
        df.Members.add(dataFriendContact1);
        DataApiResult<List<DataFriendContact>> knownUsers = this.GetFriendsStatus(df, false);

        assertTrue(knownUsers.Result.size() == 1);
        assertTrue(knownUsers.Result.get(0).PhoneNumber.equals(mSubscriptionPhone2));

        df = new DataGroupFriends();
        df.Members = new ArrayList<>();
        df.UserId = mUserId2;

        DataFriendContact dataFriendContact2 =  new DataFriendContact(mSubscriptionPhone1);
        df.Members.add(dataFriendContact2);
        DataApiResult<List<DataFriendContact>> knownUsers2 = this.GetFriendsStatus(df, false);
        assertTrue(knownUsers2.Result.size()==1);
        assertTrue(knownUsers2.Result.get(0).PhoneNumber.equals(mSubscriptionPhone1));
    }

    private void getImage()
    {
        assertFalse(true);
    }

    @Override
    protected void tearDown() throws Exception{
        super.tearDown();
    }

    @Override
    public DataApiResult<DataUser> Login(String userId, boolean async) {
        return mCloudEngine.Login(userId, false);
    }

    @Override
    public DataApiResult<DataUser> Logon(DataUser user, boolean async) {
        return mCloudEngine.Logon(user, false);
    }

    @Override
    public DataApiResult<DataUser> LoginLogon(DataUser user, boolean async) {
       return mCloudEngine.LoginLogon(user, false);
    }

    @Override
    public DataApiResult<Boolean> UpdateFriends(DataGroupFriends groupFriends, boolean async) {
        return mCloudEngine.UpdateFriends(groupFriends, false);
    }

    @Override
    public DataApiResult<List<DataFriendContact>> GetFriendsStatus(DataGroupFriends groupFriends, boolean async) {
        return mCloudEngine.GetFriendsStatus(groupFriends, false);
    }

    @Override
    public DataApiResult<Bitmap> GetImage(DataProduct dataProduct, boolean async) {
        return mCloudEngine.GetImage(dataProduct, false);
    }

    @Override
    public DataApiResult<Boolean> SendMessage(DataMessage message, boolean async) {
        return mCloudEngine.SendMessage(message, false);
    }

    @Override
    public DataApiResult<List<DataMessage>> GetMessages(UUID productId, boolean async) {
        return mCloudEngine.GetMessages(productId, false);
    }

    @Override
    public DataApiResult<List<DataMessage>> GetMessagesByDate(UUID productId, Date from, boolean async) {
        return mCloudEngine.GetMessagesByDate(productId, from, false);
    }

    @Override
    public DataApiResult<DataNotificationsModel> GetNotificationsByUserDate(String userId, Date from, boolean async) {
        return mCloudEngine.GetNotificationsByUserDate(userId, from, false);
    }

    @Override
    public DataApiResult<DataProduct> PublishProduct(DataProduct product, boolean async) {
        return mCloudEngine.PublishProduct(product, false);
    }

    @Override
    public DataApiResult<List<DataProduct>> GetProducts(String publisherId, boolean async) {
        return mCloudEngine.GetProducts(publisherId, false);
    }

    @Override
    public DataApiResult<Boolean> DeleteProduct(DataProduct product, boolean async) {
        return mCloudEngine.DeleteProduct(product, false);
    }

    @Override
    public DataApiResult<Boolean> LeaveProduct(DataProduct product,String userId, boolean async) {
        return mCloudEngine.LeaveProduct(product, userId, false);
    }

    @Override
    public DataApiResult<List<DataProduct>> GetTopProducts(String publisherId, boolean async) {
        return mCloudEngine.GetTopProducts(publisherId, false);
    }

    @Override
    public DataApiResult<DataSubscription> Subscribe(DataSubscription subscription, boolean async) {
        return mCloudEngine.Subscribe(subscription, false);
    }

    @Override
    public DataApiResult<List<DataSubscription>> GetSubscriptions(String userId, boolean async) {
        return mCloudEngine.GetSubscriptions(userId, false);
    }

    @Override
    public DataApiResult<List<DataProduct>> GetProductsBySubscription(String userId, String subscriptionId, boolean async) {
        return mCloudEngine.GetProductsBySubscription(userId, subscriptionId, false);
    }

    @Override
    public DataApiResult<Boolean> DeleteSubscription(DataSubscription dataSubscription, boolean async) {
        return mCloudEngine.DeleteSubscription(dataSubscription, false);
    }
}
