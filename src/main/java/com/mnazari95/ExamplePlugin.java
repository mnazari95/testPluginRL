package com.mnazari95;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.NpcDespawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@PluginDescriptor(
	name = "testPluginRL",
		description = "first runelite plugin testing api"
)
public class ExamplePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ExampleConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ExamplePluginOverlay examplePluginOverlay;

	@Getter(AccessLevel.PACKAGE)
	private final PlayerDamages playerDamages = new PlayerDamages();

	@Override
	protected void startUp() throws Exception
	{
		log.info("Example started!");
		examplePluginOverlay = new ExamplePluginOverlay(playerDamages);
		overlayManager.add(examplePluginOverlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
		overlayManager.remove(examplePluginOverlay);
	}


	private final static int HELLHOUND_ID = 104;
	private List<Integer> animationList;
	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "testPluginRL says " + config.greeting(), null);

		}
	}

	private List<NPC> localNpcs = new ArrayList<>();
	private NPC playerFightingNPC;
	/*
	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		//called roughly every 600ms
		localNpcs = new ArrayList<>();
		localNpcs = client.getNpcs();
		client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "first monster on list " + localNpcs.stream().findFirst().get().getName(), null);


//		counter++;
//		client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "counter "+counter, null);
	}
	*/
	@Subscribe
	public void onHitsplatApplied(HitsplatApplied hitsplatApplied)
	{
		//called whenever hitsplat applied on actor (player or npc)
		Player localPlayer = client.getLocalPlayer();
		Actor actor = hitsplatApplied.getActor();

		if (!(actor instanceof NPC))
		{
			return;
		}

		Hitsplat hitsplat = hitsplatApplied.getHitsplat();
		final int npcId = ((NPC) actor).getId();
		if (hitsplat.isMine())
		{
			playerDamages.addPlayerDamage(hitsplat.getAmount());
			playerDamages.incrementHitCounter();
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Hitsplat applied a " + playerDamages.getTotalDamage(), null);
		} else if (hitsplat.isOthers() && (npcId == HELLHOUND_ID))
		{
			log.debug("dmg to player {}", hitsplatApplied.getHitsplat().getAmount());
		}

	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned)
	{
		NPC npc = npcDespawned.getNpc();

		if (npc.getId() == HELLHOUND_ID)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "You just " + playerDamages.getHitCounter() + " hit that mob!", null);
			playerDamages.setTotalDamage(0);
			playerDamages.setHitCounter(0);
		}
	}

	@Provides
	ExampleConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ExampleConfig.class);
	}
}
