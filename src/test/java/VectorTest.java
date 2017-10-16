import com.hoxo.geometric.Vector2D;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by Hoxton on 12.10.2017.
 */
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
        assertEquals(v1.angleWith(v2),Double.NaN,0);
        assertEquals(v2.angleWith(v1),Double.NaN,0);
    }

    @Test
    public void checkAngleCalculation() {
        Vector2D v1 = new Vector2D(86,-43),
                v2 = new Vector2D(47,42);
        assertEquals(Math.abs(v1.getAngle() - v2.getAngle()), v1.angleWith(v2), 0);
    }
}
