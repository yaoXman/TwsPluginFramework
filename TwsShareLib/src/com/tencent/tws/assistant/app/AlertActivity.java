/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tencent.tws.assistant.app;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;

import android.util.Log;

/**
 * An activity that follows the visual style of an AlertDialog.
 * 
 * @see #mAlert
 * @see #mAlertParams
 * @see #setupAlert()
 */
public abstract class AlertActivity extends Activity implements DialogInterface {

    private static final String LOG_TAG = AlertActivity.class.getName();
    
    /**
     * The model for the alert.
     * 
     * @see #mAlertParams
     */
    protected AlertController mAlert;

    /**
     * The parameters for the alert.
     */
    protected AlertController.AlertParams mAlertParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAlert = new AlertController(this, this, getWindow());
        mAlertParams = new AlertController.AlertParams(this);
    }

    public void cancel() {
        finish();
    }

    public void dismiss() {
        // This is called after the click, since we finish when handling the
        // click, don't do that again here.
        if (!isFinishing()) {
            finish();
        }
    }

    protected void setBottomDialog(boolean isBottom){
        if (isBottom) {
            mAlert = new AlertController(this, this, getWindow(), isBottom);
            getWindow().setGravity(Gravity.BOTTOM);
        }
    }

    /**
     * Sets up the alert, including applying the parameters to the alert model, and installing the alert's
     * content.
     * 
     * @see #mAlert
     * @see #mAlertParams
     */
    protected void setupAlert() {
        mAlertParams.apply(mAlert);
        mAlert.installContent();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAlert.onKeyDown(keyCode, event))
            return true;
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mAlert.onKeyUp(keyCode, event))
            return true;
        //return super.onKeyUp(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking()
                && !event.isCanceled()) {
        	finish();
 			
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		overridePendingTransition(0,0);
	}

}
