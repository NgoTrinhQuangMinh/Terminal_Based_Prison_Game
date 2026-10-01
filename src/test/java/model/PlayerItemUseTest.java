package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the player's health and attack, and using items from the inventory.
 *
 * @author Xinran Tian
 */
class PlayerItemUseTest {

    private Player newPlayer() {
        return new Player(new Position(0, 0));
    }

    /**
     * Verifies that a new player starts at full health with base attack.
     */
    @Test
    void newPlayerHasFullHealthAndBaseAttack() {
        Player player = newPlayer();

        assertEquals(Player.MAX_HEALTH, player.health());
        assertEquals(Player.BASE_ATTACK, player.attack());
        assertFalse(player.weaponEquipped());
    }

    /**
     * Verifies that damage reduces health but never below zero.
     */
    @Test
    void damageReducesHealthWithLowerBoundOfZero() {
        Player player = newPlayer();

        player.damage(3);
        assertEquals(7, player.health());

        player.damage(20);
        assertEquals(0, player.health());
    }

    /**
     * Verifies that negative damage does not heal the player.
     */
    @Test
    void negativeDamageDoesNotHeal() {
        Player player = newPlayer();
        player.damage(4);

        player.damage(-5);

        assertEquals(6, player.health());
    }

    /**
     * Verifies that a herb restores health and is consumed.
     */
    @Test
    void herbRestoresHealthAndIsConsumed() {
        Player player = newPlayer();
        player.inventory().add(Inventory.HERB);
        player.damage(6);

        String feedback = player.use("herb");

        assertEquals(8, player.health());
        assertFalse(player.inventory().has(Inventory.HERB));
        assertTrue(feedback.contains("restore 4"));
    }

    /**
     * Verifies that healing never goes above maximum health.
     */
    @Test
    void herbHealingDoesNotExceedMaximum() {
        Player player = newPlayer();
        player.inventory().add(Inventory.HERB);
        player.damage(1);

        player.use("herb");

        assertEquals(Player.MAX_HEALTH, player.health());
    }

    /**
     * Verifies that only one herb is used at a time.
     */
    @Test
    void onlyOneHerbIsConsumedPerUse() {
        Player player = newPlayer();
        player.inventory().add(Inventory.HERB);
        player.inventory().add(Inventory.HERB);
        player.damage(5);

        player.use("herb");

        assertEquals(1, player.inventory().count(Inventory.HERB));
    }

    /**
     * Verifies that a herb is kept when the player is already at full health.
     */
    @Test
    void herbIsKeptAtFullHealth() {
        Player player = newPlayer();
        player.inventory().add(Inventory.HERB);

        String feedback = player.use("herb");

        assertTrue(player.inventory().has(Inventory.HERB));
        assertEquals(Player.MAX_HEALTH, player.health());
        assertTrue(feedback.contains("already full"));
    }

    /**
     * Verifies that equipping a held weapon increases attack.
     */
    @Test
    void equippingWeaponIncreasesAttack() {
        Player player = newPlayer();
        player.inventory().add(Inventory.WEAPON);

        player.use("weapon");

        assertTrue(player.weaponEquipped());
        assertEquals(Player.BASE_ATTACK + Player.WEAPON_BONUS, player.attack());
    }

    /**
     * Verifies that equipping the weapon again does not stack the bonus.
     */
    @Test
    void equippingWeaponTwiceDoesNotStack() {
        Player player = newPlayer();
        player.inventory().add(Inventory.WEAPON);

        player.use("weapon");
        String feedback = player.use("weapon");

        assertEquals(Player.BASE_ATTACK + Player.WEAPON_BONUS, player.attack());
        assertTrue(feedback.contains("already equipped"));
    }

    /**
     * Verifies that item names are matched ignoring case and spaces.
     */
    @Test
    void itemNamesIgnoreCaseAndSpaces() {
        Player player = newPlayer();
        player.inventory().add(Inventory.WEAPON);

        player.use("  SWORD ");

        assertTrue(player.weaponEquipped());
    }

    /**
     * Verifies that using an item that is not held changes nothing.
     */
    @Test
    void usingMissingItemChangesNothing() {
        Player player = newPlayer();
        player.damage(5);

        String herbFeedback = player.use("herb");
        String weaponFeedback = player.use("weapon");

        assertEquals(5, player.health());
        assertFalse(player.weaponEquipped());
        assertTrue(herbFeedback.contains("no herb"));
        assertTrue(weaponFeedback.contains("no weapon"));
    }

    /**
     * Verifies that an unknown item name lists the items that can be used.
     */
    @Test
    void unknownItemShowsUsableItems() {
        Player player = newPlayer();

        String feedback = player.use("banana");

        assertTrue(feedback.contains("use herb"));
        assertTrue(feedback.contains("use weapon"));
    }

    /**
     * Verifies that a missing item name explains how to use the command.
     */
    @Test
    void missingItemNameShowsUsage() {
        Player player = newPlayer();

        assertTrue(player.use(null).startsWith("Use what?"));
        assertTrue(player.use("   ").startsWith("Use what?"));
        assertEquals(Player.MAX_HEALTH, player.health());
    }
}
