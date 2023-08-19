package com.mnazari95;

import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.TileObject;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;
import net.runelite.client.util.ColorUtil;

import javax.inject.Inject;
import java.awt.*;

public class ExampleHighlightOverlay extends Overlay {

    private static final Color INTERACT_CLICK_COLOR = new Color(0x90ffffff);

    private final Client client;
    private final ExamplePlugin plugin;
    private final ExampleConfig config;

    private final ModelOutlineRenderer modelOutlineRenderer;

    @Inject
    ExampleHighlightOverlay(Client client, ExamplePlugin plugin, ExampleConfig config, ModelOutlineRenderer modelOutlineRenderer)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.modelOutlineRenderer = modelOutlineRenderer;
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        renderTarget();
        return null;
    }

    private void renderTarget()
    {
        Actor target = plugin.getInteractedTarget();
        if (target instanceof NPC)
        {
            Color startColor = new Color(0x9055FF00, true);
            Color endColor = new Color(0x9000C4FF, true);
            Color clickColor = getClickColor(startColor, endColor,
                    client.getGameCycle() - plugin.getGameCycle());
            modelOutlineRenderer.drawOutline((NPC) target, 1, clickColor,2);
        }
    }

    private Color getClickColor(Color start, Color end, long time)
    {
        if (time < 5)
        {
            return ColorUtil.colorLerp(start, INTERACT_CLICK_COLOR, time / 5f);
        }
        else if (time < 10)
        {
            return ColorUtil.colorLerp(INTERACT_CLICK_COLOR, end, (time - 5) / 5f);
        }
        return end;
    }
}
