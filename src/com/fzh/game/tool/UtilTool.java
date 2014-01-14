package com.fzh.game.tool;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;

public class UtilTool {

	public static final void washPoke(ArrayList<Integer> pokes) {
		Collections.shuffle(pokes);
	}

	public static final int[] getResIdforCards(ArrayList<Integer> pokes,
			int begin, int number) {
		if (begin + number >= pokes.size())
			return null;
		int[] resIds = new int[number];
		for (int i = begin; i < begin + number && i < pokes.size(); i++) {
			resIds[i - begin] = pokes.get(i);
		}
		return resIds;
	}

	public static final int getPlugPosition(String str) {
		int index = str.indexOf('+');
		if (index >= 0)
			return index;
		index = str.indexOf('#');
		if (index >= 0)
			return index;
		index = str.indexOf('*');
		if (index >= 0)
			return index;
		index = str.indexOf('/');
		if (index >= 0)
			return index;
		return -1;
	}

	public static final String[] getEveryStr(String[] answerStr) {
		if (answerStr.length < 3)
			return null;
		String[] tempStr = new String[15];
		int indexPlug = -1;
		int indexEqual = -1;
		for (int i = 0; i < answerStr.length; i++) {
			indexPlug = getPlugPosition(answerStr[i]);
			indexEqual = answerStr[i].indexOf('=');			
			tempStr[i * 5 + 0] = answerStr[i].substring(0, indexPlug).trim();
			tempStr[i * 5 + 1] = answerStr[i].substring(indexPlug,
					indexPlug + 1).trim();
			tempStr[i * 5 + 2] = answerStr[i].substring(indexPlug + 1,
					indexEqual).trim();
			tempStr[i * 5 + 3] = answerStr[i].substring(indexEqual,
					indexEqual + 1).trim();
			tempStr[i * 5 + 4] = answerStr[i].substring(indexEqual + 1,
					answerStr[i].length()).trim();
		}
		return tempStr;
	}

	public static final int getIntByStr(String str, int defaultValue) {
		int i = defaultValue;
		try {
			i = Integer.parseInt(str);
		} catch (Exception e) {
		}
		return i;
	}
	
	public static String getString(Context context, int resId) {
		return context.getString(resId).toString();
	}
}