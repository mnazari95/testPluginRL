package com.mnazari95;

import com.google.inject.Provides;

import javax.annotation.Nullable;
import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.annotations.HitsplatType;
import net.runelite.api.annotations.Varbit;
import net.runelite.api.events.*;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
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

	@Inject
	private ExampleHighlightOverlay exampleHighlightOverlay;

	@Getter(AccessLevel.PACKAGE)
	private final PlayerDamages playerDamages = new PlayerDamages();

	private int clickTick;
	@Getter(AccessLevel.PACKAGE)
	private int gameCycle;
	@Getter(AccessLevel.PACKAGE)
	private boolean attacked;
	@Getter(AccessLevel.PACKAGE)
	private TileObject interactedObject;
	private NPC interactedNpc;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Example started!");
		examplePluginOverlay = new ExamplePluginOverlay(playerDamages);
		overlayManager.add(examplePluginOverlay);
		overlayManager.add(exampleHighlightOverlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
		overlayManager.remove(examplePluginOverlay);
		overlayManager.remove(exampleHighlightOverlay);
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
		if (gameStateChanged.getGameState() == GameState.LOADING)
		{
			interactedObject = null;
		}

	}

	private List<NPC> localNpcs = new ArrayList<>();
	private NPC playerFightingNPC;

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		//called roughly every 600ms
		if (client.isPrayerActive(Prayer.PROTECT_FROM_MELEE))
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "protect from melee is on", null);
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged vb)
	{
		//when server side variable changes
		if (vb.getVarbitId() == Varbits.PRAYER_PROTECT_FROM_MELEE)
		{
			if (vb.getValue() == 1)
			{
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "MELEE ON", null);
			}

		}
	}

	@Subscribe
	public void onInteractingChanged(InteractingChanged interactingChanged)
	{
		Player localPlayer = client.getLocalPlayer();
		Actor mobT = interactingChanged.getTarget();
		Actor mobS = interactingChanged.getSource();

		if (interactingChanged.getSource() == localPlayer
				&& client.getTickCount() > clickTick && interactingChanged.getTarget() != interactedNpc)
		{
			interactedNpc = null;
			attacked = interactingChanged.getTarget() != null && interactingChanged.getTarget().getCombatLevel() > 0;
		}

		if (mobS != null && mobS.isInteracting())
		{
			log.debug("the mob source is interacting with..{}", mobS.getName());
		}

		if (mobT != null && mobT.isInteracting())
		{
			log.debug("the mob target is interacting with..{}", mobT.getName());
		}
	}
	@Subscribe
	public void onHitsplatApplied(HitsplatApplied hitsplatApplied)
	{
		//called whenever hitsplat applied on actor (player or npc)
		//hitsplat type 12 = hitting zero, 16 = regular hit, 47 = critical hit
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
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Hit a "+ hitsplat.getAmount() +" with type "+ hitsplat.getHitsplatType() +" applied a total: " + playerDamages.getTotalDamage(), null);
		} else if (hitsplat.isOthers() && (npcId == HELLHOUND_ID))
		{
			log.debug("dmg to player {}", hitsplatApplied.getHitsplat().getAmount());
		}

	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned)
	{
		NPC npc = npcDespawned.getNpc();

		if (npc == interactedNpc || npc == client.getLocalPlayer().getInteracting())
		{
			interactedNpc = null;
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "You just " + playerDamages.getHitCounter() + " hit that mob!", null);
			playerDamages.setTotalDamage(0);
			playerDamages.setHitCounter(0);
		}

	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked menuOptionClicked)
	{
		switch (menuOptionClicked.getMenuAction())
		{
			case WIDGET_TARGET_ON_GAME_OBJECT:
			case GAME_OBJECT_FIRST_OPTION:
			case GAME_OBJECT_SECOND_OPTION:
			case GAME_OBJECT_THIRD_OPTION:
			case GAME_OBJECT_FOURTH_OPTION:
			case GAME_OBJECT_FIFTH_OPTION:
			{
				int x = menuOptionClicked.getParam0();
				int y = menuOptionClicked.getParam1();
				int id = menuOptionClicked.getId();
				interactedObject = findTileObject(x, y, id);
				interactedNpc = null;
				clickTick = client.getTickCount();
				gameCycle = client.getGameCycle();
				break;
			}
			case WIDGET_TARGET_ON_NPC:
			case NPC_FIRST_OPTION:
			case NPC_SECOND_OPTION:
			case NPC_THIRD_OPTION:
			case NPC_FOURTH_OPTION:
			case NPC_FIFTH_OPTION:
			{
				interactedObject = null;
				interactedNpc = menuOptionClicked.getMenuEntry().getNpc();
				attacked = menuOptionClicked.getMenuAction() == MenuAction.NPC_SECOND_OPTION ||
						menuOptionClicked.getMenuAction() == MenuAction.WIDGET_TARGET_ON_NPC && WidgetInfo.TO_GROUP(client.getSelectedWidget().getId()) == WidgetID.SPELLBOOK_GROUP_ID;
				clickTick = client.getTickCount();
				gameCycle = client.getGameCycle();
				break;
			}
			// Any menu click which clears an interaction
			case WALK:
			case WIDGET_TARGET_ON_WIDGET:
			case WIDGET_TARGET_ON_GROUND_ITEM:
			case WIDGET_TARGET_ON_PLAYER:
			case GROUND_ITEM_FIRST_OPTION:
			case GROUND_ITEM_SECOND_OPTION:
			case GROUND_ITEM_THIRD_OPTION:
			case GROUND_ITEM_FOURTH_OPTION:
			case GROUND_ITEM_FIFTH_OPTION:
				interactedObject = null;
				interactedNpc = null;
				break;
			default:
				if (menuOptionClicked.isItemOp())
				{
					interactedObject = null;
					interactedNpc = null;
				}
		}
	}

	TileObject findTileObject(int x, int y, int id)
	{
		Scene scene = client.getScene();
		Tile[][][] tiles = scene.getTiles();
		Tile tile = tiles[client.getPlane()][x][y];
		if (tile != null)
		{
			for (GameObject gameObject : tile.getGameObjects())
			{
				if (gameObject != null && gameObject.getId() == id)
				{
					return gameObject;
				}
			}

			WallObject wallObject = tile.getWallObject();
			if (wallObject != null && wallObject.getId() == id)
			{
				return wallObject;
			}

			DecorativeObject decorativeObject = tile.getDecorativeObject();
			if (decorativeObject != null && decorativeObject.getId() == id)
			{
				return decorativeObject;
			}

			GroundObject groundObject = tile.getGroundObject();
			if (groundObject != null && groundObject.getId() == id)
			{
				return groundObject;
			}
		}
		return null;
	}

	@Nullable
	Actor getInteractedTarget()
	{
		return interactedNpc != null ? interactedNpc : client.getLocalPlayer().getInteracting();
	}

	@Provides
	ExampleConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ExampleConfig.class);
	}
}
