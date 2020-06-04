package monotonic_clock;


import org.jetbrains.annotations.NotNull;

/**
 * В теле класса решения разрешено использовать только финальные переменные типа RegularInt.
 * Нельзя volatile, нельзя другие типы, нельзя блокировки, нельзя лазить в глобальные переменные.
 */
public class Solution implements MonotonicClock {
    private final RegularInt c11 = new RegularInt(0);
    private final RegularInt c12 = new RegularInt(0);
    private final RegularInt c13 = new RegularInt(0);

    private final RegularInt c21 = new RegularInt(0);
    private final RegularInt c22 = new RegularInt(0);
    private final RegularInt c23 = new RegularInt(0);

    @Override
    public void write(@NotNull Time time) {
        //c2
        int tmp1 = time.getD1();
        int tmp2 = time.getD2();
        int tmp3 = time.getD3();
        //c2-> = any>=c2
        c21.setValue(tmp1);
        c22.setValue(tmp2);
        c23.setValue(tmp3);
        //c1<- = c2
        c13.setValue(tmp3);
        c12.setValue(tmp2);
        c11.setValue(tmp1);
    }

    @NotNull
    @Override
    public Time read() {
        int[] r1 = new int[3];
        int[] r2 = new int[3];
        //r1 := c1->
        r1[0] = c11.getValue();
        r1[1] = c12.getValue();
        r1[2] = c13.getValue();
        //r2 := c2<-
        r2[2] = c23.getValue();
        r2[1] = c22.getValue();
        r2[0] = c21.getValue();

        if (r1[0] == r2[0] && r1[1] == r2[1] && r1[2] == r2[2]) {
            return new Time(r1[0], r1[1], r1[2]);
        } else {
            int[] ans = new int[3];
            boolean flag = true;
            for (int i = 0; i < 3; i++) {
                ans[i] = (flag ? r2[i] : 0);
                if (r1[i] != r2[i])
                    flag = false;
            }
            return new Time(ans[0], ans[1], ans[2]);
        }
    }
}

