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
    ExamplePluginOverlay(PlayerDamages playerDamages)
    {
        this.playerDamages = playerDamages;
    }


    @Override
    public Dimension render(Graphics2D graphics) {

        panelComponent.getChildren().add(
                LineComponent.builder()
                        .left("overhead prayer status: ")
                        .right(Boolean.toString(playerDamages.isOverheadPrayerState()))
                        .build()
        );
        panelComponent.getChildren().add(
                LineComponent.builder()
                        .left("total Damage: ")
                        .right(Integer.toString(playerDamages.getTotalDamage()))
                        .build()
        );
        panelComponent.getChildren().add(
                LineComponent.builder()
                        .left("game tick counter: ")
                        .right(Integer.toString(playerDamages.getGameTickCounter()))
                        .build()
        );
        panelComponent.getChildren().add(
                LineComponent.builder()
                        .left("overhead counter: ")
                        .right(Integer.toString(playerDamages.getOverheadEventTriggeredCounter()))
                        .build()
        );
        panelComponent.getChildren().add(
                LineComponent.builder()
                        .left("prayer tick: ")
                        .right(Integer.toString(playerDamages.getOverheadTickCounter()))
                        .build()
        );
        return super.render(graphics);
    }
}
