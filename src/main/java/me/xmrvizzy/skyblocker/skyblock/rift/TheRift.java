package me.xmrvizzy.skyblocker.skyblock.rift;

import me.xmrvizzy.skyblocker.config.SkyblockerConfigManager;
import me.xmrvizzy.skyblocker.utils.scheduler.Scheduler;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public class TheRift {
    /**
     * @see me.xmrvizzy.skyblocker.utils.Utils#isInTheRift() Utils#isInTheRift().
     */
    public static final String LOCATION = "rift";

    public static void init() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(MirrorverseWaypoints::render);
        WorldRenderEvents.AFTER_TRANSLUCENT.register(EffigyWaypoints::render);
        Scheduler.INSTANCE.scheduleCyclic(EffigyWaypoints::updateEffigies, SkyblockerConfigManager.get().slayer.vampireSlayer.effigyUpdateFrequency);
        Scheduler.INSTANCE.scheduleCyclic(TwinClawsIndicator::updateIce, SkyblockerConfigManager.get().slayer.vampireSlayer.holyIceUpdateFrequency);
        Scheduler.INSTANCE.scheduleCyclic(ManiaIndicator::updateMania, SkyblockerConfigManager.get().slayer.vampireSlayer.maniaUpdateFrequency);
        Scheduler.INSTANCE.scheduleCyclic(StakeIndicator::updateStake, SkyblockerConfigManager.get().slayer.vampireSlayer.steakStakeUpdateFrequency);
    }
}
