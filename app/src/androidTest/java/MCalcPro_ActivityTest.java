import ca.roumani.i2c.MPro;
import ca.roumani.i2c.MPro;
import static org.junit.Assert.*;

public class MCalcPro_ActivityTest {
    public static void paymentTest() {
        MPro mp = new MPro();
        mp.setInterest("2");
        mp.setAmortization("20");
        mp.setPrinciple("400000");
        String monthlyPayment = mp.computePayment("%,.2f");
        double mPayment =  Double.parseDouble(mp.computePayment(".2f"));
        int dollars = (int) (Math.floor(mPayment));
        System.out.println(dollars);
    }

    public static void main(String[] args) {
        MPro mp = new MPro();
        mp.setInterest("2");
        mp.setAmortization("20");
        mp.setPrinciple("400000");
        String monthlyPayment = mp.computePayment("%,.2f");
        double mPayment =  Double.parseDouble(mp.computePayment(".2f"));
        int dollars = (int) (Math.floor(mPayment));
        System.out.println(dollars);
    }
}