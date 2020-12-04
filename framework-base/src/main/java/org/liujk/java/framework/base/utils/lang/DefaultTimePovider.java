
package org.liujk.java.framework.base.utils.lang;




import java.util.Date;

/**
 * 说明：
 * <p>
 *
 */
public class DefaultTimePovider implements NowProvider {
    @Override
    public Date getNow () {
        return new Date ();
    }

    @Override
    public long currentTimeMillis () {
        return System.currentTimeMillis ();
    }
}
