package com.yuccaworld.yuccaslim.utilities;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.text.format.DateUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.yuccaworld.yuccaslim.model.ActivityInfo;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Yung on 9/3/2017.
 */

public class SlimUtils {
    public static String gUid;
    public static String gUserEmail;
    public static ActivityInfo genFakeActivityInfo() {
        ActivityInfo activityInfo = new ActivityInfo();
        activityInfo.setHint("Dummy Hint");
        long now = System.currentTimeMillis();
        return activityInfo;
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
