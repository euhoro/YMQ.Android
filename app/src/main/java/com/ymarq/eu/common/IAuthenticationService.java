package com.ymarq.eu.common;

import com.ymarq.eu.entities.DataApiResult;
import com.ymarq.eu.entities.DataFriendContact;
import com.ymarq.eu.entities.DataGroupFriends;
import com.ymarq.eu.entities.DataUser;

import java.util.List;

/**
 * Created by eu on 12/13/2014.
 */
public interface IAuthenticationService {

    /// <summary>
    /// Tries to login User. If User is not exist returns null
    /// </summary>
    DataApiResult<DataUser> Login(String userId,boolean async);

    /// <summary>
    /// User Logon
    /// </summary>
    DataApiResult<DataUser> Logon(DataUser user,boolean async);

    /// <summary>
    /// User LoginLogon - performs both
    /// </summary>
    DataApiResult<DataUser> LoginLogon(DataUser user,boolean async);

    /// <summary>
    /// User Update friends
    /// </summary>
    DataApiResult<Boolean> UpdateFriends(DataGroupFriends groupFriends,boolean async);

    /// <summary>
    /// Get friends knownUser status (has app installed )
    /// </summary>
    DataApiResult<List<DataFriendContact>> GetFriendsStatus(DataGroupFriends groupFriends,boolean async);
}
