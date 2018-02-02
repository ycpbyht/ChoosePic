package bclb.upload.album.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import bclb.upload.album.view.GenericProgressDialog;
import ycpb.upload.myapplication.R;

/**
 * Created by  on 2016/7/4.
 */
public class ActivityHelper {
    final static String TAG = ActivityHelper.class.getSimpleName();

    /**
     * 对应的Activity
     */
    private Activity mActivity;


    /**
     * 对话框帮助类
     */
    private DialogHelper mDialogHelper;

    public ActivityHelper(Activity activity) {
        mActivity = activity;
        mDialogHelper = new DialogHelper(mActivity);
    }

    public void finish() {
        mDialogHelper.dismissProgressDialog();
    }

    /**
     * 弹对话框
     *
     * @param title            标题
     * @param msg              消息
     * @param positive         确定
     * @param positiveListener 确定回调
     * @param negative         否定
     * @param negativeListener 否定回调
     */
    public void alert(String title, String msg, String positive,
                      DialogInterface.OnClickListener positiveListener, String negative,
                      DialogInterface.OnClickListener negativeListener) {
        mDialogHelper.alert(title, msg, positive, positiveListener, negative, negativeListener);
    }


    /**
     * 弹对话框
     *
     * @param title                    标题
     * @param msg                      消息
     * @param positive                 确定
     * @param positiveListener         确定回调
     * @param negative                 否定
     * @param negativeListener         否定回调
     * @param isCanceledOnTouchOutside 外部是否可点取消
     */
    public void alert(String title, String msg, String positive,
                      DialogInterface.OnClickListener positiveListener, String negative,
                      DialogInterface.OnClickListener negativeListener,
                      Boolean isCanceledOnTouchOutside) {
        mDialogHelper.alert(title, msg, positive, positiveListener, negative, negativeListener,
                isCanceledOnTouchOutside);
    }

    /**
     * TOAST
     *
     * @param msg    消息
     * @param period 时长
     */
    public void toast(String msg, int period) {
        mDialogHelper.toast(msg, period);
    }

    /**
     * 显示进度对话框
     *
     * @param msg 消息
     */
    public void showProgressDialog(String msg) {
        mDialogHelper.showProgressDialog(msg);
    }

    /**
     * 显示可取消的进度对话框
     *
     * @param msg 消息
     */
    public void showProgressDialog(final String msg, final boolean cancelable,
                                   final DialogInterface.OnCancelListener cancelListener) {
        mDialogHelper.showProgressDialog(msg, cancelable, cancelListener, true);
    }

    public void dismissProgressDialog() {
        mDialogHelper.dismissProgressDialog();
    }

    public class DialogHelper {
        private Activity mActivity;
        private AlertDialog mAlertDialog;
        private Toast mToast;

        public DialogHelper(Activity activity) {
            mActivity = activity;
        }

        /**
         * 弹对话框
         *
         * @param title            标题
         * @param msg              消息
         * @param positive         确定
         * @param positiveListener 确定回调
         * @param negative         否定
         * @param negativeListener 否定回调
         */
        public void alert(final String title, final String msg, final String positive,
                          final DialogInterface.OnClickListener positiveListener,
                          final String negative, final DialogInterface.OnClickListener negativeListener) {
            alert(title, msg, positive, positiveListener, negative, negativeListener, false);
        }

        /**
         * 弹对话框
         *
         * @param title                    标题
         * @param msg                      消息
         * @param positive                 确定
         * @param positiveListener         确定回调
         * @param negative                 否定
         * @param negativeListener         否定回调
         * @param isCanceledOnTouchOutside 是否可以点击外围框
         */
        public void alert(final String title, final String msg, final String positive,
                          final DialogInterface.OnClickListener positiveListener,
                          final String negative,
                          final DialogInterface.OnClickListener negativeListener,
                          final Boolean isCanceledOnTouchOutside) {
            dismissProgressDialog();

            mActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mActivity == null || mActivity.isFinishing()) {
                        return;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    if (title != null) {
                        builder.setTitle(title);
                    }
                    if (msg != null) {
                        builder.setMessage(msg);
                    }
                    if (positive != null) {
                        builder.setPositiveButton(positive, positiveListener);
                    }
                    if (negative != null) {
                        builder.setNegativeButton(negative, negativeListener);
                    }
                    mAlertDialog = builder.show();
                    mAlertDialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
                    mAlertDialog.setCancelable(false);
                }
            });
        }

        /**
         * TOAST
         *
         * @param msg    消息
         * @param period 时长
         */
        public void toast(final String msg, final int period) {
            mActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mToast = new Toast(mActivity);
                    View view = LayoutInflater.from(mActivity).inflate(
                            R.layout.view_transient_notification, null);
                    TextView tv = (TextView) view.findViewById(android.R.id.message);
                    tv.setText(msg);
                    mToast.setView(view);
                    mToast.setDuration(period);

                    mToast.setGravity(Gravity.CENTER, 0, 0);
                    mToast.show();
                }
            });
        }

        /**
         * 显示对话框
         *
         * @param showProgressBar 是否显示圈圈
         * @param msg             对话框信息
         */
        public void showProgressDialog(boolean showProgressBar, String msg) {
            showProgressDialog(msg, true, null, showProgressBar);
        }

        /**
         * 显示进度对话框
         *
         * @param msg 消息
         */
        public void showProgressDialog(final String msg) {
            showProgressDialog(msg, true, null, true);
        }

        /**
         * 显示可取消的进度对话框
         *
         * @param msg 消息
         */
        public void showProgressDialog(final String msg, final boolean cancelable,
                                       final DialogInterface.OnCancelListener cancelListener,
                                       final boolean showProgressBar) {
            if (mAlertDialog != null&&mAlertDialog.isShowing()) {
                    return;
            }
            dismissProgressDialog();

            mActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mActivity == null || mActivity.isFinishing()) {
                        return;
                    }

                    mAlertDialog = new GenericProgressDialog(mActivity);
                    mAlertDialog.setMessage(msg);
                    ((GenericProgressDialog) mAlertDialog).setProgressVisiable(showProgressBar);
                    mAlertDialog.setCancelable(cancelable);
                    mAlertDialog.setOnCancelListener(cancelListener);

                    mAlertDialog.show();

                    mAlertDialog.setCanceledOnTouchOutside(false);
                }
            });
        }

        public void dismissProgressDialog() {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mAlertDialog != null && mAlertDialog.isShowing() && !mActivity.isFinishing()) {
                        mAlertDialog.dismiss();
                        mAlertDialog = null;
                    }
                }
            });
        }

    }

}
