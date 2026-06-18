package io.github.currenj.gelatinui.gui;

import io.github.currenj.gelatinui.gui.animation.FloatKeyframeAnimation;
import io.github.currenj.gelatinui.gui.animation.Keyframe;
import io.github.currenj.gelatinui.gui.components.Panel;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UIContainerSafeMutationTest {

    @Test
    public void updateRemovingChildDuringChildAnimationCompleteDoesNotThrow() {
        Panel container = new Panel();
        Panel childA = new Panel();
        Panel childB = new Panel();
        container.addChild(childA);
        container.addChild(childB);

        childA.playAnimation(new FloatKeyframeAnimation(
                "test",
                List.of(new Keyframe(0f, 0f), new Keyframe(0.1f, 1f)),
                v -> {},
                () -> container.removeChild(childB)));

        assertDoesNotThrow(() -> container.update(0.2f));
        assertFalse(container.getChildren().contains(childB), "childB should have been removed");
    }

    @Test
    public void updateAddingChildDuringChildAnimationCompleteDoesNotThrow() {
        Panel root = new Panel();
        Panel childA = new Panel();
        root.addChild(childA);

        Panel sphere = new Panel();
        Panel icon = new Panel();

        childA.playAnimation(new FloatKeyframeAnimation(
                "test",
                List.of(new Keyframe(0f, 0f), new Keyframe(0.1f, 1f)),
                v -> {},
                () -> sphere.addChild(icon)));

        assertDoesNotThrow(() -> root.update(0.2f));
        assertTrue(sphere.getChildren().contains(icon), "icon should have been added to sphere");
    }

    @Test
    public void clickHandlerRemovingSiblingDuringDispatchDoesNotThrow() {
        Panel container = new Panel();
        Panel childA = new Panel();
        Panel childB = new Panel();
        container.addChild(childA);
        container.addChild(childB);

        childA.onClick(event -> container.removeChild(childB));

        UIEvent clickEvent = new UIEvent(UIEvent.Type.CLICK, childA, 0, 0);
        assertDoesNotThrow(() -> assertTrue(container.handleEvent(clickEvent)));
        assertFalse(container.getChildren().contains(childB), "childB should have been removed");
    }
}
