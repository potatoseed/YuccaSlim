package com.yuccaworld.yuccaslim.utilities;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.text.format.DateUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.yuccaworld.yuccaslim.data.SlimContract;
import com.yuccaworld.yuccaslim.model.ActivityInfo;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Yung on 9/3/2017.
 */

public class SlimUtils {
    public static String gUserRegistrationEmail="";
    public static String gRegistrationPassword="";
    public static String gUid;
    public static String gUserEmail;

    public static final String EXTRA_REGISTRATION_EMAIL = "ExtraRegistrationEmail";
    public static final String EXTRA_REGISTRATION_PASSWORD = "ExtraRegistrationPassword";
    public static final String EXTRA_PURCHASE_RESULT = "ExtraPurchaseResult";


    public static String getCurrentDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(new Date());
        return currentDate;
    }

    // Get activity type icon
    public static int getActivityTypeIcon() {
        return 0;
    }

    /**
     * uuid转化 byte[]
     *
     * @param uuid
     * @return
     */
    public static byte[] toByte(UUID uuid) {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return buffer.array();
    }

    /**
     * byte[] 转换 uuid
     *
     * @param bytes
     * @return
     */
    public static UUID toUUID(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long fistLong = buffer.getLong();
        long secondLong = buffer.getLong();
        return new UUID(fistLong, secondLong);
    }

    /**
     * byte[] 转换为 16进制字符串啊
     *
     * @param bytes
     * @return
     */
    public static String bytesToHexStr(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (byte b : bytes) {
            int v = b & 0xff;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                sb.append("0");
            }
            sb.append(hv);
        }
        return sb.toString();
    }

    public static boolean isYesterday(long date) {
        Calendar now = Calendar.getInstance();
        Calendar cdate = Calendar.getInstance();
        cdate.setTimeInMillis(date);

        now.add(Calendar.DATE,-1);

        return now.get(Calendar.YEAR) == cdate.get(Calendar.YEAR)
                && now.get(Calendar.MONTH) == cdate.get(Calendar.MONTH)
                && now.get(Calendar.DATE) == cdate.get(Calendar.DATE);
    }

    public static ContentValues ConvertActivityContentValue(ActivityInfo activityInfo) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SlimContract.SlimDB.COLUMN_ACTIVITY_ID, activityInfo.getActivityID());
        contentValues.put(SlimContract.SlimDB.COLUMN_USER_ID, activityInfo.getUserID());
        contentValues.put(SlimContract.SlimDB.COLUMN_USER_EMAIL, activityInfo.getUserEmail());
        contentValues.put(SlimContract.SlimDB.COLUMN_ATIVITY_TYPE_ID, activityInfo.getActivityTypeID());
        contentValues.put(SlimContract.SlimDB.COLUMN_ACTIVITY_TIME, activityInfo.getActivityTime());
        contentValues.put(SlimContract.SlimDB.COLUMN_FOOD_ID, activityInfo.getFoodID());
        contentValues.put(SlimContract.SlimDB.COLUMN_VALUE_DECIMAL, activityInfo.getValueDecimal());
        contentValues.put(SlimContract.SlimDB.COLUMN_VALUE_INT, activityInfo.getValueInt());
        contentValues.put(SlimContract.SlimDB.COLUMN_VALUE_TEXT, activityInfo.getValueText());
        contentValues.put(SlimContract.SlimDB.COLUMN_HINT_ID, activityInfo.getHintID());
        contentValues.put(SlimContract.SlimDB.COLUMN_HINT_TEXT, activityInfo.getHint());
        contentValues.put(SlimContract.SlimDB.COLUMN_IND1, activityInfo.getInd1());
        contentValues.put(SlimContract.SlimDB.COLUMN_IND2, activityInfo.getInd2());
        return contentValues;
    }

    // Check token logic, change to method if it was required in future.
//    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // mAuth is your current firebase auth instance
//        user.getIdToken(true).addOnCompleteListener(this, new OnCompleteListener<GetTokenResult>() {
//        @Override
//        public void onComplete(@NonNull Task<GetTokenResult> task) {
//            if (task.isSuccessful()) {
//                Log.d(TAG, "token=" + task.getResult().getToken());
//            } else {
//                Log.e(TAG, "exception=" +task.getException().toString());
//            }
//        }
//    });
}
