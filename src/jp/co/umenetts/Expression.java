package jp.co.umenetts;

import java.util.ArrayList;
import java.util.List;

public class Expression {
    private final String _expression;
    private List<String> _expressionList;


    /**
     * 入力された数式文字列をもとにインスタンスを生成するコンストラクタ。
     * @param expression 数式インスタンス
     */
    public Expression(String expression) {
        this._expression = expression;
    }


    /**
     * リスト化した数式をもとにインスタンスを生成するコンストラクタ。
     * @param expressionList リスト化した数式
     */
    public Expression(List<String> expressionList) {
        this._expression = "";
        this._expressionList = expressionList;
    }

    /**
     * 数式が適正かどうかを判定する。
     *
     * @return 数式文字列に不正な値があればFalseを返却。妥当な値であればTrueを返却。
     */
    public boolean isValid() {
        // 空入力チェック
        if (_expression == null || _expression.isEmpty()) {
            System.out.println("数式が未入力です。");
            return false;
        }

        // 文字種チェック
        for (int i = 0; i < _expression.length(); i++) {
            var item = _expression.substring(i, i + 1);

            if (!ValidationUtils.isOperator(item)
                    && !ValidationUtils.isNumberCharacter(item)
                    && !ValidationUtils.isDecimalPoint(item)
                    && !ValidationUtils.isBrackets(item)) {
                System.out.println("数式に不適切な文字が含まれています：" + item);
                return false;
            }
        }

        _createExpressionList();

        // 括弧の対応関係チェック
        if (!ValidationUtils.isValidBrackets(_expressionList)) {
            System.out.println("括弧の対応関係が不正です。");
            _expressionList = null;
            return false;
        }

        // 空括弧のチェック
        if (ValidationUtils.hasEmptyBrackets(_expressionList)) {
            System.out.println("空の括弧が使用されています。");
            _expressionList = null;
            return false;
        }

        // 数式全体の形式チェック
        if (!ValidationUtils.isValidExpression(_expressionList)) {
            System.out.println("不正な数式です。");
            _expressionList = null;
            return false;
        }

        return true;
    }

    /**
     * リスト化した数式を抽出する処理。
     *
     * 実際にはValidation時に作成したリストをそのまま返却するだけ。
     * もし_expressionListがNullなら、バリデーションが行われていないのでエラー。
     *
     * @return 数式のリスト
     * @throws NotValidatedException
     */
    public List<String> toList()
            throws NotValidatedException {
        if (_expressionList == null) {
            throw new NotValidatedException("数式のバリデーションがおこなわれていません。");
        }
        return _expressionList;
    }

    /**
     * 入力された数式をリスト化する処理
     */
    private void _createExpressionList() {
        var numStr = "";
        _expressionList = new ArrayList<String>();

        for (int i = 0; i < _expression.length(); i++) {
            var item = _expression.substring(i, i + 1);

            if (ValidationUtils.isNumberCharacter(item) || ValidationUtils.isDecimalPoint(item)) {
                numStr += item;
            } else if (ValidationUtils.isOperator(item)) {
                if (!numStr.isEmpty()) {
                    _expressionList.add(numStr);
                    numStr = "";
                }
                _expressionList.add(item);
            } else if (ValidationUtils.isLeftBracket(item)) {
                _expressionList.add(item);
            } else if (ValidationUtils.isRightBracket(item)) {
                if (!numStr.isEmpty()) {
                    _expressionList.add(numStr);
                    numStr = "";
                }
                _expressionList.add(item);
            }
        }

        // 一番最後の数値文字の追加
        _expressionList.add(numStr);
    }
}
