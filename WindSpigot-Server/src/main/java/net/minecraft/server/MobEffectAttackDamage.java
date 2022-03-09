package net.minecraft.server;

public class MobEffectAttackDamage extends MobEffectList {

	protected MobEffectAttackDamage(int i, MinecraftKey minecraftkey, boolean flag, int j) {
		super(i, minecraftkey, flag, j);
	}

	@Override
	public double a(int i, AttributeModifier attributemodifier) {
		// PaperSpigot - Configurable modifiers for strength and weakness effects
		return this.id == MobEffectList.WEAKNESS.id
				? (double) (org.github.paperspigot.PaperSpigotConfig.weaknessEffectModifier * (i + 1))
				: org.github.paperspigot.PaperSpigotConfig.strengthEffectModifier * (i + 1);
	}
}
