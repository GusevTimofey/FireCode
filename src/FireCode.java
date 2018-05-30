import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class FireCode {

    public static void main(String[] args) {
        FireCode fireCode = new FireCode();

        Polynom p_polynom = new Polynom("x^7 + 1");
        Polynom f_polynom = new Polynom("x^4 + x^1 + 1");
        Polynom g_polynom = p_polynom.mul(f_polynom);

        System.out.println("\np(x) = " + p_polynom);
        System.out.println("f(x) = " + f_polynom);
        System.out.println("g(x) = " + g_polynom + "\n");


        System.out.println("Проверим условие, что p(x) не делиться на f(x) без остатка:");
        Polynom checkDiv_polynom = p_polynom.div(f_polynom).takeModPart();
        if (checkDiv_polynom.getPolynomMap().size() != 0)
            System.out.println("Остаток от деления равен: " + checkDiv_polynom + ", следовательно условие выполнено.");
        else {
            System.out.println("Выбраны не верные данные!");
            return;
        }


        System.out.println("\nВычислим длину кодового слова n через период g(x)." +
                "\nЗа параметр q возьмем степень равную степени g(x):");
        int q = g_polynom.getPolynomMap().firstKey();
        int n;
        while (true) {
            String polynomSting = "x^" + q + " + 1";
            Polynom checkN_polynom = new Polynom(polynomSting);
            Polynom Nposition_polynom = checkN_polynom.div(g_polynom).takeModPart();
            if (Nposition_polynom.getPolynomMap().size() == 0) {
                n = q;
                break;
            }
            q++;
        }
        System.out.println("Искомая длинна равна: " + n);


        System.out.println("\nПроверим правильность нахождения длинны другим способом." +
                "\nВычислим параметр n, использую формулу: n = НОК(период f(x), 2b - 1)." +
                "\nВычислим период f(x). Параметр j - начальная степень полинома, который будет делиться на f(x).");
        int j = p_polynom.getPolynomMap().firstKey() + 1;
        int len;
        int bCoeff = 7;
        while (true) {
            String polynomSting = "x^" + j + " + 1";
            Polynom checkN_polynom = new Polynom(polynomSting);
            Polynom Nposition_polynom = checkN_polynom.div(f_polynom).takeModPart();
            if (Nposition_polynom.getPolynomMap().size() == 0) {
                len = j;
                break;
            }
            j++;
        }
        System.out.println("Период f(x): " + len);
        int LCM = fireCode.lcm(bCoeff, len);
        System.out.println("Длина n равна: " + LCM);


        System.out.println("\nПроверим, совпал ли параметр n, вычисленный двумя разными способами:");
        if (LCM == n)
            System.out.println("Найденные длины различными способами совпали. Итоговое значение параметра n: " + LCM);
        else {
            System.out.println("Параметр n не совпал, перепроверьте внесенные данные!");
            return;
        }


        int k = n - g_polynom.getPolynomMap().firstKey();
        System.out.println("\nЧисло информационных символов k равно: " + k);


        System.out.println("\n\nКодирование!" +
                "\n\nКодирование будет происходить с делением на g(x).\nСообщение на выходе кодера будет иметь вид: " +
                "v(x) = u(x)x^(n - k) + ((u(x)x^(n - k)) mod g(x))");
        Polynom u_polynom = new Polynom("x^31 + x^29 + x^25 + x^15 + x^12 + x^11 + x^10 + x^7 + x^6 + x^5 + x^3 + x^1 + 1");
        System.out.println("u(x) = " + u_polynom);
        String xDegree = "x^" + (n - k);
        Polynom x_polynom = new Polynom(xDegree);
        System.out.println("х^(n - k) = " + xDegree);
        Polynom v_polynomFirstPart = u_polynom.mul(x_polynom);
        Polynom v_polynomSeconPart = v_polynomFirstPart.div(g_polynom).takeModPart();
        Polynom v_polynom = v_polynomFirstPart.sub(v_polynomSeconPart);
        System.out.println("v(x) = " + v_polynom);

        System.out.println("\n\nДекодирование!");
        Polynom e_polynom = new Polynom("x^10 +x^9 +x^7");
        System.out.println("\ne(x) = " + e_polynom);
        Polynom v_delivered_polynom = e_polynom.sub(v_polynom);
        System.out.println("v'(x) = " + v_delivered_polynom);


        System.out.println("\nВычислим синдромы:");
        Polynom s1_polynom = v_delivered_polynom.div(p_polynom).takeModPart();
        Polynom s2_polynom = v_delivered_polynom.div(f_polynom).takeModPart();
        System.out.println("s1(x) = " + s1_polynom);
        System.out.println("s2(x) = " + s2_polynom);
        if (s1_polynom.getPolynomMap().size() == 0 && s2_polynom.getPolynomMap().size() == 0) {
            System.out.println("Ошибки нет, конец декодирования!");
            return;
        }


        System.out.println("\nОпределим вид B(x):");
        TreeMap<Integer, Polynom> b_polynoms_map = new TreeMap<>();
        ArrayList<Integer> offsetArray = new ArrayList<>();
        int count = 0;
        for (int l = 0; l < 100; l++) {
            String degree = "x^" + l;
            Polynom x_degree = new Polynom(degree);
            Polynom result = x_degree.mul(s1_polynom);

            if (result.getPolynomMap().firstKey() > g_polynom.getPolynomMap().firstKey())
                result = result.div(p_polynom).takeModPart();

            if (result.getPolynomMap().firstKey() == f_polynom.getPolynomMap().firstKey() - 1) {
                b_polynoms_map.put(count, result);
                offsetArray.add(l);
                count++;
                System.out.println("Смещение: " + l + ". Вид остатка: " + result + ".");
            }
        }


        System.out.println("\nПроверим, можем ли мы определить явный вид остатка.");
        for (int i = 0; i < b_polynoms_map.size() - 1; i++) {
            Polynom checkB_polynom = b_polynoms_map.get(i);
            Polynom checkB_polynom_other = b_polynoms_map.get(i + 1);
            if (!checkB_polynom.equals(checkB_polynom_other)) {
                System.out.println("Вид остатка не возможно определить точно!");
                return;
            }
        }
        Polynom New_B_Polynom = b_polynoms_map.get(0);
        System.out.println("Да! Мы определили явный вид остатка:" +
                "\nB(x) = " + New_B_Polynom);


        System.out.println("\nНайдем действительное смещение:");
        int realOffset = (p_polynom.getPolynomMap().firstKey() - offsetArray.get(0)) % p_polynom.getPolynomMap().firstKey();
        System.out.println("i' = " + realOffset);


        System.out.println("\nНайдем l1 и l2:");
        TreeMap<Integer, Polynom> GF_extend_four_degree = fireCode.useGF_extend_four_degree();
        int tau = 1;
        int l1 = 0;
        int l2 = 0;
        for (Map.Entry<Integer, Polynom> entry : GF_extend_four_degree.entrySet()) {
            if (entry.getValue().equals(New_B_Polynom))
                l1 = entry.getKey();
            if (entry.getValue().equals(s2_polynom)) {

                l2 = entry.getKey();
            }
        }
        System.out.println("l1 = " + l1);
        System.out.println("l2 = " + l2);


        System.out.println("\nНайдем j:");
        int j_orig = 0;
        for (int h = 0; h < 100; h++) {
            int mod = (int) Math.pow(2, f_polynom.getPolynomMap().firstKey()) - 1;
            int firstPart = (tau * h * p_polynom.getPolynomMap().firstKey());
            if (l2 == (l1 + firstPart) % mod) {
                j_orig = h;
                break;
            }
        }
        System.out.println("j = " + j_orig);


        System.out.println("\nНайдем истинное смещение ошибки B(x).");
        int i = realOffset + j_orig * p_polynom.getPolynomMap().firstKey();
        System.out.println("i = " + i);


        String new_e_i_polynom = "x^" + i;
        Polynom new_e_x_polynom = new Polynom(new_e_i_polynom);
        Polynom error_polynom = New_B_Polynom.mul(new_e_x_polynom);
        System.out.println("\nx^i * B(x) = " + error_polynom);
        Polynom uExit_Polynom = (v_polynom.sub(error_polynom)).div(x_polynom);
        System.out.println();
        System.out.println("Сообщение на выходе: " + uExit_Polynom);
        if (u_polynom.equals(uExit_Polynom))
            System.out.println("Ошибки найдены и исправлены!");
    }

    public int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    public int lcm(int a, int b) {
        return a / gcd(a, b) * b;
    }

    public TreeMap<Integer, Polynom> useGF_extend_four_degree() {
        TreeMap<Integer, Polynom> GF_extends_field = new TreeMap<>();
        GF_extends_field.put(0, new Polynom("1"));
        GF_extends_field.put(1, new Polynom("x^1"));
        GF_extends_field.put(2, new Polynom("x^2"));
        GF_extends_field.put(3, new Polynom("x^3"));
        GF_extends_field.put(4, new Polynom("x^1 + 1"));
        GF_extends_field.put(5, new Polynom("x^2 + x^1"));
        GF_extends_field.put(6, new Polynom("x^3 + x^2"));
        GF_extends_field.put(7, new Polynom("x^3 + x^1 + 1"));
        GF_extends_field.put(8, new Polynom("x^2 + 1"));
        GF_extends_field.put(9, new Polynom("x^3 + x^1"));
        GF_extends_field.put(10, new Polynom("x^2 + x^1 + 1"));
        GF_extends_field.put(11, new Polynom("x^3 + x^2 + x^1"));
        GF_extends_field.put(12, new Polynom("x^3 + x^2 + x^1 + 1"));
        GF_extends_field.put(13, new Polynom("x^3 + x^2 + 1"));
        GF_extends_field.put(14, new Polynom("x^3 + 1"));
        return GF_extends_field;
    }
}


