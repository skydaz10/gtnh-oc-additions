# GTNH OC Additions

Adds additional open computers drivers for blocks of the GTNH modpack.

## Features

- Adding controls to enable/disable/toggle the Cargo Loader & Unloader & Controller (Galacticraft). Setting frequencies
  of the controller and checking whether they are valid. Get whether a rocket is docker on the connected landing pad.
- Reading the displayed text of a card inside an information panel (Nuclear Control)

## Currently added methods

- cargo_loader
  - isEnabled function(): bool --- Gets the current state of the machine.
  - setEnabled function(bool: enable): bool --- Sets on/off and returns whether its enabled.
  - toggleEnabled function(): bool --- Toggles the loader on/off and returns whether its enabled.
- cargo_unloader
  - isEnabled function(): bool --- Gets the current state of the machine.
  - setEnabled function(bool: enable): bool --- Sets on/off and returns whether its enabled.
  - toggleEnabled function(): bool --- Toggles the loader on/off and returns whether its enabled.
- cargo_launch_controller
  - isEnabled function(): bool --- Gets the current state of the machine.
  - setEnabled function(bool: enable): bool --- Sets on/off and returns whether its enabled.
  - toggleEnabled function(): bool --- Toggles the loader on/off and returns whether its enabled.
  - getFrequency function(): int --- Gets the current frequency.
  - setFrequency function(int: frequency): void --- Sets a new frequency.
  - isValidFrequency function(): bool --- Gets whether the current frequency is valid.
  - getDstFrequency function(): int --- Gets the current destination frequency.
  - setDstFrequency function(int: frequency): void --- Sets a new destination frequency.
  - isValidDstFrequency function(): bool --- Gets whether the current destination frequency is valid.
  - isRocketDocked function(): bool --- Gets whether any rocket is docked on the connected landing pad.
- info_panel
  - getCardData function(int: card index): string --- Gets the card data or null if invalid.
