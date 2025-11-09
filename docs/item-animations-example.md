# Item Animations - Usage Examples

This document provides practical examples of using item animations with `ItemRenderer` components.

## Basic Item Animation Example

```java
// Create an item renderer for a diamond
ItemRenderer diamond = UI.itemRenderer(new ItemStack(Items.DIAMOND, 1));

// Add a coin spin effect when the item is obtained
diamond.addCoinSpinEffect();

// Add to your UI
container.addChild(diamond);
```

## Inventory Item Animations

### Item Pickup Animation

```java
public void onItemPickup(ItemStack item) {
    ItemRenderer renderer = UI.itemRenderer(item);
    
    // Dramatic entrance from above
    renderer.addFallBounceEffect();
    
    // Then add a continuous pulse glow for rare items
    if (isRareItem(item)) {
        renderer.addPulseGlowEffect();
    }
    
    addToInventory(renderer);
}
```

### Item Selection Feedback

```java
public void onItemSelected(ItemRenderer itemRenderer) {
    // Quick bounce to confirm selection
    itemRenderer.addJumpBounceEffect();
}

public void onItemHovered(ItemRenderer itemRenderer) {
    // Gentle pulse when hovering
    PulseGlowEffect pulse = PulseGlowEffect.oneShot(1.0f);
    pulse.setScaleAmplitude(0.08f);
    pulse.setEnableRotation(false);
    itemRenderer.addEffect(pulse);
}
```

## Shop/Trading Animations

### Featured Item Display

```java
public VBox createFeaturedItem(ItemStack item) {
    VBox container = new VBox().spacing(10).alignment(VBox.Alignment.CENTER);
    
    // Create scaled-up item renderer
    ItemRenderer itemRenderer = UI.itemRenderer(48, 48, item)
        .itemScale(2.0f);
    
    // Add continuous pulse glow for featured items
    itemRenderer.addPulseGlowEffect();
    
    // Add label
    Label label = UI.label(context, "Featured Item!", 0xFFFFD700);
    
    container.addChild(itemRenderer);
    container.addChild(label);
    
    return container;
}
```

### Purchase Confirmation

```java
public void onItemPurchased(ItemRenderer itemRenderer) {
    // Celebratory coin spin
    CoinSpinEffect spin = new CoinSpinEffect("purchase-spin", 0, 1.5f);
    spin.setRotationSpeed(1080f); // 3 full spins!
    spin.setGlowPulse(0.3f);      // Extra flashy
    itemRenderer.addEffect(spin);
}
```

## Loot/Reward Animations

### Chest Opening Animation

```java
public void displayChestRewards(List<ItemStack> rewards) {
    HBox rewardBox = new HBox().spacing(20);
    
    int delay = 0;
    for (ItemStack reward : rewards) {
        ItemRenderer renderer = UI.itemRenderer(reward);
        
        // Stagger the fall animations
        FallBounceEffect fall = new FallBounceEffect("fall-" + delay, 0, 1.5f);
        fall.setFallDistance(150f);
        fall.setBounceCount(2);
        fall.setRotation((float) (Math.random() * 45 - 22.5)); // Random tilt
        
        // Schedule the effect with a delay
        scheduleEffect(renderer, fall, delay * 200); // 200ms between each item
        
        rewardBox.addChild(renderer);
        delay++;
    }
    
    container.addChild(rewardBox);
}
```

### Rare Drop Highlight

```java
public void highlightRareDrop(ItemRenderer itemRenderer) {
    // Fall with dramatic bounce
    FallBounceEffect fall = new FallBounceEffect("rare-fall", 0, 2.0f);
    fall.setFallDistance(200f);
    fall.setBounceHeight(50f);
    fall.setBounceDecay(0.7f); // More bouncy
    fall.setRotation(360f);    // Full spin on the way down
    itemRenderer.addEffect(fall);
    
    // After fall completes, add continuous pulse glow
    scheduleDelayed(() -> {
        itemRenderer.addPulseGlowEffect();
    }, 2000); // 2 seconds (duration of fall effect)
}
```

## Crafting/Upgrade Animations

### Item Crafting Success

```java
public void onCraftingComplete(ItemRenderer resultItem) {
    // Flip animation to reveal the result
    FlipEffect flip = new FlipEffect("craft-flip", 0, 0.8f);
    flip.setFlipRotation(360f); // Full flip
    resultItem.addEffect(flip);
}
```

### Item Upgrade Animation

```java
public void onItemUpgraded(ItemRenderer itemRenderer) {
    // Spin + pulse combination
    itemRenderer.addSpinEffect();
    
    PulseGlowEffect pulse = new PulseGlowEffect("upgrade-pulse", 0);
    pulse.setScaleAmplitude(0.2f);
    pulse.setGlowIntensity(0.6f);
    pulse.setPulseFrequency(3.0f); // Fast pulse
    itemRenderer.addEffect(pulse);
}
```

## Interactive Showcase

### Rotating Item Display

```java
public ItemRenderer createRotatingDisplay(ItemStack item) {
    ItemRenderer renderer = UI.itemRenderer(64, 64, item)
        .itemScale(3.0f);
    
    // Continuous slow rotation
    SpinEffect spin = SpinEffect.continuous(0.5f); // Half rotation per second
    renderer.addEffect(spin);
    
    // Add subtle pulse for extra flair
    PulseGlowEffect pulse = new PulseGlowEffect("display-pulse", 0);
    pulse.setScaleAmplitude(0.05f);
    pulse.setEnableRotation(false);
    renderer.addEffect(pulse);
    
    return renderer;
}
```

### Item Comparison Flip

```java
public void compareItems(ItemRenderer oldItem, ItemRenderer newItem) {
    // Flip old item out
    FlipEffect flipOut = new FlipEffect("compare-out", 0, 0.5f);
    flipOut.setFlipRotation(90f); // Flip to edge
    oldItem.addEffect(flipOut);
    
    // Then flip new item in
    scheduleDelayed(() -> {
        FlipEffect flipIn = new FlipEffect("compare-in", 0, 0.5f);
        flipIn.setFlipRotation(90f);
        newItem.addEffect(flipIn);
    }, 500);
}
```

## Button Click Feedback

### Animated Item Button

```java
public SpriteButton createItemButton(ItemStack item, Runnable onClick) {
    SpriteButton button = new SpriteButton(40, 40, 0xFF444444);
    
    // Add item renderer as child
    ItemRenderer renderer = UI.itemRenderer(item).itemScale(1.5f);
    button.addChild(renderer);
    
    button.onClick(event -> {
        // Add bounce feedback to the item when button is clicked
        renderer.addJumpBounceEffect();
        onClick.run();
    });
    
    return button;
}
```

## Advanced Combinations

### Epic Loot Drop

```java
public void showEpicLoot(ItemStack epicItem) {
    ItemRenderer renderer = UI.itemRenderer(epicItem).itemScale(2.5f);
    
    // 1. Fall from above with dramatic rotation
    FallBounceEffect fall = new FallBounceEffect("epic-fall", 5, 2.0f); // High priority
    fall.setFallDistance(300f);
    fall.setBounceHeight(60f);
    fall.setRotation(720f); // Two full rotations!
    renderer.addEffect(fall);
    
    // 2. After landing, coin spin
    scheduleDelayed(() -> {
        CoinSpinEffect spin = new CoinSpinEffect("epic-spin", 10, 1.5f);
        spin.setRotationSpeed(900f);
        spin.setGlowPulse(0.4f);
        renderer.addEffect(spin);
    }, 2000);
    
    // 3. Finally, continuous pulse glow
    scheduleDelayed(() -> {
        PulseGlowEffect pulse = new PulseGlowEffect("epic-pulse", 0);
        pulse.setScaleAmplitude(0.12f);
        pulse.setGlowIntensity(0.5f);
        renderer.addEffect(pulse);
    }, 3500);
    
    container.addChild(renderer);
}
```

## Best Practices

### 1. Match Animation to Context

```java
// Quick feedback for common actions
item.addClickBounceEffect();  // 0.22 seconds

// Dramatic for rare events
item.addFallBounceEffect();   // 1.5 seconds
item.addCoinSpinEffect();     // 1.0 seconds

// Continuous for highlights
item.addPulseGlowEffect();    // Infinite
```

### 2. Use Channels to Prevent Conflicts

```java
// Bad - effects might conflict
item.addCoinSpinEffect();
item.addSpinEffect();  // Both try to rotate!

// Good - use different channels or ensure exclusivity
CoinSpinEffect spin1 = new CoinSpinEffect("spin-1", 0, 1.0f);
SpinEffect spin2 = new SpinEffect("spin-2", 10, 1.0f); // Higher priority
item.addEffect(spin1);
item.addEffect(spin2);
```

### 3. Stagger Multiple Item Animations

```java
int delay = 0;
for (ItemRenderer item : items) {
    scheduleDelayed(() -> item.addFallBounceEffect(), delay);
    delay += 150; // 150ms between each
}
```

### 4. Clean Up Continuous Effects

```java
// Start continuous effect
item.addPulseGlowEffect();

// Stop when no longer needed
item.cancelEffectChannel("pulse-glow");
```

### 5. Adjust Timing for UX

```java
// Fast for responsive feedback
JumpBounceEffect quickBounce = new JumpBounceEffect("quick", 0, 0.6f);
quickBounce.setJumpHeight(20f);
quickBounce.setBounceCount(1);

// Slow for dramatic effect
FallBounceEffect slowFall = new FallBounceEffect("slow", 0, 2.5f);
slowFall.setFallDistance(250f);
slowFall.setBounceCount(4);
```

## Performance Considerations

- Limit the number of simultaneous animations (especially continuous ones)
- Use effect channels to prevent stacking similar effects
- Cancel continuous effects when items are hidden or removed
- Consider using lower frequencies for background effects

```java
// Efficient: One continuous effect per item
item.addPulseGlowEffect();

// Inefficient: Multiple heavy effects running simultaneously
item.addPulseGlowEffect();
item.addSpinEffect();
item.addWanderEffect();
// ... etc
```
