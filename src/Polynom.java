import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Polynom {

    private TreeMap<Integer, Integer> polynomMap;
    private TreeMap<Integer, Integer> polynomMapQuotient;

    public Polynom(String string) {
        polynomMap = regexCoeff(string);
    }

    public Polynom(TreeMap<Integer, Integer> quotient, TreeMap<Integer, Integer> remain) {
        polynomMap = remain;
        polynomMapQuotient = quotient;
    }

    public Polynom(TreeMap<Integer, Integer> polynomMap) {
        this.polynomMap = polynomMap;
    }

    private TreeMap<Integer, Integer> regexCoeff(String string) {

        TreeMap<Integer, Integer> map = new TreeMap<>(Collections.reverseOrder());
        Pattern pattern = Pattern.compile("[x+]");
        Matcher matcher = pattern.matcher(string);
        string = matcher.replaceAll("");

        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == '^') {
                StringBuilder s = new StringBuilder();
                while (i + 2 <= string.length() && string.charAt(i + 1) != ' ') {
                    s.append(string.charAt(i + 1));
                    i++;
                }

                map.put(Integer.parseInt(s.toString()), 1);
            }
            if (string.charAt(i) == ' ' && string.charAt(i + 1) != '^' && i + 2 == string.length())
                map.put(0, 1);
        }
        return map;
    }

    public Polynom takeModPart() {
        polynomMap = polynomMapQuotient;
        return new Polynom(polynomMap);
    }

    public Polynom sub(Polynom polynom) {

        TreeMap<Integer, Integer> otherM = polynom.getPolynomMap();
        TreeMap<Integer, Integer> resultMap = polynomMap;

        for (Map.Entry<Integer, Integer> b : otherM.entrySet()) {
            if (polynomMap.containsKey(b.getKey())) {
                Integer k = polynomMap.get(b.getKey()) ^ b.getValue();
                if (k != 0)
                    resultMap.put(b.getKey(), k);
                else
                    resultMap.remove(b.getKey());
            } else
                resultMap.put(b.getKey(), 1);
        }

        return new Polynom(resultMap);
    }

    public Polynom mul(Polynom polynom) {

        TreeMap<Integer, Integer> otherM = polynom.getPolynomMap();
        TreeMap<Integer, Integer> resultMap = new TreeMap<>(Collections.reverseOrder());
        for (Map.Entry<Integer, Integer> entryOther : (otherM).entrySet()) {
            int otherKey = entryOther.getKey();
            for (Map.Entry<Integer, Integer> entryThis : (polynomMap).entrySet()) {
                int thisKey = entryThis.getKey();
                int resultKey = otherKey + thisKey;
                if (resultMap.containsKey(resultKey))
                    resultMap.put(resultKey, (resultMap.get(resultKey) ^ 1));
                else
                    resultMap.put(resultKey, 1);
            }
        }

        return new Polynom(resultMap);
    }

    public Polynom div(Polynom polynom) {

        TreeMap<Integer, Integer> otherM = polynom.getPolynomMap();
        List<Integer> otherList = new ArrayList<>(Collections.nCopies(otherM.firstKey() + 1, 0));
        for (Map.Entry<Integer, Integer> otherMap : otherM.entrySet()) {
            otherList.add(otherMap.getKey(), 1);
            otherList.remove(otherMap.getKey() + 1);
        }
        Collections.reverse(otherList);

        List<Integer> thisList = new ArrayList<>(Collections.nCopies(polynomMap.firstKey() + 1, 0));
        for (Map.Entry<Integer, Integer> thisMap : polynomMap.entrySet()) {
            thisList.add(thisMap.getKey(), 1);
            thisList.remove(thisMap.getKey() + 1);
        }
        Collections.reverse(thisList);

        List<Integer> result = new ArrayList<>();
        List<Integer> returnList = new ArrayList<>();
        int count = otherList.size() - 1;

        for (int i = 0; i < otherList.size(); i++)
            result.add(i, (otherList.get(i) ^ thisList.get(i)));
        returnList.add(1);
        byte j = 0;

        while (count <= thisList.size()) {
            while (result.size() > 0 && result.get(0) == 0)
                result.remove(0);
            if (count >= thisList.size() - 1)
                break;
            while (result.size() < otherList.size() && count < thisList.size() - 1) {
                if (result.size() <= otherList.size() - 2) {
                    returnList.add(0);
                }
                count++;
                result.add(thisList.get(count));
            }
            if (result.get(0) == 0) {
                returnList.add(0);
                continue;
            }
            if (count > thisList.size() - 1 || result.size() < otherList.size())
                break;
            for (Integer anOtherList : otherList) {
                result.add(result.get(j) ^ anOtherList);
                result.remove(j);
            }
            returnList.add(1);
        }

        TreeMap<Integer, Integer> quotientMap = new TreeMap<>(Collections.reverseOrder());
        TreeMap<Integer, Integer> resultMap = new TreeMap<>(Collections.reverseOrder());

        Collections.reverse(result);
        Collections.reverse(returnList);

        if (result.size() > 0) {
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i) != 0)
                    quotientMap.put(i, 1);
            }
        }

        for (int i = returnList.size() - 1; i >= 0; i--) {
            if (returnList.get(i) != 0)
                resultMap.put(i, 1);
        }

        return new Polynom(quotientMap, resultMap);
    }

    public String toString() {

        StringBuilder string = new StringBuilder();
        for (Map.Entry<Integer, Integer> v : polynomMap.entrySet()) {
            if (v.getKey() == 0)
                string.append("1");
            else
                string.append("x^").append(v.getKey()).append(" + ");
        }

        while ((string.toString().endsWith(" ") || string.toString().endsWith("+")))
            string.deleteCharAt(string.length() - 1);


        return string.toString();
    }

    public TreeMap<Integer, Integer> getPolynomMap() {
        return polynomMap;
    }

    public boolean equals(Polynom polynom) {

        TreeMap<Integer, Integer> thisM = polynomMap;
        TreeMap<Integer, Integer> otherM = polynom.getPolynomMap();

        if (thisM.size() != otherM.size())
            return false;

        for (Map.Entry<Integer, Integer> v : thisM.entrySet()) {
            if (!otherM.containsKey(v.getKey()))
                return false;
        }

        return true;
    }
}


