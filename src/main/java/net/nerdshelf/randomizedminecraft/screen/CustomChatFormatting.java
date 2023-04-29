package net.nerdshelf.randomizedminecraft.screen;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;

public enum CustomChatFormatting implements StringRepresentable {
	CUSTOM_DARK_GRAY("CUSTOM_DARK_GRAY", '1', 1, 4144959), RESET("RESET", 'r', -1, (Integer) null);

	public static final Codec<CustomChatFormatting> CODEC = StringRepresentable.fromEnum(CustomChatFormatting::values);
	public static final char PREFIX_CODE = '\u00a7';
	private static final Map<String, CustomChatFormatting> FORMATTING_BY_NAME = Arrays.stream(values())
			.collect(Collectors.toMap((p_126660_) -> {
				return cleanName(p_126660_.name);
			}, (p_126652_) -> {
				return p_126652_;
			}));
	private static final Pattern STRIP_FORMATTING_PATTERN = Pattern.compile("(?i)\u00a7[0-9A-FK-OR]");
	private final String name;
	private final char code;
	private final boolean isFormat;
	private final String toString;
	private final int id;
	@Nullable
	private final Integer color;

	private static String cleanName(String p_126663_) {
		return p_126663_.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
	}

	private CustomChatFormatting(String p_126627_, @Nullable char p_126628_, int p_126629_, Integer p_126630_) {
		this(p_126627_, p_126628_, false, p_126629_, p_126630_);
	}

	private CustomChatFormatting(String p_126634_, char p_126635_, boolean p_126636_) {
		this(p_126634_, p_126635_, p_126636_, -1, (Integer) null);
	}

	private CustomChatFormatting(String p_126640_, char p_126641_, @Nullable boolean p_126642_, int p_126643_,
			Integer p_126644_) {
		this.name = p_126640_;
		this.code = p_126641_;
		this.isFormat = p_126642_;
		this.id = p_126643_;
		this.color = p_126644_;
		this.toString = "\u00a7" + p_126641_;
	}

	public char getChar() {
		return this.code;
	}

	public int getId() {
		return this.id;
	}

	public boolean isFormat() {
		return this.isFormat;
	}

	public boolean isColor() {
		return !this.isFormat && this != RESET;
	}

	@Nullable
	public Integer getColor() {
		return this.color;
	}

	public String getName() {
		return this.name().toLowerCase(Locale.ROOT);
	}

	public String toString() {
		return this.toString;
	}

	@Nullable
	public static String stripFormatting(@Nullable String p_126650_) {
		return p_126650_ == null ? null : STRIP_FORMATTING_PATTERN.matcher(p_126650_).replaceAll("");
	}

	@Nullable
	public static CustomChatFormatting getByName(@Nullable String p_126658_) {
		return p_126658_ == null ? null : FORMATTING_BY_NAME.get(cleanName(p_126658_));
	}

	@Nullable
	public static CustomChatFormatting getById(int p_126648_) {
		if (p_126648_ < 0) {
			return RESET;
		} else {
			for (CustomChatFormatting customchatformatting : values()) {
				if (customchatformatting.getId() == p_126648_) {
					return customchatformatting;
				}
			}

			return null;
		}
	}

	@Nullable
	public static CustomChatFormatting getByCode(char p_126646_) {
		char c0 = Character.toString(p_126646_).toLowerCase(Locale.ROOT).charAt(0);

		for (CustomChatFormatting customchatformatting : values()) {
			if (customchatformatting.code == c0) {
				return customchatformatting;
			}
		}

		return null;
	}

	public static Collection<String> getNames(boolean p_126654_, boolean p_126655_) {
		List<String> list = Lists.newArrayList();

		for (CustomChatFormatting customchatformatting : values()) {
			if ((!customchatformatting.isColor() || p_126654_) && (!customchatformatting.isFormat() || p_126655_)) {
				list.add(customchatformatting.getName());
			}
		}

		return list;
	}

	public String getSerializedName() {
		return this.getName();
	}
}