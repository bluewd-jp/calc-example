package jp.co.umenetts;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        var sc = new Scanner(System.in);
        var calculator = new Calculator();

        Outer:
        while (true) {
            var text = "";
            var hasError = false;
            do {
                System.out.print("数式を入力してください（終了する場合は空入力）：");
                text = sc.nextLine();

                if (text.equals("")) break Outer;

                hasError = !calculator.validate(text);
            } while (hasError);

            System.out.println("計算結果：" + calculator.calculate(text));
        }

        System.out.println("計算処理を終了します。");
    }
}
