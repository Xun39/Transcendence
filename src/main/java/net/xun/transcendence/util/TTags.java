package net.xun.transcendence.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.xun.lib.common.api.util.CommonUtils;
import net.xun.transcendence.Transcendence;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class TTags {
    public enum NameSpace {
        MOD(Transcendence.MOD_ID),
        COMMON("c");

        public final String id;

        NameSpace(String id) {
            this.id = id;
        }

        public ResourceLocation id(Enum<?> entry, @Nullable String pathOverride) {
            return CommonUtils.modLoc(pathOverride != null ? pathOverride : entry.name().toLowerCase(Locale.ROOT));
        }
    }

    public enum Items {
        CHARM;

        public final TagKey<Item> tag;

        Items() {
			this(NameSpace.MOD);
        }

        Items(NameSpace namespace) {
            this(namespace, null);
        }

        Items(NameSpace namespace, @Nullable String pathOverride) {
            this.tag = TagKey.create(Registries.ITEM, namespace.id(this, pathOverride));
        }
    }
}
