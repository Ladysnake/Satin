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
package ladysnake.satin.api.experimental.managed;

import org.apiguardian.api.API;
import org.jetbrains.annotations.ApiStatus;

import static org.apiguardian.api.API.Status.DEPRECATED;

/**
 * @deprecated use the promoted version in {@code ladysnake.satin.api.managed.uniform}
 */
@Deprecated
@ApiStatus.ScheduledForRemoval
@API(status = DEPRECATED, since = "1.4.0")
public interface SamplerUniform extends ladysnake.satin.api.managed.uniform.SamplerUniform {

}
