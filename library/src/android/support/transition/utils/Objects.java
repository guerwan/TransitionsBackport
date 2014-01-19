package android.support.transition.utils;

/**
 * Created by stephane on 11/2/13.
 */
public class Objects {

    /**
     * Returns true if two possibly-null objects are equal.
     */
    public static boolean equal(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    public static int hashCode(Object o) {
        return (o == null) ? 0 : o.hashCode();
    }
}
