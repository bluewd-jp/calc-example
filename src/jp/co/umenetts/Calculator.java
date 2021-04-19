package jp.co.umenetts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Calculator {

    // 公開関数 ///////////////////////////////////////////////////////////
    /**
     * 使用者に公開している計算処理
     * @param expression 数式の文字列
     * @return 計算結果
     */
    public BigDecimal calculate(Expression expression) {
        try {
            return _calculate(expression);
        } catch (NotValidatedException e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }

    // 非公開関数 ////////////////////////////////////////////////////////
    /**
     * メインの計算処理
     * @param expression Expression
     * @return 計算結果
     */
    private BigDecimal _calculate(Expression expression)
            throws NotValidatedException {
        var expressionList = expression.toList();

        // 括弧の計算
        _calculateBrackets(expressionList);

        // 乗除の計算
        _calculateMulDiv(expressionList);

        // 和差の計算
        _calculateAddSub(expressionList);

        // ゼロ除算があった場合、空のリストが来るようになっている。このときは計算できないためゼロを返す。
        if (expressionList.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(expressionList.get(0));
    }


    /**
     * 括弧内の計算処理。括弧がなくなるまで計算する。
     * @param expressionList 数式のリスト
     */
    private void _calculateBrackets(List<String> expressionList) {
        // 括弧がなければ終了
        if (!ValidationUtils.hasBrackets(expressionList)) return;

        // 括弧があれば、括弧の中の式を抽出
        int leftBracketIndex = expressionList.indexOf("(");
        int rightBracketIndex = expressionList.lastIndexOf(")");
        var tmpList = expressionList.subList(leftBracketIndex + 1, rightBracketIndex);

        // 抽出した式の中に括弧があれば再帰呼び出し
        if (ValidationUtils.hasBrackets(tmpList)) {
             _calculateBrackets(tmpList);
        }

        // 括弧がなければ計算
        // subListで抽出したリストは抽出元リストと値を共有しているので、この処理で 1 * ( 計算結果 ) / 3 の状態になる
        try {
            _calculate(new Expression(tmpList));
        } catch (NotValidatedException e) {
            // 当例外はExpressionインスタンスの_expressionListフィールドに値が設定されていないときに発生する。
            // リストを直接引数にとってインスタンス化した場合は事実上発生しないため、
            // 想定外のときのためにスタックトレースを出力する以外に処理はおこなわない。
            e.printStackTrace();
        }

        // 左右の括弧を削除
        expressionList.remove(leftBracketIndex); // この結果 1 * 計算結果 ) / 3 の状態になる
        expressionList.remove(leftBracketIndex + 1); // この結果 1 * 計算結果 / 3 の状態になる
    }


    /**
     * 乗除の計算処理
     * @param expressionList 数式のリスト
     */
    private void _calculateMulDiv(List<String> expressionList) {
        for (int i = 0; i < expressionList.size() ; /* not increment */) {
            var item = expressionList.get(i);
            var left = BigDecimal.ZERO;
            var right = BigDecimal.ZERO;

            switch (item) {
                case "*":
                    left = new BigDecimal(expressionList.get(i - 1));
                    right = new BigDecimal(expressionList.get(i + 1));
                    expressionList.set(i - 1, left.multiply(right).toString());
                    expressionList.remove(i); // 演算子を削除
                    expressionList.remove(i); // 右の項を削除

                    break;

                case "/":
                    left = new BigDecimal(expressionList.get(i - 1));
                    right = new BigDecimal(expressionList.get(i + 1));

                    if (right.equals(BigDecimal.ZERO)) {
                        // ゼロ除算があった場合は計算できないため、空のリストを返却
                        System.out.println("ゼロによる除算が確認されたため計算を中止します。");
                    }

                    expressionList.set(i - 1, left.divide(right).toString());
                    expressionList.remove(i); // 演算子を削除
                    expressionList.remove(i); // 右の項を削除

                    break;

                default:
                    i++; // 要素の置き換えをおこなわなかったときのみ増分
                    break;
            }
        }
    }


    /**
     * 和差の計算処理
     * @param expressionList 数式のリスト
     */
    private void _calculateAddSub(List<String> expressionList) {
        for (int i = 0; i < expressionList.size() ; /* not increment*/) {
            var item = expressionList.get(i);
            var left = BigDecimal.ZERO;
            var right = BigDecimal.ZERO;

            switch (item) {
                case "+":
                    left = new BigDecimal(expressionList.get(i - 1));
                    right = new BigDecimal(expressionList.get(i + 1));

                    expressionList.set(i - 1, left.add(right).toString());
                    expressionList.remove(i); // 演算子を削除
                    expressionList.remove(i); // 右の項を削除
                    break;

                case "-":
                    left = new BigDecimal(expressionList.get(i - 1));
                    right = new BigDecimal(expressionList.get(i + 1));

                    expressionList.set(i - 1, left.subtract(right).toString());
                    expressionList.remove(i); // 演算子を削除
                    expressionList.remove(i); // 右の項を削除
                    break;

                default:
                    i++; // 要素の置き換えをおこなわなかったときのみ増分
                    break;
            }
        }
    }
}
