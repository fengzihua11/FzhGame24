package com.fzh.game.tool;

/**
 * 计算数据
 */
public class NNumCalculateToM {
    private final static int PRECISION = 0;
    private final static int NUM = 4;
    private final static int RESULT = 24;
    private int number[] = null;
    private String[] expression = new String[NUM - 1];

    public String[] getAnswerString(int[] numbers) {
        number = numbers;
        if (circule(NUM))
            return expression;
        return null;
    }

    private boolean circule(int n) {
        if (n == 1) {
            if (Math.abs(number[0] - RESULT) == PRECISION) {
                return true;
            } else {
                return false;
            }
        }
        for (int i = 0; i < n; ++i)
            for (int j = i + 1; j < n; ++j) {
                int numi, numj;
                numi = number[i];
                numj = number[j];
                number[j] = number[n - 1];
                // �ӵĴ���
                number[i] = numi + numj;
                expression[NUM - n] = numi + "+" + numj + "=" + number[i];
                if (circule(n - 1))
                    return true;

                // ���Ĵ������������expi-expj,expj-expi
                number[i] = numi - numj;
                expression[NUM - n] = numi + "-" + numj + "=" + number[i];
                if (circule(n - 1))
                    return true;

                number[i] = numj - numi;
                expression[NUM - n] = numj + "-" + numi + "=" + number[i];
                if (circule(n - 1))
                    return true;

                // �˵Ĵ���
                number[i] = numi * numj;
                expression[NUM - n] = numi + "*" + numj + "=" + number[i];
                if (circule(n - 1))
                    return true;

                // ���Ĵ������������expi/expj,expj/expi
                if (numj != PRECISION && (numi % numj) == 0) {
                    number[i] = numi / numj;
                    expression[NUM - n] = numi + "/" + numj + "=" + number[i];
                    if (circule(n - 1))
                        return true;
                }
                if (numi != PRECISION && (numj % numi) == 0) {
                    number[i] = numj / numi;
                    expression[NUM - n] = numj + "/" + numi + "=" + number[i];
                    if (circule(n - 1))
                        return true;
                }
                // �ָ�����
                number[i] = numi;
                number[j] = numj;
            }
        return false;
    }
}