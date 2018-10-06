package chido.com.chido_chido;

import android.app.Activity;
import android.content.Intent;


/**
 * Created by james
 */

public class ChidoSDK {

    String customerId;
    String authKey;
    String authSecret;
    String userId;
    int REQUEST_CODE = 7197;

    public interface LoanResultCallback {
        void onSuccess();
        void onFailed(String msg);
    }

    LoanResultCallback loanResultCallback;

    public ChidoSDK(String userId, String customerId, String authKey, String authSecret) {
        this.customerId = customerId;
        this.authKey = authKey;
        this.authSecret = authSecret;
        this.userId = userId;
    }

    public void loanRequest(LoanResultCallback callback, Activity activity) {
        loanResultCallback = callback;
        Intent applyLoan = new Intent(activity, LoadAirtimeActivity.class);
        applyLoan.putExtra("userId", userId);
        applyLoan.putExtra("customerId", customerId);
        applyLoan.putExtra("authKey", authKey);
        applyLoan.putExtra("authSecret", authSecret);
        activity.startActivityForResult(applyLoan, REQUEST_CODE);
    }

    public void onResponse(int arg0, int arg1, Intent arg2) {
        if(arg0 == REQUEST_CODE) {
            if (arg1 == -1) {
                Boolean resp = arg2.getBooleanExtra("result", false);
                String error_msg = arg2.getStringExtra("error_msg");
                if (resp) {
                    loanResultCallback.onSuccess();
                } else {
                    loanResultCallback.onFailed(error_msg);
                }
            } else if (arg1 == 0) {
                loanResultCallback.onFailed("User cancelled operation");
            }
        }
    }


}
