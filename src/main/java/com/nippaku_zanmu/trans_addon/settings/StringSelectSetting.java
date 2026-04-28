/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package com.nippaku_zanmu.trans_addon.settings;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import meteordevelopment.meteorclient.settings.IVisible;
import meteordevelopment.meteorclient.settings.Setting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StringSelectSetting extends Setting<Set<String>> {
    public final Predicate<String> filter;
    private List<String> suggestions;
    public Set<String> validValues;
    private final static List<String> groups = List.of("animal", "wateranimal", "monster", "ambient", "misc");

    public StringSelectSetting(String name, String description, Set<String> defaultValue, Consumer<Set<String>> onChanged, Consumer<Setting<Set<String>>> onModuleActivated, IVisible visible, Predicate<String> filter, Set<String> validValues) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);
        this.validValues = validValues;
        this.filter = filter;
    }

    @Override
    public void resetImpl() {
        value = new ObjectOpenHashSet<>(defaultValue);
    }

    @Override
    protected Set<String> parseImpl(String str) {
        return Arrays.stream(str.split(",")).collect(Collectors.toSet());
//        String[] values = str.split(",");
//        Set<EntityType<?>> entities = new ObjectOpenHashSet<>(values.length);
//
//        try {
//            for (String value : values) {
//                EntityType<?> entity = parseId(Registries.ENTITY_TYPE, value);
//                if (entity != null) entities.add(entity);
//                else {
//                    String lowerValue = value.trim().toLowerCase();
//                    if (!groups.contains(lowerValue)) continue;
//
//                    for (EntityType<?> entityType : Registries.ENTITY_TYPE) {
//                        if (filter != null && !filter.test(entityType)) continue;
//
//                        switch (lowerValue) {
//                            case "animal" -> {
//                                if (entityType.getSpawnGroup() == SpawnGroup.CREATURE) entities.add(entityType);
//                            }
//                            case "wateranimal" -> {
//                                if (entityType.getSpawnGroup() == SpawnGroup.WATER_AMBIENT
//                                    || entityType.getSpawnGroup() == SpawnGroup.WATER_CREATURE
//                                    || entityType.getSpawnGroup() == SpawnGroup.UNDERGROUND_WATER_CREATURE
//                                    || entityType.getSpawnGroup() == SpawnGroup.AXOLOTLS) entities.add(entityType);
//                            }
//                            case "monster" -> {
//                                if (entityType.getSpawnGroup() == SpawnGroup.MONSTER) entities.add(entityType);
//                            }
//                            case "ambient" -> {
//                                if (entityType.getSpawnGroup() == SpawnGroup.AMBIENT) entities.add(entityType);
//                            }
//                            case "misc" -> {
//                                if (entityType.getSpawnGroup() == SpawnGroup.MISC) entities.add(entityType);
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (Exception ignored) {}
//
//        return entities;
    }

    @Override
    protected boolean isValueValid(Set<String> value) {
        return true;
    }

    @Override
    public List<String> getSuggestions() {
        if (suggestions==null){
            suggestions = new ArrayList<>(groups);
            for (String str : validValues) {
                if (filter == null || filter.test(str))
                    suggestions.add(str);
            }
        }
        return suggestions;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag valueTag = new ListTag();
        for (String s : get()) {
            valueTag.add(StringTag.valueOf(s));
        }
        tag.put("value", valueTag);

        return tag;
    }

    @Override
    public Set<String> load(CompoundTag tag) {
        get().clear();

        ListTag valueTag = tag.getListOrEmpty("value");
        for (Tag tagI : valueTag) {
            String s = tagI.asString().orElse("");
            if ((filter == null || filter.test(s))&& validValues.contains(s))get().add(s);
//            EntityType<?> type = Registries.ENTITY_TYPE.get(Identifier.of(tagI.asString()));
//            if (filter == null || filter.test(type)) get().add(type);
        }

        return get();
    }

    public static class Builder extends SettingBuilder<Builder, Set<String>, StringSelectSetting> {
        private Predicate<String> filter;
        private Set<String> validValues = new LinkedHashSet<>();

        public Builder() {
            super(new ObjectOpenHashSet<>(0));
        }

        public Builder defaultValue(String... defaults) {
            return defaultValue(defaults != null ? new ObjectOpenHashSet<>(defaults) : new ObjectOpenHashSet<>(0));
        }
        public Builder validValues(String... defaults) {
            validValues.addAll(List.of(defaults));
            return this;
            //return defaultValue(defaults != null ? new ObjectOpenHashSet<>(defaults) : new ObjectOpenHashSet<>(0));
        }
        public Builder validValues(Set<String> validValues) {
            this.validValues.addAll(validValues);
            return this;
            //return defaultValue(defaults != null ? new ObjectOpenHashSet<>(defaults) : new ObjectOpenHashSet<>(0));
        }

        public Builder filter(Predicate<String> filter) {
            this.filter = filter;
            return this;
        }

        @Override
        public StringSelectSetting build() {
            return new StringSelectSetting(name, description, defaultValue, onChanged, onModuleActivated, visible, filter,validValues);
        }
    }
}
