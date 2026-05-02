# Trowel Arm Swing Animation

## Summary

Add the arm swing animation to the trowel when it is used (right-clicked on a block), matching the behavior of vanilla tools like the pickaxe.

## Problem

`Trowel.kt#useOnBlock` returns `ActionResult.PASS` on the client side to skip client-side placement logic. This suppresses the arm swing, so using the trowel has no visual feedback.

## Design

**File:** `src/main/kotlin/com/theendercore/trowel/Trowel.kt`

Change the client-side early return from `ActionResult.PASS` to `ActionResult.SUCCESS`:

```kotlin
// Before
if (c.world.isClient) return ActionResult.PASS

// After
if (c.world.isClient) return ActionResult.SUCCESS
```

`ActionResult.SUCCESS` signals Minecraft to play the arm swing animation. The arm swings optimistically on every right-click, consistent with vanilla tool behavior — no server confirmation needed.

## Scope

- One line change, no new files, no new dependencies.
- Server-side placement logic is unchanged.
- Arm swings on every right-click regardless of whether placement succeeds (same as pickaxe swinging whether or not it mines a block).
