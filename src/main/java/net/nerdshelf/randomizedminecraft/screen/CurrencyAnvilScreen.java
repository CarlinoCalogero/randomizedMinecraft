package net.nerdshelf.randomizedminecraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.nerdshelf.randomizedminecraft.RandomizedMinecraftMod;

public class CurrencyAnvilScreen extends AbstractContainerScreen<CurrencyAnvilMenu> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(RandomizedMinecraftMod.MOD_ID,
			"textures/gui/currency_anvil_gui.png");

	EditBox name;

	public CurrencyAnvilScreen(CurrencyAnvilMenu menu, Inventory inventory, Component component) {
		super(menu, inventory, component);
	}

	@Override
	protected void containerTick() {
		super.containerTick();

		// Add ticking logic for EditBox in editBox
		this.name.tick();
	}

	@Override
	protected void init() {
		super.init();

		// Add widgets and precomputed values
		int x = (this.width - imageWidth) / 2;
		int y = (this.height - imageHeight) / 2;
		name = new EditBox(this.font, x + 62, y + 24, 103, 12, Component.translatable("container.repair"));
		name.setCanLoseFocus(false);
		name.setTextColor(-1);
		name.setTextColorUneditable(-1);
		name.setBordered(false);
		name.setMaxLength(50);
		name.setResponder(this::onNameChanged);
		name.setValue("");
		this.addRenderableWidget(name);
		this.setInitialFocus(name);
		// name.setEditable(false);
	}

	@Override
	public void resize(Minecraft p_97886_, int p_97887_, int p_97888_) {
		String s = this.name.getValue();
		this.init(p_97886_, p_97887_, p_97888_);
		this.name.setValue(s);
	}

	@Override
	public boolean keyPressed(int p_97878_, int p_97879_, int p_97880_) {
		if (p_97878_ == 256) {
			this.minecraft.player.closeContainer();
		}

		return !this.name.keyPressed(p_97878_, p_97879_, p_97880_) && !this.name.canConsumeInput()
				? super.keyPressed(p_97878_, p_97879_, p_97880_)
				: true;
	}

	private void onNameChanged(String name) {
		this.menu.setItemName(name);
		this.minecraft.player.connection.send(new ServerboundRenameItemPacket(name));
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

		this.name.render(pPoseStack, mouseX, mouseY, delta);
		/*
		 * This method is added by the container screen to render a tooltip for whatever
		 * slot is hovered over.
		 */
		this.renderTooltip(pPoseStack, mouseX, mouseY);
	}

}