package samples;

import net.thucydides.core.annotations.Feature;

/**
 * Created by IntelliJ IDEA.
 * User: johnsmart
 * Date: 31/08/11
 * Time: 3:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class Application {
    @Feature
    class MakeWidgets {
        class MakeBigWidgets {}
        class MakeSmallWidgets {}
    }

    @Feature
    class SellWidgets {
        class SellRetailWidgets {}
        class SellWidgetsOnline {}
    }
}
