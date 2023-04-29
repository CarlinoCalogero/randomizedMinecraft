package net.nerdshelf.randomizedminecraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.nerdshelf.randomizedminecraft.RandomizedMinecraftMod;

public class CurrencyAnvilScreen extends AbstractContainerScreen<CurrencyAnvilMenu> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(RandomizedMinecraftMod.MOD_ID,
			"textures/gui/currency_anvil_gui.png");
	private static final Component CONTAINER_TITLE = Component.translatable("randomizedminecraftmod.container.repair");

	EditBox name;
	private final Player player;

	public CurrencyAnvilScreen(CurrencyAnvilMenu menu, Inventory inventory, Component component) {
		super(menu, inventory, component);
		this.player = inventory.player;
	}

	@Override
	protected void containerTick() {
		super.containerTick();

		// Add ticking logic for EditBox in editBox
		this.name.tick();

		if (!this.menu.isSlotOEmpty()) {
			this.name.setEditable(true);
			this.setFocused(this.name);
		}
	}

	@Override
	protected void init() {
		super.init();

		// Add widgets and precomputed values
		int x = (this.width - imageWidth) / 2;
		int y = (this.height - imageHeight) / 2;
		this.name = new EditBox(this.font, x + 62, y + 24, 103, 12,
				Component.translatable("randomizedminecraftmod.container.repair"));
		this.name.setCanLoseFocus(false);
		this.name.setTextColor(-1);
		this.name.setTextColorUneditable(-1);
		this.name.setBordered(false);
		this.name.setMaxLength(50);
		this.name.setResponder(this::onNameChanged);
		this.name.setValue("");
		this.addRenderableWidget(this.name);
		this.setInitialFocus(this.name);
		this.name.setEditable(false);
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

	protected void renderLabels(PoseStack p_97890_, int p_97891_, int p_97892_) {
		RenderSystem.disableBlend();
		super.renderLabels(p_97890_, p_97891_, p_97892_);
		int i = this.menu.getCost();
		if (i > 0) {
			int j = 8453920;
			Component component;
			if (this.menu.isSlot2Empty() && !this.menu.isWasCraftedButPlayerCouldNotAfford()) {
				component = null;
			} else {
				component = Component.translatable("randomizedminecraftmod.container.repair.cost", i);
				if (!this.menu.customMayPickup(this.player)) {
					j = 16736352;
				}
			}

			if (component != null) {
				int k = this.imageWidth - 8 - this.font.width(component) - 2;
				int l = 69;
				fill(p_97890_, k - 2, 67, this.imageWidth - 8, 79, 1325400064);
				this.font.drawShadow(p_97890_, component, (float) k, 69.0F, j);
			}
		}

		// Renders "Repair & Name" text
		int k = this.imageWidth - 44 - this.font.width(CONTAINER_TITLE) - 2;
		this.font.drawShadow(p_97890_, CONTAINER_TITLE, (float) k, 5.0F, 10000536);

	}

	@Override
	protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		int x = (this.width - imageWidth) / 2;
		int y = (this.height - imageHeight) / 2;

		this.blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight); // renders the texture

		if (this.menu.isSlotOEmpty()) {
			this.blit(pPoseStack, x + 59, y + 20, 0, 182, 110, 16); // renders TextBox texture
		} else {
			this.blit(pPoseStack, x + 59, y + 20, 0, 166, 110, 16); // renders TextBox texture
		}

		if (!this.menu.isSlot1Empty() && !(!this.menu.isSlotOEmpty() && !this.menu.isSlot1Empty())) {
			this.blit(pPoseStack, x + 99, y + 45, 176, 0, 28, 21); // renders crossed arrow texture
		}

		if (this.menu.isWasCraftedButPlayerCouldNotAfford()) {
			this.blit(pPoseStack, x + 99, y + 45, 176, 0, 28, 21); // renders crossed arrow texture
		}

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