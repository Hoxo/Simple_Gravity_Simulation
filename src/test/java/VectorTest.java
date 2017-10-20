import com.hoxo.geometric.Point;
import com.hoxo.geometric.Vector2D;
import org.junit.Test;

import static org.junit.Assert.*;

public class VectorTest {

    @Test
    public void angleTest() {
        Vector2D v1 = new Vector2D(12,12),
                v2 = new Vector2D(2,2);
        assertEquals(v1.angleWith(v2),v2.angleWith(v1),0);
    }

    @Test
    public void angleWithNullVector() {
        Vector2D v1 = Vector2D.nullVector(),
                v2 = new Vector2D(12,56);
        assertEquals(v1.angleWith(v2),v2.getAngle(),0);
        assertEquals(v2.angleWith(v1),v2.getAngle(),0);
    }

    @Test
    public void checkAngleCalculation() {
        for (int i = 1; i < 100; i++ )
            for(int k = 1; k < 100; k++) {
                Vector2D v1 = new Vector2D(i,k),
                        v2 = new Vector2D(i * Math.cos(i * k),k * Math.cos(i * k));
                assertNotEquals(v1.angleWith(v2),Double.NaN,0);
            }
    }

    @Test
    public void nullVectorAngleCheck() {
        Vector2D v1 = Vector2D.nullVector();
        assertEquals(v1.getAngle(),0,0);
    }

    @Test
    public void setLengthCheck() {
        Vector2D v1 = new Vector2D(1345,-231);
        v1.setLength(7);
        assertEquals(v1.length(),7,0.000001);
    }

    @Test
    public void scaleLengthCheck() {
        Vector2D v1 = new Vector2D(11,29);
        double length = 10,
                scale = 0.8;
        v1.setLength(length);
        v1.scaleLength(scale);
        assertEquals(v1.length(),length * scale, 0.000001);
    }

    @Test
    public void nullVectorLengthCheck() {
        Vector2D v1 = Vector2D.nullVector();
        v1.setLength(123456);
        v1.scaleLength(123);
        assertEquals(v1.length(),0,0);
    }

    @Test
    public void radiusVectorCheck() {
        double angle = Math.PI/7,
                length = 47;
        Vector2D v1 = Vector2D.vectorR(angle,length);
        assertEquals(v1.getAngle(),angle,0.0000001);
        assertEquals(v1.length(),length,0.0000001);
    }

    @Test
    public void differentConstructorsCheck() {
        Vector2D v1 = new Vector2D(23,45),
                v2 = new Vector2D(new Point(111,324),new Point(134,369));
        assertTrue(v1.equals(v2));
    }

}
