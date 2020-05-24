/*
 * Satin
 * Copyright (C) 2019-2020 Ladysnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; If not, see <https://www.gnu.org/licenses>.
 */
package ladysnake.satin.config;

public class OptionalFeature {
    /**Indicates that this feature is used by at least one mod*/
    private transient boolean used;
    /**Indicates that this feature is enabled by the config*/
    private boolean configEnabled;

    OptionalFeature enableByDefault() {
        this.configEnabled = true;
        return this;
    }

    public void use() {
        this.used = true;
    }

    public boolean isConfigEnabled() {
        return configEnabled;
    }

    public boolean isActive() {
        return configEnabled && used;
    }
}
