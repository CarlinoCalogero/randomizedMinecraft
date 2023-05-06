package net.nerdshelf.randomizedminecraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.nerdshelf.randomizedminecraft.RandomizedMinecraftMod;

public class BankVaultScreen extends AbstractContainerScreen<BankVaultMenu> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(RandomizedMinecraftMod.MOD_ID,
			"textures/gui/bank_vault_gui.png");
	private static final Component CONTAINER_TITLE = Component.translatable("randomizedminecraftmod.container.vault");

	private final Player player;

	public BankVaultScreen(BankVaultMenu menu, Inventory inventory, Component component) {
		super(menu, inventory, component);
		this.player = inventory.player;
	}

	@Override
	protected void containerTick() {
		super.containerTick();
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	public void resize(Minecraft p_97886_, int p_97887_, int p_97888_) {
		this.init(p_97886_, p_97887_, p_97888_);
	}

	@Override
	protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		int x = (this.width - imageWidth) / 2;
		int y = (this.height - imageHeight) / 2;

		this.blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight); // renders the texture

	}

	@Override
	public void render(PoseStack pPoseStack, int mouseX, int mouseY, float delta) {
		renderBackground(pPoseStack);
		super.render(pPoseStack, mouseX, mouseY, delta);

		/*
		 * This method is added by the container screen to render a tooltip for whatever
		 * slot is hovered over.
		 */
		this.renderTooltip(pPoseStack, mouseX, mouseY);
	}

	@Override
	public void onClose() {
		// TODO Auto-generated method stub
		super.onClose();
		System.out.println("GUI CLOSED");
		this.menu.blockEntity.giveCurrency(this.menu.blockEntity);
	}

}