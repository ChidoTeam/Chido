package chido.com.chido_chido;

/*
        11-14 03:23:02.287 18109-18109/chido.com.chido E/USSDService: onAccessibilityEvent android.app.ProgressDialog Event Text [USSD code running] Source android.widget.FrameLayout Text null
        11-14 03:23:02.307 18109-18109/chido.com.chido E/USSDService:  null
        11-14 03:23:07.051 18109-18109/chido.com.chido E/USSDService: onAccessibilityEvent android.app.AlertDialog Event Text [Input Number to send to:, Cancel, Send] Source android.widget.FrameLayout Text null
        11-14 03:23:07.076 18109-18109/chido.com.chido E/USSDService: NOt null
        11-14 03:23:07.153 18109-18109/chido.com.chido D/USSDService: Input Number to send to:
        11-14 03:23:07.200 18109-18109/chido.com.chido E/USSDService: onAccessibilityEvent android.widget.EditText Event Text [] Source android.widget.EditText Text 0788718756
        11-14 03:23:07.201 18109-18109/chido.com.chido E/USSDService:  null
        11-14 03:23:07.206 18109-18109/chido.com.chido E/USSDService: onAccessibilityEvent android.widget.FrameLayout Event Text [] Source android.widget.FrameLayout Text null
        null
        11-14 03:23:07.237 18109-18109/chido.com.chido E/USSDService: onAccessibilityEvent android.app.ProgressDialog Event Text [USSD code running] Source android.widget.FrameLayout Text null
        11-14 03:23:07.251 18109-18109/chido.com.chido E/USSDService:  null
        11-14 03:23:08.041 18109-18109/chido.com.chido E/USSDService: onAccessibilityEvent android.app.AlertDialog Event Text [Enter amount to send:, Cancel, Send] Source android.widget.FrameLayout Text null
        11-14 03:23:08.066 18109-18109/chido.com.chido E/USSDService: NOt null
        11-14 03:23:08.146 18109-18109/chido.com.chido D/USSDService: Enter amount to send:
        11-14 03:23:08.190 18109-18109/chido.com.chido E/USSDService: onAccessibilityEvent android.widget.EditText Event Text [] Source android.widget.EditText Text 0788718756
        11-14 03:23:08.191 18109-18109/chido.com.chido E/USSDService:  null
        11-14 03:23:08.197 18109-18109/chido.com.chido E/USSDService: onAccessibilityEvent android.widget.FrameLayout Event Text [] Source android.widget.FrameLayout Text null
        null
        11-14 03:23:08.202 18109-18109/chido.com.chido E/USSDService: onAccessibilityEvent android.app.ProgressDialog Event Text [USSD code running] Source android.widget.FrameLayout Text null
        11-14 03:23:08.207 18109-18109/chido.com.chido E/USSDService:  null
        11-14 03:23:10.159 18109-18109/chido.com.chido E/USSDService: onAccessibilityEvent android.app.AlertDialog Event Text [Y'ello. Your request is being processed. Please wait for a confirmation message. Thank You., OK] Source android.widget.FrameLayout Text null
        11-14 03:23:10.165 18109-18109/chido.com.chido E/USSDService:  null
        11-14 03:23:10.184 18109-18109/chido.com.chido D/USSDService: Y'ello. Your request is being processed. Please wait for a confirmation message. Thank You.
        11-14 03:23:10.201 18109-18109/chido.com.chido I/Timeline: Timeline: Activity_idle id: android.os.BinderProxy@af978d2 time:4648168
*/

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

public class USSDService extends AccessibilityService {

    public static String TAG = USSDService.class.getSimpleName();


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {


        if(ChidoUtil.getPrefBoolean(ChidoUtil.USSD_IN_SESSION,USSDService.this)) {
            Toast.makeText(this, "onAccessibilityEvent", Toast.LENGTH_SHORT).show();
            AccessibilityNodeInfo source = event.getSource();
            Log.e(TAG, "onAccessibilityEvent " + event.getClassName() + " Event Text " + event.getText() + " Source " + source.getClassName() + " Text " + source.getText());

            List<CharSequence>  eventText =  event.getText();
            String text = processUSSDText(eventText);


            if (String.valueOf(event.getClassName()).contains("android.app.ProgressDialog")) {
               // startService(new Intent(USSDService.this, ProgressService.class));
                Toast.makeText(this, "should start hiding", Toast.LENGTH_SHORT).show();
            }

            if(text != null && text.contains("Input Number to send to:")){
                ChidoUtil.savePrefBoolean(ChidoUtil.ENTER_PHONE_NUMBER,true,USSDService.this);
                ChidoUtil.savePrefBoolean(ChidoUtil.ENTER_AMOUNT,false,USSDService.this);

            } else if(text != null && text.contains("Enter amount to send:")){
                ChidoUtil.savePrefBoolean(ChidoUtil.ENTER_AMOUNT,true,USSDService.this);
                ChidoUtil.savePrefBoolean(ChidoUtil.ENTER_PHONE_NUMBER,false,USSDService.this);
            }else{
                if (String.valueOf(event.getClassName()).contains("android.app.AlertDialog Event")) {
                  //   stopService(new Intent(USSDService.this, ProgressService.class));
                    Toast.makeText(this, "should stop hiding", Toast.LENGTH_SHORT).show();
                }
            }

          //  if(String.valueOf(event.getClassName()).contains("EditText")) {

                if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && (source == null || !source.getClassName().equals("android.widget.EditText"))) {
                    if (ChidoUtil.getPrefBoolean(ChidoUtil.ENTER_PHONE_NUMBER, USSDService.this)) {
                        ChidoUtil.savePrefBoolean(ChidoUtil.ENTER_PHONE_NUMBER, false, USSDService.this);
                        enterValue(source, ChidoUtil.getPref(ChidoUtil.USSD_PHONE_NUMBER, "", USSDService.this));
                        clickSend(source);
                    }

                    if (ChidoUtil.getPrefBoolean(ChidoUtil.ENTER_AMOUNT, USSDService.this)) {
                        ChidoUtil.savePrefBoolean(ChidoUtil.ENTER_AMOUNT, false, USSDService.this);
                        ChidoUtil.savePrefBoolean(ChidoUtil.USSD_IN_SESSION, false, USSDService.this);
                        enterValue(source, ChidoUtil.getPref(ChidoUtil.USSD_AMOUNT, "", USSDService.this));
                        clickSend(source);
                    }
                }
          //  }
        }

        /* if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && !event.getClassName().equals("android.app.AlertDialog")) { // android.app.AlertDialog is the standard but not for all phones  */
/*
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && !String.valueOf(event.getClassName()).contains("AlertDialog")) {
            Toast.makeText(this, "AlertDialog " + source.getText(), Toast.LENGTH_SHORT).show();
            return;
        }
        if(event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && (source == null || !source.getClassName().equals("android.widget.TextView"))) {
            Toast.makeText(this, "TextView " +  source.getText(), Toast.LENGTH_SHORT).show();
            return;
        }
        if(event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && (source == null || !source.getClassName().equals("android.widget.EditText"))) {
            Toast.makeText(this, "EditText " + source.getText(), Toast.LENGTH_SHORT).show();
            return;
        }
        if(event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && TextUtils.isEmpty(source.getText())) {
            return;
        }

        List<CharSequence> eventText;

        if(event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            eventText = event.getText();
        } else {
            eventText = Collections.singletonList(source.getText());
        }

        String text = processUSSDText(eventText);

        if( TextUtils.isEmpty(text) ) return;

        // Close dialog
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            performGlobalAction(GLOBAL_ACTION_BACK); // This works on 4.1+ only
        }

        Log.d(TAG, text);
        // Handle USSD response here
*/

    }

    private String processUSSDText(List<CharSequence> eventText) {
        for (CharSequence s : eventText) {
            String text = String.valueOf(s);
            // Return text if text is the expected ussd response
            if( true ) {
                return text;
            }
        }
        return null;
    }

    private void enterValue( AccessibilityNodeInfo source,String Value){
        AccessibilityNodeInfo nodeInput = null;
        Log.e(TAG, "changing value to: " + Value);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            nodeInput = source.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
            Bundle bundle = new Bundle();
            bundle.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,Value);
            if (nodeInput != null) {
                Log.e(TAG, "NOt null");
                nodeInput.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    nodeInput.refresh();
                }
            } else {
                Log.e(TAG, " null");
            }
        }
    }

   private void setValue( AccessibilityNodeInfo source,String from, String to){
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
           List<AccessibilityNodeInfo> nodeInfos = source.findAccessibilityNodeInfosByText(from);
           Log.e(TAG, "changing value to: " + nodeInfos);
           AccessibilityNodeInfo nodeInput = nodeInfos.get(0);
           Log.e(TAG, "changing value to: " + nodeInput);
           Bundle bundle = new Bundle();
           bundle.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,to);
           if (nodeInput != null) {
               Log.e(TAG, "NOt null");
           //    nodeInput.setVisibleToUser(false);
               nodeInput.performAction(AccessibilityNodeInfo.ACTION_CLEAR_SELECTION, bundle);
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                   nodeInput.refresh();
               }
           } else {
               Log.e(TAG, " null");
           }
       }
   }

    private void clickSend(AccessibilityNodeInfo source){

        List<AccessibilityNodeInfo> list = source.findAccessibilityNodeInfosByText("Send");
        for (AccessibilityNodeInfo node : list) {
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "onServiceConnected");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.packageNames = new String[]{"com.android.phone"};
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);
        Intent applyLoan = new Intent(USSDService.this, LoadAirtimeActivity.class);
        applyLoan.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(applyLoan);

    }
}