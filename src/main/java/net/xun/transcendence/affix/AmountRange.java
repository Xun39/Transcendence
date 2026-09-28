package net.xun.transcendence.affix;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;

public record AmountRange(double min, double max) {
    public static final Codec<Double> NUMBER = Codec.either(Codec.DOUBLE, Codec.STRING).comapFlatMap(either -> either.map(DataResult::success, value -> {
        try {
            return DataResult.success(Double.parseDouble(value));
        }
        catch (NumberFormatException e) {
            return DataResult.error(() -> "Expected a number, got: " + value);
        }
    }), Either::left);

    public static final Codec<AmountRange> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    NUMBER.fieldOf("min").forGetter(AmountRange::min),
                    NUMBER.fieldOf("max").forGetter(AmountRange::max)
            ).apply(instance, AmountRange::new)
    );

    public static final AmountRange ZERO = new AmountRange(0.0F, 0.0F);

    public AmountRange {
        if (!Double.isFinite(min) || !Double.isFinite(max)) {
            throw new IllegalArgumentException("Affix amount must be finite");
        }
        if (min > max) {
            throw new IllegalArgumentException("Affix amount min cannot be greater than max");
        }
    }

    public double roll(RandomSource random) {
        if (min == max) {
            return min;
        }
        return min + (max - min) * random.nextDouble();
    }
}
