package bastion14.thepast.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ThePastConfiguration {
    public static class CommonConfig {
        public CommonConfig(ForgeConfigSpec.Builder builder){

        }
    }
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final CommonConfig COMMON_CONFIG;
    static {
        final Pair<CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        COMMON_SPEC = specPair.getRight();
        COMMON_CONFIG = specPair.getLeft();
    }
}
