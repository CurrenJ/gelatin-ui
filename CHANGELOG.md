# Changelog

## 1.0.40

Fixed clicks near the right edge of the screen being ignored on Minecraft 1.21.1. An invisible page scrollbar caught clicks, hover and the mouse wheel there even on screens that don't scroll, so buttons and scrollbars under it stopped responding. It now only takes input while the screen can actually scroll.

## 1.0.39

Fixed clipped GUI content appearing in the wrong place on Minecraft 1.21.1. Scissor rectangles now follow the current GUI position and scale instead of landing at the screen origin.

## 1.0.38

Added Forge support for Minecraft 1.21.1 alongside Fabric and NeoForge.
