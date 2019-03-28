package pait.com.kkcabagent.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import pait.com.kkcabagent.constant.Constant

class MySMSBroadcastReceiver1 : BroadcastReceiver() {
    private val codePattern = "(\\d{6})".toRegex()

    internal var receiver: OTPReceiveListener? = null

    fun initOTPListener(_receiver: OTPReceiveListener) {
        this.receiver = _receiver
        Constant.showLog("In initOTPListener")
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status = extras!!.get(SmsRetriever.EXTRA_STATUS) as Status

            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {

                    // Get SMS message contents
                    val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                    /*val code: MatchResult? = codePattern.find(message)
                    if (code?.value != null) {
                        Constant.showLog(code.toString());
                    } else {
                        Constant.showLog("Error");
                    }*/
                    val message1 = message.replace("<#> Your OTP code is: ", "").split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                    receiver!!.onOTPReceived(message1)

                }
                CommonStatusCodes.TIMEOUT -> {
                    Constant.showLog("TimeOut");
                }
            }

        }
    }

    interface OTPReceiveListener {
        fun onOTPReceived(otp: String)
        fun onOTPTimeOut()
    }
}