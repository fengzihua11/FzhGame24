package com.fzh.game.ershi;

import cn.domob.android.ads.DomobAdEventListener;
import cn.domob.android.ads.DomobAdView;
import cn.domob.android.ads.DomobAdManager.ErrorCode;

import com.fzh.game.tool.UtilTool;
import com.fzh.game.view.Game24AnswerView;
import com.fzh.game.view.Game24View;
import com.fzh.game.view.Game24View.OnRectClickListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class FzhGame24Activity extends Activity implements OnRectClickListener {

	public static final String XMLNAME = "showdialog";
	public static final String AD_KEY = "showad";
	
	private static final String DOMOB_PUBLISHER_ID = "56OJyZrYuMOm5Tj/ZK";
    private static final String DOMOB_INLINE_PPID = "16TLwanaAc0hzY7r7gZ0F7-i";

	private Game24View gameView;
	private Game24AnswerView answerView;
	private LinearLayout domobParent;
	private DomobAdView mAdview320x50;
	private int showAd = 1;
	
	private DisplayMetrics mDisplay;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        getDisplay();
		setContentView(R.layout.main);
		makeView();
		if (getInt(1) == 1)
			openAdTip();
	}
	
	private void getDisplay() {
        Resources res = getResources();
        mDisplay = res.getDisplayMetrics();
        Log.d("fengzihua", "--" + mDisplay.toString());
	}

	public void makeView() {
		gameView = (Game24View) findViewById(R.id.gameView);
		gameView.setOnRectClickListener(this);
		answerView = (Game24AnswerView) findViewById(R.id.answerView);
		answerView.setOnRectClickListener(this);
		domobParent = (LinearLayout) findViewById(R.id.domobParent);
		
		mAdview320x50 = new DomobAdView(this, DOMOB_PUBLISHER_ID, DOMOB_INLINE_PPID, DomobAdView.INLINE_SIZE_728X90);
        mAdview320x50.setKeyword("game");
        mAdview320x50.setUserGender("male");
        mAdview320x50.setUserBirthdayStr("2000-08-08");
        mAdview320x50.setUserPostcode("123456");

        mAdview320x50.setAdEventListener(new DomobAdEventListener() {
                        
            @Override
            public void onDomobAdReturned(DomobAdView adView) {
                Log.i("DomobSDKDemo", "onDomobAdReturned");             
            }

            @Override
            public void onDomobAdOverlayPresented(DomobAdView adView) {
                Log.i("DomobSDKDemo", "overlayPresented");
            }

            @Override
            public void onDomobAdOverlayDismissed(DomobAdView adView) {
                Log.i("DomobSDKDemo", "Overrided be dismissed");                
            }

            @Override
            public void onDomobAdClicked(DomobAdView arg0) {
                Log.i("DomobSDKDemo", "onDomobAdClicked");              
            }

            @Override
            public void onDomobAdFailed(DomobAdView arg0, ErrorCode arg1) {
                Log.i("DomobSDKDemo", "onDomobAdFailed");               
            }

            @Override
            public void onDomobLeaveApplication(DomobAdView arg0) {
                Log.i("DomobSDKDemo", "onDomobLeaveApplication");               
            }

            @Override
            public Context onDomobAdRequiresCurrentContext() {
                return FzhGame24Activity.this;
            }
        });
        LinearLayout.LayoutParams layout=new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        domobParent.addView(mAdview320x50, layout);
	}

	public void pushInt(int value) {
		SharedPreferences.Editor editor = getSharedPreferences(XMLNAME,
				Context.MODE_WORLD_WRITEABLE).edit();
		editor.putInt(AD_KEY, value);
		editor.commit();
	}

	public int getInt(int defValue) {
		SharedPreferences pre = getSharedPreferences(XMLNAME,
				Context.MODE_WORLD_READABLE);
		showAd = pre.getInt(AD_KEY, defValue);
		return showAd;
	}

	public void onRectClick(int flag) {
		switch (flag) {
		case Game24View.EXIT_GAME:
			showCloseAppDailog();
			break;

		case Game24View.GAME_OVER:
			showGameAgaimDailog();
			break;

		case Game24View.SHOW_ANSWER:
			answerView.setVisibility(View.VISIBLE);
			answerView.setPicIds(gameView.getPic());
			gameView.setTouchable(false);
			break;

		case Game24View.CLOSE_ANSWER:
			answerView.setVisibility(View.GONE);
			gameView.setTouchable(true);
			break;
		}
	}

	protected void openAdTip() {
		final AlertDialog dialog = new AlertDialog.Builder(this).setIcon(
				R.drawable.icon).setTitle(R.string.ad_miss_icon).setMessage(
				R.string.ad_show_content).setPositiveButton(
				R.string.ad_noshow_miss_icon,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						pushInt(0);
						dialog.dismiss();
					}
				}).setNegativeButton(R.string.ad_show_miss_icon, null).create();
		dialog.show();
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_UP) {
			if (answerView.isShown()) {
				answerView.setVisibility(View.GONE);
				gameView.setTouchable(true);
				return true;
			} else {
				showCloseAppDailog();
				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}

	protected void showCloseAppDailog() {
		AlertDialog dialog = new AlertDialog.Builder(this).setIcon(
				R.drawable.icon).setTitle(R.string.login_out_title_tip)
				.setPositiveButton(R.string.login_out_sure,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}
						}).setNegativeButton(R.string.login_out_cancel, null)
				.create();
		dialog.show();
	}

	protected void showGameAgaimDailog() {
		AlertDialog dialog = new AlertDialog.Builder(this).setIcon(
				R.drawable.icon).setTitle(R.string.game_over_title_tip)
				.setPositiveButton(R.string.game_over_yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}
						}).setNegativeButton(R.string.game_over_again,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								gameView.again();
							}
						}).create();
		dialog.show();
	}

	protected void openGameDescription() {
		AlertDialog dialog = new AlertDialog.Builder(this).setIcon(
				R.drawable.icon).setTitle(R.string.game_description_icon)
				.setMessage(R.string.game_description).setNegativeButton(
						R.string.rule_sure, null).create();
		dialog.show();
	}

	private void getHelpBySms() {
		int ids[] = gameView.getFourNumber();
		if (ids == null) {
			Toast.makeText(this,
					UtilTool.getString(this, R.string.counting_tip_washing),
					Toast.LENGTH_SHORT).show();
			return;
		}
		String content = UtilTool.getString(this, R.string.msg_tip_title);
		for (int i = 0; i < ids.length; i++) {
			content += ids[i] + ", ";
		}
		content += UtilTool.getString(this, R.string.msg_tip_tip);
		Uri smsToUri = Uri.parse("smsto:");
		Intent mIntent = new Intent(Intent.ACTION_SENDTO, smsToUri);

		mIntent.putExtra("sms_body", content);// ���ŵ�����
		startActivity(mIntent);
	}

	protected void makeQuestionDailog() {
		View group = ((LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.questions, null);
		final EditText number1 = (EditText) group.findViewById(R.id.number1);
		final EditText number2 = (EditText) group.findViewById(R.id.number2);
		final EditText number3 = (EditText) group.findViewById(R.id.number3);
		final EditText number4 = (EditText) group.findViewById(R.id.number4);
		final AlertDialog dialog = new AlertDialog.Builder(this).setIcon(
				R.drawable.icon).setTitle(R.string.make_question_demo).setView(
				group).setNeutralButton(R.string.make_question_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						sendNumber(number1.getText().toString(), number2
								.getText().toString(), number3.getText()
								.toString(), number4.getText().toString());
					}
				}).setNegativeButton(R.string.make_question_cancel, null)
				.create();
		dialog.show();
	}

	public void sendNumber(String num1, String num2, String num3, String num4) {
		int[] numbers = new int[4];
		numbers[0] = getNumber(num1);
		if (numbers[0] == -1) {
			showMsg(R.string.number_wrong_tip);
			return;
		}
		numbers[1] = getNumber(num2);
		if (numbers[1] == -1) {
			showMsg(R.string.number_wrong_tip);
			return;
		}
		numbers[2] = getNumber(num3);
		if (numbers[2] == -1) {
			showMsg(R.string.number_wrong_tip);
			return;
		}
		numbers[3] = getNumber(num4);
		if (numbers[3] == -1) {
			showMsg(R.string.number_wrong_tip);
			return;
		}
		gameView.setPics(numbers);
	}

	public int getNumber(String num) {
		int value = -1;
		try {
			value = Integer.parseInt(num);
		} catch (Exception e) {
		}
		return value;
	}

	private void showMsg(int resId) {
		Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 1, getResources().getString(
				R.string.game_description_icon));
		menu.add(0, 2, 1, getResources().getString(R.string.test_other_people));
		menu
				.add(0, 3, 1, getResources().getString(
						R.string.make_question_demo));
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			openGameDescription();
			break;
		case 2:
			getHelpBySms();
			break;
		case 3:
			makeQuestionDailog();
			break;
		}
		return true;
	}

	protected void onDestroy() {
		super.onDestroy();
	}
	
	//////////================广告位回调====================//////////////
}