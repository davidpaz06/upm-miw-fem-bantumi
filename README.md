# Bantumi - Persistence (preferences, files and Room database)

Android project (Java) built with Gradle. It builds with `assembleDebug` (verified).

## Required features

1. **Restart**: "Reiniciar" menu option with a confirmation dialog.
2. **Save game**: the game is written to the app's internal storage.
3. **Restore game**: if the current game has been modified since it was started or restored, a confirmation is requested before replacing it.
4. **Save score**: when the game ends, the score is stored in Room (player name taken from Settings, date and time, seeds in each store). It is saved only once per game.
5. **Best results**: top 10 sorted by the highest number of seeds of either player (ties: most recent first). Option to delete all results with confirmation.

## Saved game file format

One file per saved game: `partida_{milliseconds}.txt` (UTF-8).

```
Line 1: TURNO;s0,s1,...,s13   (turn: TURNO_J1 or TURNO_J2; 14 pits)
Line 2: elapsed time in milliseconds (stopwatch)
```

When restoring, the content is validated (turn, 14 values, non-negative). If it is invalid, an error is shown and the current game is left untouched.

## Database (Room)

Table `Resultado`: `id`, `nombreJugador`, `fecha`, `semillasJ1`, `semillasJ2`, `duracionMs`.

Schema version 2. Since `duracionMs` was added, `fallbackToDestructiveMigration()` is used, so a previous installation loses its stored results (the app is unpublished). Writes are performed on a background thread.

## Optional features implemented

- Initial seeds per pit (2 to 8, default 4) in Settings.
- Starting player: player, computer or random.
- Turn and store colors in Settings.
- Border around the last pit chosen by each player.
- Game stopwatch; the duration is saved and shown in the results.
- Best-results filter by player and by minimum seeds.
- Multiple saved games, with a list to choose which one to restore.

## Decisions and clarifications

- The computer plays on its own, with an 800 ms pause, when it is its turn (previously it moved when the user tapped a pit). It is paused while the app is in the background.
- The winner is decided by comparing the two stores (it does not depend on the initial seeds, so it is valid for restored games).
- Changes in Settings (initial seeds, starting player) apply when the game is restarted; colors apply when returning to the game screen.
- Each save creates a new file; there is no limit and no option to delete them.
- If the player name is empty, "Jugador 1" is used.
