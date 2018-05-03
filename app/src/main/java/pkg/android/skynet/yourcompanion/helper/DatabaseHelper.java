package pkg.android.skynet.yourcompanion.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

import pkg.android.skynet.yourcompanion.models.FriendsData;

/**
 * Created by ST-3 on 14-09-2017.
 */

public class DatabaseHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "yourcompanion.db";
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase db;
    SQLiteQueryBuilder queryBuilder;

    private final String TBL_FRIENDS = "friends";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        db = getReadableDatabase();
        queryBuilder = new SQLiteQueryBuilder();
    }


    /**
     * Used to save all the friends..
     * @param datas
     */
    public void saveFriendsLocal(List<FriendsData> datas) {
        deleteAllFriends();
        for (int i = 0; i < datas.size(); i++) {
            ContentValues values = new ContentValues();
            values.put("id", datas.get(i).getId());
            values.put("user_id", datas.get(i).getUserId());
            values.put("friend_id", datas.get(i).getFriendId());
            values.put("date_time", datas.get(i).getDateTime());
            values.put("fname", datas.get(i).getFirstName());
            values.put("lname", datas.get(i).getLastName());
            values.put("phone", datas.get(i).getPhone());
            values.put("user_img", datas.get(i).getUserImg());
            values.put("latitude", datas.get(i).getLatitude());
            values.put("longitude", datas.get(i).getLongitude());

            db.insert(TBL_FRIENDS, null, values);
        }
    }


    /**
     * Used to get all the bookmark list..
     * @return
     */
    public List<FriendsData> getAllFriendsLocal() {
        List<FriendsData> friendsDatas = new ArrayList<>();

        String [] sqlSelect = {"*"};

        queryBuilder.setTables(TBL_FRIENDS);
        Cursor c = queryBuilder.query(db, sqlSelect, null, null, null, null, null);

        c.moveToFirst();

        while (!c.isAfterLast()) {
            FriendsData  friendsData = new FriendsData();
            friendsData.setId(c.getString(0));
            friendsData.setUserId(c.getString(1));
            friendsData.setFriendId(c.getString(2));
            friendsData.setDateTime(c.getString(3));
            friendsData.setFirstName(c.getString(4));
            friendsData.setLastName(c.getString(5));
            friendsData.setPhone(c.getString(6));
            friendsData.setUserImg(c.getString(7));
            friendsData.setLatitude(c.getString(8));
            friendsData.setLongitude(c.getString(9));

            friendsDatas.add(friendsData);

            c.moveToNext();
        }
        c.close();
        return friendsDatas;
    }


    /**
     * Used to delete bookmark..
     */
    public void deleteAllFriends() {
        db.delete(TBL_FRIENDS, null, null);
    }
}
