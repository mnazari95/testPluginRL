package com.mnazari95;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("test")
public interface ExampleConfig extends Config
{
	@ConfigItem(
		keyName = "welcome message",
		name = "YES SIRR",
		description = "The message to show to the user when they login"
	)
	default String greeting()
	{
		return "Hello";
	}
}
