package com.wolfeiii.emeraldplinko.game.win;

import com.wolfeiii.emeraldplinko.game.objects.GameDifficulty;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

@Getter
public class WinPool {

    private final Material material;
    private final GameDifficulty difficulty;
    private final double winMultiplier;
    private final Particle particle;

    public WinPool(@NotNull Material material, @NotNull GameDifficulty difficulty, @NotNull String particle, double winMultiplier) {
        this.material = material;
        this.difficulty = difficulty;
        this.particle = Particle.valueOf(particle);
        this.winMultiplier = winMultiplier;
    }

    public int getMoneyForBet(int bet) {
        return (int) Math.round(bet * winMultiplier);
    }

}
