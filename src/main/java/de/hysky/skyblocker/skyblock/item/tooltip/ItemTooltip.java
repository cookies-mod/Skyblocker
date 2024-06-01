package de.hysky.skyblocker.skyblock.item.tooltip;

import de.hysky.skyblocker.config.SkyblockerConfigManager;
import de.hysky.skyblocker.config.configs.GeneralConfig;
import de.hysky.skyblocker.utils.Constants;
import de.hysky.skyblocker.utils.Utils;
import de.hysky.skyblocker.utils.scheduler.Scheduler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class ItemTooltip {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ItemTooltip.class.getName());
    private static final MinecraftClient client = MinecraftClient.getInstance();
    public static final GeneralConfig.ItemTooltip config = SkyblockerConfigManager.get().general.itemTooltip;
    private static volatile boolean sentNullWarning = false;

    @NotNull
    public static String getNeuName(String internalID, String neuName) {
        switch (internalID) {
            case "PET" -> {
                neuName = neuName.replaceAll("LVL_\\d*_", "");
                String[] parts = neuName.split("_");
                String type = parts[0];
                neuName = neuName.replaceAll(type + "_", "");
                neuName = neuName + "-" + type;
                neuName = neuName.replace("UNCOMMON", "1")
                        .replace("COMMON", "0")
                        .replace("RARE", "2")
                        .replace("EPIC", "3")
                        .replace("LEGENDARY", "4")
                        .replace("MYTHIC", "5")
                        .replace("-", ";");
            }
            case "RUNE" -> neuName = neuName.replaceAll("_(?!.*_)", ";");
            case "POTION" -> neuName = "";
            case "ATTRIBUTE_SHARD" ->
                    neuName = internalID + "+" + neuName.replace("SHARD-", "").replaceAll("_(?!.*_)", ";");
            default -> neuName = neuName.replace(":", "-");
        }
        return neuName;
    }

    public static void nullWarning() {
        if (!sentNullWarning && client.player != null) {
            LOGGER.warn(Constants.PREFIX.get().append(Text.translatable("skyblocker.itemTooltip.nullMessage")).getString());
            sentNullWarning = true;
        }
    }

    public static Text getCoinsMessage(double price, int count) {
        // Format the price string once
        String priceString = String.format(Locale.ENGLISH, "%1$,.1f", price);

        // If count is 1, return a simple message
        if (count == 1) {
            return Text.literal(priceString + " Coins").formatted(Formatting.DARK_AQUA);
        }

        // If count is greater than 1, include the "each" information
        String priceStringTotal = String.format(Locale.ENGLISH, "%1$,.1f", price * count);

        return Text.literal(priceStringTotal + " Coins ").formatted(Formatting.DARK_AQUA)
                   .append(Text.literal("(" + priceString + " each)").formatted(Formatting.GRAY));
    }

    // If these options is true beforehand, the client will get first data of these options while loading.
    // After then, it will only fetch the data if it is on Skyblock.
    public static int minute = 0;

    public static void init() {
        Scheduler.INSTANCE.scheduleCyclic(() -> {
            if (!Utils.isOnSkyblock() && 0 < minute) {
                sentNullWarning = false;
                return;
            }

            if (++minute % 60 == 0) {
                sentNullWarning = false;
            }

            List<CompletableFuture<Void>> futureList = new ArrayList<>();

            TooltipInfoType.NPC.downloadIfEnabled(futureList);
            TooltipInfoType.BAZAAR.downloadIfEnabled(futureList);
            TooltipInfoType.LOWEST_BINS.downloadIfEnabled(futureList);

            if (config.enableAvgBIN) {
                GeneralConfig.Average type = config.avg;

                if (type == GeneralConfig.Average.BOTH || TooltipInfoType.ONE_DAY_AVERAGE.getData() == null || TooltipInfoType.THREE_DAY_AVERAGE.getData() == null || minute % 5 == 0) {
                    TooltipInfoType.ONE_DAY_AVERAGE.download(futureList);
                    TooltipInfoType.THREE_DAY_AVERAGE.download(futureList);
                } else if (type == GeneralConfig.Average.ONE_DAY) {
                    TooltipInfoType.ONE_DAY_AVERAGE.download(futureList);
                } else if (type == GeneralConfig.Average.THREE_DAY) {
                    TooltipInfoType.THREE_DAY_AVERAGE.download(futureList);
                }
            }

            TooltipInfoType.MOTES.downloadIfEnabled(futureList);
            TooltipInfoType.MUSEUM.downloadIfEnabled(futureList);
            TooltipInfoType.COLOR.downloadIfEnabled(futureList);
            TooltipInfoType.ACCESSORIES.downloadIfEnabled(futureList);

            CompletableFuture.allOf(futureList.toArray(CompletableFuture[]::new)).exceptionally(e -> {
                LOGGER.error("Encountered unknown error while downloading tooltip data", e);
                return null;
            });
        }, 1200, true);
    }
}