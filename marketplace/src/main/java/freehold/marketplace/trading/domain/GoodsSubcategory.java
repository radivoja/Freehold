package freehold.marketplace.trading.domain;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public enum GoodsSubcategory {

    // Rakija
    SLJIVOVICA(GoodsCategory.RAKIJA),
    LOZOVACA(GoodsCategory.RAKIJA),
    DUNJEVACA(GoodsCategory.RAKIJA),
    KAJSIJEVACA(GoodsCategory.RAKIJA),
    VILJAMOVKA(GoodsCategory.RAKIJA),
    KLEKOVACA(GoodsCategory.RAKIJA),

    // Juice
    APPLE_JUICE(GoodsCategory.JUICE),
    PEAR_JUICE(GoodsCategory.JUICE),
    RASPBERRY_JUICE(GoodsCategory.JUICE),
    SOUR_CHERRY_JUICE(GoodsCategory.JUICE),

    // Preserve
    AJVAR(GoodsCategory.PRESERVE),
    PINDJUR(GoodsCategory.PRESERVE),
    LJUTENICA(GoodsCategory.PRESERVE),
    SLATKO(GoodsCategory.PRESERVE),
    PEKMEZ(GoodsCategory.PRESERVE),

    // Dairy
    KAJMAK(GoodsCategory.DAIRY),
    PRLJO(GoodsCategory.DAIRY),
    ZLATIBORSKI_SIR(GoodsCategory.DAIRY),

    // Meat
    UZICKA_PRSUTA(GoodsCategory.MEAT),
    SREMSKA_KOBASICA(GoodsCategory.MEAT),
    DIMLJENO_MESO(GoodsCategory.MEAT),
    SUDZUK(GoodsCategory.MEAT),

    // Honey
    LIPOV_MED(GoodsCategory.HONEY),
    BAGREMOV_MED(GoodsCategory.HONEY),
    LIVADSKI_MED(GoodsCategory.HONEY),

    // Fruit
    PLUMS(GoodsCategory.FRUIT),
    RASPBERRIES(GoodsCategory.FRUIT),
    QUINCE(GoodsCategory.FRUIT),
    APPLES(GoodsCategory.FRUIT),
    WALNUTS(GoodsCategory.FRUIT),

    // Vegetable
    PEPPERS(GoodsCategory.VEGETABLE),
    TOMATOES(GoodsCategory.VEGETABLE),
    CUCUMBERS(GoodsCategory.VEGETABLE),
    POTATOES(GoodsCategory.VEGETABLE);

    private final GoodsCategory category;

    private static final Map<GoodsCategory, Set<GoodsSubcategory>> BY_CATEGORY = buildCategoryMap();

    private static Map<GoodsCategory, Set<GoodsSubcategory>> buildCategoryMap() {
        EnumMap<GoodsCategory, Set<GoodsSubcategory>> map = new EnumMap<>(GoodsCategory.class);
        for (GoodsSubcategory sub : values()) {
            map.computeIfAbsent(sub.getCategory(), k -> EnumSet.noneOf(GoodsSubcategory.class)).add(sub);
        }
        return Map.copyOf(map);
    }

    GoodsSubcategory(GoodsCategory category) {
        this.category = category;
    }

    public GoodsCategory getCategory() {
        return category;
    }

    public static Set<GoodsSubcategory> byCategory(GoodsCategory category) {
        return BY_CATEGORY.getOrDefault(category, EnumSet.noneOf(GoodsSubcategory.class));
    }
}
