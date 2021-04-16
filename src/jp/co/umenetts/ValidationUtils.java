package jp.co.umenetts;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class ValidationUtils {
    /**
     * 文字が数字文字かどうかを判定する。
     * @param s １文字の文字列
     * @return 数字文字ならTrue。そうでなければFalse。
     */
    public static boolean isNumberCharacter(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    /**
     * 演算子かどうかを判定する。
     * @param s １文字の文字列
     * @return 演算子ならTrue。そうでなければFalse。
     */
    public static boolean isOperator(String s) {
       switch (s) {
           case "+":
           case "-":
           case "*":
           case "/":
               return true;
       }
       return false;
    }


    /**
     * 小数点かどうかを判定する。
     * @param s １文字の文字列
     * @return 小数点ならTrue。そうでなければFalse。
     */
    public static boolean isDecimalPoint(String s) {
        return s.equals(".");
    }


    /**
     * 左括弧（かどうかを判定する。
     * @param s １文字の文字列
     * @return 左括弧ならTrue。そうでなければFalse。
     */
    public static boolean isLeftBracket(String s) {
        return s.equals("(");
    }


    /**
     * 右括弧）かどうかを判定する。
     * @param s １文字の文字列
     * @return 右括弧ならTrue。そうでなければFalse。
     */
    public static boolean isRightBracket(String s) {
        return s.equals(")");
    }


    /**
     * 括弧かどうかを判定する。
     * @param s １文字の文字列
     * @return 括弧であればTrue。そうでなければFalse。
     */
    public static boolean isBrackets(String s) {
        return isLeftBracket(s) || isRightBracket(s);
    }


    /**
     * 空の括弧がないかを確認する。括弧の対応関係はここでは検証しない。
     *
     * @param expressionList 数式のリスト
     * @return 空のカッコがあればTrue。なければFalse。
     */
    public static boolean hasEmptyBrackets(List<String> expressionList) {
        var bracketFlag = false;
        for (var item : expressionList) {
            if (isLeftBracket(item)) {
                bracketFlag = true;
            }
            else if (isRightBracket(item) && bracketFlag) {
                // 右括弧がみつかり、かつ左括弧との間に項目がなかった場合はエラー
                return true;
            }
            else {
                bracketFlag = false; // 括弧以外が見つかればFalseに戻す
            }
        }

        // 問題がなければFalse
        return false;
    }


    /**
     * 式に括弧が存在するかを検証する。
     * 対応関係が保証されていることを前提に、左括弧があるかどうかで見る。
     *
     * @param expressionList 数式のリスト
     * @return 左括弧があればTrue。なければFalse。
     */
    public static boolean hasBrackets(List<String> expressionList) {
        return expressionList
                .stream()
                .anyMatch(ValidationUtils::isLeftBracket);
    }


    /**
     * 括弧の開始と終了の対応関係をチェックする。
     *
     * @param expressionList 数式のリスト
     * @return 左括弧（ を見つけたらカウントアップ、右括弧） を見つけたらカウントダウンをおこない、
     *         途中で値がマイナスになったり、最終的にカウントが0でなければエラー（False）とする。
     */
    public static boolean isValidBrackets(List<String> expressionList) {
        int bracketsCount = 0;
        for(var item : expressionList) {
            if (isLeftBracket(item)) {
                bracketsCount += 1;
            }
            else if (isRightBracket(item)) {
                bracketsCount -= 1;
            }

            // 途中で値がマイナスになった場合は、余計な右括弧が存在する、ということになるのでエラー
            if (bracketsCount < 0) {
                return false;
            }
        }

        // 最終的にカウントがゼロにならない場合は、括弧の対応関係が崩れていることになるのでエラー
        if (bracketsCount != 0) {
            return false;
        }

        // カウントがゼロになった場合は問題なし
        return true;
    }


    /**
     * 数式の構造が 項 演算子 項 演算子 ... の形式になっているかを判定する。
     * 括弧は判定の上で邪魔なので、括弧を除去したリストを作成して検証する。
     *
     * @param expressionList 数式のリスト
     * @return 項 演算子 項 演算子 ... という正しい構造になっていればTrue。不正な構造であればFalse。
     */
    public static boolean isValidExpression(List<String> expressionList) {
        // 括弧を除去したリストを作成（括弧はここでは検証しない）
        var tmpList =
                expressionList.stream()
                .filter(item -> !isBrackets(item))
                .collect(Collectors.toList());

        // 括弧を除いた上で、偶数項・奇数項を確認
        for (int i = 0; i < tmpList.size(); i++) {
            var item = tmpList.get(i);

            if (i % 2 == 0) {
                // 偶数項はオペランド（数値文字）である必要がある
                if (!isValidOperand(item)) {
                    return false;
                }
            }
            else {
                // 奇数項はオペレータ（演算子）である必要がある
                if (!isOperator(item)) {
                    return false;
                }
            }
        }

        return true;
    }


    /**
     * 文字列がオペランドとして適切なものかを判定する。
     * 小数点が複数ある場合等を検出。
     *
     * @param operand 数値文字列
     * @return 数値文字列として適切ならTrue。不適切ならFalse。
     */
    public static boolean isValidOperand(String operand) {
        try {
            new BigDecimal(operand);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


}
