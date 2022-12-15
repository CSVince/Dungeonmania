package dungeonmania.entities.inventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.entities.BreakableItem;
import dungeonmania.entities.Entity;
import dungeonmania.entities.EntityFactory;
import dungeonmania.entities.Player;
import dungeonmania.entities.buildables.Bow;
import dungeonmania.entities.buildables.Sceptre;
import dungeonmania.entities.collectables.Arrow;
import dungeonmania.entities.collectables.Key;
import dungeonmania.entities.collectables.SunStone;
import dungeonmania.entities.collectables.Sword;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.collectables.Wood;
import dungeonmania.entities.enemies.ZombieToast;

public class Inventory {
    private List<InventoryItem> items = new ArrayList<>();

    public boolean add(InventoryItem item) {
        items.add(item);
        return true;
    }

    public void remove(InventoryItem item) {
        items.remove(item);
    }

    public List<String> getBuildables(Game game) {

        int wood = count(Wood.class);
        int arrows = count(Arrow.class);
        int treasure = count(Treasure.class);
        int keys = count(Key.class);
        int sunstones = count(SunStone.class);
        List<String> result = new ArrayList<>();

        if (wood >= 1 && arrows >= 3) {
            result.add("bow");
        }
        if (wood >= 2 && (treasure >= 1 || keys >= 1 || sunstones >= 1)) {
            result.add("shield");
        }
        if (canBuildSceptre()) {
            result.add("sceptre");
        }
        if (canBuildMidnightArmour(game)) {
            result.add("midnight_armour");
        }
        return result;
    }

    public InventoryItem checkBuildCriteria(Player p, boolean remove, String entity, EntityFactory factory) {

        List<Wood> wood = getEntities(Wood.class);
        List<Arrow> arrows = getEntities(Arrow.class);
        List<Treasure> treasure = getEntities(Treasure.class);
        List<Key> keys = getEntities(Key.class);
        List<SunStone> sunstones = getEntities(SunStone.class);
        List<Sword> swords = getEntities(Sword.class);

        if (entity.equals("bow")) {
            if (remove) {
                items.remove(wood.get(0));
                items.remove(arrows.get(0));
                items.remove(arrows.get(1));
                items.remove(arrows.get(2));
            }
            return factory.buildBow();
        } else if (entity.equals("shield")) {
            if (remove) {
                items.remove(wood.get(0));
                items.remove(wood.get(1));
                if (treasure.size() - sunstones.size() >= 1) {
                    removeTreasure();
                } else if (keys.size() >= 1) {
                    items.remove(keys.get(0));
                }
            }
            return factory.buildShield();
        } else if (entity.equals("sceptre")) {
            if (remove) {
                if (wood.size() < 1) {
                    items.remove(arrows.get(0));
                    items.remove(arrows.get(1));
                } else {
                    items.remove(wood.get(0));
                }
                // Remove 1 key or 1 treasure
                if (keys.size() >= 1) {
                    items.remove(keys.get(0));
                } else if (treasure.size() - sunstones.size() >= 1) {
                    removeTreasure();
                }
                // Remove a sun stone
                if (sunstones.get(0) != null) {
                    items.remove(sunstones.get(0));
                } else {
                    items.remove(sunstones.get(1));
                }
            }
            return factory.buildSceptre();
        } else if (entity.equals("midnight_armour")) {
            if (remove) {
                items.remove(swords.get(0));
                items.remove(sunstones.get(0));
            }
            return factory.buildMidnightArmour();
        }
        return null;
    }

    public void removeTreasure() {
        List<Treasure> treasure = getEntities(Treasure.class);
        items.remove(treasure.stream()
                    .filter(treasureItem -> !(treasureItem instanceof SunStone))
                    .findFirst()
                    .get());
    }

    public boolean canBuildMidnightArmour(Game game) {
        List<SunStone> sunstones = getEntities(SunStone.class);
        List<Sword> swords = getEntities(Sword.class);
        return (sunstones.size() >= 1 && swords.size() >= 1 && game.getEntities(ZombieToast.class).size() == 0);
    }

    public boolean canBuildSceptre() {
        List<Wood> wood = getEntities(Wood.class);
        List<Arrow> arrows = getEntities(Arrow.class);
        List<Treasure> treasure = getEntities(Treasure.class);
        List<Key> keys = getEntities(Key.class);
        List<SunStone> sunstones = getEntities(SunStone.class);

        if (wood.size() < 1 && arrows.size() < 2) {
            return false;
        }
        if (sunstones.size() < 1) {
            return false;
        }
        if (keys.size() < 1 && treasure.size() - sunstones.size() < 1 && sunstones.size() < 2) {
            return false;
        }
        return true;
    }

    public <T extends InventoryItem> T getFirst(Class<T> itemType) {
        for (InventoryItem item : items)
            if (itemType.isInstance(item)) return itemType.cast(item);
        return null;
    }

    public <T extends InventoryItem> int count(Class<T> itemType) {
        int count = 0;
        for (InventoryItem item : items)
            if (itemType.isInstance(item)) count++;
        return count;
    }

    public Entity getEntity(String itemUsedId) {
        for (InventoryItem item : items)
            if (((Entity) item).getId().equals(itemUsedId)) return (Entity) item;
        return null;
    }

    public List<Entity> getEntities() {
        return items.stream().map(Entity.class::cast).collect(Collectors.toList());
    }

    public <T> List<T> getEntities(Class<T> clz) {
        return items.stream().filter(clz::isInstance).map(clz::cast).collect(Collectors.toList());
    }

    public boolean hasWeapon() {
        return getFirst(Sword.class) != null || getFirst(Bow.class) != null;
    }

    public BreakableItem getWeapon() {
        BreakableItem weapon = getFirst(Sword.class);
        if (weapon == null)
            return getFirst(Bow.class);
        return weapon;
    }

    public void updateAfterBribe(int amount) {
        if (amount == 0) return;
        int counter = 0;
        Iterator<InventoryItem> iterator = items.iterator();
        while (iterator.hasNext()) {
            InventoryItem item = iterator.next();
            if (item instanceof Treasure && !(item instanceof SunStone)) {
                iterator.remove();
                counter++;
            }
            if (counter == amount) {
                return;
            }
        }
    }

    public List<InventoryItem> getItems() {
        return items;
    }

    public void loadInItem(JSONObject json, EntityFactory factory) {
        Entity newItem = factory.createEntity((JSONObject) json);
        if (newItem == null) {
            newItem = (Entity) checkBuildCriteria(null, false, json.getString("type"), factory);
        }
        if (newItem == null) {
            return;
        }
        if (newItem instanceof BreakableItem) {
            ((BreakableItem) newItem).setDurability(json.getInt("durability"));
        }
        add((InventoryItem) newItem);
    }

    public void repopulateInventory(JSONArray json, EntityFactory factory) {
        json.forEach(item -> loadInItem((JSONObject) item, factory));
    }

    public int getSceptreDuration() {
        return getFirst(Sceptre.class).getDuration();
    }

}
