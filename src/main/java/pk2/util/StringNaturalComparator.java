package pk2.util;

import java.util.Comparator;

public class StringNaturalComparator implements Comparator<String> {
    @Override
    public int compare(String a, String b) {

        if (a == null || b == null) {
            return a == null ? (b == null ? 0 : -1) : 1;
        }

        int n = Math.min(a.length(), b.length());

        int pos_a = 0;
        int pos_b = 0;

        while (pos_a < n && pos_b<n) {
            char ac = a.charAt(pos_a);
            char bc = b.charAt(pos_b);

            if(Character.isDigit(ac) && Character.isDigit(bc)){

                int num_a = 0;
                while (pos_a < n && Character.isDigit(a.charAt(pos_a))) {
                    num_a = 10 * num_a + (a.charAt(pos_a) - '0');
                    ++pos_a;
                }

                int num_b = 0;
                while (pos_b < n && Character.isDigit(b.charAt(pos_b))) {
                    num_b = 10 * num_b + (b.charAt(pos_b) - '0');
                    ++pos_b;
                }

                if(num_a!=num_b){
                    return num_a - num_b;
                }             
            }
            else if(ac !=bc){
                return ac - bc;
            }

            ++pos_a;
            ++pos_b;
        }
        
        return a.length() - b.length();
    }
}