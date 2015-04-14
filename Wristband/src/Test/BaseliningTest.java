package Test;

import client.Baselining;
import client.DataPoint;

import org.junit.Assert;

import static junit.framework.Assert.assertEquals;

/**
 * Created by magic_000 on 4/14/2015.
 */
public class BaseliningTest extends Baselining {

    public static void main(String[] args) {
        public void testupdateSum() {
            Baselining test = new Baselining();
            test.getSessionData().add(new DataPoint(2, 1));
            test.getSessionData().add(new DataPoint(4, 2));
            test.updateSum(test.getSessionData());
            //System.out.println(test.getSum());
            assertEquals(test.getSum(), (float) 6.0, .001);
            test.getSessionData().add(new DataPoint(4, 5));
            test.updateSum(test.getSessionData());
            assertEquals(test.getSum(), (float) 16.0, .001);
        }


}

}