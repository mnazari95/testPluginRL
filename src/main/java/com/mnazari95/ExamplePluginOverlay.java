package com.mnazari95;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;

public class ExamplePluginOverlay extends OverlayPanel
{

    final private PlayerDamages playerDamages;


    @Inject
    ExamplePluginOverlay(PlayerDamages playerDamages) {
        this.playerDamages = playerDamages;
    }


    @Override
    public Dimension render(Graphics2D graphics) {

        panelComponent.getChildren().add(
                LineComponent.builder()
                        .left("total Damage: ")
                        .right(Integer.toString(playerDamages.getTotalDamage()))
                        .build()
        );
        return super.render(graphics);
    }
}
